package com.kingodogo.buildscape.block.behavior;
public final class MiningSpeedCalculator {

    private MiningSpeedCalculator() {}
    public static float calculateDestroyProgress(
            float destroySpeed,
            float toolDestroySpeed,
            int efficiencyLevel,
            boolean hasCorrectTool
    ) {
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        float speedMultiplier = toolDestroySpeed;
        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0
                    ? efficiencyLevel * efficiencyLevel + 1
                    : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = hasCorrectTool ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
