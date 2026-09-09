package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EyeblossomTransitionHandler {
    private static final Map<ServerLevel, Tracker> TRACKERS = new IdentityHashMap<>();

    private EyeblossomTransitionHandler() {
    }

    static boolean shouldTransition(boolean currentlyOpen, boolean waxed, boolean night) {
        return !waxed && currentlyOpen != night;
    }

    static void track(ServerLevel level, BlockPos pos) {
        tracker(level).positionsByChunk
                .computeIfAbsent(chunkKey(pos), ignored -> new HashSet<>())
                .add(pos.asLong());
    }

    static void untrack(ServerLevel level, BlockPos pos) {
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

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerLevel level)) {
            return;
        }

        Tracker tracker = TRACKERS.get(level);
        if (tracker == null) {
            return;
        }

        boolean night = level.isNight();
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

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        Tracker existingTracker = TRACKERS.get(level);
        long chunkKey = chunk.getPos().toLong();
        if (existingTracker != null) {
            existingTracker.positionsByChunk.remove(chunkKey);
        }

        LevelChunkSection[] sections = chunk.getSections();
        for (LevelChunkSection section : sections) {
            if (section.hasOnlyAir() || !section.maybeHas(state -> state.getBlock() instanceof EyeblossomBlock)) {
                continue;
            }

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (!(state.getBlock() instanceof EyeblossomBlock)) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(
                                chunk.getPos().getMinBlockX() + x,
                                section.bottomBlockY() + y,
                                chunk.getPos().getMinBlockZ() + z
                        );
                        track(level, pos);
                        level.scheduleTick(pos, state.getBlock(), 1);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld() instanceof ServerLevel level) {
            Tracker tracker = TRACKERS.get(level);
            if (tracker != null) {
                tracker.positionsByChunk.remove(event.getChunk().getPos().toLong());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof ServerLevel level) {
            TRACKERS.remove(level);
        }
    }

    private static Tracker tracker(ServerLevel level) {
        return TRACKERS.computeIfAbsent(level, ignored -> new Tracker(level.isNight()));
    }

    private static long chunkKey(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static final class Tracker {
        private final Map<Long, Set<Long>> positionsByChunk = new HashMap<>();
        private boolean night;

        private Tracker(boolean night) {
            this.night = night;
        }
    }
}
