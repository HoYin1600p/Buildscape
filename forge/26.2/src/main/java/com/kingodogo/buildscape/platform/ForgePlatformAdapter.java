package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.PlatformAdapterBase;

public class ForgePlatformAdapter extends PlatformAdapterBase {
    @Override public int beforeHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState replacement) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        int experience = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(level, player.gameMode.getGameModeForPlayer(), player, pos);
        if (experience < 0) return -1;
        net.minecraftforge.common.util.BlockSnapshot snapshot = net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, pos);
        net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent event = new net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent(snapshot, level.getBlockState(pos.below()), player);
        ((com.kingodogo.buildscape.mixin.forge.PlacementEventAccessor) (Object) event).buildscape$setPlacedBlock(replacement);
        if (net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent.BUS.post(event)) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket(pos, level.getBlockState(pos)));
            return -1;
        }
        return experience;
    }
    @Override public void awardHammerExperience(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos, int experience) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        if (!player.isCreative() && experience > 0 && level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.BLOCK_DROPS)) {
            net.minecraft.world.entity.ExperienceOrb.award(level, net.minecraft.world.phys.Vec3.atCenterOf(pos), experience);
        }
    }
    @Override public boolean canPlayerBreakBlock(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
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
    public String getModVersion() { return net.minecraftforge.fml.ModList.getModContainerById("buildscape").map(mod -> mod.getModInfo().getVersion().toString()).orElse(""); }

    @Override
    public String getPlatformName() {
        return "Forge 26.2";
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

