package com.kingodogo.buildscape.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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

            chopTree(level, player, pos, startState.getBlock());
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

    private void chopTree(
            ServerLevel level,
            ServerPlayer player,
            BlockPos startPos,
            Block targetBlock
    ) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        java.util.List<BlockPos> orderedBlocks = new java.util.ArrayList<>();

        visited.add(startPos);
        queue.add(startPos);
        orderedBlocks.add(startPos);

        int count = 0;

        while (!queue.isEmpty() && count < MAX_LOGS) {
            BlockPos current = queue.poll();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos neighbor = current.offset(dx, dy, dz);
                        if (!visited.contains(neighbor) && level.isLoaded(neighbor)) {
                            BlockState neighborState = level.getBlockState(neighbor);
                            if (neighborState.getBlock() == targetBlock) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                                orderedBlocks.add(neighbor);
                                count++;
                            }
                        }
                    }
                }
            }
        }

        scheduleNextBreak(level, player, targetBlock, orderedBlocks, 0, 1, 0);
    }

    private void scheduleNextBreak(
            ServerLevel level,
            ServerPlayer player,
            Block targetBlock,
            java.util.List<BlockPos> blocks,
            int currentIndex,
            int batchSize,
            int consecutiveTicks
    ) {
        if (currentIndex >= blocks.size()) {
            return;
        }

        int end = Math.min(currentIndex + batchSize, blocks.size());
        for (int i = currentIndex; i < end; i++) {
            BlockPos pos = blocks.get(i);
            if (!level.isLoaded(pos)) continue;
            if (!player.isAlive() || !player.isCreative() || !player.hasPermissions(2) || !player.mayBuild()) return;
            if (player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) continue;
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() != targetBlock) continue;

            if (i % 3 == 0) {
                level.levelEvent(2001, pos, Block.getId(state));
            }
            level.destroyBlock(pos, false);
        }

        int nextBatchSize = batchSize;
        int nextConsecutiveTicks = consecutiveTicks + 1;

        if (nextConsecutiveTicks % 3 == 0 && batchSize < 8) {
            nextBatchSize++;
        }

        int finalNextBatchSize = nextBatchSize;
        int nextIndex = end;

        java.util.concurrent.CompletableFuture.delayedExecutor(100, java.util.concurrent.TimeUnit.MILLISECONDS).execute(() -> {
            if (level.getServer() == null || !level.getServer().isRunning()) return;
            level.getServer().execute(() -> {
                scheduleNextBreak(level, player, targetBlock, blocks, nextIndex, finalNextBatchSize, nextConsecutiveTicks);
            });
        });
    }
}
