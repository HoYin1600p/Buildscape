package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
public final class EyeblossomTransitionHandler {

    private static final Map<ServerLevel, Tracker> TRACKERS = new IdentityHashMap<>();

    private EyeblossomTransitionHandler() {}

    public static boolean shouldTransition(boolean currentlyOpen, boolean waxed, boolean night) {
        return !waxed && currentlyOpen != night;
    }

    public static void track(ServerLevel level, BlockPos pos) {
        tracker(level).positionsByChunk
                .computeIfAbsent(chunkKey(pos), ignored -> new HashSet<>())
                .add(pos.asLong());
    }

    public static void untrack(ServerLevel level, BlockPos pos) {
        Tracker tracker = TRACKERS.get(level);
        if (tracker == null) {
            return;
        }

        long chunkKey = chunkKey(pos);
        Set<Long> positions = tracker.positionsByChunk.get(chunkKey);
        if (positions != null) {
            positions.remove(pos.asLong());
            if (positions.isEmpty()) {
                tracker.positionsByChunk.remove(chunkKey);
            }
        }
    }
    public static void onWorldTick(ServerLevel level) {
        Tracker tracker = TRACKERS.get(level);
        if (tracker == null) {
            return;
        }

        boolean night = com.kingodogo.buildscape.platform.Services.PLATFORM.isNight(level);
        if (tracker.night == night) {
            return;
        }
        tracker.night = night;

        for (Set<Long> positions : tracker.positionsByChunk.values()) {
            Iterator<Long> iterator = positions.iterator();
            while (iterator.hasNext()) {
                BlockPos pos = BlockPos.of(iterator.next());
                if (!level.hasChunkAt(pos)) {
                    continue;
                }

                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof EyeblossomBlock eyeblossom) {
                    eyeblossom.synchronizeWithTime(level, pos, state, true);
                } else {
                    iterator.remove();
                }
            }
        }
        tracker.positionsByChunk.values().removeIf(Set::isEmpty);
    }
    public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
        Tracker existingTracker = TRACKERS.get(level);
        long chunkKey = Services.PLATFORM.packChunkPos(chunk.getPos());
        if (existingTracker != null) {
            existingTracker.positionsByChunk.remove(chunkKey);
        }

        Services.PLATFORM.scanChunkForBlock(level, chunk, state -> state.getBlock() instanceof EyeblossomBlock, (pos, state) -> {
            track(level, pos);
            level.scheduleTick(pos, state.getBlock(), 1);
        });
    }
    public static void onChunkUnload(ServerLevel level, ChunkPos pos) {
        Tracker tracker = TRACKERS.get(level);
        if (tracker != null) {
            tracker.positionsByChunk.remove(com.kingodogo.buildscape.platform.Services.PLATFORM.packChunkPos(pos));
        }
    }
    public static void onWorldUnload(ServerLevel level) {
        TRACKERS.remove(level);
    }

    public static void onServerStopping() {
        TRACKERS.clear();
    }

    private static Tracker tracker(ServerLevel level) {
        return TRACKERS.computeIfAbsent(level, ignored -> new Tracker(com.kingodogo.buildscape.platform.Services.PLATFORM.isNight(level)));
    }

    private static long chunkKey(BlockPos pos) {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.packChunkPos(pos);
    }

    private static final class Tracker {
        private final Map<Long, Set<Long>> positionsByChunk = new HashMap<>();
        private boolean night;

        private Tracker(boolean night) {
            this.night = night;
        }
    }
}
