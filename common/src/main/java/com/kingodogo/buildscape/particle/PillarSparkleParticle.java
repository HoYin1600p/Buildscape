package com.kingodogo.buildscape.particle;
public final class PillarSparkleParticle {
    private PillarSparkleParticle() {}

    public static float[] parseColorCode(String colorCode) {
        return PillarSparkleParticleLogic.parseColorCode(colorCode);
    }

    public static void queueColor(double x, double y, double z, String colorCode) {
        if (colorCode != null && !colorCode.isEmpty()) {
            PillarSparkleDataQueue.queueColor(x, y, z, colorCode);
        }
    }

    public static void queueSize(double x, double y, double z, float sizeMultiplier) {
        if (sizeMultiplier > 0.0F) {
            PillarSparkleDataQueue.queueSize(x, y, z, sizeMultiplier);
        }
    }

    @Deprecated
    public static void queueColor(String colorCode) {}
}
