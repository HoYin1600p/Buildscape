package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v118x.PlatformAdapterBase;
import com.kingodogo.buildscape.registry.ForgeRegistryAdapter;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

public final class ForgePlatformAdapter extends PlatformAdapterBase implements com.kingodogo.buildscape.world.GameRuleAccess {
    @Override public String getPlatformName() { return "Forge 1.18.2"; }
    @Override public boolean isModLoaded(String modId) { return ModList.get().isLoaded(modId); }
    @Override public boolean isClient() { return FMLEnvironment.dist.isClient(); }
    @Override public java.nio.file.Path getConfigDir() { return FMLPaths.CONFIGDIR.get(); }
    @Override public CompoundTag getEntityData(Entity entity) { return entity.getPersistentData(); }
    @Override public CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity entity) {
        return entity.getTileData();
    }

    /** Forge 1.18.2 patches RecipeSerializer to extend IForgeRegistryEntry; wrap vanilla-compiled serializers. */
    @Override public <T extends net.minecraft.world.item.crafting.Recipe<?>> net.minecraft.world.item.crafting.RecipeSerializer<T>
    createRecipeSerializer(com.kingodogo.buildscape.adapter.v118x.RecipeFactory.SerializerLogic<T> delegate) {
        return new ForgeRecipeSerializer<>(delegate);
    }

    private static final class ForgeRecipeSerializer<T extends net.minecraft.world.item.crafting.Recipe<?>>
            extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.world.item.crafting.RecipeSerializer<?>>
            implements net.minecraft.world.item.crafting.RecipeSerializer<T> {
        private final com.kingodogo.buildscape.adapter.v118x.RecipeFactory.SerializerLogic<T> delegate;
        ForgeRecipeSerializer(com.kingodogo.buildscape.adapter.v118x.RecipeFactory.SerializerLogic<T> delegate) { this.delegate = delegate; }
        @Override public T fromJson(ResourceLocation id, com.google.gson.JsonObject json) { return delegate.fromJson(id, json); }
        @Override public T fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buffer) { return delegate.fromNetwork(id, buffer); }
        @Override public void toNetwork(net.minecraft.network.FriendlyByteBuf buffer, T recipe) { delegate.toNetwork(buffer, recipe); }
    }

    @Override public net.minecraft.world.level.Level currentServerRuleLevel() {
        var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
        return server != null && server.isSameThread() ? server.overworld() : null;
    }

    @Override @SuppressWarnings("unchecked")
    public Object registerBooleanRule(com.kingodogo.buildscape.world.ModGameRules.Definition definition) {
        try {
            var create = net.minecraftforge.fml.util.ObfuscationReflectionHelper.findMethod(
                    net.minecraft.world.level.GameRules.BooleanValue.class, "m_46252_", boolean.class,
                    java.util.function.BiConsumer.class);
            java.util.function.BiConsumer<net.minecraft.server.MinecraftServer, net.minecraft.world.level.GameRules.BooleanValue> changed =
                    (server, value) -> { if (server != null) com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld()); };
            var type = (net.minecraft.world.level.GameRules.Type<net.minecraft.world.level.GameRules.BooleanValue>)
                    create.invoke(null, definition.defaultValue(), changed);
            return net.minecraft.world.level.GameRules.register(definition.name(),
                    net.minecraft.world.level.GameRules.Category.valueOf(definition.category()), type);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot register rule " + definition.name(), exception);
        }
    }

    /**
     * Neutral code names content that 26.2 took from vanilla (minecraft:pink_petals). On 1.18.2 it
     * is still the mod's own content, so a missing minecraft id maps back through the shared alias table.
     */
    private static final java.util.Map<String, String> VANILLA_TO_MOD_BLOCK = new java.util.HashMap<>();
    private static final java.util.Map<String, String> VANILLA_TO_MOD_ITEM = new java.util.HashMap<>();
    static {
        for (var alias : com.kingodogo.buildscape.registry.VanillaReplacementAliases.ALL) {
            if (alias.block()) VANILLA_TO_MOD_BLOCK.put(alias.target(), alias.source());
            if (alias.item()) VANILLA_TO_MOD_ITEM.put(alias.target(), alias.source());
        }
    }

    @Override public net.minecraft.world.level.block.Block getBlock(CommonId id) {
        var block = super.getBlock(id);
        if (id == null || !"minecraft".equals(id.getNamespace()) || block != net.minecraft.world.level.block.Blocks.AIR) return block;
        String mapped = VANILLA_TO_MOD_BLOCK.get(id.toString());
        return super.getBlock(mapped != null ? CommonId.parse(mapped) : new CommonId("buildscape", id.getPath()));
    }
    @Override public net.minecraft.world.item.Item getItem(CommonId id) {
        var item = super.getItem(id);
        if (id == null || !"minecraft".equals(id.getNamespace()) || item != net.minecraft.world.item.Items.AIR) return item;
        String mapped = VANILLA_TO_MOD_ITEM.get(id.toString());
        return super.getItem(mapped != null ? CommonId.parse(mapped) : new CommonId("buildscape", id.getPath()));
    }
    @Override public net.minecraft.world.item.Item createExperienceBucketItem(net.minecraft.world.item.Item.Properties properties) {
        return new com.kingodogo.buildscape.item.ForgeExperienceBucketItem(
                com.kingodogo.buildscape.fluid.ForgeExperienceFluids.EXPERIENCE_STILL, properties);
    }
    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity>
    net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityTypeForBlocks(BlockEntityFactory<T> factory,
                                                                                          net.minecraft.world.level.block.Block... blocks) {
        return net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }
    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity>
    net.minecraft.world.level.block.entity.BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory,
            java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> valid) {
        var blocks = ((ForgeRegistryAdapter) Services.REGISTRY).getModBlocks().values().stream()
                .filter(block -> valid.test(block.defaultBlockState())).toArray(net.minecraft.world.level.block.Block[]::new);
        return createBlockEntityTypeForBlocks(factory, blocks);
    }
    @Override public net.minecraft.world.level.material.Fluid getBucketFluid(net.minecraft.world.item.Item item) {
        return item instanceof net.minecraft.world.item.BucketItem bucket ? bucket.getFluid() : net.minecraft.world.level.material.Fluids.EMPTY;
    }

    @Override protected void registerFlowerPotPlants() {
        var pot = (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
        var blocks = ((ForgeRegistryAdapter) Services.REGISTRY).getModBlocks();
        blocks.forEach((path, block) -> {
            if (path.startsWith("potted_")) {
                String plantPath = path.substring("potted_".length());
                if (blocks.containsKey(plantPath)) pot.addPlant(new ResourceLocation("buildscape", plantPath), () -> block);
            }
        });
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

    @Override public void registerWorldGen() {
        super.registerWorldGen();
        com.kingodogo.buildscape.worldgen.LoaderWorldGen.register((ForgeRegistryAdapter) Services.REGISTRY);
    }
    @Override public void registerConfiguredFeature(CommonId id, Object value) {
        if (!com.kingodogo.buildscape.worldgen.LoaderWorldGen.contains(id)) {
            throw new IllegalStateException("Unscheduled configured feature: " + id);
        }
    }

    @Override
    public <V> void register(Registry<V> registry, CommonId id, V value) {
        ((ForgeRegistryAdapter) Services.REGISTRY).enqueue(registry, id, value);
    }

    @Override
    public void registerParticleType(CommonId id, ParticleType<?> type) {
        register(Registry.PARTICLE_TYPE, id, type);
    }

    @Override
    public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        // CUSTOM_STAT is a vanilla registry, not an IForgeRegistryEntry registry.
        // Main registers the key in the minecraft namespace; player stats files store that key.
        ResourceLocation value = new ResourceLocation(statId.getNamespace(), statId.getPath());
        ResourceLocation key = new ResourceLocation(registryName);
        wrapRegistryAction(() -> {
            if (!Registry.CUSTOM_STAT.containsKey(key)) Registry.register(Registry.CUSTOM_STAT, key, value);
        });
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }

    @Override
    public void wrapRegistryAction(Runnable action) {
        // Forge owns intrusive holders and registry thawing. Keep its maps intact,
        // including during nested construction of blocks, items, BEs, entities and fluids.
        action.run();
    }
}
