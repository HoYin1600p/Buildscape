package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.adapter.v26x.client.ClientWorldHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ClientWorldGeometryTest {
    @Test void markerBoxContainsExactlyItsTwelveEdges() {
        var vertices = new ArrayList<Vector3f>();
        VertexConsumer consumer = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[] {VertexConsumer.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("addVertex")) {
                        assertEquals(4, arguments.length);
                        vertices.add(new Vector3f((float) arguments[1], (float) arguments[2], (float) arguments[3]));
                    }
                    return proxy;
                });
        PoseStack pose = new PoseStack();
        ClientWorldHooks.renderLineBox(pose, consumer, -1, 2, 3, 4, 8, 10, 1, 0, 0, 1);
        assertEquals(24, vertices.size());
        assertEquals(8, new HashSet<>(vertices).size());
        var edges = new HashSet<String>();
        for (int i = 0; i < vertices.size(); i += 2) {
            Vector3f start = vertices.get(i), end = vertices.get(i + 1);
            int changedAxes = (start.x != end.x ? 1 : 0) + (start.y != end.y ? 1 : 0) + (start.z != end.z ? 1 : 0);
            assertEquals(1, changedAxes);
            assertTrue(edges.add(start.toString() + end));
        }
        assertEquals(12, edges.size());
        assertTrue(pose.isEmpty());
    }
}
