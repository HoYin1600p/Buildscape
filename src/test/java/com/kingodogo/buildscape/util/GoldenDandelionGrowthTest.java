package com.kingodogo.buildscape.util;

import net.minecraft.nbt.CompoundTag;

public final class GoldenDandelionGrowthTest {
    private GoldenDandelionGrowthTest() {
    }

    public static void main(String[] args) {
        expect(true, GoldenDandelionGrowth.canToggle(true, false), "unfrozen baby can freeze");
        expect(true, GoldenDandelionGrowth.canToggle(true, true), "frozen baby can unfreeze");
        expect(false, GoldenDandelionGrowth.canToggle(false, false), "unfrozen adult is ignored");
        expect(true, GoldenDandelionGrowth.canToggle(false, true), "tagged adult can unfreeze");
        expect(false, GoldenDandelionGrowth.shouldAdvanceNaturally(true), "frozen age remains unchanged");
        expect(true, GoldenDandelionGrowth.shouldAdvanceNaturally(false), "normal age advances");

        CompoundTag entityData = new CompoundTag();
        CompoundTag forgeData = new CompoundTag();
        forgeData.putBoolean(GoldenDandelionGrowth.FROZEN_TAG, true);
        entityData.put("ForgeData", forgeData);
        expect(true, GoldenDandelionGrowth.consumeLegacyFlag(entityData), "legacy flag is detected");
        expect(false, forgeData.contains(GoldenDandelionGrowth.FROZEN_TAG), "legacy flag is removed");
        expect(false, GoldenDandelionGrowth.consumeLegacyFlag(entityData), "migration is one-time");
    }

    private static void expect(boolean expected, boolean actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
