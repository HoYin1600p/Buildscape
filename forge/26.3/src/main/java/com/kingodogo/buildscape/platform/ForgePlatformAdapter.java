package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase;

public class ForgePlatformAdapter extends PlatformAdapterBase {
    @Override public boolean canTreeChopBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        return net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(level, player.gameMode.getGameModeForPlayer(), player, pos) >= 0;
    }

    @Override public boolean canRotateBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        boolean canceled = net.minecraftforge.event.ForgeEventFactory.onBlockPlace(player,
                net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, pos), net.minecraft.core.Direction.UP);
        if (canceled) player.connection.send(new net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket(pos, level.getBlockState(pos)));
        return !canceled;
    }


    @Override
    public <V> void register(net.minecraft.core.Registry<V> registry,
                             com.kingodogo.buildscape.util.CommonId id, V value) {
        ((com.kingodogo.buildscape.registry.ForgeRegistryAdapter)
                com.kingodogo.buildscape.platform.Services.REGISTRY).enqueue(registry, id, value);
    }

    @Override
    public String getPlatformName() {
        return "Forge 26.3";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return net.minecraftforge.fml.ModList.isLoaded(modId);
    }

    @Override
    public java.nio.file.Path getGameDir() {
        return net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get();
    }
    @Override
    public java.nio.file.Path getConfigDir() {
        return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient();
    }
}

