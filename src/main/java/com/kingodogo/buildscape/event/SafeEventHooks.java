package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildscapeCommon;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Optional gameplay hooks must not interrupt vanilla packet handling. */
public final class SafeEventHooks {
    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    private SafeEventHooks() {}

    public static void run(String hook, Runnable action) {
        call(hook, () -> { action.run(); return null; }, null);
    }

    public static <T> T call(String hook, Supplier<T> action, T fallback) {
        try {
            return action.get();
        } catch (Exception exception) {
            if (REPORTED.add(hook)) {
                BuildscapeCommon.LOGGER.error("Buildscape hook {} failed; allowing vanilla handling to continue", hook, exception);
            }
            return fallback;
        }
    }
}
