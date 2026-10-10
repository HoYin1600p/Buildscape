package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;

public final class GoldenDandelionGrowth {
    public static final String FROZEN_TAG = "buildscape:frozen_growth";

    private GoldenDandelionGrowth() {
    }

    public static boolean isFrozen(AgeableMob mob) {
        return Services.PLATFORM.hasEntityTag(mob, FROZEN_TAG);
    }

    public static boolean canToggle(boolean baby, boolean frozen) {
        return baby || frozen;
    }

    public static boolean setFrozen(AgeableMob mob, boolean frozen) {
        return frozen
                ? Services.PLATFORM.addEntityTag(mob, FROZEN_TAG)
                : Services.PLATFORM.removeEntityTag(mob, FROZEN_TAG);
    }

    public static boolean shouldAdvanceNaturally(boolean frozen) {
        return !frozen;
    }

    public static boolean consumeLegacyFlag(CompoundTag entityData) {
        CompoundTag forgeData = Services.PLATFORM.getTagCompound(entityData, "ForgeData");
        if (forgeData == null || !Services.PLATFORM.getTagBoolean(forgeData, FROZEN_TAG, false)) {
            return false;
        }
        forgeData.remove(FROZEN_TAG);
        return true;
    }
}
