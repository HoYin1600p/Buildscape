package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v118x.PlatformAdapterBase;
import com.kingodogo.buildscape.adapter.v118x.PacketFactory;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.world.GameRuleAccess;
import com.kingodogo.buildscape.world.ModGameRules;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public final class FabricPlatformAdapter extends PlatformAdapterBase implements GameRuleAccess {
    private static final ThreadLocal<Integer> REGISTRY_DEPTH = ThreadLocal.withInitial(() -> 0);
    private boolean worldGenPrepared;
    private boolean worldGenRegistered;

    public static boolean isRegistryAction() { return REGISTRY_DEPTH.get() > 0; }

    @Override public void wrapRegistryAction(Runnable action) {
        int previousDepth = REGISTRY_DEPTH.get();
        REGISTRY_DEPTH.set(previousDepth + 1);
        try {
            // Fabric owns the vanilla intrusive-holder maps; nested actions retain them.
            action.run();
        } finally {
            if (previousDepth == 0) REGISTRY_DEPTH.remove();
            else REGISTRY_DEPTH.set(previousDepth);
        }
    }

    @Override public <T extends net.minecraft.world.item.crafting.Recipe<?>>
    net.minecraft.world.item.crafting.RecipeSerializer<T> createRecipeSerializer(
            com.kingodogo.buildscape.adapter.v118x.RecipeFactory.SerializerLogic<T> delegate) {
        return new net.minecraft.world.item.crafting.RecipeSerializer<T>() {
            @Override public T fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
                return delegate.fromJson(id, json);
            }
            @Override public T fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buffer) {
                return delegate.fromNetwork(id, buffer);
            }
            @Override public void toNetwork(net.minecraft.network.FriendlyByteBuf buffer, T recipe) {
                delegate.toNetwork(buffer, recipe);
            }
        };
    }

    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity>
    net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityTypeForBlocks(
            BlockEntityFactory<T> factory, net.minecraft.world.level.block.Block... blocks) {
        var result = new java.util.concurrent.atomic.AtomicReference<net.minecraft.world.level.block.entity.BlockEntityType<T>>();
        wrapRegistryAction(() -> result.set(net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder
                .<T>create(factory::create, blocks).build()));
        return result.get();
    }

    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity>
    net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory,
            java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> valid) {
        var blocks = ((com.kingodogo.buildscape.registry.FabricRegistryAdapter)Services.REGISTRY).getModBlocks().values().stream()
                .filter(block -> valid.test(block.defaultBlockState())).toArray(net.minecraft.world.level.block.Block[]::new);
        return createBlockEntityTypeForBlocks(factory, blocks);
    }

    @Override public net.minecraft.world.level.material.Fluid getBucketFluid(net.minecraft.world.item.Item item) {
        return item instanceof net.minecraft.world.item.BucketItem
                ? ((com.kingodogo.buildscape.mixin.fabric.BucketFluidAccessor)item).buildscape$getFluid()
                : net.minecraft.world.level.material.Fluids.EMPTY;
    }

    @Override protected void registerFlowerPotPlants() {
        var pots = com.kingodogo.buildscape.mixin.fabric.FlowerPotPlantsAccessor.buildscape$getPottedByContent();
        var blocks = ((com.kingodogo.buildscape.registry.FabricRegistryAdapter)Services.REGISTRY).getModBlocks();
        wrapRegistryAction(() -> blocks.forEach((path, block) -> {
            if (path.startsWith("potted_") && block instanceof net.minecraft.world.level.block.FlowerPotBlock) {
                var plant = blocks.get(path.substring("potted_".length()));
                if (plant != null) pots.put(plant, block);
            }
        }));
    }

    @Override public void registerWorldGen() {
        if (worldGenPrepared) return;
        wrapRegistryAction(super::registerWorldGen);
        worldGenPrepared = true;
    }

    @Override public void registerCommonLifecycleInteractions() {
        super.registerCommonLifecycleInteractions();
        if (worldGenPrepared && !worldGenRegistered) {
            wrapRegistryAction(() -> com.kingodogo.buildscape.worldgen.LoaderWorldGen.register(
                    (com.kingodogo.buildscape.registry.FabricRegistryAdapter)Services.REGISTRY));
            com.kingodogo.buildscape.worldgen.ModBiomeModifications.register();
            worldGenRegistered = true;
        }
    }

    @Override public net.minecraft.nbt.CompoundTag getEntityData(net.minecraft.world.entity.Entity entity) {
        return ((com.kingodogo.buildscape.data.FabricPersistentData)entity).buildscape$getPersistentData();
    }
    @Override public net.minecraft.nbt.CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity entity) {
        return ((com.kingodogo.buildscape.data.FabricPersistentData)entity).buildscape$getPersistentData();
    }
    @Override public net.minecraft.world.item.Item createExperienceBucketItem(net.minecraft.world.item.Item.Properties properties) {
        return new com.kingodogo.buildscape.item.FabricExperienceBucketItem(
                () -> com.kingodogo.buildscape.fluid.FabricExperienceFluid.STILL, properties);
    }
    @Override public void registerConfiguredFeature(CommonId id, Object value) {
        var registry = net.minecraft.data.BuiltinRegistries.CONFIGURED_FEATURE;
        var key = new ResourceLocation(id.getNamespace(), id.getPath());
        wrapRegistryAction(() -> {
            if (!registry.containsKey(key)) Registry.register(registry, key,
                    (net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?>)value);
        });
    }
    @Override public String getPlatformName() { return "Fabric 1.18.2"; }
    @Override public boolean isModLoaded(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }
    @Override public boolean isClient() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }
    @Override public java.nio.file.Path getConfigDir() { return FabricLoader.getInstance().getConfigDir(); }

    @Override public Object registerBooleanRule(ModGameRules.Definition definition) {
        return GameRuleRegistry.register(definition.name(), GameRules.Category.valueOf(definition.category()),
                GameRuleFactory.createBooleanRule(definition.defaultValue(),
                        (server, value) -> ModGameRules.syncIfChanged(server.overworld())));
    }
    @Override public Level currentServerRuleLevel() {
        var server = PacketFactory.getServer();
        return server != null && server.isSameThread() ? server.overworld() : null;
    }
    @Override public <V> void register(Registry<V> registry, CommonId id, V value) {
        wrapRegistryAction(() -> Registry.register(registry, new ResourceLocation(id.getNamespace(), id.getPath()), value));
    }
    @Override public void registerParticleType(CommonId id, net.minecraft.core.particles.ParticleType<?> type) {
        register(Registry.PARTICLE_TYPE, id, type);
    }
    @Override public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        var key = new ResourceLocation(registryName);
        var value = new ResourceLocation(statId.getNamespace(), statId.getPath());
        wrapRegistryAction(() -> {
            if (!Registry.CUSTOM_STAT.containsKey(key)) Registry.register(Registry.CUSTOM_STAT, key, value);
        });
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }

    private static final java.util.Map<String, String> BLOCK_ALIASES = new java.util.HashMap<>();
    private static final java.util.Map<String, String> ITEM_ALIASES = new java.util.HashMap<>();
    static {
        for (var alias : com.kingodogo.buildscape.registry.VanillaReplacementAliases.ALL) {
            if (alias.block()) BLOCK_ALIASES.put(alias.target(), alias.source());
            if (alias.item()) ITEM_ALIASES.put(alias.target(), alias.source());
        }
    }
    @Override public net.minecraft.world.level.block.Block getBlock(CommonId id) {
        var value = super.getBlock(id);
        if (id == null || !"minecraft".equals(id.getNamespace()) || value != net.minecraft.world.level.block.Blocks.AIR) return value;
        return super.getBlock(CommonId.parse(BLOCK_ALIASES.getOrDefault(id.toString(), "buildscape:" + id.getPath())));
    }
    @Override public net.minecraft.world.item.Item getItem(CommonId id) {
        var value = super.getItem(id);
        if (id == null || !"minecraft".equals(id.getNamespace()) || value != net.minecraft.world.item.Items.AIR) return value;
        return super.getItem(CommonId.parse(ITEM_ALIASES.getOrDefault(id.toString(), "buildscape:" + id.getPath())));
    }
    @Override protected void registerCauldronInteractions() {
        super.registerCauldronInteractions();
        var interactions = com.kingodogo.buildscape.adapter.v118x.ExperienceCauldronInteractions.INTERACTIONS;
        var cauldron = getBlock(new CommonId("buildscape", "experience_cauldron"));
        var xpBucket = getItem(new CommonId("buildscape", "experience_bucket"));
        interactions.put(net.minecraft.world.item.Items.BUCKET, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL) != 3) return net.minecraft.world.InteractionResult.PASS;
            if (!level.isClientSide) {
                player.awardStat(net.minecraft.stats.Stats.USE_CAULDRON);
                level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.CAULDRON.defaultBlockState());
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_FILL, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
                exchange(player, hand, stack, new net.minecraft.world.item.ItemStack(xpBucket));
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        });
        interactions.put(net.minecraft.world.item.Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide) {
                player.awardStat(net.minecraft.stats.Stats.USE_CAULDRON);
                int amount = state.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL);
                level.setBlockAndUpdate(pos, amount > 1 ? state.setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, amount - 1)
                        : net.minecraft.world.level.block.Blocks.CAULDRON.defaultBlockState());
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_FILL, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
                exchange(player, hand, stack, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.EXPERIENCE_BOTTLE));
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        });
        interactions.put(xpBucket, (state, level, pos, player, hand, stack) -> {
            if (state.getValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL) >= 3) return net.minecraft.world.InteractionResult.PASS;
            if (!level.isClientSide) {
                player.awardStat(net.minecraft.stats.Stats.FILL_CAULDRON);
                level.setBlockAndUpdate(pos, cauldron.defaultBlockState().setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3));
                level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
                exchange(player, hand, stack, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
        });
    }
    private static void exchange(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
                                 net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.ItemStack result) {
        if (player.getAbilities().instabuild) return;
        stack.shrink(1);
        if (stack.isEmpty()) player.setItemInHand(hand, result);
        else if (!player.getInventory().add(result)) player.drop(result, false);
    }


}

