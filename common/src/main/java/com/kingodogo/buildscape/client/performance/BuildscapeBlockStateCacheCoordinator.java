package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BuildscapeBlockStateCacheCoordinator {
    private static final List<BlockState> PENDING_STATES = new ArrayList<>();
    private static volatile boolean collecting;

    private BuildscapeBlockStateCacheCoordinator() {
    }

    public static synchronized void begin() {
        PENDING_STATES.clear();
        collecting = true;
    }

    public static boolean isCollecting() {
        return collecting;
    }

    public static boolean deferIfBuildscape(BlockState state) {
        if (!collecting || state == null || state.getBlock() == null) {
            return false;
        }

        try {
            CommonId id = Services.PLATFORM.getBlockId(state.getBlock());
            if (id == null || !"buildscape".equals(id.getNamespace())) {
                return false;
            }
        } catch (Throwable t) {
            return false;
        }

        PENDING_STATES.add(state);
        return true;
    }

    public static void finish() {
        List<BlockState> states;
        synchronized (BuildscapeBlockStateCacheCoordinator.class) {
            collecting = false;
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
            BuildscapeCommon.logWarning("Buildscape parallel block-state cache setup failed; retrying sequentially: " + exception.getMessage());
            retrySequentially.clear();
            retrySequentially.addAll(states);
        }

        for (BlockState state : retrySequentially) {
            state.initCache();
        }

        long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000L;
        System.out.println(String.format(
                "[Buildscape] Startup initialized %d block-state caches in parallel (%d ms, %d sequential retries)",
                states.size(),
                elapsedMillis,
                retrySequentially.size()
        ));
    }
}
