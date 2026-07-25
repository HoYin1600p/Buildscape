package com.kingodogo.buildscape.client.performance;

import com.kingodogo.buildscape.BuildScape;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads optional LaunchFaster settings without introducing a dependency on
 * that mod. Buildscape yields only when LaunchFaster owns the same operation.
 *
 * @author hoyin1600p
 */
public final class LaunchFasterInterop {
    private static final String CONFIG_CLASS = "com.ruben.launchfaster.LaunchFasterConfig";
    private static final Set<String> REPORTED_FAILURES = ConcurrentHashMap.newKeySet();
    private static final boolean PRESENT = classExists(CONFIG_CLASS);

    private LaunchFasterInterop() {
    }

    public static boolean isParallelModelLoadingEnabled() {
        return readBoolean("parallelModelLoading");
    }

    public static boolean isParallelModelBakingEnabled() {
        return readBoolean("parallelModelBaking");
    }

    private static boolean readBoolean(String settingName) {
        if (!PRESENT) {
            return false;
        }

        try {
            Class<?> configClass = Class.forName(
                    CONFIG_CLASS,
                    false,
                    LaunchFasterInterop.class.getClassLoader()
            );
            Field clientField = configClass.getField("CLIENT");
            Object clientConfig = clientField.get(null);
            Field settingField = clientConfig.getClass().getField(settingName);
            Object configValue = settingField.get(clientConfig);
            Method getMethod = configValue.getClass().getMethod("get");
            return Boolean.TRUE.equals(getMethod.invoke(configValue));
        } catch (
                ClassNotFoundException |
                NoSuchFieldException |
                NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException |
                RuntimeException exception
        ) {
            if (REPORTED_FAILURES.add(settingName)) {
                BuildScape.LOGGER.warn(
                        "Could not read LaunchFaster setting {}; keeping the overlapping " +
                                "Buildscape optimization disabled",
                        settingName,
                        exception
                );
            }
            return true;
        }
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className, false, LaunchFasterInterop.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}
