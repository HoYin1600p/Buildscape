package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.block.entity.IBlockEntityReadData;
import com.kingodogo.buildscape.block.entity.IBlockEntityWriteData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameplaySafetyPortTest {
    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void icicleCascadeSkipsNestedRemovalAndClearsGuardAfterFailure() throws Exception {
        PointedIcicleBlock icicle = allocate(PointedIcicleBlock.class);
        net.minecraft.server.level.ServerLevel level = allocate(net.minecraft.server.level.ServerLevel.class);
        Field field = PointedIcicleBlock.class.getDeclaredField("CASCADE_ACTIVE");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") ThreadLocal<Boolean> active = (ThreadLocal<Boolean>) field.get(null);
        try {
            active.set(true);
            // A nested callback must not inspect the removed block or start another column walk.
            assertDoesNotThrow(() -> icicle.onIcicleRemove(null, level, net.minecraft.core.BlockPos.ZERO,
                    net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), false));
            assertEquals(Boolean.TRUE, active.get());
            active.remove();
            // A failure after entering the cascade must not disable future independent cascades.
            assertThrows(NullPointerException.class, () -> icicle.onIcicleRemove(null, level,
                    net.minecraft.core.BlockPos.ZERO, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), false));
            assertNull(active.get());
        } finally {
            active.remove();
        }
    }

    @Test
    void loadedColorsKeepFirstFiveNonemptyEntriesAndReloadReplacesThem() throws Exception {
        // Exercise persistence without initializing the loader's mod registry.
        GlowLightsBlockEntity lights = allocate(GlowLightsBlockEntity.class);
        Field colors = GlowLightsBlockEntity.class.getDeclaredField("dyeColors");
        colors.setAccessible(true);
        colors.set(lights, new ArrayList<String>());
        List<String> values = Arrays.asList("", null, "#000001", "#000002", "#000003", "#000004", "#000005", "#000006");
        lights.readData(data(values));
        assertEquals(values.subList(2, 7), lights.getDyeColors());
        assertFalse(lights.canAddMoreColors());
        List<String> saved = new ArrayList<>();
        lights.writeData((IBlockEntityWriteData) Proxy.newProxyInstance(IBlockEntityWriteData.class.getClassLoader(),
                new Class<?>[]{IBlockEntityWriteData.class}, (proxy, method, args) -> {
                    if (method.getName().equals("putStringList")) {
                        assertEquals("DyeColors", args[0]);
                        for (Object value : (List<?>) args[1]) saved.add((String) value);
                        return null;
                    }
                    throw new AssertionError("Unexpected persistence write: " + method.getName());
                }));
        assertEquals(values.subList(2, 7), saved);
        lights.readData(data(List.of("#FFFFFF")));
        assertEquals(List.of("#FFFFFF"), lights.getDyeColors());
        assertTrue(lights.canAddMoreColors());
        lights.readData(data(List.of()));
        assertEquals(List.of(), lights.getDyeColors());
    }

    @Test
    void resinOnlyHandlesEmptyHandsAndSuccessfulServerPlacements() {
        assertEquals(net.minecraft.world.InteractionResult.PASS, CreakingHeartBlock.resinInteractionResult(false, 2, false));
        assertEquals(net.minecraft.world.InteractionResult.PASS, CreakingHeartBlock.resinInteractionResult(true, 0, false));
        assertEquals(net.minecraft.world.InteractionResult.CONSUME, CreakingHeartBlock.resinInteractionResult(true, 1, false));
        assertEquals(net.minecraft.world.InteractionResult.SUCCESS, CreakingHeartBlock.resinInteractionResult(true, 0, true));
    }

    private static IBlockEntityReadData data(List<String> colors) {
        return (IBlockEntityReadData) Proxy.newProxyInstance(IBlockEntityReadData.class.getClassLoader(),
                new Class<?>[]{IBlockEntityReadData.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getStringListOrEmpty")) return colors;
                    throw new AssertionError("Unexpected persistence read: " + method.getName());
                });
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field singleton = unsafeClass.getDeclaredField("theUnsafe");
        singleton.setAccessible(true);
        Object unsafe = singleton.get(null);
        return type.cast(unsafeClass.getMethod("allocateInstance", Class.class).invoke(unsafe, type));
    }
}
