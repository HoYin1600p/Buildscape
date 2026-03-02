package com.kingodogo.buildscape.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Supplier;

public class TreeChopPacket {

    private static final int MAX_LOGS = 200; // Limit to prevent server lag
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

            if (!player.isCreative()) {
                return;
            }

            Level level = player.getLevel();
            if (level.isClientSide) {
                return;
            }

            if (!level.getGameRules().getBoolean(com.kingodogo.buildscape.world.ModGameRules.CREATIVE_TREE_BREAKER)) {
                return;
            }

            if (!level.isLoaded(pos)) {
                return;
            }

            BlockState startState = level.getBlockState(pos);
            if (!isLog(startState)) {
                return;
            }

            chopTree((net.minecraft.server.level.ServerLevel) level, pos, startState.getBlock());
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
            net.minecraft.server.level.ServerLevel level,
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

        // BFS to find all blocks
        while (!queue.isEmpty() && count < MAX_LOGS) {
            BlockPos current = queue.poll();

            // Neighbors
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

        // Start the sequential breaking with a delay
        scheduleNextBreak(level, orderedBlocks, 0, 1, 0);
    }

    private void scheduleNextBreak(
            net.minecraft.server.level.ServerLevel level,
            java.util.List<BlockPos> blocks,
            int currentIndex,
            int batchSize,
            int consecutiveTicks
    ) {
        if (currentIndex >= blocks.size()) {
            return;
        }

        // Run this batch
        int end = Math.min(currentIndex + batchSize, blocks.size());
        for (int i = currentIndex; i < end; i++) {
            BlockPos pos = blocks.get(i);
            BlockState state = level.getBlockState(pos);

            // Only spawn particles for every 3rd block to reduce spam
            if (i % 3 == 0) {
                level.levelEvent(2001, pos, Block.getId(state));
            }
            level.destroyBlock(pos, false);
        }

        // Schedule next batch with a proper delay (2 ticks = 100ms)
        // Incremental speed: Increase batch size every 3 ticks
        int nextBatchSize = batchSize;
        int nextConsecutiveTicks = consecutiveTicks + 1;

        if (nextConsecutiveTicks % 3 == 0 && batchSize < 8) {
            nextBatchSize++;
        }

        int finalNextBatchSize = nextBatchSize;
        int nextIndex = end;

        // Schedule next batch with a 100ms (~2 ticks) delay without blocking the server thread
        java.util.concurrent.CompletableFuture.delayedExecutor(100, java.util.concurrent.TimeUnit.MILLISECONDS).execute(() -> {
            level.getServer().execute(() -> {
                scheduleNextBreak(level, blocks, nextIndex, finalNextBatchSize, nextConsecutiveTicks);
            });
        });
    }
}
