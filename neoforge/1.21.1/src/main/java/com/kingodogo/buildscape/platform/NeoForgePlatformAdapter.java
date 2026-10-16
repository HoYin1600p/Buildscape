package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase;
import com.kingodogo.buildscape.registry.NeoForgeRegistryAdapter;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.feature.stateproviders.*;
import net.minecraft.world.level.levelgen.feature.treedecorators.*;
import net.minecraft.world.level.levelgen.feature.foliageplacers.*;
import net.minecraft.world.level.levelgen.feature.trunkplacers.*;

public final class NeoForgePlatformAdapter extends PlatformAdapterBase implements com.kingodogo.buildscape.world.GameRuleAccess {
    @Override public String getPlatformName() { return "NeoForge 1.21.1"; }
    @Override public boolean isModLoaded(String id) { return net.neoforged.fml.ModList.get().isLoaded(id); }
    @Override public boolean isClient() { return net.neoforged.fml.loading.FMLEnvironment.dist.isClient(); }
    @Override public java.nio.file.Path getGameDir() { return net.neoforged.fml.loading.FMLPaths.GAMEDIR.get(); }
    @Override public java.nio.file.Path getConfigDir() { return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get(); }
    @Override public <V> void register(Registry<V> registry, CommonId id, V value) {
        ((NeoForgeRegistryAdapter) Services.REGISTRY).enqueue(registry, id, value);
    }
    @Override public void wrapRegistryAction(Runnable action) {
        // NeoForge owns registry thawing and intrusive holders. Preserve its maps unchanged,
        // including when construction nests inside an enclosing registry action.
        action.run();
    }
    @Override public net.minecraft.world.level.material.Fluid getBucketFluid(net.minecraft.world.item.Item item) {
        return item instanceof net.minecraft.world.item.BucketItem bucket ? bucket.content : net.minecraft.world.level.material.Fluids.EMPTY;
    }
    @Override public net.minecraft.world.entity.EquipmentSlot getEquipmentSlot(net.minecraft.world.item.ItemStack stack) {
        var slot = stack.getEquipmentSlot();
        if (slot != null) return slot;
        if (stack.getItem() instanceof net.minecraft.world.item.Equipable equipable) return equipable.getEquipmentSlot();
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof net.minecraft.world.item.Equipable equipable) return equipable.getEquipmentSlot();
        return stack.is(net.minecraft.world.item.Items.SHIELD)
                ? net.minecraft.world.entity.EquipmentSlot.OFFHAND : net.minecraft.world.entity.EquipmentSlot.MAINHAND;
    }
    @Override public net.minecraft.nbt.CompoundTag getEntityData(net.minecraft.world.entity.Entity entity) { return entity.getPersistentData(); }
    @Override public net.minecraft.nbt.CompoundTag getBlockEntityData(net.minecraft.world.level.block.entity.BlockEntity entity) { return entity.getPersistentData(); }
    @Override public <T extends net.minecraft.world.inventory.AbstractContainerMenu> net.minecraft.world.inventory.MenuType<T>
    createMenuType(com.kingodogo.buildscape.menu.MenuTypeBridge.MenuFactory<T> factory) {
        return net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id, inventory, buffer) -> factory.create(id, inventory));
    }
    @Override public <T extends net.minecraft.world.level.block.entity.BlockEntity> net.minecraft.world.level.block.entity.BlockEntityType<T>
    createBlockEntityType(BlockEntityFactory<T> factory, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> valid) {
        var blocks = ((NeoForgeRegistryAdapter) Services.REGISTRY).getModBlocks().values().stream()
                .filter(block -> valid.test(block.defaultBlockState())).toArray(net.minecraft.world.level.block.Block[]::new);
        return net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(factory::create, blocks).build(null);
    }
    @Override public <T extends BlockStateProvider> BlockStateProviderType<T> createBlockStateProviderType(MapCodec<T> codec) { return new BlockStateProviderType<>(codec); }
    @Override public <T extends TreeDecorator> TreeDecoratorType<T> createTreeDecoratorType(MapCodec<T> codec) { return new TreeDecoratorType<>(codec); }
    @Override public <T extends FoliagePlacer> FoliagePlacerType<T> createFoliagePlacerType(MapCodec<T> codec) { return new FoliagePlacerType<>(codec); }
    @Override public <T extends TrunkPlacer> TrunkPlacerType<T> createTrunkPlacerType(MapCodec<T> codec) { return new TrunkPlacerType<>(codec); }
    @Override protected com.mojang.blaze3d.platform.NativeImage getSpriteImage(net.minecraft.client.renderer.texture.SpriteContents contents) {
        return com.kingodogo.buildscape.platform.client.NeoForgeClientAccess.spriteImage(contents);
    }
    @Override public void registerParticleProviders() {
        if (isClient()) com.kingodogo.buildscape.BuildscapeNeoForgeClient.registerParticleProviders();
    }
    @Override public void registerMenuScreens() {
        if (isClient()) {
            com.kingodogo.buildscape.BuildscapeNeoForgeClient.registerMenuScreens();
        }
    }
    @Override public void registerRenderers() {
        if (isClient()) com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.registerRenderers();
    }
    @Override public net.minecraft.world.level.Level currentServerRuleLevel() {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        return server != null && server.isSameThread() ? server.overworld() : null;
    }
    @Override public Object registerBooleanRule(com.kingodogo.buildscape.world.ModGameRules.Definition definition) {
        return GameRules.register(definition.name(), GameRules.Category.valueOf(definition.category()),
                GameRules.BooleanValue.create(definition.defaultValue(), (server, value) -> {
                    if (server != null) com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld());
                }));
    }
}
