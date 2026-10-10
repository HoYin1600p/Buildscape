package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
public final class PillarMarkerRenderer {
    private static final ThreadLocal<Object> BUFFER = new ThreadLocal<>();
    private PillarMarkerRenderer() {}

    public static void renderPillarMarkers(PoseStack poseStack, Camera camera) {
        Services.PLATFORM.renderPillarMarkers(poseStack, camera);
    }

    public static Object currentBuffer() { return BUFFER.get(); }

    public static void renderPillarMarkers(PoseStack poseStack, Camera camera, Object buffer) {
        Object previous = BUFFER.get();
        BUFFER.set(buffer);
        try { Services.PLATFORM.renderPillarMarkers(poseStack, camera); }
        finally { if (previous == null) BUFFER.remove(); else BUFFER.set(previous); }
    }
}
