package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.PlatformAdapterBase;
import com.kingodogo.buildscape.adapter.PacketFactory;
import com.kingodogo.buildscape.mixin.fabric.RegistryStateAccessor;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.stateproviders.*;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;

public final class FabricPlatformAdapter extends PlatformAdapterBase implements com.kingodogo.buildscape.world.GameRuleAccess {
    @Override public int beforeHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState replacement) {
        return canPlayerBreakBlock(player, pos) ? 0 : -1;
    }

    @Override public void afterHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.item.ItemStack hammer,
            com.kingodogo.buildscape.item.HammerItem.HammerTier tier) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        if (player.isCreative() || !level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS)) return;
        net.minecraft.world.item.ItemStack tool = new net.minecraft.world.item.ItemStack(tier.getHarvestToolItem());
        tool.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, hammer.getEnchantments());
        oldState.spawnAfterBreak(level, pos, tool, true);
    }

    @Override public boolean canPlayerBreakBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
        net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean allowed = net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.invoker()
                .beforeBlockBreak(level, player, pos, state, blockEntity);
        if (!allowed) net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.CANCELED.invoker()
                .onBlockBreakCanceled(level, player, pos, state, blockEntity);
        return allowed;
    }

    @Override public void afterPlayerBlockBreak(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity) {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.AFTER.invoker()
                .afterBlockBreak((net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player), player, pos, state, blockEntity);
    }


    @Override
    public net.minecraft.world.item.BlockItem createDoublePlantBlockItem(net.minecraft.world.level.block.Block block,
                                                                       net.minecraft.world.item.Item.Properties properties) {
        return new net.minecraft.world.item.BlockItem(block, properties) {
            @Override
            protected boolean placeBlock(net.minecraft.world.item.context.BlockPlaceContext context,
                                         net.minecraft.world.level.block.state.BlockState state) {
                // Forge defers shape updates until setPlacedBy installs the upper half.
                // Keep replaced vegetation from removing the lower half before that happens.
                return context.getLevel().setBlock(context.getClickedPos(), state, 11 | 16);
            }
        };
    }

    @Override public String getModVersion() { return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("buildscape").map(mod -> mod.getMetadata().getVersion().getFriendlyString()).orElse(""); }
    @Override public String getPlatformName() { return "Fabric 1.21.1"; }
    @Override public boolean isModLoaded(String id) { return FabricLoader.getInstance().isModLoaded(id); }
    @Override public boolean isClient() { return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT; }
    @Override public java.nio.file.Path getGameDir() { return FabricLoader.getInstance().getGameDir(); }
    @Override public java.nio.file.Path getConfigDir() { return FabricLoader.getInstance().getConfigDir(); }
    @Override public <V> void register(Registry<V> registry, CommonId id, V value) {
        wrapRegistryAction(() -> Registry.register(registry,
                ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()), value));
    }
    @Override public void wrapRegistryAction(Runnable action) {
        var registries = java.util.List.of(BuiltInRegistries.BLOCK, BuiltInRegistries.ITEM,
                BuiltInRegistries.BLOCK_ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE, BuiltInRegistries.FLUID);
        var states = new java.util.ArrayList<RegistryStateAccessor>();
        var prevHolders = new java.util.ArrayList<java.util.Map<?, ?>>();
        var frozen = new java.util.ArrayList<Boolean>();
        // Only intrusive registries receive holder maps. Nested calls retain the outer map.
        for (var registry : registries) {
            var state = (RegistryStateAccessor) registry;
            states.add(state);
            prevHolders.add(state.buildscape$getHolders());
            frozen.add(state.buildscape$isFrozen());
            state.buildscape$setFrozen(false);
            if (state.buildscape$getHolders() == null) state.buildscape$setHolders(new java.util.IdentityHashMap<>());
        }
        try { action.run(); }
        finally {
            for (int i = states.size() - 1; i >= 0; i--) {
                states.get(i).buildscape$setHolders(prevHolders.get(i));
                states.get(i).buildscape$setFrozen(frozen.get(i));
            }
        }
    }
    @Override public net.minecraft.world.level.material.Fluid getBucketFluid(net.minecraft.world.item.Item item) {
        if (item instanceof net.minecraft.world.item.BucketItem bucket) {
            for (var f : net.minecraft.world.item.BucketItem.class.getDeclaredFields()) {
                if (net.minecraft.world.level.material.Fluid.class.isAssignableFrom(f.getType())) {
                    try { f.setAccessible(true); return (net.minecraft.world.level.material.Fluid) f.get(bucket); }
                    catch (Throwable ignored) {}
                }
            }
        }
        return net.minecraft.world.level.material.Fluids.EMPTY;
    }
    @Override public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(net.minecraft.world.item.ItemStack stack) {
        var eq = stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        if (eq != null) return eq.slot();
        return stack.is(net.minecraft.world.item.Items.SHIELD)
                ? net.minecraft.world.entity.EquipmentSlot.OFFHAND : net.minecraft.world.entity.EquipmentSlot.MAINHAND;
    }
    @Override public net.minecraft.nbt.CompoundTag getEntityData(net.minecraft.world.entity.Entity entity) {
        return ((com.kingodogo.buildscape.data.FabricPersistentData) entity).buildscape$getPersistentData();
    }
    @Override public net.minecraft.nbt.CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity entity) {
        return ((com.kingodogo.buildscape.data.FabricPersistentData) entity).buildscape$getPersistentData();
    }
    @Override public <T extends net.minecraft.world.inventory.AbstractContainerMenu> net.minecraft.world.inventory.MenuType<T>
    createMenuType(com.kingodogo.buildscape.menu.MenuTypeBridge.MenuFactory<T> factory) {
        return new net.minecraft.world.inventory.MenuType<>((id, inventory) -> factory.create(id, inventory),
                net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
    }
    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T>
    createBlockEntityType(BlockEntityFactory<T> factory, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> valid) {
        var blocks = ((com.kingodogo.buildscape.registry.FabricRegistryAdapter) Services.REGISTRY).getModBlocks().values().stream()
                .filter(block -> valid.test(block.defaultBlockState())).toArray(net.minecraft.world.level.block.Block[]::new);
        return net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.<T>create(factory::create, blocks).build();
    }
    @Override public <T extends BlockStateProvider> BlockStateProviderType<T> createBlockStateProviderType(MapCodec<T> codec) { return new BlockStateProviderType<>(codec); }
    @Override public <T extends TreeDecorator> TreeDecoratorType<T> createTreeDecoratorType(MapCodec<T> codec) { return new TreeDecoratorType<>(codec); }
    @Override public <T extends FoliagePlacer> FoliagePlacerType<T> createFoliagePlacerType(MapCodec<T> codec) { return new FoliagePlacerType<>(codec); }
    @Override public <T extends TrunkPlacer> TrunkPlacerType<T> createTrunkPlacerType(MapCodec<T> codec) { return new TrunkPlacerType<>(codec); }
    @Override protected com.mojang.blaze3d.platform.NativeImage getSpriteImage(net.minecraft.client.renderer.texture.SpriteContents contents) {
        return com.kingodogo.buildscape.platform.client.FabricClientAccess.spriteImage(contents);
    }
    @Override public void registerParticleProviders() {
        if (isClient()) com.kingodogo.buildscape.platform.client.FabricClientAccess.registerParticleProviders();
    }
    @Override public void registerMenuScreens() {
        if (isClient()) com.kingodogo.buildscape.platform.client.FabricClientAccess.registerMenuScreens();
    }
    @Override public void registerRenderers() {
        if (isClient()) com.kingodogo.buildscape.platform.client.FabricClientAccess.registerRenderers();
    }
    @Override public net.minecraft.world.level.Level currentServerRuleLevel() {
        var server = PacketFactory.getServer();
        return server != null && server.isSameThread() ? server.overworld() : null;
    }
    @Override public Object registerBooleanRule(com.kingodogo.buildscape.world.ModGameRules.Definition definition) {
        return net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry.register(definition.name(),
                net.minecraft.world.level.GameRules.Category.valueOf(definition.category()),
                net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory.createBooleanRule(definition.defaultValue(),
                        (server, value) -> com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld())));
    }
    @Override public net.minecraft.stats.Stat<?> registerCustomStat(String registryName, CommonId statId) {
        var key = ResourceLocation.withDefaultNamespace(registryName);
        var value = ResourceLocation.fromNamespaceAndPath(statId.getNamespace(), statId.getPath());
        if (!BuiltInRegistries.CUSTOM_STAT.containsKey(key)) register(BuiltInRegistries.CUSTOM_STAT,
                new CommonId(key.getNamespace(), key.getPath()), value);
        return net.minecraft.stats.Stats.CUSTOM.get(value, net.minecraft.stats.StatFormatter.DEFAULT);
    }
    @Override public net.minecraft.world.item.CreativeModeTab createCreativeTab(CommonId tabId, String titleKey,
            java.util.function.Supplier<net.minecraft.world.item.ItemStack> iconSupplier,
            java.util.List<String> orderedItemIds, java.util.Map<String, net.minecraft.world.item.Item> items) {
        return net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder()
                .title(net.minecraft.network.chat.Component.translatable(titleKey)).icon(iconSupplier)
                .displayItems((parameters, output) -> {
                    var added = new java.util.HashSet<String>();
                    for (var id : orderedItemIds) {
                        var item = items.get(id);
                        if (item != null && added.add(id)) output.accept(item);
                    }
                    items.forEach((id, item) -> { if (added.add(id)) output.accept(item); });
                }).build();
    }
}
