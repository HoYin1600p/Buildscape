package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.block.*;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.Optional;

/** Dispatches classes retired in 26.2 that are still required by 1.21.1. */
final class RestoredBlockFactory {
    private RestoredBlockFactory() {}

    static Block create(BlockDefinition def, BlockBehaviour.Properties props) {
        return switch (def.getBlockType()) {
            case "HazeBushBlock" -> new HazeBushBlock(
                    net.minecraft.world.item.DyeColor.byName(def.getId().replace("_haze_bush", ""), null), props) {
                @Override public net.minecraft.world.item.BlockItem createHazeItem(net.minecraft.world.item.Item.Properties properties) {
                    return new HazeBushItem(this, properties);
                }
                @Override public void animateTick(BlockState state, net.minecraft.world.level.Level level,
                        BlockPos pos, net.minecraft.util.RandomSource random) {
                    com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.animateTick(state, level, pos, random);
                }
                @Override protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BushBlock> codec() {
                    return com.mojang.serialization.MapCodec.unit(this);
                }
                @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,
                        BlockState state, net.minecraft.world.level.Level level, BlockPos pos,
                        net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
                        net.minecraft.world.phys.BlockHitResult hit) {
                    return itemResult(onInteract(state, level, pos, player, hand));
                }
                @Override public net.minecraft.world.item.ItemStack getCloneItemStack(
                        net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
                    return cloneStack(state);
                }
            };
            case "PottedHazeBushBlock" -> new PottedHazeBushBlock(
                    net.minecraft.world.item.DyeColor.byName(def.getId().replace("potted_", "").replace("_haze_bush", ""), null),
                    registeredBlock(def.getId().substring(7)), props) {
                @Override public void animateTick(BlockState state, net.minecraft.world.level.Level level,
                        BlockPos pos, net.minecraft.util.RandomSource random) {
                    com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.animateTick(state, level, pos, random);
                }
                @Override protected net.minecraft.world.ItemInteractionResult useItemOn(net.minecraft.world.item.ItemStack stack,
                        BlockState state, net.minecraft.world.level.Level level, BlockPos pos,
                        net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
                        net.minecraft.world.phys.BlockHitResult hit) {
                    return itemResult(onInteract(state, level, pos, player, hand));
                }
                @Override protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state,
                        net.minecraft.world.level.Level level, BlockPos pos, net.minecraft.world.entity.player.Player player,
                        net.minecraft.world.phys.BlockHitResult hit) {
                    return onInteract(state, level, pos, player, net.minecraft.world.InteractionHand.MAIN_HAND);
                }
                @Override public net.minecraft.world.item.ItemStack getCloneItemStack(
                        net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
                    return plantStack(state);
                }
                @Override protected java.util.List<net.minecraft.world.item.ItemStack> getDrops(BlockState state,
                        net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
                    var drops = super.getDrops(state, builder);
                    if (!state.getValue(HAS_HAZE)) for (var drop : drops) {
                        if (drop.is(getPlant().asItem())) com.kingodogo.buildscape.item.HazeBushItem.markDrained(drop);
                    }
                    return drops;
                }
            };
            case "CopperTorchBlock" -> new CopperTorchBlock(props);
            case "CopperWallTorchBlock" -> new CopperWallTorchBlock(props);
            case "CopperChestBlock" -> new CopperChestBlock(def.getId().startsWith("waxed_"), props,
                    () -> RestoredContentTypes.blockEntity("copper_chest"));
            case "PotentSulfurBlock" -> new PotentSulfurBlock(props);
            case "SulfurSpikeBlock" -> new SulfurSpikeBlock(props);
            case "EyeblossomBlock" -> new EyeblossomBlock(def.getId().equals("open_eyeblossom"),
                    def.getId().equals("open_eyeblossom") ? MobEffects.CONFUSION : MobEffects.BLINDNESS, 7, props);
            case "CactusFlowerBlock" -> new CactusFlowerBlock(props);
            case "DryGrassBlock" -> new DryGrassBlock(props);
            case "TallDryGrassBlock" -> new TallDryGrassBlock(props);
            case "FireflyBushBlock" -> new FireflyBushBlock(props);
            case "GoldenDandelionBlock" -> new GoldenDandelionBlock(props);
            case "WeatheringLanternBlock" -> new WeatheringLanternBlock(props);
            case "WeatheringClimbableChainBlock" -> new WeatheringClimbableChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                        LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
                }
            };
            case "LightningRodBlock" -> new LightningRodBlock(props);
            case "WeatheringRodBlock" -> new LightningRodBlock(props.randomTicks()) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    RestoredCopperOxidation.tryOxidize(level, pos, state);
                }
            };
            case "WeatheringBarsBlock" -> def.getId().endsWith("copper_bars") ? new WeatheringBarsBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
                    RestoredCopperOxidation.tryOxidize(level, pos, state);
                }
            } : null;
            case "FlowerPotBlock" -> new FlowerPotBlock(registeredBlock(def.getId().substring(7)), props);
            case "SaplingBlock" -> new SaplingBlock(new TreeGrower("buildscape:" + def.getId(), Optional.empty(),
                    Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE,
                            ResourceLocation.fromNamespaceAndPath("buildscape", def.getId().replace("_sapling", "")))),
                    Optional.empty()), props) {};
            default -> null;
        };
    }

    private static net.minecraft.world.ItemInteractionResult itemResult(net.minecraft.world.InteractionResult result) {
        if (result == net.minecraft.world.InteractionResult.SUCCESS) return net.minecraft.world.ItemInteractionResult.SUCCESS;
        if (result == net.minecraft.world.InteractionResult.CONSUME) return net.minecraft.world.ItemInteractionResult.CONSUME;
        if (result == net.minecraft.world.InteractionResult.FAIL) return net.minecraft.world.ItemInteractionResult.FAIL;
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static final java.util.Map<String, Block> REFERENCE_BASES = new java.util.HashMap<>();
    private static final java.util.Set<String> CONSTRUCTING = new java.util.HashSet<>();

    static Block registeredBlock(String id) {
        for (var entry : Services.REGISTRY.getRegisteredBlocks()) {
            if (!entry.getPath().equals(id)) continue;
            Object value = entry.get();
            if (value instanceof Block block) return block;
            if (value instanceof BlockDefinition definition) {
                if (definition.getBlock() == null) {
                    if (!CONSTRUCTING.add(id)) throw new IllegalStateException("Circular block dependency: " + id);
                    try {
                        Services.PLATFORM.wrapRegistryAction(() -> definition.setBlock(new BlockFactory().createBlock(definition)));
                    } finally {
                        CONSTRUCTING.remove(id);
                    }
                }
                return definition.getBlock();
            }
        }
        // Retained variants must copy main's original base properties, not the newer
        // vanilla replacement's sound/hardness. These unregistered bases are internal
        // constructor references only; removed content stays out of the registry.
        for (var removal : com.kingodogo.buildscape.registry.ContentRemovalPolicy.V121X) {
            if (!removal.block() || !removal.id().equals("buildscape:" + id)) continue;
            Block cached = REFERENCE_BASES.get(id);
            if (cached != null) return cached;
            Services.PLATFORM.wrapRegistryAction(() -> {
                var props = ReferenceBlockProperties.create(id, RestoredBlockFactory::registeredBlock);
                if (props == null) throw new IllegalStateException("Missing reference base properties: " + id);
                Block base;
                if (id.endsWith("_log") || id.endsWith("_wood") || id.equals("bamboo_block") || id.equals("stripped_bamboo_block")) {
                    base = new net.minecraft.world.level.block.RotatedPillarBlock(props);
                } else if (id.endsWith("_slab")) {
                    base = new net.minecraft.world.level.block.SlabBlock(props);
                } else if (id.endsWith("_sign")) {
                    base = new net.minecraft.world.level.block.StandingSignBlock(
                            net.minecraft.world.level.block.state.properties.WoodType.OAK, props);
                } else {
                    base = new Block(props);
                }
                REFERENCE_BASES.put(id, base);
                // The base is never registered; drop its intrusive holder so the registry freeze accepts the set.
                // Unit tests run without mixins, so the accessor is only present in the game.
                if ((Object) net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        instanceof com.kingodogo.buildscape.mixin.MappedRegistryV121xAccessor<?> accessor) {
                    var holders = accessor.buildscape$unregisteredIntrusiveHolders();
                    if (holders != null) holders.remove(base);
                }
            });
            return REFERENCE_BASES.get(id);
        }
        Block block = Services.PLATFORM.getBlock(new CommonId("buildscape", id));
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
            block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                    ResourceLocation.tryParse(id));
        }
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) {
            throw new IllegalStateException("Missing block dependency: " + id);
        }
        return block;
    }
}



