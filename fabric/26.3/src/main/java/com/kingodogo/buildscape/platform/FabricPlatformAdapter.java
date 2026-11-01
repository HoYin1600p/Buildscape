package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformAdapter extends PlatformAdapterBase {
    @Override public int beforeHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState replacement) {
        return canPlayerBreakBlock(player, pos) ? 0 : -1;
    }

    @Override public void afterHammerReplacement(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos,
            net.minecraft.world.level.block.state.BlockState oldState, net.minecraft.world.item.ItemStack hammer,
            com.kingodogo.buildscape.item.HammerItem.HammerTier tier) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) Services.PLATFORM.getEntityLevel(player);
        if (player.isCreative() || !level.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.BLOCK_DROPS)) return;
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
    public java.nio.file.Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }
    @Override
    public java.nio.file.Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getPlatformName() {
        return "Fabric 26.3";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
