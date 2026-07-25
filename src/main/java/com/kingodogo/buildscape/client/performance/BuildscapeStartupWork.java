package com.kingodogo.buildscape.client.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

/**
 * Runs bounded, short-lived startup work without occupying Minecraft's shared
 * background executor.
 *
 * @author hoyin1600p
 */
public final class BuildscapeStartupWork {
    private static final AtomicInteger POOL_IDS = new AtomicInteger();

    private BuildscapeStartupWork() {
    }

    public static void forEachIndex(int itemCount, IntConsumer action) {
        if (itemCount <= 0) {
            return;
        }

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int workerCount = Math.min(itemCount, Math.max(1, availableProcessors / 2));
        if (workerCount == 1) {
            for (int index = 0; index < itemCount; index++) {
                action.accept(index);
            }
            return;
        }

        int poolId = POOL_IDS.incrementAndGet();
        AtomicInteger threadIds = new AtomicInteger();
        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(
                    runnable,
                    "Buildscape-Startup-" + poolId + "-" + threadIds.incrementAndGet()
            );
            thread.setDaemon(true);
            return thread;
        };

        ExecutorService executor = Executors.newFixedThreadPool(workerCount, threadFactory);
        List<Future<?>> futures = new ArrayList<>(workerCount);
        AtomicInteger nextIndex = new AtomicInteger();
        int chunkSize = Math.max(1, itemCount / (workerCount * 32));

        try {
            for (int worker = 0; worker < workerCount; worker++) {
                futures.add(executor.submit(() -> {
                    int start;
                    while ((start = nextIndex.getAndAdd(chunkSize)) < itemCount) {
                        int end = Math.min(itemCount, start + chunkSize);
                        for (int index = start; index < end; index++) {
                            action.accept(index);
                        }
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Buildscape startup work was interrupted", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Buildscape startup work failed", cause);
        } finally {
            executor.shutdownNow();
        }
    }
}
