package com.kingodogo.buildscape.particle;

import java.util.*;

/**
 * ParticleShapeLibrary - A solid foundational base for defining particle wing shapes.
 * Optimized for thin "mesh-like" wing planes with gaps between "particles".
 * Supports Editor features like stats and exporting.
 */
public class ParticleShapeLibrary {

    public static class WingShapeConfig {
        public String id;
        public double wingLength;
        public double wingHeight;
        public double maxWidth; // Restored for editor compatibility
        public double lengthStep;
        public String pattern;

        public WingShapeConfig(String id, double wingLength, double wingHeight, double maxWidth, double lengthStep, String pattern) {
            this.id = id;
            this.wingLength = wingLength;
            this.wingHeight = wingHeight;
            this.maxWidth = maxWidth;
            this.lengthStep = lengthStep;
            this.pattern = pattern;
        }
    }

    public record WingParticlePos(float x, float y, float z) {}

    private static final Map<String, List<WingParticlePos>> SHAPE_CACHE = new HashMap<>();
    private static final Map<String, WingShapeConfig> REGISTERED_CONFIGS = new HashMap<>();

    static {
        // Reduced density and optimized steps for better visual "particle" feel
        registerShape("snowflake", new WingShapeConfig("snowflake", 1.9, 1.7, 0.7, 0.12, "snowflake"));
        registerShape("heart", new WingShapeConfig("heart", 1.6, 1.8, 0.9, 0.08, "heart"));
        registerShape("sparkle", new WingShapeConfig("sparkle", 2.2, 1.4, 0.4, 0.15, "sparkle"));
        registerShape("cake", new WingShapeConfig("cake", 1.7, 1.5, 0.3, 0.10, "cake"));
        registerShape("spore", new WingShapeConfig("spore", 2.0, 1.8, 0.5, 0.12, "spore"));
    }

    public static void registerShape(String id, WingShapeConfig config) {
        REGISTERED_CONFIGS.put(id.toLowerCase(), config);
    }

    public static WingShapeConfig getShape(String id) {
        return REGISTERED_CONFIGS.getOrDefault(id.toLowerCase(), REGISTERED_CONFIGS.get("snowflake"));
    }

    public static List<WingParticlePos> getWingParticles(String shapeId) {
        return SHAPE_CACHE.computeIfAbsent(shapeId.toLowerCase(), id -> {
            List<WingParticlePos> particles = new ArrayList<>();
            WingShapeConfig config = getShape(id);
            
            switch (config.pattern.toLowerCase()) {
                case "heart" -> generateHeartShape(particles, config);
                case "sparkle" -> generateSparkleShape(particles, config);
                case "cake" -> generateCakeShape(particles, config); // Fountain pattern
                case "snowflake" -> generateSnowflakeShape(particles, config); // Crystalline pattern
                case "spore" -> generateSporeShape(particles, config);
                default -> generateDefaultWing(particles, config);
            }
            return Collections.unmodifiableList(particles);
        });
    }

    private static void generateDefaultWing(List<WingParticlePos> particles, WingShapeConfig config) {
        Random rand = new Random(config.id.hashCode());
        for (float t = 0.45f; t <= 1.0f; t += config.lengthStep) {
            float x = t * (float)config.wingLength;
            float upperCurve = (float) (Math.pow(t, 0.4) * (1 - t) * config.wingHeight);
            float lowerCurve = (float) (Math.pow(t, 0.8) * (1 - t) * config.wingHeight * 0.6f);
            
            float hAtX = upperCurve + lowerCurve;
            int count = Math.max(1, (int)(hAtX * 10));
            for (int i = 0; i < count; i++) {
                float h = (i / (float) count);
                float y = (h * hAtX) - lowerCurve; 
                // Add jitter to prevent clumping
                particles.add(new WingParticlePos(x + (rand.nextFloat() - 0.5f) * 0.05f, y + (rand.nextFloat() - 0.5f) * 0.05f, 0));
            }
        }
    }

    private static void generateHeartShape(List<WingParticlePos> particles, WingShapeConfig config) {
        Random rand = new Random(789);
        for (float angle = 0; angle < Math.PI * 2; angle += 0.12f) {
            float r = (float) (config.wingLength * 0.55f);
            float hX = (float) (16 * Math.pow(Math.sin(angle), 3));
            float hY = (float) (13 * Math.cos(angle) - 5 * Math.cos(2 * angle) - 2 * Math.cos(3 * angle) - Math.cos(4 * angle));
            
            float x = (hX / 16f) * r + r; 
            float y = (hY / 16f) * (float)config.wingHeight;
            
            particles.add(new WingParticlePos(x, y, 0));
            
            if (angle < Math.PI) {
                for (float f = 0.4f; f < 0.9f; f += 0.25f) {
                    particles.add(new WingParticlePos(x * f + (rand.nextFloat()-0.5f)*0.1f, y * f + (rand.nextFloat()-0.5f)*0.1f, 0));
                }
            }
        }
    }

    private static void generateSparkleShape(List<WingParticlePos> particles, WingShapeConfig config) {
        Random random = new Random(42);
        for (int i = 0; i < 65; i++) {
            float dist = (0.05f + random.nextFloat() * 0.95f) * (float)config.wingLength;
            float angle = (random.nextFloat() - 0.5f) * 1.4f;
            
            float x = (float)Math.cos(angle) * dist;
            float y = (float)Math.sin(angle) * dist;
            particles.add(new WingParticlePos(x, y, 0));
        }
    }

    private static void generateSnowflakeShape(List<WingParticlePos> particles, WingShapeConfig config) {
        // Crystalline pattern (Snowflake) - Heavily cropped center to keep it only in the "outer boxes"
        float[] masterAngles = {-0.6f, -0.25f, 0.1f, 0.45f}; 
        Random rand = new Random(555);
        float innerCutoff = 0.45f; // Aggressive cutoff to clear redundant center particles

        for (float mAngle : masterAngles) {
            float baseCos = (float)Math.cos(mAngle);
            float baseSin = (float)Math.sin(mAngle);
            
            for (float t = innerCutoff; t <= 1.0f; t += config.lengthStep * 0.8f) {
                float x = (baseCos * t * (float)config.wingLength);
                float y = (baseSin * t * (float)config.wingLength);
                particles.add(new WingParticlePos(x, y, 0));
                
                if (t > 0.6f && (int)(t * 100) % 25 == 0) {
                    float subAngle = 0.6f;
                    float subLen = 0.3f * t;
                    particles.add(new WingParticlePos(x + (float)Math.cos(mAngle + subAngle)*subLen, y + (float)Math.sin(mAngle + subAngle)*subLen, 0));
                    particles.add(new WingParticlePos(x + (float)Math.cos(mAngle - subAngle)*subLen, y + (float)Math.sin(mAngle - subAngle)*subLen, 0));
                }
            }
        }
    }

    private static void generateCakeShape(List<WingParticlePos> particles, WingShapeConfig config) {
        // Fountain Pattern: Vertical streams that spray out and drop down
        Random rand = new Random(777);
        int streamCount = 12;
        float innerCutoff = 0.45f;

        for (int s = 0; s < streamCount; s++) {
            float streamAngle = (s / (float)streamCount) * 1.2f - 0.2f;
            float streamMaxLen = 0.6f + rand.nextFloat() * 0.6f;
            
            for (float t = innerCutoff; t <= 1.0f; t += 0.08f) {
                float dist = t * streamMaxLen * (float)config.wingLength;
                float x = (float)Math.cos(streamAngle) * dist;
                float heightFactor = (float)Math.sin(t * Math.PI);
                float y = (float)Math.sin(streamAngle) * dist + (heightFactor * 0.8f);
                
                if (t > 0.62f) { // Dropping trails only on the outer segments
                    for (float drop = 0; drop < 0.6f; drop += 0.2f) {
                        particles.add(new WingParticlePos(x + (rand.nextFloat()-0.5f)*0.05f, y - drop, 0));
                    }
                } else {
                    particles.add(new WingParticlePos(x, y, 0));
                }
            }
        }
    }

    private static void generateSporeShape(List<WingParticlePos> particles, WingShapeConfig config) {
        // Wispy, organic, non-symmetric spore wisps
        Random rand = new Random(888);
        for (int wisp = 0; wisp < 8; wisp++) {
            float baseAngle = (wisp / 8.0f - 0.5f) * 1.5f;
            float wispLen = (0.6f + rand.nextFloat() * 0.4f) * (float)config.wingLength;
            
            for (float t = 0.0f; t <= 1.0f; t += 0.1f) {
                float curve = (float) (Math.sin(t * 3.0 + wisp) * 0.2);
                float x = (float)Math.cos(baseAngle) * t * wispLen;
                float y = (float)Math.sin(baseAngle) * t * wispLen + curve;
                
                particles.add(new WingParticlePos(x, y, 0));
                // Add some stray spores around the wisp
                if (rand.nextFloat() > 0.6f) {
                    particles.add(new WingParticlePos(x + (rand.nextFloat()-0.5f)*0.3f, y + (rand.nextFloat()-0.5f)*0.3f, 0));
                }
            }
        }
    }

    public static String getShapeStats(String shapeId) {
        List<WingParticlePos> particles = getWingParticles(shapeId);
        return String.format("Shape: %s | Particles: %d", shapeId, particles.size());
    }

    public static String exportShapeConfig(String shapeId) {
        WingShapeConfig config = getShape(shapeId);
        return String.format("{\"id\":\"%s\",\"len\":%.2f,\"h\":%.2f,\"maxW\":%.2f,\"step\":%.3f,\"pat\":\"%s\"}",
            config.id, config.wingLength, config.wingHeight, config.maxWidth, config.lengthStep, config.pattern);
    }

    public static void clearCache() {
        SHAPE_CACHE.clear();
    }
}
