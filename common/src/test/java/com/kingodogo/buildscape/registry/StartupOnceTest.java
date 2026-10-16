package com.kingodogo.buildscape.registry;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class StartupOnceTest {
    @Test
    void simultaneousEntrypointsInitializeOnce() throws Exception {
        StartupOnce startup = new StartupOnce();
        AtomicInteger invocations = new AtomicInteger();
        try (var executor = Executors.newFixedThreadPool(4)) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
            for (int i = 0; i < 16; i++) {
                futures.add(executor.submit(() -> startup.run(invocations::incrementAndGet)));
            }
            for (var future : futures) future.get();
        }
        assertEquals(1, invocations.get());
    }

    @Test
    void failedStartupIsNotRetriedAgainstPartiallyRegisteredEntries() {
        StartupOnce startup = new StartupOnce();
        IllegalArgumentException failure = new IllegalArgumentException("registration failed");
        assertSame(failure, assertThrows(IllegalArgumentException.class,
                () -> startup.run(() -> { throw failure; })));
        AtomicInteger retries = new AtomicInteger();
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> startup.run(retries::incrementAndGet)).getCause());
        assertEquals(0, retries.get());
    }

    @Test
    void recursionFailsBeforeReenteringInitialization() {
        StartupOnce startup = new StartupOnce();
        AtomicInteger recursiveInvocations = new AtomicInteger();
        assertThrows(IllegalStateException.class,
                () -> startup.run(() -> startup.run(recursiveInvocations::incrementAndGet)));
        assertEquals(0, recursiveInvocations.get());
    }
}
