package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import org.joml.Vector4f;

/** Shared reference fog values, also callable from the Fabric fog extraction hook. */
public final class ExperienceFluidFog {
    private ExperienceFluidFog() {}

    public static boolean isExperienceFluid(Camera camera, ClientLevel level) {
        if (camera == null || level == null) return false;
        var fluid = level.getFluidState(camera.blockPosition()).getType();
        return fluid == ExperienceFluids.still() || fluid == ExperienceFluids.flowing();
    }

    public static void color(Vector4f color) { color.set(0.3F, 0.9F, 0.1F, 1.0F); }

    public static void distances(FogData fog) {
        fog.environmentalStart = 0.5F;
        fog.environmentalEnd = 12.0F;
    }

    /** Apply after vanilla extraction, before FogRenderer uploads the fog buffer. */
    public static void apply(Camera camera, ClientLevel level, FogData fog) {
        if (!isExperienceFluid(camera, level)) return;
        color(fog.color);
        distances(fog);
    }
}
