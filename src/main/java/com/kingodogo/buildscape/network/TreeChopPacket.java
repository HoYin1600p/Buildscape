package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.TreeChopTraversal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class TreeChopPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "tree_chop");
    private static final int MAX_LOGS = 200;

    private final BlockPos pos;

    public TreeChopPacket(BlockPos pos) {
        this.pos = pos;
    }

    public TreeChopPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    public static TreeChopPacket decode(FriendlyByteBuf buffer) {
        return new TreeChopPacket(buffer);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (!serverPlayer.isCreative() || !com.kingodogo.buildscape.platform.Services.PLATFORM.hasPermission(serverPlayer, 2) || !serverPlayer.mayBuild()) {
            return;
        }

        ServerLevel level = (ServerLevel) com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityLevel(serverPlayer);
        if (level == null || level.isClientSide()) return;

        if (!level.isLoaded(pos)
                || serverPlayer.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64.0D
                || serverPlayer.blockActionRestricted(level, pos, serverPlayer.gameMode.getGameModeForPlayer())) {
            return;
        }

        BlockState startState = level.getBlockState(pos);
        if (!isLog(startState)) {
            return;
        }

        Set<BlockPos> connected = TreeChopTraversal.findConnectedLogs(level, pos, startState.getBlock(), MAX_LOGS);
        if (!connected.isEmpty()) {
            TreeChopJobManager.start(serverPlayer, level, startState.getBlock(), connected);
        }
    }

    private static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }
}
