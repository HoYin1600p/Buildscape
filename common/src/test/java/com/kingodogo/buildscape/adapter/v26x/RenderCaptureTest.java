package com.kingodogo.buildscape.adapter.v26x;

import com.mojang.blaze3d.vertex.PoseStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RenderCaptureTest {
    @Test
    void recordsLocalTransformBeforeTheHelperMutatesItsPose() {
        RenderCapture capture = new RenderCapture();
        PoseStack local = new PoseStack();
        local.translate(2, 3, 4);
        capture.record(local, (pose, collector, camera) -> {
            assertEquals(12, pose.last().pose().m30(), 0.0001);
            assertEquals(23, pose.last().pose().m31(), 0.0001);
            assertEquals(34, pose.last().pose().m32(), 0.0001);
        });
        local.translate(100, 100, 100);
        PoseStack world = new PoseStack();
        world.translate(10, 20, 30);
        capture.submit(world, null, null);
        assertEquals(10, world.last().pose().m30(), 0.0001);
        assertTrue(world.isEmpty());
    }

    @Test
    void restoresCallerPoseWhenSubmissionFails() {
        RenderCapture capture = new RenderCapture();
        capture.record(new PoseStack(), (pose, collector, camera) -> {
            pose.translate(7, 8, 9);
            throw new IllegalStateException("draw failed");
        });
        PoseStack world = new PoseStack();
        assertThrows(IllegalStateException.class, () -> capture.submit(world, null, null));
        assertTrue(world.isEmpty());
        assertEquals(0, world.last().pose().m30());
    }
}
