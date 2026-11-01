package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase;

public class NeoForgePlatformAdapter extends PlatformAdapterBase {
    @Override public boolean canTreeChopBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
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
    public String getPlatformName() {
        return "NeoForge 26.3";
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
