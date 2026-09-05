package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.TreeChopTraversal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class TreeChopPacket {

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

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            if (!player.isCreative() || !player.hasPermissions(2) || !player.mayBuild()) {
                return;
            }

            ServerLevel level = player.getLevel();
            if (level.isClientSide) {
                return;
            }

            if (!level.isLoaded(pos)
                    || player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 64.0D
                    || player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) {
                return;
            }

            BlockState startState = level.getBlockState(pos);
            if (!isLog(startState)) {
                return;
            }

            Set<BlockPos> blocks = TreeChopTraversal.collect(
                    pos,
                    candidate -> level.isLoaded(candidate)
                            && level.getBlockState(candidate).getBlock() == startState.getBlock(),
                    MAX_LOGS);
            TreeChopJobManager.start(player, level, startState.getBlock(), blocks);
        });
        context.setPacketHandled(true);
    }

    private boolean isLog(BlockState state) {
        return (
                state.is(BlockTags.LOGS) ||
                        state.is(BlockTags.WARPED_STEMS) ||
                        state.is(BlockTags.CRIMSON_STEMS)
        );
    }

}
