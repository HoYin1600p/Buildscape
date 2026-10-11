package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.config.PillarParticleConfig;
public final class PillarSparkleParticleLogic {
    public static final int FRAME_COUNT = 10;
    public record SpawnData(float red, float green, float blue, float sizeMultiplier) {}

    private PillarSparkleParticleLogic() {}

    public static SpawnData consume(double x, double y, double z) {
        PillarSparkleDataQueue.PendingSpawn pending = PillarSparkleDataQueue.getPending();
        boolean matches = pending.matches(x, y, z);
        float[] colors = parseColorOrDefault(matches ? pending.colorCode : null);
        float size = matches && pending.sizeMultiplier > 0.0F ? pending.sizeMultiplier : 1.0F;
        if (matches) pending.clear();
        return new SpawnData(colors[0], colors[1], colors[2], size);
    }

    public static float[] parseColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty() || !colorCode.startsWith("#")) {
            return new float[]{1.0F, 1.0F, 1.0F};
        }
        try {
            String hex = colorCode.substring(1);
            if (hex.length() != 6) return new float[]{1.0F, 1.0F, 1.0F};
            int red = Integer.parseInt(hex.substring(0, 2), 16);
            int green = Integer.parseInt(hex.substring(2, 4), 16);
            int blue = Integer.parseInt(hex.substring(4, 6), 16);
            return new float[]{red / 255.0F, green / 255.0F, blue / 255.0F};
        } catch (NumberFormatException ignored) {
            return new float[]{1.0F, 1.0F, 1.0F};
        }
    }

    public static float[] configuredColorOrDefault(String fallback) {
        PillarParticleConfig config = PillarParticleConfig.get();
        String colorCode = config != null && config.particle_color != null && !config.particle_color.isEmpty()
                ? config.particle_color.get(0) : fallback;
        return parseColorCode(colorCode);
    }

    private static float[] parseColorOrDefault(String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) {
            PillarParticleConfig config = PillarParticleConfig.get();
            colorCode = config.particle_color != null && !config.particle_color.isEmpty()
                    ? config.particle_color.get(0) : "#FFFFFF";
        }
        return parseColorCode(colorCode);
    }

    public static int frame(int age, int lifetime) {
        int frame = lifetime <= 0 ? FRAME_COUNT - 1 : age * FRAME_COUNT / lifetime;
        return Math.min(frame, FRAME_COUNT - 1);
    }

    public static float alpha(int age, int lifetime) {
        float fadeStart = lifetime * 0.9F;
        if (age <= fadeStart) return 1.0F;
        return 1.0F - (age - fadeStart) / (lifetime * 0.1F);
    }

    public static float u0(float spriteU0, float spriteU1) {
        return spriteU0 + (spriteU1 - spriteU0) * 0.02F;
    }

    public static float u1(float spriteU0, float spriteU1) {
        return spriteU1 - (spriteU1 - spriteU0) * 0.02F;
    }

    public static float v0(float spriteV0, float spriteV1, int frame) {
        float height = 1.0F / FRAME_COUNT;
        return spriteV0 + (spriteV1 - spriteV0) * (frame * height + height * 0.02F);
    }

    public static float v1(float spriteV0, float spriteV1, int frame) {
        float height = 1.0F / FRAME_COUNT;
        return spriteV0 + (spriteV1 - spriteV0) * ((frame + 1) * height - height * 0.02F);
    }
}
