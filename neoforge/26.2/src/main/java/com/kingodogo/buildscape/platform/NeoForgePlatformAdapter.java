package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;

public class NeoForgePlatformAdapter extends PlatformAdapterBase {
    @Override public int beforeHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState replacement) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        net.minecraft.world.level.block.state.BlockState oldState = level.getBlockState(pos);
        if (net.neoforged.neoforge.common.CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, oldState).isCanceled()) return -1;
        net.neoforged.neoforge.common.util.BlockSnapshot snapshot = net.neoforged.neoforge.common.util.BlockSnapshot.create(level.dimension(), level, pos);
        net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent event = new net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent(snapshot, level.getBlockState(pos.below()), player) {
            @Override public net.minecraft.world.level.block.state.BlockState getState() { return replacement; }
            @Override public net.minecraft.world.level.block.state.BlockState getPlacedBlock() { return replacement; }
        };
        if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled()) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket(pos, oldState));
            return -1;
        }
        net.minecraft.world.item.ItemStack hammer = player.getMainHandItem();
        if (!(hammer.getItem() instanceof com.kingodogo.buildscape.item.HammerItem item)) return 0;
        net.minecraft.world.item.ItemStack tool = new net.minecraft.world.item.ItemStack(item.getHammerTier().getHarvestToolItem());
        tool.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, hammer.getEnchantments());
        return oldState.getExpDrop(level, pos, level.getBlockEntity(pos), player, tool);
    }

    @Override public void awardHammerExperience(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos, int experience) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        if (!player.isCreative() && experience > 0 && level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.BLOCK_DROPS)) {
            net.minecraft.world.entity.ExperienceOrb.award(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), experience);
        }
    }

    @Override public boolean canPlayerBreakBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        return !net.neoforged.neoforge.common.CommonHooks.fireBlockBreak(level,
                player.gameMode.getGameModeForPlayer(), player, pos, level.getBlockState(pos)).isCanceled();
    }

    @Override public boolean canRotateBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        boolean canceled = net.neoforged.neoforge.event.EventHooks.onBlockPlace(player,
                net.neoforged.neoforge.common.util.BlockSnapshot.create(level.dimension(), level, pos), net.minecraft.core.Direction.UP);
        if (canceled) player.connection.send(new net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket(pos, level.getBlockState(pos)));
        return !canceled;
    }


    @Override
    public <V> void register(net.minecraft.core.Registry<V> registry,
                             com.kingodogo.buildscape.util.CommonId id, V value) {
        ((com.kingodogo.buildscape.registry.NeoForgeRegistryAdapter)
                com.kingodogo.buildscape.platform.Services.REGISTRY).enqueue(registry, id, value);
    }

    @Override
    public String getModVersion() { return net.neoforged.fml.ModList.get().getModContainerById("buildscape").map(mod -> mod.getModInfo().getVersion().toString()).orElse(""); }

    @Override
    public String getPlatformName() {
        return "NeoForge 26.2";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return net.neoforged.fml.ModList.get().isLoaded(modId);
    }

    @Override
    public java.nio.file.Path getGameDir() {
        return net.neoforged.fml.loading.FMLPaths.GAMEDIR.get();
    }
    @Override
    public java.nio.file.Path getConfigDir() {
        return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return net.neoforged.fml.loading.FMLEnvironment.getDist().isClient();
    }
}
