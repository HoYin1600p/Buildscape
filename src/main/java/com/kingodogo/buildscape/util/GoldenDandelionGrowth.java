package com.kingodogo.buildscape.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.AgeableMob;

public final class GoldenDandelionGrowth {
    public static final String FROZEN_TAG = "buildscape:frozen_growth";

    private GoldenDandelionGrowth() {
    }

    public static boolean isFrozen(AgeableMob mob) {
        return mob.getTags().contains(FROZEN_TAG);
    }

    public static boolean canToggle(boolean baby, boolean frozen) {
        return baby || frozen;
    }

    public static boolean setFrozen(AgeableMob mob, boolean frozen) {
        return frozen ? mob.addTag(FROZEN_TAG) : mob.removeTag(FROZEN_TAG);
    }

    public static boolean shouldAdvanceNaturally(boolean frozen) {
        return !frozen;
    }

    public static boolean consumeLegacyFlag(CompoundTag entityData) {
        if (!entityData.contains("ForgeData", Tag.TAG_COMPOUND)) {
            return false;
        }

        CompoundTag forgeData = entityData.getCompound("ForgeData");
        if (!forgeData.getBoolean(FROZEN_TAG)) {
            return false;
        }

        forgeData.remove(FROZEN_TAG);
        return true;
    }
}
