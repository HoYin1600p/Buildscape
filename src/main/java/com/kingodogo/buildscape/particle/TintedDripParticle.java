package com.kingodogo.buildscape.particle;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TintedDripParticle extends TextureSheetParticle {

    private static final Map<String, ColorEntry> POSITION_COLOR_MAP =
            new ConcurrentHashMap<>();

    private static class ColorEntry {

        final String color;
        final long timestamp;

        ColorEntry(String color) {
            this.color = color;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private final SpriteSet sprites;
    private final boolean isFloating;

    protected TintedDripParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.pickSprite(sprites);

        this.isFloating = ySpeed > -0.006D;

        if (this.isFloating) {
            this.gravity = 0.0F;
            this.lifetime = 150 + level.random.nextInt(100);
        } else {
            this.gravity = 0.005F;
            this.lifetime = 300 + level.random.nextInt(150);
        }
        this.hasPhysics = true;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        String posKey = makePositionKey(x, y, z);
        ColorEntry entry = POSITION_COLOR_MAP.remove(posKey);

        if (entry != null && entry.color != null && !entry.color.isEmpty()) {
            float[] color = parseColorCode(entry.color);
            this.setColor(color[0], color[1], color[2]);
        } else {
            this.setColor(0.92F, 0.58F, 0.84F);
        }

        cleanupOldEntries();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if (this.isFloating) {
                this.xd += (this.random.nextDouble() - 0.5D) * 0.002D;
                this.yd += (this.random.nextDouble() - 0.5D) * 0.002D;
                this.zd += (this.random.nextDouble() - 0.5D) * 0.002D;

                double maxVel = 0.03D;
                this.xd = Math.max(-maxVel, Math.min(maxVel, this.xd));
                this.yd = Math.max(-maxVel, Math.min(maxVel, this.yd));
                this.zd = Math.max(-maxVel, Math.min(maxVel, this.zd));

                this.xd *= 0.995D;
                this.yd *= 0.995D;
                this.zd *= 0.995D;
            } else {
                this.yd -= this.gravity;

                this.xd *= 0.98D;
                this.zd *= 0.98D;
            }

            this.move(this.xd, this.yd, this.zd);

            if (this.age > this.lifetime - 20) {
                float fade = (float) (this.lifetime - this.age) / 20.0F;
                this.alpha = fade;
            }

            if (this.onGround && !this.isFloating) {
                this.remove();
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static void registerColorForPosition(
            double x,
            double y,
            double z,
            String colorCode
    ) {
        String posKey = makePositionKey(x, y, z);
        POSITION_COLOR_MAP.put(posKey, new ColorEntry(colorCode));
    }

    private static String makePositionKey(double x, double y, double z) {
        return String.format("%.4f,%.4f,%.4f", x, y, z);
    }

    private static void cleanupOldEntries() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, ColorEntry>> iterator =
                POSITION_COLOR_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ColorEntry> entry = iterator.next();
            if (now - entry.getValue().timestamp > 1000) {
                iterator.remove();
            }
        }
    }

    public static void queueColor(String colorCode) {
    }

    private static float[] parseColorCode(String colorCode) {
        if (
                colorCode == null || colorCode.isEmpty() || !colorCode.startsWith("#")
        ) {
            return new float[]{0.92F, 0.58F, 0.84F};
        }

        try {
            String hex = colorCode.substring(1);
            if (hex.length() != 6) {
                return new float[]{0.92F, 0.58F, 0.84F};
            }

            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
        } catch (NumberFormatException e) {
            return new float[]{0.92F, 0.58F, 0.84F};
        }
    }

    public static void clearColorCache() {
        POSITION_COLOR_MAP.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new TintedDripParticle(
                    level,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    sprites
            );
        }
    }
}
