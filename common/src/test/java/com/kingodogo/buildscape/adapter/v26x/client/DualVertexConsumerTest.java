package com.kingodogo.buildscape.adapter.v26x.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DualVertexConsumerTest {
    @Test void texturedAndFoilPassesReceiveIdenticalVerticesAndAttributes() {
        var base = new ArrayList<String>();
        var foil = new ArrayList<String>();
        VertexConsumer consumer = new DualVertexConsumer(recording(base), recording(foil));
        consumer.addVertex(1, 2, 3).setColor(1, 2, 3, 4).setColor(0xFF123456).setUv(0.2F, 0.8F)
                .setUv1(0, 10).setUv2(12, 15).setNormal(0, 1, 0).setLineWidth(1);
        assertEquals(8, base.size());
        assertEquals(base, foil);
    }

    private static VertexConsumer recording(List<String> calls) {
        return (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[] {VertexConsumer.class}, (proxy, method, arguments) -> {
                    calls.add(method.getName() + java.util.Arrays.toString(arguments));
                    return proxy;
                });
    }
}
