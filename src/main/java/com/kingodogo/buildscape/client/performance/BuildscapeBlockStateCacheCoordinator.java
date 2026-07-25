package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Defers only Buildscape block-state caches until the block registry bake can
 * calculate them in parallel, then returns with every cache fully initialized.
 *
 * @author hoyin1600p
 */
public final class BuildscapeBlockStateCacheCoordinator {
    private static final List<BlockState> PENDING_STATES = new ArrayList<>();
    private static boolean collecting;

    private BuildscapeBlockStateCacheCoordinator() {
    }

    public static synchronized void begin() {
        PENDING_STATES.clear();
        collecting = true;
    }

    public static synchronized boolean isCollecting() {
        return collecting;
    }

    public static synchronized boolean deferIfBuildscape(BlockState state) {
        if (!collecting) {
            return false;
        }

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null || !BuildScape.MODID.equals(blockId.getNamespace())) {
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
