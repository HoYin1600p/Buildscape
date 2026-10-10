package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
public final class PillarMarkerRenderer {
    private PillarMarkerRenderer() {}

    public static void renderPillarMarkers(PoseStack poseStack, Camera camera) {
        Services.PLATFORM.renderPillarMarkers(poseStack, camera);
    }
}
