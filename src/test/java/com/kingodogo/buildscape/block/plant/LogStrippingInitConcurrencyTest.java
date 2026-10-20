package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogStrippingInitConcurrencyTest extends BootstrappedGameTest {
    @Test
    public void coldInitializationSerializesCallersAndEveryCallerSeesTheCompleteMap() throws Exception {
        Field mapField = strippingMapField();
        Map<Object, Object> original = map(mapField);
        Map<Object, Object> originalContents = new HashMap<>(original);
        List<Thread> callers = new ArrayList<>();
        ConcurrentLinkedQueue<Map<Object, Object>> observed = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> failures = new ConcurrentLinkedQueue<>();
        CountDownLatch ready = new CountDownLatch(8);
        boolean allBlocked;
        try {
            original.clear();
            LogStrippingHandler.init();
            Map<Object, Object> expected = new HashMap<>(map(mapField));
            assertTrue("Single-threaded initialization must register stripping pairs", expected.size() > 1);
            map(mapField).clear();

            // Holding the initialization monitor makes serialization observable without relying on a lucky race.
            synchronized (LogStrippingHandler.class) {
                for (int i = 0; i < 8; i++) {
                    Thread caller = new Thread(() -> {
                        ready.countDown();
                        try {
                            LogStrippingHandler.init();
                            observed.add(new HashMap<>(map(mapField)));
                        } catch (Throwable failure) {
                            failures.add(failure);
                        }
                    }, "stripping-init-test-" + i);
                    callers.add(caller);
                    caller.start();
                }
                assertTrue("All callers must reach init", ready.await(5, TimeUnit.SECONDS));
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                do {
                    allBlocked = callers.stream().allMatch(thread -> thread.getState() == Thread.State.BLOCKED);
                    if (allBlocked || callers.stream().anyMatch(thread -> !thread.isAlive())) {
                        break;
                    }
                    Thread.yield();
                } while (System.nanoTime() < deadline);
            }
            for (Thread caller : callers) {
                caller.join(5000);
                assertFalse("Initialization caller must finish", caller.isAlive());
            }
            assertTrue("Concurrent init callers must wait for the initialization monitor", allBlocked);
            assertTrue("Initialization failed: " + failures, failures.isEmpty());
            assertEquals(8, observed.size());
            for (Map<Object, Object> snapshot : observed) {
                assertEquals(expected.size(), snapshot.size());
                assertEquals(expected, snapshot);
            }
        } finally {
            for (Thread caller : callers) {
                caller.join(5000);
            }
            original.clear();
            original.putAll(originalContents);
            if (!Modifier.isFinal(mapField.getModifiers())) {
                mapField.set(null, original);
            }
        }
    }

    private static Field strippingMapField() throws Exception {
        for (Field field : LogStrippingHandler.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Map.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new AssertionError("Missing stripping map");
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> map(Field field) throws IllegalAccessException {
        return (Map<Object, Object>) field.get(null);
    }
}
