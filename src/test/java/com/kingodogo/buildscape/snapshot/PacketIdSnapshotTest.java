package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.testutil.BootstrappedGameTest;
import com.kingodogo.buildscape.network.ModMessages;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertFalse;

/** Packet ids, their classes and directions as registered by ModMessages must not change. */
public class PacketIdSnapshotTest extends BootstrappedGameTest {

    private static Object field(Object target, String name) throws ReflectiveOperationException {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    @Test
    public void packetIdOrder() throws IOException, ReflectiveOperationException {
        ModMessages.register();
        Object codec = field(ModMessages.INSTANCE, "indexedCodec");
        Map<?, ?> indices = (Map<?, ?>) field(codec, "indicies");
        TreeMap<Integer, String> sorted = new TreeMap<>();
        for (Map.Entry<?, ?> entry : indices.entrySet()) {
            Object handler = entry.getValue();
            Object type = field(handler, "messageType");
            Object direction = field(handler, "networkDirection");
            sorted.put(((Number) entry.getKey()).intValue(), ((Class<?>) type).getSimpleName() + " " + direction);
        }
        assertFalse(sorted.isEmpty());
        List<String> lines = new ArrayList<>();
        sorted.forEach((id, text) -> lines.add(id + " " + text));
        SnapshotSupport.assertSnapshot("network-packet-ids.txt", lines);
    }
}
