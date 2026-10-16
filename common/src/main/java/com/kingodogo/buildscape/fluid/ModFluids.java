package com.kingodogo.buildscape.fluid;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.level.material.Fluid;

public final class ModFluids {
    private static final CommonId EXPERIENCE_STILL_ID = new CommonId("buildscape", "experience_still");
    private static final CommonId EXPERIENCE_FLOWING_ID = new CommonId("buildscape", "experience_flowing");

    private ModFluids() {}

    public static Fluid getStill() {
        return Services.PLATFORM.getFluid(EXPERIENCE_STILL_ID);
    }

    public static Fluid getFlowing() {
        return Services.PLATFORM.getFluid(EXPERIENCE_FLOWING_ID);
    }

    public static boolean isExperience(Fluid fluid) {
        if (fluid == null) return false;
        CommonId id = Services.PLATFORM.getFluidId(fluid);
        if (id == null) return false;
        return "buildscape".equals(id.getNamespace())
                && ("experience_still".equals(id.getPath()) || "experience_flowing".equals(id.getPath()));
    }
}
