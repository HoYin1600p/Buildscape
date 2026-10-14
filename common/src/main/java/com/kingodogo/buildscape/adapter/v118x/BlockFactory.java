package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import com.kingodogo.buildscape.platform.Services;

import com.kingodogo.buildscape.block.AshenKingPillarBlock;
import com.kingodogo.buildscape.block.BigBookBlock;
import com.kingodogo.buildscape.block.BigCandleBlock;
import com.kingodogo.buildscape.block.BigOrnamentBlock;
import com.kingodogo.buildscape.block.BlockDefinition;
import com.kingodogo.buildscape.block.BoneDiceBlock;
import com.kingodogo.buildscape.block.BuildersWorkbenchBlock;
import com.kingodogo.buildscape.block.CloverBlock;
import com.kingodogo.buildscape.block.ColoredMossBlock;
import com.kingodogo.buildscape.block.ColoredSporeBlossomBlock;
import com.kingodogo.buildscape.block.CascadeBlock;
import com.kingodogo.buildscape.block.CascadeBlockNoMist;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import com.kingodogo.buildscape.block.CommonBlockProperties;
import com.kingodogo.buildscape.block.CopperBulbBlock;
import com.kingodogo.buildscape.block.CopperOxidationHandler;
import com.kingodogo.buildscape.block.CreakingHeartBlock;
import com.kingodogo.buildscape.block.CushionBlock;
import com.kingodogo.buildscape.block.DecoratedPotBlock;
import com.kingodogo.buildscape.block.ExperienceFluidBlock;
import com.kingodogo.buildscape.block.FallingSandBlock;
import com.kingodogo.buildscape.block.FestiveLampBlock;
import com.kingodogo.buildscape.block.FestiveStockingBlock;
import com.kingodogo.buildscape.block.FroglightBlock;
import com.kingodogo.buildscape.block.FrostRoseBlock;
import com.kingodogo.buildscape.block.GlassJarBlock;
import com.kingodogo.buildscape.block.GlazedGlassBlock;
import com.kingodogo.buildscape.block.GlowLightsBlock;
import com.kingodogo.buildscape.block.GrassSlabBlock;
import com.kingodogo.buildscape.block.HangingMossBlock;
import com.kingodogo.buildscape.block.HayBaleSlabBlock;
import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.block.ICommonRemoval;
import com.kingodogo.buildscape.block.IcicleBlock;
import com.kingodogo.buildscape.block.IcicleCauldronBlock;
import com.kingodogo.buildscape.block.LeafHedgeBlock;
import com.kingodogo.buildscape.block.LeafLayersBlock;
import com.kingodogo.buildscape.block.MangroveLeavesBlock;
import com.kingodogo.buildscape.block.MangrovePropaguleBlock;
import com.kingodogo.buildscape.block.MangroveRootsBlock;
import com.kingodogo.buildscape.block.PackedIcicleBlock;
import com.kingodogo.buildscape.block.PetalBlock;
import com.kingodogo.buildscape.block.PointedIcicleBlock;
import com.kingodogo.buildscape.block.ResinClumpBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyGlassBlock;
import com.kingodogo.buildscape.block.SilkTouchOnlyPaneBlock;
import com.kingodogo.buildscape.block.ModBlock;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.block.ModBushBlock;
import com.kingodogo.buildscape.block.ModIronBarsBlock;
import com.kingodogo.buildscape.block.ModLadderBlock;
import com.kingodogo.buildscape.block.ModLayerBlock;
import com.kingodogo.buildscape.block.MossLayersBlock;
import com.kingodogo.buildscape.block.ColoredMossLayersBlock;
import com.kingodogo.buildscape.block.LogSlabBlock;
import com.kingodogo.buildscape.block.ModSlabBlock;
import com.kingodogo.buildscape.block.ModStairBlock;
import com.kingodogo.buildscape.block.ModWallBlock;
import com.kingodogo.buildscape.block.MonetFlowerBlock;
import com.kingodogo.buildscape.block.MossOverlayBlock;
import com.kingodogo.buildscape.block.MudBlock;
import com.kingodogo.buildscape.block.MudSlabBlock;
import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.block.MushroomShelvesBlock;
import com.kingodogo.buildscape.block.MulticolorGlowLightsBlock;
import com.kingodogo.buildscape.block.OrnamentBlock;
import com.kingodogo.buildscape.block.PillarBlock;
import com.kingodogo.buildscape.block.PillarBlockEntity;
import com.kingodogo.buildscape.block.PipeBlock;
import com.kingodogo.buildscape.block.RoseVinesBlock;
import com.kingodogo.buildscape.block.ShelfBlock;
import com.kingodogo.buildscape.block.SmokeVentBlock;
import com.kingodogo.buildscape.block.SnowOverlayBlock;
import com.kingodogo.buildscape.block.SnowyBushBlock;
import com.kingodogo.buildscape.block.SnowyFernBlock;
import com.kingodogo.buildscape.block.SnowyGrassBlock;
import com.kingodogo.buildscape.block.SnowyLargeFernBlock;
import com.kingodogo.buildscape.block.SnowyLeavesBlock;
import com.kingodogo.buildscape.block.SnowyShortGrassBlock;
import com.kingodogo.buildscape.block.SnowyTallGrassBlock;
import com.kingodogo.buildscape.block.SoftFabricBlock;
import com.kingodogo.buildscape.block.SpoolBlock;
import com.kingodogo.buildscape.block.StarBlock;
import com.kingodogo.buildscape.block.SteelBoltBlock;
import com.kingodogo.buildscape.block.StrawBedBlock;
import com.kingodogo.buildscape.block.StringLightBlock;
import com.kingodogo.buildscape.block.TrappedDecoratedPotBlock;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import com.kingodogo.buildscape.block.WallpaperFlatBlock;
import com.kingodogo.buildscape.block.WaterloggableGrateBlock;
import com.kingodogo.buildscape.block.WeatheringBarsBlock;
import com.kingodogo.buildscape.block.WeatheringBoltBlock;
import com.kingodogo.buildscape.block.WeatheringLargeChainBlock;
import com.kingodogo.buildscape.block.WeatheringBlockLogic;
import com.kingodogo.buildscape.block.WildflowersBlock;
import com.kingodogo.buildscape.block.WoolLayersBlock;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
public class BlockFactory implements IBlockFactory {
    private static java.util.function.BiFunction<Supplier<FlowingFluid>, BlockBehaviour.Properties, Block> experienceFluidFactory;

    public static void installExperienceFluidFactory(
            java.util.function.BiFunction<Supplier<FlowingFluid>, BlockBehaviour.Properties, Block> factory) {
        experienceFluidFactory = java.util.Objects.requireNonNull(factory);
    }

    private static final class WoodDoor extends DoorBlock {
        private WoodDoor(BlockBehaviour.Properties properties) { super(properties); }
    }
    private static final class WoodTrapdoor extends TrapDoorBlock {
        private WoodTrapdoor(BlockBehaviour.Properties properties) { super(properties); }
    }
    /** Reference sign filter: only dyes, glow ink and an empty, non-crouching hand reach vanilla sign use. */
    private static boolean signUseFilter(boolean bamboo, Level level, BlockPos pos, Player player, InteractionHand hand) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && (heldItem.getItem() instanceof net.minecraft.world.item.DyeItem
                || heldItem.getItem() == net.minecraft.world.item.Items.GLOW_INK_SAC)) {
            return true;
        }
        return (bamboo ? blockEntity instanceof com.kingodogo.buildscape.block.BambooSignBlockEntity
                : blockEntity instanceof com.kingodogo.buildscape.block.MangroveSignBlockEntity)
                && !player.isCrouching() && heldItem.isEmpty();
    }
    private static final class WoodButton extends WoodButtonBlock {
        private WoodButton(BlockBehaviour.Properties properties) { super(properties); }
    }
    private static final class WoodPressurePlate extends PressurePlateBlock {
        private WoodPressurePlate(BlockBehaviour.Properties properties) { super(Sensitivity.EVERYTHING, properties); }
    }

    private static final class CustomWoodType extends WoodType {
        private CustomWoodType(String name) {
            super(name);
        }

        private static WoodType createRegistered(String name) {
            return com.kingodogo.buildscape.mixin.WoodTypeV118xInvoker.buildscape$register(new CustomWoodType(name));
        }
    }
    // Block setup initializes this factory before the client builds its sign models.
    private static final WoodType MANGROVE_WOOD_TYPE = CustomWoodType.createRegistered("buildscape:mangrove");
    private static final WoodType BAMBOO_WOOD_TYPE = CustomWoodType.createRegistered("buildscape:bamboo");

    @Override
    public Block createBlock(BlockDefinition def) {
        BlockBehaviour.Properties props = buildProperties(def);

        // Choose vanilla state-bearing classes before the shared family predicates.
        if (def.isLightningRod()) {
            return new net.minecraft.world.level.block.LightningRodBlock(props);
        }
        if (def.isWeightedPressurePlate()) {
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(150, props) {};
        }
        if (def.getBlockType().equals("CarpetBlock")) {
            return new net.minecraft.world.level.block.CarpetBlock(props) {};
        }
        if (def.getId().equals("snowy_grass_block_slab")) {
            return new SlabBlock(props);
        }
        if (def.getBlockType().equals("IceCrystalBlock")) {
            return new net.minecraft.world.level.block.AmethystClusterBlock(7, 3, props);
        }
        if (def.getId().endsWith("_redstone_lamp")) {
            return new net.minecraft.world.level.block.RedstoneLampBlock(props);
        }
        if (def.getBlockType().equals("FlowerPotBlock")) {
            return new net.minecraft.world.level.block.FlowerPotBlock(
                    restoredBlock(def.getId().substring("potted_".length())), props);
        }
        switch (def.getBlockType()) {
            case "HazeBushBlock": {
                var color = net.minecraft.world.item.DyeColor.byName(def.getId().replace("_haze_bush", ""), null);
                return new com.kingodogo.buildscape.block.HazeBushBlock(color, props.noCollission().instabreak().sound(SoundType.GRASS)) {
                    @Override public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                        super.animateTick(state, level, pos, random);
                        HazeAnimation118x.animate(state, level, pos, random);
                    }
                    @Override public net.minecraft.world.item.BlockItem createHazeItem(net.minecraft.world.item.Item.Properties properties) {
                        return new HazeBushItem(this, properties);
                    }
                    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                            InteractionHand hand, BlockHitResult hit) {
                        InteractionResult result = onInteract(state, level, pos, player, hand);
                        return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                    }
                    @Override public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                        return cloneStack(state);
                    }
                };
            }
            case "PottedHazeBushBlock": {
                var color = net.minecraft.world.item.DyeColor.byName(
                        def.getId().replace("potted_", "").replace("_haze_bush", ""), null);
                var plant = com.kingodogo.buildscape.block.ModBlocks.get(com.kingodogo.buildscape.block.ModBlocks.COLORED_HAZE_BUSHES.get(color));
                return new com.kingodogo.buildscape.block.PottedHazeBushBlock(color, plant, props) {
                    @Override public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                        super.animateTick(state, level, pos, random);
                        HazeAnimation118x.animate(state, level, pos, random);
                    }
                    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                            InteractionHand hand, BlockHitResult hit) {
                        return onInteract(state, level, pos, player, hand);
                    }
                    @Override public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                        return plantStack(state);
                    }
                };
            }
            case "CopperTorchBlock": return new com.kingodogo.buildscape.block.CopperTorchBlock(props);
            case "CopperWallTorchBlock": return new com.kingodogo.buildscape.block.CopperWallTorchBlock(props);
            case "CopperChestBlock": return new com.kingodogo.buildscape.block.CopperChestBlock(
                    def.getId().startsWith("waxed_"), props, () -> RestoredContentTypes.blockEntity("copper_chest"));
            case "PotentSulfurBlock": return new com.kingodogo.buildscape.block.PotentSulfurBlock(props);
            case "SulfurSpikeBlock": return new com.kingodogo.buildscape.block.SulfurSpikeBlock(props);
            case "SculkCatalystBlock": return new com.kingodogo.buildscape.block.SculkCatalystBlock(props);
            case "SculkVeinBlock": return new com.kingodogo.buildscape.block.SculkVeinBlock(props);
            case "EyeblossomBlock": return new com.kingodogo.buildscape.block.EyeblossomBlock(
                    def.getId().equals("open_eyeblossom"), def.getId().equals("open_eyeblossom")
                    ? net.minecraft.world.effect.MobEffects.CONFUSION : net.minecraft.world.effect.MobEffects.BLINDNESS, 7, props);
            case "CactusFlowerBlock": return new com.kingodogo.buildscape.block.CactusFlowerBlock(props);
            case "DryGrassBlock": return new com.kingodogo.buildscape.block.DryGrassBlock(props);
            case "TallDryGrassBlock": return new com.kingodogo.buildscape.block.TallDryGrassBlock(props);
            case "FireflyBushBlock": return new com.kingodogo.buildscape.block.FireflyBushBlock(props);
            case "GoldenDandelionBlock": return new com.kingodogo.buildscape.block.GoldenDandelionBlock(props);
            case "WeatheringGrateBlock": return new com.kingodogo.buildscape.block.WeatheringGrateBlock(props);
            case "WeatheringLanternBlock": return new com.kingodogo.buildscape.block.WeatheringLanternBlock(props);
            case "WeatheringClimbableChainBlock": return new com.kingodogo.buildscape.block.WeatheringClimbableChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                        LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
                }
            };
            case "WeatheringRodBlock": return new net.minecraft.world.level.block.LightningRodBlock(props.randomTicks()) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    RestoredCopperOxidation.tryOxidize(level, pos, state);
                }
            };
            case "WeatheringDoorBlock": return restoredCopperDoor(props, true);
            case "WeatheringTrapdoorBlock": return restoredCopperTrapdoor(props, true);
            case "ModStandingSignBlock", "ModBambooStandingSignBlock": {
                boolean bamboo = def.getId().startsWith("bamboo_");
                return new StandingSignBlock(props, bamboo ? BAMBOO_WOOD_TYPE : MANGROVE_WOOD_TYPE) {
                    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                        return bamboo ? new com.kingodogo.buildscape.block.BambooSignBlockEntity(pos, state)
                                : new com.kingodogo.buildscape.block.MangroveSignBlockEntity(pos, state);
                    }
                    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                            InteractionHand hand, BlockHitResult hit) {
                        return signUseFilter(bamboo, level, pos, player, hand)
                                ? super.use(state, level, pos, player, hand, hit) : InteractionResult.PASS;
                    }
                };
            }
            case "ModWallSignBlock", "ModBambooWallSignBlock": {
                boolean bamboo = def.getId().startsWith("bamboo_");
                return new WallSignBlock(props, bamboo ? BAMBOO_WOOD_TYPE : MANGROVE_WOOD_TYPE) {
                    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
                        return bamboo ? new com.kingodogo.buildscape.block.BambooSignBlockEntity(pos, state)
                                : new com.kingodogo.buildscape.block.MangroveSignBlockEntity(pos, state);
                    }
                    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                            InteractionHand hand, BlockHitResult hit) {
                        return signUseFilter(bamboo, level, pos, player, hand)
                                ? super.use(state, level, pos, player, hand, hit) : InteractionResult.PASS;
                    }
                };
            }
            case "ModButtonBlock": return new net.minecraft.world.level.block.StoneButtonBlock(props) {};
            case "SaplingBlock": {
                net.minecraft.world.level.block.grower.AbstractTreeGrower grower = def.getId().equals("cherry_sapling")
                        ? new com.kingodogo.buildscape.worldgen.CherryTreeGrower()
                        : def.getId().equals("poplar_sapling")
                        ? new com.kingodogo.buildscape.worldgen.PoplarTreeGrower()
                        : new com.kingodogo.buildscape.worldgen.PaleOakTreeGrower();
                return new net.minecraft.world.level.block.SaplingBlock(grower, props) {};
            }
            default: break;
        }

        if (def.isLargeChain()) {
            return new com.kingodogo.buildscape.block.LargeChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isClimbableChain()) {
            return new com.kingodogo.buildscape.block.ClimbableChainBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isCopperBulb()) {
            if (!def.getId().startsWith("waxed_") && !def.getId().startsWith("oxidized_")) props.randomTicks();
            return new CopperBulbBlock(props, def.copperBulbLightLevel()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return !def.getId().startsWith("waxed_") && !def.getId().startsWith("oxidized_"); }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { RestoredCopperOxidation.tryOxidize(level, pos, state); }
                @Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
            };
        } else if (def.isWaterloggableGrate()) {
            return new WaterloggableGrateBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
            };
        } else if (def.isWeatheringBars()) {
            return new WeatheringBarsBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    if (def.getId().endsWith("copper_bars")) RestoredCopperOxidation.tryOxidize(level, pos, state);
                    else onRandomTick(state, level, pos);
                }
            };
        } else if (def.isWeatheringBolt()) {
            return new WeatheringBoltBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringLargeChain()) {
            return new WeatheringLargeChainBlock(props) {
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onRandomTick(state, level, pos); }
            };
        } else if (def.isWeatheringBlock()) {
            return new Block(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    if (def.getId().endsWith("chiseled_copper")) RestoredCopperOxidation.tryOxidize(level, pos, state);
                    else WeatheringBlockLogic.randomTick(state, level, pos);
                }
            };
        } else if (def.isWeatheringButton()) {
            return new net.minecraft.world.level.block.StoneButtonBlock(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringPressurePlate()) {
            return new net.minecraft.world.level.block.WeightedPressurePlateBlock(WeatheringBlockLogic.PRESSURE_PLATE_MAX_WEIGHT, props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                // Reference oxidation schedules the powered replacement's release tick.
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { RestoredCopperOxidation.tryOxidize(level, pos, state); }
            };
        } else if (def.isWeatheringSlab()) {
            return new net.minecraft.world.level.block.SlabBlock(props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringStair()) {
            return new ModStairBlock(getBaseState(def), referenceStairProperties(getBaseState(def), props.randomTicks())) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isWeatheringVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props.randomTicks()) {
                @Override public boolean isRandomlyTicking(BlockState state) { return true; }
                @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) { WeatheringBlockLogic.randomTick(state, level, pos); }
            };
        } else if (def.isStair()) {
            // These reference registrations use vanilla stairs, preserving both the
            // state-bearing base and the supplied properties without safe flattening.
            if (def.getId().equals("stripped_bamboo_block_stairs")
                    || def.getId().equals("framed_caution_stairs")
                    || (def.getId().startsWith("caution_") && def.getId().endsWith("_stairs"))) {
                return new net.minecraft.world.level.block.StairBlock(getBaseState(def), props) {};
            }
            return new ModStairBlock(getBaseState(def), referenceStairProperties(getBaseState(def), props)) {
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && super.propagatesSkylightDown(state, level, pos); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isVerticalSlab()) {
            return new VerticalSlabBlock(getBaseBlock(def), props);
        } else if (def.isGrassSlab()) {
            props.randomTicks();
            Supplier<Block> dirtSlabSupplier = resolveBlockSupplier("dirt_slab");
            class V118xGrassSlabBlock extends GrassSlabBlock implements BonemealableBlock {
                public V118xGrassSlabBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction == Direction.UP) {
                        state = state.setValue(SNOWY, isSnowySetting(level, pos));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    if (!canBeGrass(state, level, pos)) {
                        Block dirtSlab = dirtSlabSupplier.get();
                        if (dirtSlab != null) {
                            level.setBlockAndUpdate(pos, dirtSlab.defaultBlockState()
                                    .setValue(TYPE, state.getValue(TYPE))
                                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
                        }
                        return;
                    }
                    if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                        for (int i = 0; i < 4; ++i) {
                            BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                            BlockState targetState = level.getBlockState(targetPos);
                            Block dirtSlab = dirtSlabSupplier.get();
                            if (dirtSlab != null && targetState.is(dirtSlab) && canPropagate(state, level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, this.defaultBlockState()
                                        .setValue(TYPE, targetState.getValue(TYPE))
                                        .setValue(WATERLOGGED, targetState.getValue(WATERLOGGED))
                                        .setValue(SNOWY, isSnowySetting(level, targetPos)));
                            } else if (targetState.is(Blocks.DIRT) && canPropagate(Blocks.GRASS_BLOCK.defaultBlockState(), level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
                            }
                        }
                    }
                }

                private boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    BlockState aboveState = level.getBlockState(abovePos);
                    if (aboveState.is(Blocks.SNOW) && aboveState.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS) == 1) {
                        return true;
                    }
                    if (aboveState.getFluidState().getAmount() == 8) {
                        return false;
                    }
                    int lightLevel = net.minecraft.world.level.lighting.LayerLightEngine.getLightBlockInto(
                            level,
                            state,
                            pos,
                            aboveState,
                            abovePos,
                            Direction.UP,
                            aboveState.getLightBlock(level, abovePos)
                    );
                    return lightLevel < level.getMaxLightLevel();
                }

                private boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack held = player.getItemInHand(hand);
                    if (held.getItem() instanceof net.minecraft.world.item.BoneMealItem) {
                        if (isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                            if (level instanceof ServerLevel serverLevel) {
                                if (isBonemealSuccess(level, level.getRandom(), pos, state)) {
                                    performBonemeal(serverLevel, level.getRandom(), pos, state);
                                    if (!player.getAbilities().instabuild) {
                                        held.shrink(1);
                                    }
                                    level.levelEvent(2005, pos, 0);
                                }
                            }
                            level.playSound(player, pos, net.minecraft.sounds.SoundEvents.BONE_MEAL_USE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return level.getBlockState(pos.above()).isAir();
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos abovePos = pos.above();
                    Supplier<Block> snowyShortGrassSupplier = resolveBlockSupplier("snowy_short_grass");
                    BlockState foliage = Blocks.GRASS.defaultBlockState();
                    if (random.nextInt(8) == 0 && snowyShortGrassSupplier != null && snowyShortGrassSupplier.get() != null) {
                        foliage = snowyShortGrassSupplier.get().defaultBlockState();
                    }
                    if (foliage.canSurvive(level, abovePos)) {
                        level.setBlock(abovePos, foliage, 3);
                    }
                }
            }
            return new V118xGrassSlabBlock(props);
        } else if (def.isHayBaleSlab()) {
            return new HayBaleSlabBlock(props) {
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    entity.causeFallDamage(fallDistance, 0.2F, net.minecraft.world.damagesource.DamageSource.FALL);
                }
            };
        } else if (def.isLogSlab()) {
            return new LogSlabBlock(getBaseBlock(def), referenceSlabProperties(def, props)) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    return res != InteractionResult.PASS ? res : super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isSlab()) {
            return new ModSlabBlock(getBaseBlock(def), referenceSlabProperties(def, props)) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { return null; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return !isTintedBase() && (isRegularGlassLike() || super.propagatesSkylightDown(state, level, pos)); }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTintedBase() ? 15 : super.getLightBlock(state, level, pos); }
            };
        } else if (def.isMudSlab()) {
            return new MudSlabBlock(props);
        } else if (def.isMud()) {
            return new MudBlock(props);
        } else if (def.isMossOverlay()) {
            return new MossOverlayBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isSnowOverlay()) {
            return new SnowOverlayBlock(props) {
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) { return type == net.minecraft.world.level.pathfinder.PathComputationType.LAND; }
            };
        } else if (def.isWall()) {
            return new ModWallBlock(props);
        } else if (def.isFalling()) {
            return new FallingSandBlock(props) {
                @Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { onNeighborUpdate(level, pos, state, block, fromPos, isMoving); }
                @Override public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) { onScheduledTick(state, level, pos); }
            };
        } else if (def.isCopperDoor()) {
            return new DoorBlock(props) {
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 10);
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_DOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_DOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(player, state.getValue(OPEN) ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            };
        } else if (def.isDoor()) {
            return new WoodDoor(props);
        } else if (def.isCopperTrapdoor()) {
            return new TrapDoorBlock(props) {
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    state = state.cycle(OPEN);
                    level.setBlock(pos, state, 2);
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN : net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            };
        } else if (def.isTrapdoor()) {
            return new WoodTrapdoor(props);
        } else if (def.isButton()) {
            return new WoodButton(props);
        } else if (def.isPressurePlate()) {
            return new WoodPressurePlate(props);
        } else if (def.isMushroomShelves()) {
            return new MushroomShelvesBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(MushroomShelvesBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
                    return canClimb(level, pos);
                }
            };
        } else if (def.isShelf()) {
            return new ShelfBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(ShelfBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                        this.updateNeighborsAfterPoweringDown(level, pos, state);
                    }
                }
            };
        } else if (def.isWorkbench()) {
            return new BuildersWorkbenchBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
            };
        } else if (def.isGlassJar()) {
            return new GlassJarBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isSmokeVent()) {
            return new SmokeVentBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    onAnimateTick(state, level, pos);
                }
                @Override
                public boolean hasAnalogOutputSignal(BlockState state) {
                    return true;
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isMuff()) {
            return new MuffBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
            };
        } else if (def.isFenceGate()) {
            return new FenceGateBlock(props);
        } else if (def.isFence()) {
            return new FenceBlock(props);
        } else if (def.isIronBars()) {
            return new ModIronBarsBlock(props);
        } else if (def.isLadder()) {
            return new ModLadderBlock(props);
        } else if (def.isLantern()) {
            return new LanternBlock(props);
        } else if (def.isLeafHedge()) {
            return new LeafHedgeBlock(props) {
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(state, level, pos, entity);
                    super.entityInside(state, level, pos, entity);
                }
            };
        } else if (def.isMangroveLeaves()) {
            class V118xMangroveLeavesBlock extends LeavesBlock implements BonemealableBlock {
                public V118xMangroveLeavesBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                    BlockState base = this.stateDefinition.any();
                    if (base.hasProperty(BlockStateProperties.WATERLOGGED)) {
                        base = base.setValue(BlockStateProperties.WATERLOGGED, false);
                    }
                    this.registerDefaultState(base.setValue(DISTANCE, 7).setValue(PERSISTENT, false));
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return MangroveLeavesBlock.canGrowPropagule(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    MangroveLeavesBlock.growPropagule(level, pos);
                }
            }
            return new V118xMangroveLeavesBlock(props);
        } else if (def.isMangrovePropagule()) {
            Supplier<Block> propaguleLeaves = resolveBlockSupplier("mangrove_leaves");
            Supplier<Block> propaguleMud = resolveBlockSupplier("mud");
            Supplier<Block> propaguleMuddyRoots = resolveBlockSupplier("muddy_mangrove_roots");
            class V118xMangrovePropaguleBlock extends MangrovePropaguleBlock implements BonemealableBlock {
                public V118xMangrovePropaguleBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                private boolean isLeavesAbove(BlockState aboveState) {
                    return aboveState.is(net.minecraft.tags.BlockTags.LEAVES) || aboveState.is(propaguleLeaves.get());
                }
                @Override
                public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
                    if (state.getValue(HANGING)) {
                        return isLeavesAbove(level.getBlockState(pos.above()));
                    }
                    BlockState belowState = level.getBlockState(pos.below());
                    return (belowState.is(net.minecraft.tags.BlockTags.DIRT) && !belowState.is(Blocks.DIRT_PATH))
                            || belowState.is(Blocks.FARMLAND)
                            || belowState.is(Blocks.MOSS_BLOCK)
                            || belowState.is(propaguleMud.get())
                            || belowState.is(Blocks.CLAY)
                            || belowState.is(propaguleMuddyRoots.get());
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    if (state.getValue(HANGING)) {
                        if (direction == Direction.UP && !isLeavesAbove(level.getBlockState(pos.above()))) {
                            return Blocks.AIR.defaultBlockState();
                        }
                    } else if (direction == Direction.DOWN) {
                        if (!canSurvive(state, level, pos)) {
                            return Blocks.AIR.defaultBlockState();
                        }
                        return state.setValue(ON_FARMLAND, shouldSink(level, pos));
                    }
                    return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return canBonemeal(level, pos, state);
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    if (!state.getValue(HANGING) && state.getValue(STAGE) >= 1) {
                        advanceTree(level, pos, state, random, true);
                        return;
                    }
                    performBonemealEffect(level, pos, state);
                }
                @Override
                public boolean isRandomlyTicking(BlockState state) {
                    return !state.getValue(HANGING);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    if (state.getValue(HANGING)) return;
                    if (level.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(pos).inflate(32)).isEmpty()) return;
                    if (random.nextInt(7) == 0) advanceTree(level, pos, state, random, false);
                }
                private void advanceTree(ServerLevel level, BlockPos pos, BlockState state, java.util.Random random, boolean bonemeal) {
                    if (state.getValue(HANGING)) return;
                    if (state.getValue(STAGE) < 1) {
                        level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), 4);
                        return;
                    }
                    if (!bonemeal && !canGrowTree(level, pos)) return;
                    String variant = com.kingodogo.buildscape.worldgen.ModConfiguredFeatures.getRandomMangroveTreeVariant(random);
                    ResourceLocation featureLocation = new ResourceLocation("buildscape", variant);
                    var featureHolder = level.registryAccess().registry(Registry.CONFIGURED_FEATURE_REGISTRY)
                            .flatMap(registry -> registry.getHolder(net.minecraft.resources.ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, featureLocation)));
                    if (featureHolder.isEmpty()) {
                        com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Mangrove tree feature {} not found in registry! Cannot grow tree.", featureLocation);
                        level.setBlock(pos, state, 4);
                        return;
                    }
                    var feature = featureHolder.get().value();
                    if (feature.feature() != net.minecraft.world.level.levelgen.feature.Feature.TREE) {
                        com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Mangrove tree feature {} is not a tree feature!", featureLocation);
                        level.setBlock(pos, state, 4);
                        return;
                    }
                    BlockPos belowPos = pos.below();
                    if (level.getBlockState(belowPos).is(Blocks.GRASS_BLOCK)) {
                        level.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), 3);
                    }
                    level.setBlock(pos, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
                    @SuppressWarnings("unchecked")
                    net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration treeConfig =
                            (net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration) feature.config();
                    var placeContext = new net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<>(
                            java.util.Optional.empty(), level, level.getChunkSource().getGenerator(), random, pos, treeConfig);
                    if (!net.minecraft.world.level.levelgen.feature.Feature.TREE.place(placeContext)) {
                        level.setBlock(pos, state, Block.UPDATE_ALL);
                    }
                }
                private boolean canGrowTree(LevelReader level, BlockPos pos) {
                    if (level.getMaxLocalRawBrightness(pos) < 9) return false;
                    for (int y = 1; y <= 6; y++) {
                        BlockState check = level.getBlockState(pos.above(y));
                        if (!check.isAir() && !check.is(net.minecraft.tags.BlockTags.DIRT) && !isLogOrWood(check)) return false;
                    }
                    boolean solidNearby = false;
                    for (int x = -4; x <= 4 && !solidNearby; x++) {
                        for (int z = -4; z <= 4 && !solidNearby; z++) {
                            if (x == 0 && z == 0) continue;
                            for (int y = -4; y <= 4; y++) {
                                BlockPos check = pos.offset(x, y, z);
                                if (isSolidBlock(level, check, level.getBlockState(check))) { solidNearby = true; break; }
                            }
                        }
                    }
                    if (!solidNearby) return false;
                    for (int x = -4; x <= 4; x++) {
                        for (int z = -4; z <= 4; z++) {
                            for (int y = 1; y <= 11; y++) {
                                BlockPos check = pos.offset(x, -y, z);
                                if (isSolidBlock(level, check, level.getBlockState(check))) return true;
                            }
                        }
                    }
                    return false;
                }
                private boolean isLogOrWood(BlockState state) {
                    return state.is(net.minecraft.tags.BlockTags.LOGS) || state.is(net.minecraft.tags.BlockTags.PLANKS)
                            || state.getBlock() == resolveBlockSupplier("mangrove_log").get()
                            || state.getBlock() == resolveBlockSupplier("mangrove_wood").get()
                            || state.getBlock() == resolveBlockSupplier("stripped_mangrove_log").get()
                            || state.getBlock() == resolveBlockSupplier("stripped_mangrove_wood").get();
                }
                private boolean isSolidBlock(BlockGetter level, BlockPos pos, BlockState state) {
                    if (state.is(propaguleMud.get())) return false;
                    return state.isFaceSturdy(level, pos, Direction.UP) && state.getMaterial().isSolid() && !state.getMaterial().isReplaceable();
                }
            }
            return new V118xMangrovePropaguleBlock(props);
        } else if (def.isMangroveRoots()) {
            return new MangroveRootsBlock(props) {
                {
                    registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
            };
        } else if (def.isLeaves()) {
            return new LeavesBlock(props);
        } else if (def.isSnowyLeaves()) {
            return new LeavesBlock(props) {
                @Override
                public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
                    return Shapes.empty();
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
                    return 1.0F;
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isColoredMossLayers()) {
            Supplier<Block> fullBlock = resolveBlockSupplier(def.getId().replace("_layers", "_block"));
            class V118xColoredMossLayersBlock extends ColoredMossLayersBlock {
                public V118xColoredMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties, fullBlock);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V118xColoredMossLayersBlock(props) {
                // Reference: drops one layer item per layer, ignoring the loot table (which gives a single item).
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return List.of(new ItemStack(net.minecraft.core.Registry.ITEM.get(net.minecraft.core.Registry.BLOCK.getKey(this)), state.getValue(LAYERS)));
                }
            };
        } else if (def.isMossLayers()) {
            class V118xMossLayersBlock extends MossLayersBlock {
                public V118xMossLayersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                    super.randomTick(state, level, pos, random);
                }

                @Override
                public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
                    onPlayerWillDestroy(level, pos, state, player);
                    super.playerWillDestroy(level, pos, state, player);
                }

                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
            }
            return new V118xMossLayersBlock(props) {
                // Reference: drops one layer item per layer, ignoring the loot table (which gives a single item).
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return List.of(new ItemStack(net.minecraft.core.Registry.ITEM.get(net.minecraft.core.Registry.BLOCK.getKey(this)), state.getValue(LAYERS)));
                }
            };
        } else if (def.isWoolLayers()) {
            String color = def.getId().replace("_carpet_layers", "").replace("_layered_wool_layers", "");
            Supplier<Item> carpetSupplier = resolveItemSupplier(color + "_carpet_layers");
            return new WoolLayersBlock(props, color, carpetSupplier) {
                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state);
                }
            };
        } else if (def.isLeafLayers()) {
            return new LeafLayersBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.level.pathfinder.PathComputationType type) {
                    return isCommonPathfindable(state, type);
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isLayer()) {
            return new ModLayerBlock(props);
        } else if (def.isAshenKingPillar()) {
            return new AshenKingPillarBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
                                if (id != null) {
                                    tag.putString("ITEM", id.toString());
                                }
                            }
                            String pattern = pillarBE.getParticlePattern();
                            if (pattern != null && !pattern.isEmpty()) {
                                tag.putString("PATTERN", pattern);
                            }
                        });
                    }
                    return stack;
                }
            };
        } else if (def.isPillar()) {
            return new PillarBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(PillarBlock.WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    if (fromPos.equals(pos.above()) || fromPos.equals(pos.below())) {
                        onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                    }
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    ItemStack stack = super.getCloneItemStack(level, pos, state);
                    if (level.getBlockEntity(pos) instanceof PillarBlockEntity pillarBE) {
                        com.kingodogo.buildscape.platform.Services.PLATFORM.updateCustomData(stack, tag -> {
                            if (pillarBE.hasDisplayItem()) {
                                CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getItemId(pillarBE.getDisplayedItem().getItem());
                                if (id != null) {
                                    tag.putString("ITEM", id.toString());
                                }
                            }
                            String pattern = pillarBE.getParticlePattern();
                            if (pattern != null && !pattern.isEmpty()) {
                                tag.putString("PATTERN", pattern);
                            }
                        });
                    }
                    return stack;
                }
            };
        } else if (def.isHollowLog()) {
            return new HollowLogBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                    }
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (!level.isClientSide() && level instanceof Level lvl) {
                        com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager.onNeighborChanged(lvl, pos, state, neighborPos);
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
            };
        } else if (def.isHollowPipe()) {
            return new HollowPipeBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                    }
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                    }
                    if (state.getValue(LAVA_LOGGED)) {
                        level.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
                    }
                    if (state.getValue(WATER_LEVEL) > 0) {
                        level.scheduleTick(pos, this, Fluids.WATER.getTickDelay(level));
                    }
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (state.getValue(WATER_LEVEL) > 0 && updated.getValue(WATER_LEVEL) == 0) {
                        updated = updated.setValue(WATER_LEVEL, state.getValue(WATER_LEVEL));
                    }
                    updated = updateConnections(level, pos, updated);
                    if (!level.isClientSide() && level instanceof Level lvl) {
                        com.kingodogo.buildscape.pipe.transport.HollowPipeTransportManager.onNeighborChanged(lvl, pos, updated, neighborPos);
                    }
                    return updated;
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    super.tick(state, level, pos, random);
                    onBlockTick(level, pos, state);
                }
                public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
                    return hasVerticalChannel(state);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(level, pos, state);
                }
            };
        } else if (def.isPipe()) {
            return new PipeBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
                    onNeighborUpdate(level, pos, state, block, fromPos, isMoving);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
            };
        } else if (def.isRoseVines()) {
            return new RoseVinesBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return state;
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    onRandomTick(state, level, pos);
                }
                public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
                    return canClimb(level, pos);
                }
            };
        } else if (def.isFroglight()) {
            return new FroglightBlock(props);
        } else if (def.isRotatedPillar() || def.isLog() || def.isWood() || def.isCautionBlock() || def.isBambooBlock()) {
            return new RotatedPillarBlock(props);
        } else if (def.isTrappedDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new TrappedDecoratedPotBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isDecoratedPot()) {
            props.strength(0.0F).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            return new DecoratedPotBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return getAnalogOutput(state, level, pos); }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) dropStoredContents(level, pos);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isFestiveStocking()) {
            return new FestiveStockingBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isCascadeNoMist()) {
            return new CascadeBlockNoMist(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return false; }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.level.material.Fluid fluid) { return false; }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(level, pos, state, player, hand, hit);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isCascade()) {
            return new CascadeBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return false; }
                @Override
                public boolean canBeReplaced(BlockState state, net.minecraft.world.level.material.Fluid fluid) { return false; }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isGlazedGlass()) {
            return new GlazedGlassBlock(props) {
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = getBeaconColor(state, level, pos, beaconPos);
                    return new float[]{((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F, (color & 255) / 255.0F};
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
                    return true;
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
            };
        } else if (def.isIcicleCauldron()) {
            return new IcicleCauldronBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    if (!state.is(newState.getBlock())) {
                        onBlockRemoved(level, pos, state);
                        super.onRemove(state, level, pos, newState, isMoving);
                    }
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isIcicleBlock()) {
            props.friction(0.989F);
            return new IcicleBlock(props) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isPackedIcicleBlock()) {
            props.friction(0.989F);
            return new PackedIcicleBlock(props) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return 0; }
                @Override public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) { return true; }
            };
        } else if (def.isBoneDice()) {
            return new BoneDiceBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
                    return getAnalogOutput(state, level, pos);
                }
            };
        } else if (def.isStar()) {
            return new StarBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                    super.entityInside(state, level, pos, entity);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops();
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isCushion()) {
            return new CushionBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
                    if (entity.isSuppressingBounce()) super.fallOn(level, state, pos, entity, fallDistance);
                    else entity.causeFallDamage(fallDistance, 0.0F, net.minecraft.world.damagesource.DamageSource.FALL);
                }
                @Override
                public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
                    if (entity.isSuppressingBounce()) super.updateEntityAfterFallOn(level, entity);
                    else bounceUp(entity);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isBigBook()) {
            return new BigBookBlock(props) {};
        } else if (def.isBigCandle()) {
            props.lightLevel(state -> state.getValue(BigCandleBlock.LIT) ? BigCandleBlock.LIGHT_LEVEL : 0);
            return new BigCandleBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return 0;
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
                @Override
                public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
                    return 1.0F;
                }
                @Override
                public boolean useShapeForLightOcclusion(BlockState state) {
                    return false;
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public boolean isRandomlyTicking(BlockState state) {
                    return state.getValue(LIT);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    onRandomTick(state, level, pos, random::nextInt, random::nextFloat);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextFloat, random::nextInt);
                }
            };
        } else if (def.isSoftFabric()) {
            return new SoftFabricBlock(props);
        } else if (def.isSpool()) {
            return new SpoolBlock(props);
        } else if (def.isSteelBolt()) {
            return new SteelBoltBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    return onUpdateShape(super.updateShape(state, direction, adjacentState, level, pos, adjacentPos), direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state);
                }
            };
        } else if (def.isFestiveLamp()) {
            return new FestiveLampBlock(props);
        } else if (def.isBigOrnament()) {
            boolean tinted = def.isBigTintedOrnament();
            // Light is supplied by the 1.18.2 reference catalog.
            if (tinted) props.dynamicShape();
            return new BigOrnamentBlock(props, tinted) {
                @Override
                public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
                    return isTinted() ? 0 : super.getLightBlock(state, level, pos);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = getBeaconColor(state, level, pos, beaconPos);
                    return new float[]{((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F, (color & 0xFF) / 255.0F};
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isOrnament()) {
            boolean tinted = def.isTintedOrnament();
            // Light is supplied by the 1.18.2 reference catalog.
            if (tinted) props.dynamicShape();
            return new OrnamentBlock(props, tinted) {
                @Override public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) { return isTinted() ? 0 : super.getLightBlock(state, level, pos); }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) { int color = getBeaconColor(state, level, pos, beaconPos); return new float[]{((color >> 16) & 255) / 255.0F, ((color >> 8) & 255) / 255.0F, (color & 255) / 255.0F}; }
                @Override public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) { if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level)); if (!canSurvive(state, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1); return super.updateShape(state, direction, neighborState, level, pos, neighborPos); }
                @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return onInteract(level, pos, state, player, hand, hit); }
            };
        } else if (def.isStringLight()) {
            return new StringLightBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(super.updateShape(state, direction, neighborState, level, pos, neighborPos), direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isExperienceCauldron()) {
            return new LayeredCauldronBlock(props, (precipitation) -> false, ExperienceCauldronInteractions.INTERACTIONS) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    int levelVal = state.getValue(LEVEL);
                    if (levelVal > 0 && random.nextInt(25 - levelVal * 5) == 0) {
                        double fillHeight = 0.4375D + (double) levelVal * 0.1875D;
                        double x = (double) pos.getX() + 0.2D + random.nextDouble() * 0.6D;
                        double y = (double) pos.getY() + fillHeight;
                        double z = (double) pos.getZ() + 0.2D + random.nextDouble() * 0.6D;
                        if (ModParticles.XP_PARTICLE.isPresent()) {
                            level.addParticle(ModParticles.XP_PARTICLE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
                        } else {
                            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
                @Override
                public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
                    return new ItemStack(Items.CAULDRON);
                }
            };
        } else if (def.isMulticolorGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new MulticolorGlowLightsBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return MulticolorGlowLightsBlock.updateMulticolorShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isGlowLights()) {
            props.lightLevel(state -> state.getValue(GlowLightsBlock.LIT) ? 15 : 0);
            return new GlowLightsBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (state.getValue(WATERLOGGED)) {
                        level.scheduleTick(pos, net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.WATER.getTickDelay(level));
                    }
                    return GlowLightsBlock.updateGlowLightShape(state, direction, neighborState, level, neighborPos);
                }
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
            };
        } else if (def.isFrostRose()) {
            return new FrostRoseBlock(props) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (!canSurvive(adjusted, level, pos) && level instanceof ServerLevel) level.scheduleTick(pos, this, 1);
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
                @Override
                public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
                    return true;
                }
            };
        } else if (def.isStrawBed()) {
            return new StrawBedBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult res = onInteract(level, pos, state, player, hand, hit);
                    if (res != InteractionResult.PASS) {
                        return res;
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }
            };
        } else if (def.isHangingMoss()) {
            props.randomTicks();
            class V118xHangingMossBlock extends HangingMossBlock implements BonemealableBlock {
                public V118xHangingMossBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    if (direction == Direction.UP && !state.canSurvive(level, pos)) {
                        return Blocks.AIR.defaultBlockState();
                    }
                    if (direction == Direction.DOWN) {
                        return state.setValue(TIP, !adjacentState.is(this));
                    }
                    return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public boolean isRandomlyTicking(BlockState state) {
                    return state.getValue(TIP) && !state.getValue(SHEARED);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    if (state.getValue(TIP) && !state.getValue(SHEARED)) {
                        if (random.nextInt(10) == 0) {
                            BlockPos growPos = pos.below();
                            BlockState growState = level.getBlockState(growPos);
                            if (growState.isAir() || growState.getMaterial().isReplaceable()) {
                                level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                                level.setBlock(pos, state.setValue(TIP, false), 3);
                            }
                        }
                    }
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    ItemStack held = player.getItemInHand(hand);
                    if (held.getItem() instanceof net.minecraft.world.item.ShearsItem) {
                        BlockPos tipPos = findTip(level, pos, this);
                        BlockState tipState = level.getBlockState(tipPos);
                        if (tipState.hasProperty(SHEARED) && !tipState.getValue(SHEARED)) {
                            if (!level.isClientSide) {
                                level.setBlockAndUpdate(tipPos, tipState.setValue(SHEARED, true));
                                level.playSound(null, tipPos, net.minecraft.sounds.SoundEvents.GROWING_PLANT_CROP, net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                                level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.SHEAR, tipPos);
                                held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                            }
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                    return super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return false;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    return growState.isAir() || growState.getMaterial().isReplaceable();
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    return !(tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED));
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    BlockPos tipPos = findTip(level, pos, this);
                    BlockState tipState = level.getBlockState(tipPos);
                    if (tipState.hasProperty(SHEARED) && tipState.getValue(SHEARED)) {
                        return;
                    }
                    BlockPos growPos = tipPos.below();
                    BlockState growState = level.getBlockState(growPos);
                    if (growState.isAir() || growState.getMaterial().isReplaceable()) {
                        level.setBlock(growPos, this.defaultBlockState().setValue(TIP, true).setValue(SHEARED, false), 3);
                        level.setBlock(tipPos, tipState.setValue(TIP, false), 3);
                    }
                }
            }
            return new V118xHangingMossBlock(props);
        } else if (def.isClover()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V118xCloverBlock extends CloverBlock implements BonemealableBlock {
                public V118xCloverBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (direction == Direction.DOWN) return adjusted;
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growClover(level, pos, state);
                }
            }
            return new V118xCloverBlock(props);
        } else if (def.isPetal() || def.isLeafLitter()) {
            props.sound(com.kingodogo.buildscape.sound.ModSounds.FLOWER_BED_SOUNDS());
            class V118xPetalBlock extends PetalBlock implements BonemealableBlock {
                public V118xPetalBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (direction == Direction.DOWN) return adjusted;
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }

                @Override
                public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
                    onEntityInside(level, pos, state, entity);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growPetal(level, pos, state);
                }
            }
            return new V118xPetalBlock(props);
        } else if (def.isWildflowers()) {
            props.sound(SoundType.GRASS).noCollission();
            class V118xWildflowersBlock extends WildflowersBlock implements BonemealableBlock {
                public V118xWildflowersBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growFlowers(level, pos, state);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state);
                }
            }
            return new V118xWildflowersBlock(props);
        } else if (def.isColoredMoss()) {
            Supplier<Block> carpet = resolveBlockSupplier(def.getId().replace("_block", "_carpet"));
            Supplier<Block> overlay = resolveBlockSupplier(def.getId().replace("_block", "_overlay"));
            Supplier<Block> layers = resolveBlockSupplier(def.getId().replace("_block", "_layers"));
            Supplier<Block> sapling = resolveBlockSupplier(def.getParentBlockId());
            class V118xColoredMossBlock extends ColoredMossBlock implements BonemealableBlock {
                public V118xColoredMossBlock(BlockBehaviour.Properties properties) {
                    super(properties, carpet, overlay, layers, sapling, null);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }

                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    growMoss(level, pos, state, random::nextInt);
                }
            }
            return new V118xColoredMossBlock(props);
        } else if (def.isColoredSporeBlossom()) {
            int colorRGB = getSporeBlossomColor(def.getId());
            class V118xColoredSporeBlossomBlock extends ColoredSporeBlossomBlock {
                public V118xColoredSporeBlossomBlock(BlockBehaviour.Properties properties) {
                    super(properties, colorRGB);
                }

                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(state, level, pos, random::nextDouble, random::nextInt);
                }
            }
            return new V118xColoredSporeBlossomBlock(props);
        } else if (def.isCreakingHeart()) {
            Supplier<Block> resinClumpSupplier = resolveBlockSupplier("resin_clump");
            class V118xCreakingHeartBlock extends CreakingHeartBlock {
                public V118xCreakingHeartBlock(BlockBehaviour.Properties properties) {
                    super(properties, resinClumpSupplier);
                }

                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(state, level, pos, player, hand);
                }

                @Override
                public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    onScheduledTick(state, level, pos);
                }
            }
            return new V118xCreakingHeartBlock(props);
        } else if (def.isMonetFlower()) {
            class V118xMonetFlowerBlock extends MonetFlowerBlock implements BonemealableBlock {
                public V118xMonetFlowerBlock(BlockBehaviour.Properties properties) {
                    super(properties);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return true;
                }
                @Override
                public boolean isBonemealSuccess(Level level, java.util.Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, java.util.Random random, BlockPos pos, BlockState state) {
                    performBonemealAction(level, pos, this);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
                    BlockState adjusted = onUpdateShape(state, direction, adjacentState, level, pos, adjacentPos);
                    if (direction == Direction.DOWN) return adjusted;
                    return super.updateShape(adjusted, direction, adjacentState, level, pos, adjacentPos);
                }
            }
            return new V118xMonetFlowerBlock(props);
        } else if (def.isSnowyBush()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyBushBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction != Direction.DOWN && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isSnowyFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V118xSnowyFernBlock extends SnowyFernBlock implements BonemealableBlock {
                public V118xSnowyFernBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyLargeFern);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction != Direction.DOWN && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyFernBlock(props);
        } else if (def.isSnowyShortGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            class V118xSnowyShortGrassBlock extends SnowyShortGrassBlock implements BonemealableBlock {
                public V118xSnowyShortGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowBricks, snowyTallGrass);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    if (direction != Direction.DOWN && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }
                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }
                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealGrowth(level, pos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyShortGrassBlock(props);
        } else if (def.isSnowyLargeFern()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyLargeFernBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isSnowyTallGrass()) {
            Supplier<Block> snowBricks = resolveBlockSupplier("snow_bricks");
            return new SnowyTallGrassBlock(props, snowBricks) {
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
                    if (updated.isAir()) return updated;
                    return onUpdateShape(updated, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(state, builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            };
        } else if (def.isSnowyGrass()) {
            Supplier<Block> dirtSlab = resolveBlockSupplier("dirt_slab");
            Supplier<Block> snowyGrassSlab = resolveBlockSupplier("snowy_grass_block_slab");
            Supplier<Block> snowyShortGrass = resolveBlockSupplier("snowy_short_grass");
            Supplier<Block> snowyBush = resolveBlockSupplier("snowy_bush");
            Supplier<Block> snowyFern = resolveBlockSupplier("snowy_fern");
            Supplier<Block> snowyTallGrass = resolveBlockSupplier("snowy_tall_grass");
            Supplier<Block> snowyLargeFern = resolveBlockSupplier("snowy_large_fern");
            class V118xSnowyGrassBlock extends SnowyGrassBlock implements BonemealableBlock {
                public V118xSnowyGrassBlock(BlockBehaviour.Properties properties) {
                    super(properties, snowyShortGrass, snowyBush, snowyFern, snowyTallGrass, snowyLargeFern);
                }

                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                    if (!canBeGrass(state, level, pos)) {
                        level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
                        return;
                    }
                    if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                        BlockState snowyGrassState = this.defaultBlockState();
                        for (int i = 0; i < 4; ++i) {
                            BlockPos targetPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                            BlockState targetState = level.getBlockState(targetPos);
                            Block dirtSlabBlock = dirtSlab != null ? dirtSlab.get() : null;
                            Block snowyGrassSlabBlock = snowyGrassSlab != null ? snowyGrassSlab.get() : null;
                            if (dirtSlabBlock != null && snowyGrassSlabBlock != null && targetState.is(dirtSlabBlock) && canPropagate(snowyGrassState, level, targetPos)) {
                                BlockState grassSlabState = snowyGrassSlabBlock.defaultBlockState()
                                        .setValue(net.minecraft.world.level.block.SlabBlock.TYPE, targetState.getValue(net.minecraft.world.level.block.SlabBlock.TYPE))
                                        .setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, targetState.getValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED));
                                level.setBlockAndUpdate(targetPos, grassSlabState);
                            } else if (targetState.is(Blocks.DIRT) && canPropagate(snowyGrassState, level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, snowyGrassState);
                            }
                        }
                    }
                }

                private boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    BlockState aboveState = level.getBlockState(abovePos);
                    if (aboveState.is(Blocks.SNOW) && aboveState.getValue(net.minecraft.world.level.block.SnowLayerBlock.LAYERS) == 1) {
                        return true;
                    }
                    if (aboveState.getFluidState().getAmount() == 8) {
                        return false;
                    }
                    int lightLevel = net.minecraft.world.level.lighting.LayerLightEngine.getLightBlockInto(
                            level,
                            state,
                            pos,
                            aboveState,
                            abovePos,
                            Direction.UP,
                            aboveState.getLightBlock(level, abovePos)
                    );
                    return lightLevel < level.getMaxLightLevel();
                }

                private boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
                    BlockPos abovePos = pos.above();
                    return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(net.minecraft.tags.FluidTags.WATER);
                }

                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return isValidBonemeal(level, pos);
                }

                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return true;
                }

                @Override
                public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
                    performBonemealFoliage(level, pos);
                }

                @Override
                public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
                    return getReferenceDrops(builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL));
                }
            }
            return new V118xSnowyGrassBlock(props);
        } else if (def.isExperienceFluid()) {
            Supplier<FlowingFluid> fluidSupplier = () -> resolveFlowingFluid(def.getParentBlockId());
            if (experienceFluidFactory != null) return experienceFluidFactory.apply(fluidSupplier, props);
            return new ExperienceFluidBlock(fluidSupplier, props) {
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
                    super.animateTick(state, level, pos, random);
                    if (random.nextInt(30) == 0) {
                        spawnXpParticle(level, pos, random.nextDouble(), random.nextDouble());
                    }
                }
            };
        } else if (def.isSilkTouchOnlyGlass()) {
            return new SilkTouchOnlyGlassBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = state.getMapColor(level, pos).col;
                    return new float[]{((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f};
                }
            };
        } else if (def.isSilkTouchOnlyPane()) {
            return new SilkTouchOnlyPaneBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    return onInteract(level, pos, state, player, hand, hit);
                }
                public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
                    int color = state.getMapColor(level, pos).col;
                    return new float[]{((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, (color & 0xFF) / 255.0f};
                }
            };
        } else if (def.isResinClump()) {
            props.lightLevel(state -> 7);
            return new net.minecraft.world.level.block.GlowLichenBlock(props) {
                public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
                    return 7;
                }
                @Override
                public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
                    return false;
                }
                @Override
                public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
                    return false;
                }
            };
        } else if (def.isWallpaperFlat()) {
            return new WallpaperFlatBlock(props) {};
        } else if (def.isPointedIcicle()) {
            return new PointedIcicleBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand,
                            player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.PickaxeItem);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }
                @Override
                public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
                    onIcicleRemove(state, level, pos, newState, isMoving);
                    super.onRemove(state, level, pos, newState, isMoving);
                }
                @Override
                public void tick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onIcicleTick(state, level, pos);
                }
                @Override
                public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
                    return onUpdateShape(state, direction, neighborState, level, pos, neighborPos);
                }
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
                    onNeighborUpdate(state, level, pos, neighborBlock, neighborPos, isMoving);
                }
                @Override
                public void randomTick(BlockState state, ServerLevel level, BlockPos pos, java.util.Random random) {
                    onRandomTick(state, level, pos);
                }
                @Override
                public void animateTick(BlockState state, Level level, BlockPos pos, java.util.Random random) {
                    onAnimateTick(state, level, pos);
                }
            };
        } else if (def.isPlant()) {
            return new ModBushBlock(props) {
                @Override
                public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
                    InteractionResult result = onInteract(state, level, pos, player, hand);
                    return result != InteractionResult.PASS ? result : super.use(state, level, pos, player, hand, hit);
                }
            };
        } else {
            return new ModBlock(props);
        }
    }

    private static int getSporeBlossomColor(String id) {
        if (id.contains("red")) return 0xFE4A4E;
        if (id.contains("cyan")) return 0x1FDEE2;
        if (id.contains("blue")) return 0x4A7DF4;
        if (id.contains("purple")) return 0xA146F2;
        if (id.contains("orange")) return 0xF76E32;
        return 0xFFFFFF;
    }

    private Block restoredCopperDoor(BlockBehaviour.Properties props, boolean weathering) {
        return new DoorBlock(weathering ? props.randomTicks() : props) {
            @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                if (weathering) RestoredCopperOxidation.tryOxidize(level, pos, state);
            }
            @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                    InteractionHand hand, BlockHitResult hit) {
                state = state.cycle(OPEN);
                level.setBlock(pos, state, 10);
                level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_DOOR_OPEN
                        : net.minecraft.sounds.SoundEvents.WOODEN_DOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
                level.gameEvent(player, state.getValue(OPEN) ? net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN
                        : net.minecraft.world.level.gameevent.GameEvent.BLOCK_CLOSE, pos);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        };
    }

    private Block restoredCopperTrapdoor(BlockBehaviour.Properties props, boolean weathering) {
        return new TrapDoorBlock(weathering ? props.randomTicks() : props) {
            @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
                if (weathering) RestoredCopperOxidation.tryOxidize(level, pos, state);
            }
            @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                    InteractionHand hand, BlockHitResult hit) {
                state = state.cycle(OPEN);
                level.setBlock(pos, state, 2);
                if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                level.playSound(player, pos, state.getValue(OPEN) ? net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_OPEN
                        : net.minecraft.sounds.SoundEvents.WOODEN_TRAPDOOR_CLOSE, net.minecraft.sounds.SoundSource.BLOCKS, 1, 1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        };
    }

    private Block findDefinedBlock(String path) {
        for (var supplier : Services.REGISTRY.getRegisteredBlocks()) {
            if (!supplier.getPath().equals(path)) continue;
            Object value = supplier.get();
            if (value instanceof Block block) return block;
            if (value instanceof BlockDefinition definition) {
                Block block = definition.getBlock();
                if (block == null) {
                    var created = new java.util.concurrent.atomic.AtomicReference<Block>();
                    Services.PLATFORM.wrapRegistryAction(() -> created.set(createBlock(definition)));
                    block = created.get();
                    definition.setBlock(block);
                }
                return block;
            }
        }
        return null;
    }

    private Block restoredBlock(String path) {
        Block block = findDefinedBlock(path);
        if (block == null) throw new IllegalStateException("Missing restored block dependency: " + path);
        return block;
    }

    private Block getBaseBlock(BlockDefinition def) {
        Block reference = ReferenceBlockBases.find(def.getId(), this::restoredBlock);
        if (reference != null) return reference;
        String parentId = def.getParentBlockId();
        if (parentId != null) {
            String cleanId = parentId.toLowerCase().replace("blocks.", "").replace("modblocks.", "").replace(".get(", "");
            Block defined = findDefinedBlock(cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(':') + 1) : cleanId);
            if (defined != null) return defined;
            ResourceLocation rl = ResourceLocation.tryParse(cleanId.contains(":") ? cleanId : "buildscape:" + cleanId);
            if (rl != null) {
                Block b = Registry.BLOCK.get(rl);
                if (b != null && b != Blocks.AIR) return b;
                ResourceLocation mcRl = new ResourceLocation(cleanId);
                b = Registry.BLOCK.get(mcRl);
                if (b != null && b != Blocks.AIR) return b;
            }
        }
        return Blocks.OAK_PLANKS;
    }

    private Supplier<Block> resolveBlockSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("blocks.", "").replace("modblocks.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        return () -> {
            Block defined = findDefinedBlock(path);
            if (defined != null) return defined;
            ResourceLocation rl = new ResourceLocation(namespace, path);
            Block b = Registry.BLOCK.get(rl);
            if (b != null && b != Blocks.AIR) return b;
            b = Registry.BLOCK.get(new ResourceLocation("minecraft", path));
            if (b != null && b != Blocks.AIR) return b;
            return null;
        };
    }

    private Supplier<Item> resolveItemSupplier(String id) {
        if (id == null) return () -> null;
        String cleanId = id.toLowerCase().replace("items.", "").replace("moditems.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        return () -> {
            ResourceLocation rl = new ResourceLocation(namespace, path);
            Item it = Registry.ITEM.get(rl);
            if (it != null && it != Items.AIR) return it;
            it = Registry.ITEM.get(new ResourceLocation("minecraft", path));
            if (it != null && it != Items.AIR) return it;
            return null;
        };
    }

    private FlowingFluid resolveFlowingFluid(String id) {
        if (id == null) return (FlowingFluid) Fluids.WATER;
        String cleanId = id.toLowerCase().replace("fluids.", "").replace("modfluids.", "").replace(".get(", "").replace(")", "").trim();
        String namespace = cleanId.contains(":") ? cleanId.substring(0, cleanId.indexOf(":")) : "buildscape";
        String path = cleanId.contains(":") ? cleanId.substring(cleanId.indexOf(":") + 1) : cleanId;
        ResourceLocation rl = new ResourceLocation(namespace, path);
        Fluid f = Registry.FLUID.get(rl);
        if (f instanceof FlowingFluid flowing && f != Fluids.EMPTY) {
            return flowing;
        }
        return (FlowingFluid) Fluids.WATER;
    }

    private BlockBehaviour.Properties referenceSlabProperties(BlockDefinition def, BlockBehaviour.Properties properties) {
        // The properties-only reference constructor keeps its supplied properties.
        // Only its base-block constructor applies ModSlabBlock.safeProperties.
        Block base = ReferenceBlockBases.find(def.getId(), this::restoredBlock);
        return base == null ? properties : referenceStairProperties(base.defaultBlockState(), properties);
    }

    /**
     * Reference ModSlabBlock and ModStairBlock flatten properties of state-bearing base blocks
     * before delegating to vanilla slabs or stairs. This is specific to the 1.18.2 catalog.
     */
    private BlockBehaviour.Properties referenceStairProperties(BlockState baseState, BlockBehaviour.Properties properties) {
        if (baseState.getProperties().isEmpty()) return properties;
        Block base = baseState.getBlock();
        float hardness = 2.0F;
        if (base == Blocks.AZALEA_LEAVES || base == Blocks.FLOWERING_AZALEA_LEAVES) hardness = 0.2F;
        else if (base == Blocks.REDSTONE_LAMP) hardness = 0.3F;
        else if (base == Blocks.HAY_BLOCK) hardness = 0.5F;
        else if (base == Blocks.CARVED_PUMPKIN || base == Blocks.TARGET) hardness = 1.0F;
        else if (base == Blocks.PURPUR_PILLAR) hardness = 1.5F;
        BlockBehaviour.Properties result = BlockBehaviour.Properties.of(baseState.getMaterial())
                .strength(hardness, base.getExplosionResistance()).sound(base.getSoundType(baseState));
        if (baseState.requiresCorrectToolForDrops()) result.requiresCorrectToolForDrops();
        return result;
    }
    private BlockState getBaseState(BlockDefinition def) {
        return getBaseBlock(def).defaultBlockState();
    }

    /** Reference properties for content retired only on 26.2. */
    private BlockBehaviour.Properties restoredProperties(String id) {
        return switch (id) {
            case "sculk_pillar" -> BlockBehaviour.Properties.of(Material.SCULK, MaterialColor.COLOR_BLACK).strength(0.6f, 0.6f).sound(net.minecraft.world.level.block.SoundType.SCULK_SENSOR).noOcclusion();
            case "decorated_pot" -> BlockBehaviour.Properties.of(Material.STONE).strength(0.0f).sound(com.kingodogo.buildscape.sound.ModSounds.DECORATED_POT_SOUNDS()).noOcclusion();
            case "bamboo_block" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO);
            case "stripped_bamboo_block" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO);
            case "bamboo_door" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(3.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO) .noOcclusion();
            case "bamboo_trapdoor" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(3.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO) .noOcclusion() .isValidSpawn((state, reader, pos, entityType) -> false);
            case "bamboo_sign" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(1.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO) .noCollission();
            case "bamboo_wall_sign" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW) .strength(1.0f) .sound(net.minecraft.world.level.block.SoundType.BAMBOO) .noCollission() .dropsLike(restoredBlock("bamboo_sign"));
            case "copper_chain" -> BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE) .strength(3.0f, 6.0f) .requiresCorrectToolForDrops() .sound(net.minecraft.world.level.block.SoundType.CHAIN) .noOcclusion() .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "exposed_copper_chain" -> BlockBehaviour.Properties.of( Material.METAL, MaterialColor.TERRACOTTA_ORANGE) .strength(3.0f, 6.0f) .requiresCorrectToolForDrops() .sound(net.minecraft.world.level.block.SoundType.CHAIN) .noOcclusion() .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "weathered_copper_chain" -> BlockBehaviour.Properties.of( Material.METAL, MaterialColor.WARPED_WART_BLOCK) .strength(3.0f, 6.0f) .requiresCorrectToolForDrops() .sound(net.minecraft.world.level.block.SoundType.CHAIN) .noOcclusion() .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "oxidized_copper_chain" -> BlockBehaviour.Properties.of( Material.METAL, MaterialColor.WARPED_NYLIUM) .strength(3.0f, 6.0f) .requiresCorrectToolForDrops() .sound(net.minecraft.world.level.block.SoundType.CHAIN) .noOcclusion() .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "pink_petal" -> BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_PINK) .noCollission() .instabreak() .sound(SoundType.GRASS);
            case "snowy_leaves" -> BlockBehaviour.Properties.of(Material.LEAVES) .strength(0.2f) .sound(net.minecraft.world.level.block.SoundType.GRASS) .noOcclusion() .isValidSpawn((state, reader, pos, entityType) -> false) .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "mangrove_log" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "stripped_mangrove_log" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_wood" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "stripped_mangrove_wood" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_leaves" -> BlockBehaviour.Properties.of(Material.LEAVES, MaterialColor.COLOR_GREEN) .strength(0.2f) .randomTicks() .sound(net.minecraft.world.level.block.SoundType.GRASS) .noOcclusion() .isValidSpawn((state, reader, pos, entityType) -> false) .isSuffocating((state, reader, pos) -> false) .isViewBlocking((state, reader, pos) -> false);
            case "mangrove_roots" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(0.7f) .sound( com.kingodogo.buildscape.sound.ModSounds .MANGROVE_ROOTS_SOUNDS()) .noOcclusion();
            case "muddy_mangrove_roots" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN) .strength(0.7f) .sound( com.kingodogo.buildscape.sound.ModSounds .MUDDY_MANGROVE_ROOTS_SOUNDS()) .noOcclusion();
            case "mangrove_propagule" -> BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN) .strength(0.0f) .randomTicks() .sound(net.minecraft.world.level.block.SoundType.GRASS) .noCollission() .noOcclusion();
            case "mangrove_planks" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f, 3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_stairs" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f, 3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_slab" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f, 3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_fence" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f, 3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_fence_gate" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(2.0f, 3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD);
            case "mangrove_door" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noOcclusion();
            case "mangrove_trapdoor" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(3.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noOcclusion() .isValidSpawn((state, reader, pos, entityType) -> false);
            case "mangrove_button" -> BlockBehaviour.Properties.of( Material.DECORATION, MaterialColor.COLOR_RED) .strength(0.5f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noCollission();
            case "mangrove_pressure_plate" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(0.5f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noCollission();
            case "mangrove_sign" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(1.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noCollission();
            case "mangrove_wall_sign" -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED) .strength(1.0f) .sound(net.minecraft.world.level.block.SoundType.WOOD) .noCollission() .dropsLike(restoredBlock("mangrove_sign"));
            case "sulfur" -> BlockBehaviour.Properties.copy(Blocks.STONE);
            case "polished_sulfur" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "chiseled_sulfur" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "sulfur_bricks" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "sulfur_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "sulfur_slab" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "sulfur_wall" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur"));
            case "polished_sulfur_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("polished_sulfur"));
            case "polished_sulfur_slab" -> BlockBehaviour.Properties.copy(restoredBlock("polished_sulfur"));
            case "polished_sulfur_wall" -> BlockBehaviour.Properties.copy(restoredBlock("polished_sulfur"));
            case "sulfur_bricks_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur_bricks"));
            case "sulfur_bricks_slab" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur_bricks"));
            case "sulfur_bricks_wall" -> BlockBehaviour.Properties.copy(restoredBlock("sulfur_bricks"));
            case "sulfur_spike" -> BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE);
            case "potent_sulfur" -> BlockBehaviour.Properties.copy(Blocks.STONE);
            case "cinnabar" -> BlockBehaviour.Properties.copy(Blocks.STONE);
            case "polished_cinnabar" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "chiseled_cinnabar" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "cinnabar_bricks" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "cinnabar_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "cinnabar_slab" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "cinnabar_wall" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar"));
            case "polished_cinnabar_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("polished_cinnabar"));
            case "polished_cinnabar_slab" -> BlockBehaviour.Properties.copy(restoredBlock("polished_cinnabar"));
            case "polished_cinnabar_wall" -> BlockBehaviour.Properties.copy(restoredBlock("polished_cinnabar"));
            case "cinnabar_bricks_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar_bricks"));
            case "cinnabar_bricks_slab" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar_bricks"));
            case "cinnabar_bricks_wall" -> BlockBehaviour.Properties.copy(restoredBlock("cinnabar_bricks"));
            case "tuff_slab" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "tuff_stairs" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "tuff_wall" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "polished_tuff" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "polished_tuff_slab" -> BlockBehaviour.Properties.copy(restoredBlock("polished_tuff"));
            case "polished_tuff_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("polished_tuff"));
            case "polished_tuff_wall" -> BlockBehaviour.Properties.copy(restoredBlock("polished_tuff"));
            case "chiseled_tuff" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "tuff_bricks" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "tuff_brick_slab" -> BlockBehaviour.Properties.copy(restoredBlock("tuff_bricks"));
            case "tuff_brick_stairs" -> BlockBehaviour.Properties.copy(restoredBlock("tuff_bricks"));
            case "tuff_brick_wall" -> BlockBehaviour.Properties.copy(restoredBlock("tuff_bricks"));
            case "chiseled_tuff_bricks" -> BlockBehaviour.Properties.copy(Blocks.TUFF);
            case "pale_oak_log" -> BlockBehaviour.Properties.copy(Blocks.OAK_LOG);
            case "stripped_pale_oak_log" -> BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG);
            case "pale_oak_wood" -> BlockBehaviour.Properties.copy(Blocks.OAK_WOOD);
            case "stripped_pale_oak_wood" -> BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD);
            case "pale_oak_planks" -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS);
            case "pale_oak_stairs" -> BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS);
            case "pale_oak_slab" -> BlockBehaviour.Properties.copy(Blocks.OAK_SLAB);
            case "pale_oak_fence" -> BlockBehaviour.Properties.copy(Blocks.OAK_FENCE);
            case "pale_oak_fence_gate" -> BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE);
            case "pale_oak_door" -> BlockBehaviour.Properties.copy(Blocks.OAK_DOOR);
            case "pale_oak_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR);
            case "pale_oak_button" -> BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON);
            case "pale_oak_pressure_plate" -> BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE);
            case "pale_oak_leaves" -> BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES);
            case "pale_oak_sapling" -> BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING);
            case "cherry_log" -> BlockBehaviour.Properties.copy(Blocks.OAK_LOG);
            case "stripped_cherry_log" -> BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_LOG);
            case "cherry_wood" -> BlockBehaviour.Properties.copy(Blocks.OAK_WOOD);
            case "stripped_cherry_wood" -> BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD);
            case "cherry_planks" -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS);
            case "cherry_stairs" -> BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS);
            case "cherry_slab" -> BlockBehaviour.Properties.copy(Blocks.OAK_SLAB);
            case "cherry_fence" -> BlockBehaviour.Properties.copy(Blocks.OAK_FENCE);
            case "cherry_fence_gate" -> BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE);
            case "cherry_door" -> BlockBehaviour.Properties.copy(Blocks.OAK_DOOR);
            case "cherry_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR);
            case "cherry_button" -> BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON);
            case "cherry_pressure_plate" -> BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE);
            case "cherry_leaves" -> BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES);
            case "cherry_sapling" -> BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING);
            case "closed_eyeblossom" -> BlockBehaviour.Properties.copy(Blocks.POPPY);
            case "open_eyeblossom" -> BlockBehaviour.Properties.copy(Blocks.POPPY).lightLevel(state -> 7).hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true);
            case "wildflowers" -> BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().sound(SoundType.GRASS);
            case "leaf_litter" -> BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).noCollission().sound(SoundType.GRASS);
            case "cactus_flower" -> BlockBehaviour.Properties.copy(Blocks.DANDELION);
            case "bush" -> BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH);
            case "firefly_bush" -> BlockBehaviour.Properties.copy(Blocks.GRASS).lightLevel(state -> 2).hasPostProcess((state, getter, pos) -> true).emissiveRendering((state, getter, pos) -> true);
            case "dry_grass" -> BlockBehaviour.Properties.copy(Blocks.GRASS);
            case "tall_dry_grass" -> BlockBehaviour.Properties.copy(Blocks.TALL_GRASS);
            case "golden_dandelion" -> BlockBehaviour.Properties.copy(Blocks.DANDELION);
            case "potted_closed_eyeblossom" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_open_eyeblossom" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY).lightLevel(state -> 7);
            case "potted_golden_dandelion" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_cactus_flower" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_bush" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_firefly_bush" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY).lightLevel(state -> 2);
            case "potted_dry_grass" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_pale_oak_sapling" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_cherry_sapling" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_mangrove_propagule" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_wildflowers" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_leaf_litter" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "potted_tall_dry_grass" -> BlockBehaviour.Properties.copy(Blocks.POTTED_POPPY);
            case "ochre_froglight" -> BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15);
            case "pearlescent_froglight" -> BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15);
            case "verdant_froglight" -> BlockBehaviour.Properties.copy(Blocks.SHROOMLIGHT).lightLevel(state -> 15);
            case "sculk" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR);
            case "sculk_slab" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR);
            case "sculk_stairs" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR);
            case "sculk_wall" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR);
            case "sculk_vertical_slab" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR);
            case "sculk_vein" -> BlockBehaviour.Properties.copy(Blocks.GLOW_LICHEN);
            case "sculk_catalyst" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).lightLevel(state -> 6);
            case "sculk_shrieker" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).noOcclusion();
            case "sculk_sensor" -> BlockBehaviour.Properties.copy(Blocks.SCULK_SENSOR).lightLevel(state -> 1).noOcclusion();
            case "copper_torch" -> BlockBehaviour.Properties.copy(Blocks.TORCH).lightLevel(state -> 14);
            case "copper_wall_torch" -> BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lightLevel(state -> 14);
            case "copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "waxed_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "exposed_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "waxed_exposed_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "weathered_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "waxed_weathered_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "oxidized_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "waxed_oxidized_copper_rod" -> BlockBehaviour.Properties.copy(Blocks.LIGHTNING_ROD);
            case "copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "waxed_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "exposed_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "waxed_exposed_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "weathered_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "waxed_weathered_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "oxidized_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "waxed_oxidized_copper_lantern" -> BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(state -> 15).sound(SoundType.COPPER);
            case "chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.CUT_COPPER);
            case "waxed_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.CUT_COPPER);
            case "exposed_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER);
            case "waxed_exposed_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER);
            case "weathered_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER);
            case "waxed_weathered_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER);
            case "oxidized_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER);
            case "waxed_oxidized_chiseled_copper" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER);
            case "copper_grate" -> BlockBehaviour.Properties.copy(Blocks.CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "waxed_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "exposed_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "waxed_exposed_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "weathered_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "waxed_weathered_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "oxidized_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "waxed_oxidized_copper_grate" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER).noOcclusion().sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_GRATE_SOUNDS());
            case "copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 15 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "waxed_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 15 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "exposed_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 12 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "waxed_exposed_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 12 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "weathered_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 8 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "waxed_weathered_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 8 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "oxidized_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 4 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "waxed_oxidized_copper_bulb" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER) .lightLevel((state) -> state.getValue(CopperBulbBlock.LIT) ? 4 : 0) .sound(com.kingodogo.buildscape.sound.ModSounds.COPPER_BULB_SOUNDS()) .isRedstoneConductor((state, reader, pos) -> false);
            case "copper_door" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER);
            case "waxed_copper_door" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER);
            case "exposed_copper_door" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_exposed_copper_door" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "weathered_copper_door" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_weathered_copper_door" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "oxidized_copper_door" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_oxidized_copper_door" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER);
            case "waxed_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion().sound(SoundType.COPPER);
            case "exposed_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_exposed_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "weathered_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_weathered_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "oxidized_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "waxed_oxidized_copper_trapdoor" -> BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER).noOcclusion().sound(SoundType.COPPER);
            case "copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "waxed_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "exposed_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "waxed_exposed_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "weathered_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "waxed_weathered_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "oxidized_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "waxed_oxidized_copper_bars" -> BlockBehaviour.Properties.copy(Blocks.IRON_BARS).sound(SoundType.COPPER);
            case "copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "exposed_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "weathered_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "oxidized_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "waxed_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "waxed_exposed_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "waxed_weathered_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "waxed_oxidized_copper_chest" -> BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.CHEST).sound(net.minecraft.world.level.block.SoundType.COPPER);
            case "oak_shelf" -> BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).strength(2.0f, 3.0f);
            case "spruce_shelf" -> BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).strength(2.0f, 3.0f);
            case "birch_shelf" -> BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS).strength(2.0f, 3.0f);
            case "jungle_shelf" -> BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS).strength(2.0f, 3.0f);
            case "acacia_shelf" -> BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS).strength(2.0f, 3.0f);
            case "dark_oak_shelf" -> BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).strength(2.0f, 3.0f);
            case "mangrove_shelf" -> BlockBehaviour.Properties.copy(restoredBlock("mangrove_planks")).strength(2.0f, 3.0f);
            case "cherry_shelf" -> BlockBehaviour.Properties.copy(restoredBlock("cherry_planks")).strength(2.0f, 3.0f);
            case "crimson_shelf" -> BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).strength(2.0f, 3.0f);
            case "warped_shelf" -> BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).strength(2.0f, 3.0f);
            case "bamboo_shelf" -> BlockBehaviour.Properties.copy(restoredBlock("bamboo_block")).strength(2.0f, 3.0f);
            case "pale_oak_shelf" -> BlockBehaviour.Properties.copy(restoredBlock("pale_oak_planks")).strength(2.0f, 3.0f);
            default -> null;
        };
    }

    private BlockBehaviour.Properties buildProperties(BlockDefinition def) {
        BlockBehaviour.Properties reference = ReferenceBlockProperties.create(def.getId(), this::restoredBlock);
        if (reference != null) return reference;
        if (def.getId().endsWith("_redstone_lamp")) {
            return BlockBehaviour.Properties.of(Material.BUILDABLE_GLASS)
                    .strength(0.3F).sound(SoundType.GLASS)
                    .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? 15 : 0);
        }
        BlockBehaviour.Properties restored = restoredProperties(def.getId());
        if (restored != null) return restored;
        CommonBlockProperties cp = def.getProperties();
        net.minecraft.world.level.material.Material mat = mapMaterial(cp.getMaterial());
        MaterialColor color = mapColor(cp.getMapColor());

        BlockBehaviour.Properties props = color != null ?
                BlockBehaviour.Properties.of(mat, color) :
                BlockBehaviour.Properties.of(mat);

        props.strength(cp.getHardness(), cp.getResistance());
        if (cp.requiresCorrectTool()) {
            props.requiresCorrectToolForDrops();
        }
        if (cp.isNoCollision()) {
            props.noCollission();
        }
        if (cp.isNoOcclusion()) {
            props.noOcclusion();
        }
        if (cp.getLightLevel() > 0) {
            final int light = cp.getLightLevel();
            props.lightLevel(state -> light);
        }
        props.sound(mapSound(cp.getSound()));
        return props;
    }

    private net.minecraft.world.level.material.Material mapMaterial(String mat) {
        if (mat == null) return net.minecraft.world.level.material.Material.STONE;
        return switch (mat.toUpperCase()) {
            case "WOOD" -> net.minecraft.world.level.material.Material.WOOD;
            case "METAL", "IRON", "HEAVY_METAL" -> net.minecraft.world.level.material.Material.METAL;
            case "SAND" -> net.minecraft.world.level.material.Material.SAND;
            case "GLASS" -> net.minecraft.world.level.material.Material.GLASS;
            case "DIRT", "GRAVEL" -> net.minecraft.world.level.material.Material.DIRT;
            case "PLANT" -> net.minecraft.world.level.material.Material.PLANT;
            case "LEAVES" -> net.minecraft.world.level.material.Material.LEAVES;
            case "WOOL", "CLOTH" -> net.minecraft.world.level.material.Material.WOOL;
            case "CLAY" -> net.minecraft.world.level.material.Material.CLAY;
            case "WATER" -> net.minecraft.world.level.material.Material.WATER;
            case "DECORATION" -> net.minecraft.world.level.material.Material.DECORATION;
            case "ICE" -> net.minecraft.world.level.material.Material.ICE;
            case "SNOW" -> net.minecraft.world.level.material.Material.SNOW;
            default -> net.minecraft.world.level.material.Material.STONE;
        };
    }

    private SoundType mapSound(String sound) {
        if (sound == null) return SoundType.STONE;
        return switch (sound.toUpperCase()) {
            case "WOOD" -> SoundType.WOOD;
            case "GRAVEL" -> SoundType.GRAVEL;
            case "GRASS" -> SoundType.GRASS;
            case "METAL" -> SoundType.METAL;
            case "GLASS" -> SoundType.GLASS;
            case "WOOL" -> SoundType.WOOL;
            case "SAND" -> SoundType.SAND;
            case "SNOW" -> SoundType.SNOW;
            case "LADDER" -> SoundType.LADDER;
            case "ANVIL" -> SoundType.ANVIL;
            case "SLIME" -> SoundType.SLIME_BLOCK;
            case "HONEY" -> SoundType.HONEY_BLOCK;
            case "COPPER" -> SoundType.COPPER;
            case "BAMBOO" -> SoundType.BAMBOO;
            case "BONE" -> SoundType.BONE_BLOCK;
            case "CHAIN" -> SoundType.CHAIN;
            case "CANDLE" -> SoundType.CANDLE;
            default -> SoundType.STONE;
        };
    }

    private MaterialColor mapColor(String color) {
        if (color == null) return MaterialColor.NONE;
        return switch (color.toUpperCase()) {
            case "COLOR_BLACK", "BLACK" -> MaterialColor.COLOR_BLACK;
            case "COLOR_BLUE", "BLUE" -> MaterialColor.COLOR_BLUE;
            case "COLOR_BROWN", "BROWN" -> MaterialColor.COLOR_BROWN;
            case "COLOR_CYAN", "CYAN" -> MaterialColor.COLOR_CYAN;
            case "COLOR_GRAY", "GRAY" -> MaterialColor.COLOR_GRAY;
            case "COLOR_GREEN", "GREEN" -> MaterialColor.COLOR_GREEN;
            case "COLOR_LIGHT_BLUE", "LIGHT_BLUE" -> MaterialColor.COLOR_LIGHT_BLUE;
            case "COLOR_LIGHT_GRAY", "LIGHT_GRAY" -> MaterialColor.COLOR_LIGHT_GRAY;
            case "COLOR_LIGHT_GREEN", "LIME" -> MaterialColor.COLOR_LIGHT_GREEN;
            case "COLOR_MAGENTA", "MAGENTA" -> MaterialColor.COLOR_MAGENTA;
            case "COLOR_ORANGE", "ORANGE" -> MaterialColor.COLOR_ORANGE;
            case "COLOR_PINK", "PINK" -> MaterialColor.COLOR_PINK;
            case "COLOR_PURPLE", "PURPLE" -> MaterialColor.COLOR_PURPLE;
            case "COLOR_RED", "RED" -> MaterialColor.COLOR_RED;
            case "COLOR_YELLOW", "YELLOW" -> MaterialColor.COLOR_YELLOW;
            case "WOOD", "DIRT" -> MaterialColor.WOOD;
            case "STONE" -> MaterialColor.STONE;
            case "METAL" -> MaterialColor.METAL;
            case "WATER" -> MaterialColor.WATER;
            case "SAND" -> MaterialColor.SAND;
            case "SNOW", "WHITE" -> MaterialColor.SNOW;
            default -> MaterialColor.NONE;
        };
    }

}
