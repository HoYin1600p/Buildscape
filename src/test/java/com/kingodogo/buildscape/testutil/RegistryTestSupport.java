package com.kingodogo.buildscape.testutil;

import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Reads the mod's registrations in unit tests without a running game, so tests need not parse source files. */
public final class RegistryTestSupport {
    private RegistryTestSupport() {
    }

    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    /** Registered path (without namespace) by Java field name, for every {@link RegistryObject} field of a holder. */
    public static Map<String, String> pathsByField(Class<?> holder) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Field field : holder.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !RegistryObject.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                field.setAccessible(true);
                RegistryObject<?> entry = (RegistryObject<?>) field.get(null);
                if (entry != null) {
                    result.put(field.getName(), entry.getId().getPath());
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return result;
    }

    /**
     * Creates the registered object for a path by running its registration supplier (needs a bootstrapped game).
     * Vanilla constructors register an intrusive holder, so the block registry is unfrozen for the call only.
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(DeferredRegister<T> register, String path) {
        Object holders = null;
        Field frozen = null;
        try {
            holders = holderHelper(Registry.BLOCK);
            frozen = holders.getClass().getDeclaredField("frozen");
            frozen.setAccessible(true);
            frozen.setBoolean(holders, false);

            Field field = DeferredRegister.class.getDeclaredField("entries");
            field.setAccessible(true);
            Map<RegistryObject<T>, Supplier<? extends T>> entries =
                    (Map<RegistryObject<T>, Supplier<? extends T>>) field.get(register);
            for (Map.Entry<RegistryObject<T>, Supplier<? extends T>> entry : entries.entrySet()) {
                if (entry.getKey().getId().getPath().equals(path)) {
                    return entry.getValue().get();
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                if (frozen != null) {
                    frozen.setBoolean(holders, true);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalArgumentException("not registered: " + path);
    }

    private static Object holderHelper(Object wrapper) throws IllegalAccessException {
        for (Class<?> type = wrapper.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.getType().getSimpleName().equals("NamespacedHolderHelper")) {
                    field.setAccessible(true);
                    return field.get(wrapper);
                }
            }
        }
        throw new IllegalStateException("no holder helper in " + wrapper.getClass());
    }
}
