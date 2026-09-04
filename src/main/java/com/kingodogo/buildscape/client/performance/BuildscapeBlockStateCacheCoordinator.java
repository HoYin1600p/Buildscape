package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BuildscapeBlockStateCacheCoordinator {
    private static final List<BlockState> PENDING_STATES = new ArrayList<>();
    private static volatile Set<Block> buildscapeBlocks = Set.of();
    private static volatile boolean collecting;

    private BuildscapeBlockStateCacheCoordinator() {
    }

    public static synchronized void begin() {
        PENDING_STATES.clear();
        Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());
        ModBlocks.BLOCKS.getEntries().forEach(entry -> blocks.add(entry.get()));
        buildscapeBlocks = blocks;
        collecting = true;
    }

    public static boolean isCollecting() {
        return collecting;
    }

    public static boolean deferIfBuildscape(BlockState state) {
        if (!collecting || !buildscapeBlocks.contains(state.getBlock())) {
            return false;
        }

        PENDING_STATES.add(state);
        return true;
    }

    public static void finish() {
        List<BlockState> states;
        synchronized (BuildscapeBlockStateCacheCoordinator.class) {
            collecting = false;
            buildscapeBlocks = Set.of();
            states = List.copyOf(PENDING_STATES);
            PENDING_STATES.clear();
        }

        if (states.isEmpty()) {
            return;
        }

        long startedAt = System.nanoTime();
        ConcurrentLinkedQueue<BlockState> retrySequentially = new ConcurrentLinkedQueue<>();

        try {
            BuildscapeStartupWork.forEachIndex(states.size(), index -> {
                BlockState state = states.get(index);
                try {
                    state.initCache();
                } catch (RuntimeException exception) {
                    retrySequentially.add(state);
                }
            });
        } catch (RuntimeException exception) {
            BuildScape.LOGGER.warn(
                    "Buildscape parallel block-state cache setup failed; retrying sequentially",
                    exception
            );
            retrySequentially.clear();
            retrySequentially.addAll(states);
        }

        for (BlockState state : retrySequentially) {
            state.initCache();
        }

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        BuildScape.LOGGER.info(
                "Buildscape startup initialized {} block-state caches in parallel ({} ms, {} sequential retries)",
                states.size(),
                elapsedMillis,
                retrySequentially.size()
        );
    }
}
