package com.kingodogo.buildscape.client.screen;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigGuiBatchTest {
    @Test
    public void unfinishedBufferReproducesReportedException() {
        BufferBuilder unfinished = new BufferBuilder(256);
        unfinished.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int vertex = 0; vertex < 4; vertex++) {
            unfinished.vertex(0, 0, 0).color(255, 255, 255, 255).endVertex();
        }
        assertThrows(IndexOutOfBoundsException.class, unfinished::popNextBuffer);
        unfinished.end();
    }

    @Test
    public void submittedQuadsAreFinalizedBeforeUploadAndCanBeReused() {
        for (int frame = 0; frame < 3; frame++) {
            ConfigGuiBatch.fill(new PoseStack(), 0, 0, 20, 10, 0xFFFFFFFF);
            ConfigGuiBatch.fill(new PoseStack(), 20, 0, 40, 10, 0xFFFFFFFF);
            BufferBuilder ready = ConfigGuiBatch.finishPending();
            assertNotNull(ready);
            assertEquals(8, ready.popNextBuffer().getFirst().vertexCount());
            assertNull(ConfigGuiBatch.finishPending());
        }
    }
}
