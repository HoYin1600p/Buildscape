package com.kingodogo.buildscape.adapter.v26x;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.lang.reflect.Proxy;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;

import static org.junit.jupiter.api.Assertions.*;

class RenderCaptureTest {
    @Test void deferredGeometryIncludesVerticesFilledAfterCollection() {
        var deferred = new ArrayList<SubmitNodeCollector.CustomGeometryRenderer>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[] {SubmitNodeCollector.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("submitCustomGeometry"))
                        deferred.add((SubmitNodeCollector.CustomGeometryRenderer) arguments[2]);
                    return null;
                });
        RenderCapture capture = new RenderCapture();
        VertexConsumer buffer = capture.geometry(null);
        capture.submit(new PoseStack(), collector, null);
        buffer.addVertex(1, 2, 3).setColor(0xFF123456).setUv(0.25F, 0.75F).setUv2(5, 9).setNormal(0, 1, 0);
        var positions = new ArrayList<Vector3f>();
        var attributes = new ArrayList<String>();
        VertexConsumer output = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[] {VertexConsumer.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("addVertex"))
                        positions.add(new Vector3f((float) arguments[1], (float) arguments[2], (float) arguments[3]));
                    if (method.getName().equals("setColor")) attributes.add("color:" + arguments[0]);
                    if (method.getName().equals("setUv2")) attributes.add("light:" + arguments[0] + ":" + arguments[1]);
                    return proxy;
                });
        assertEquals(1, deferred.size());
        deferred.getFirst().render(new PoseStack().last(), output);
        assertEquals(java.util.List.of(new Vector3f(1, 2, 3)), positions);
        assertTrue(attributes.contains("color:" + 0xFF123456));
        assertTrue(attributes.contains("light:5:9"));
    }

    @Test void replayUsesTheExtractionTransformAndRestoresTheSubmissionPose() {
        RenderCapture capture = new RenderCapture();
        PoseStack extraction = new PoseStack();
        extraction.translate(2, 3, 4);
        var positions = new ArrayList<Vector3f>();
        capture.record(extraction, (pose, collector, camera) ->
                positions.add(pose.last().pose().transformPosition(new Vector3f())));
        extraction.translate(100, 100, 100);

        PoseStack submission = new PoseStack();
        submission.translate(10, 20, 30);
        for (int i = 0; i < 2; i++) capture.submit(submission, null, null);
        assertEquals(2, positions.size());
        for (Vector3f position : positions) assertEquals(new Vector3f(12, 23, 34), position);
        assertEquals(new Vector3f(10, 20, 30), submission.last().pose().transformPosition(new Vector3f()));
        assertTrue(submission.isEmpty());
    }

    @Test void aFailedDrawStillRestoresTheSubmissionPose() {
        RenderCapture capture = new RenderCapture();
        PoseStack extraction = new PoseStack();
        extraction.translate(2, 0, 0);
        capture.record(extraction, (pose, collector, camera) -> { throw new IllegalStateException("draw failed"); });
        PoseStack submission = new PoseStack();
        assertThrows(IllegalStateException.class, () -> capture.submit(submission, null, null));
        assertTrue(submission.isEmpty());
        assertEquals(new Vector3f(), submission.last().pose().transformPosition(new Vector3f()));
    }
}
