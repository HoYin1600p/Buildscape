package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.PillarSparkleParticleLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class ParticleFactory {

    private static final int[] CONFETTI_COLORS = {0xFF0000, 0x00FFFF, 0x1919EA, 0x3CDFFF, 0xFFFF00, 0xFF5C00, 0xBFFE00, 0x39FF14, 0xF686B7, 0xAB87FF, 0xFF00FF};

    private ParticleFactory() {}

    public static void registerProviders() {
        ParticleEngine pe = Minecraft.getInstance().particleEngine;
        pe.register(ModParticles.GLOW_LIME_SPARKLE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                private final TextureAtlasSprite baseSprite;
                private int currentFrame;
                {
                    setSpriteFromAge(sprites);
                    this.baseSprite = this.sprite;
                    this.xd = dx; this.yd = dy; this.zd = dz; this.lifetime = 100;
                    PillarSparkleParticleLogic.SpawnData data = PillarSparkleParticleLogic.consume(x, y, z);
                    setColor(data.red(), data.green(), data.blue());
                    this.quadSize = 0.2F * data.sizeMultiplier();
                    this.hasPhysics = false;
                    this.alpha = 1.0F;
                }
                @Override public void tick() {
                    super.tick();
                    this.currentFrame = PillarSparkleParticleLogic.frame(this.age, this.lifetime);
                    this.alpha = PillarSparkleParticleLogic.alpha(this.age, this.lifetime);
                }
                @Override protected float getU0() { return PillarSparkleParticleLogic.u0(baseSprite.getU0(), baseSprite.getU1()); }
                @Override protected float getU1() { return PillarSparkleParticleLogic.u1(baseSprite.getU0(), baseSprite.getU1()); }
                @Override protected float getV0() { return PillarSparkleParticleLogic.v0(baseSprite.getV0(), baseSprite.getV1(), currentFrame); }
                @Override protected float getV1() { return PillarSparkleParticleLogic.v1(baseSprite.getV0(), baseSprite.getV1(), currentFrame); }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
                @Override public int getLightColor(float partialTick) { return 0xF000F0; }
            }
        );
        pe.register(ModParticles.TINTED_DRIP_FALL.get(), ParticleFactory::tintedDripProvider);
        pe.register(ModParticles.TINTED_SPORE.get(), ParticleFactory::tintedSporeProvider);
        pe.register(ModParticles.SNOWFLAKE.get(), sprites -> snowflakeProvider(sprites, false));
        pe.register(ModParticles.SNOWFLAKE_STILL.get(), sprites -> snowflakeProvider(sprites, true));
        pe.register(ModParticles.CONFETTI.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                private final float rotationSpeed;
                {
                    pickSprite(sprites);
                    int rgb = CONFETTI_COLORS[this.random.nextInt(CONFETTI_COLORS.length)];
                    setColor(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
                    this.gravity = 0.05F + this.random.nextFloat() * 0.04F;
                    this.lifetime = 70 + this.random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.08F + this.random.nextFloat() * 0.12F;
                    this.roll = this.random.nextFloat() * (float)(Math.PI * 2.0D);
                    this.oRoll = this.roll;
                    this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.5F;
                    this.xd = dx; this.yd = dy; this.zd = dz; this.alpha = 1.0F;
                }
                @Override public void tick() {
                    super.tick(); this.oRoll = this.roll; this.roll += this.rotationSpeed;
                    this.xd *= 0.98D; this.zd *= 0.98D;
                    if (this.age % 5 == 0) { this.xd += (this.random.nextDouble() - 0.5D) * 0.01D; this.zd += (this.random.nextDouble() - 0.5D) * 0.01D; }
                    if (this.age > this.lifetime * 0.8F) this.alpha = 1.0F - (this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            }
        );
        pe.register(ModParticles.TINTABLE_HEART.get(), ParticleFactory::tintableHeartProvider);
        pe.register(ModParticles.CAKE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.05F;
                    this.lifetime = 60 + this.random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.2F + this.random.nextFloat() * 0.1F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.age > this.lifetime * 0.7F) this.alpha = 1.0F - (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            }
        );
        pe.register(ModParticles.CHERRY.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                private final float rotationSpeed = (level.random.nextFloat() - 0.5F) * 0.1F;
                {
                    this.lifetime = 60 + level.random.nextInt(40);
                    this.gravity = 0.05F;
                    this.hasPhysics = true;
                    this.roll = level.random.nextFloat() * ((float)Math.PI * 2.0F);
                    this.oRoll = this.roll;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    int spriteIndex = level.random.nextInt(6) + (level.random.nextBoolean() ? 6 : 0);
                    setSprite(sprites.get(spriteIndex, 12));
                    this.quadSize *= 0.5F;
                    setColor(1.0F, 1.0F, 1.0F);
                }
                @Override public void tick() {
                    super.tick();
                    this.oRoll = this.roll; this.roll += this.rotationSpeed;
                    if (this.age > this.lifetime * 0.7F) this.alpha = 1.0F - (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            }
        );
        pe.register(ModParticles.BUBBLE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.05F;
                    this.lifetime = 60 + this.random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.1F + this.random.nextFloat() * 0.1F;
                    this.xd = dx + (this.random.nextDouble() - 0.5D) * 0.02D;
                    this.yd = dy;
                    this.zd = dz + (this.random.nextDouble() - 0.5D) * 0.02D;
                    setColor(1.0F, 1.0F, 1.0F);
                    this.alpha = 0.8F + this.random.nextFloat() * 0.2F;
                }
                @Override public void tick() {
                    super.tick();
                    this.oRoll = this.roll;
                    this.roll += 0.05F;
                    if (this.age > this.lifetime * 0.8F) {
                        this.alpha = (1.0F - (this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F)) * 0.6F;
                    }
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            }
        );
        pe.register(ModParticles.TRAIL_NOTE.get(), ParticleFactory::trailNoteProvider);
        pe.register(ModParticles.COLORED_SMOKE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z) {
                {
                    this.quadSize = 0.3F;
                    this.lifetime = 200 + level.random.nextInt(100);
                    this.gravity = 3.0E-6F;
                    this.hasPhysics = true;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    pickSprite(sprites);
                    float[] color = com.kingodogo.buildscape.particle.SmokeColorRegistry.consumeRgb(x, y, z);
                    setColor(color[0], color[1], color[2]);
                    this.alpha = 0.9F;
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    this.yd -= this.gravity;
                    this.move(this.xd, this.yd, this.zd);
                    this.alpha = 0.9F * (1.0F - (float)this.age / (float)this.lifetime);
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            }
        );
        pe.register(ModParticles.CASCADE.get(), ParticleFactory::cascadeProvider);
        pe.register(ModParticles.NOXIOUS_GAS.get(), ParticleFactory::noxiousGasProvider);
        pe.register(ModParticles.COPPER_FIRE_FLAME.get(), FlameParticle.Provider::new);
        pe.register(ModParticles.FIREFLY.get(), ParticleFactory::fireflyProvider);
        pe.register(ModParticles.SULFUR_BUBBLES.get(), ParticleFactory::sulfurBubbleProvider);
        pe.register(ModParticles.GEYSER.get(), geyserEruptionProvider());
        pe.register(ModParticles.GEYSER_BASE.get(), ParticleFactory::geyserBaseProvider);
        pe.register(ModParticles.GEYSER_POOF.get(), ParticleFactory::geyserBaseProvider);
        pe.register(ModParticles.GEYSER_PLUME.get(), ParticleFactory::geyserPlumeProvider);
        pe.register(ModParticles.XP_PARTICLE.get(), ParticleFactory::xpProvider);
    }

    private static net.minecraft.client.particle.ParticleProvider<com.kingodogo.buildscape.particle.GeyserParticleOptions> geyserEruptionProvider() {
        return (type, level, x, y, z, dx, dy, dz) -> new net.minecraft.client.particle.NoRenderParticle(level, x, y, z) {
            private final int waterBlocks = type.getWaterBlocks();
            private final com.kingodogo.buildscape.particle.GeyserParticleOptions plume = new com.kingodogo.buildscape.particle.GeyserParticleOptions(ModParticles.GEYSER_PLUME.get(), waterBlocks);
            private final com.kingodogo.buildscape.particle.GeyserBaseParticleOptions base = new com.kingodogo.buildscape.particle.GeyserBaseParticleOptions(ModParticles.GEYSER_BASE.get(), waterBlocks, 1.5F);
            private final com.kingodogo.buildscape.particle.GeyserBaseParticleOptions poof = new com.kingodogo.buildscape.particle.GeyserBaseParticleOptions(ModParticles.GEYSER_POOF.get(), waterBlocks, 2.0F);
            { this.lifetime = 20; }
            @Override public void tick() {
                super.tick();
                if (this.age % 2 == 0) for (int i = 0; i < 2; i++) this.level.addAlwaysVisibleParticle(base, true, this.x, this.y, this.z, dx, dy, dz);
                for (int i = 0; i < waterBlocks + 2; i++) this.level.addAlwaysVisibleParticle(plume, true, this.x, this.y, this.z, dx, dy, dz);
                if (this.age % 10 == 0) for (int i = 0; i < 20; i++) this.level.addAlwaysVisibleParticle(poof, true, this.x, this.y, this.z, dx, dy, dz);
            }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<com.kingodogo.buildscape.particle.GeyserBaseParticleOptions> geyserBaseProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new net.minecraft.client.particle.BaseAshSmokeParticle(
                level, x + (level.random.nextFloat() - 0.5F) * 0.5F, y + (level.random.nextFloat() - 0.5F) * 0.5F + 0.2D,
                z + (level.random.nextFloat() - 0.5F) * 0.5F, type.getBurstImpulseBase() + 0.25F * type.getWaterBlocks(),
                type.getBurstImpulseBase() + 0.25F * type.getWaterBlocks(), type.getBurstImpulseBase() + 0.25F * type.getWaterBlocks(),
                dx, dy, dz, 3.0F + 0.125F * type.getWaterBlocks(), sprites, 0.0F, 0, 0.0F, true) {
            { this.friction = 0.725F; setColor(1.0F, 1.0F, 1.0F); this.yd = Math.abs(this.yd); this.lifetime = (int)(25.0F * (0.8F + 0.2F * level.random.nextFloat())); setSize(3.0F, 3.0F); }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<com.kingodogo.buildscape.particle.GeyserParticleOptions> geyserPlumeProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> {
            double px = x + (level.random.nextFloat() - 0.5F) * 0.2F, py = y + level.random.nextFloat(), pz = z + (level.random.nextFloat() - 0.5F) * 0.2F;
            return new TextureSheetParticle(level, px, py, pz, dx, dy, dz) {
                private final int height = 5 * Math.max(1, type.getWaterBlocks());
                private final double startY = py, maxY = py + height - 1.0D;
                private final float sprayX = (level.random.nextFloat() - 0.5F) * 0.2F, sprayZ = (level.random.nextFloat() - 0.5F) * 0.2F;
                private final float propulsion = (type.getWaterBlocks() == 1 ? 1.5F : 1.0F) * height * 1.45F;
                private final float minSize = this.quadSize * 0.75F * (2.0F + height / 8.0F), maxSize = this.quadSize * 0.75F * (3.0F + height / 8.0F);
                private boolean done;
                { this.hasPhysics = true; this.lifetime = height * 5; this.yd = 0.0D; this.friction = 1.0F; this.gravity = -propulsion; this.quadSize = minSize; setSpriteFromAge(sprites); }
                @Override public void tick() {
                    super.tick(); if (!done && (this.yd < 0.0D || this.y > maxY || this.y == this.yo)) { this.lifetime = Math.min(this.lifetime, this.age + 5); this.friction = 0.0F; done = true; }
                    double p = Math.max(0.0D, Math.min(1.0D, (this.y - startY) / (maxY - startY)));
                    this.gravity = propulsion * (float)Math.pow(p, 3.0D) * 0.12F; this.xd = p * sprayX; this.zd = p * sprayZ; setSpriteFromAge(sprites); this.quadSize = minSize + (float)p * (maxSize - minSize);
                }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }
            };
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> fireflyProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> {
            if (!com.kingodogo.buildscape.particle.FireflyTracker.canSpawn(level, x, y, z)) return null;
            return new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
                {
                    this.lifetime = 200 + level.random.nextInt(101); this.gravity = 0.0F; this.hasPhysics = false;
                    this.xd = dx; this.yd = dy; this.zd = dz; this.quadSize = 0.08F + level.random.nextFloat() * 0.05F;
                    setColor(1.0F, 0.92F + level.random.nextFloat() * 0.06F, 0.55F + level.random.nextFloat() * 0.15F);
                    this.alpha = 0.0F; pickSprite(sprites);
                    com.kingodogo.buildscape.particle.FireflyTracker.register(this, level, x, y, z);
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { remove(); return; }
                    this.xd = Math.max(-0.02D, Math.min(0.02D, this.xd + (this.random.nextFloat() - 0.5F) * 0.002F));
                    this.yd = Math.max(-0.01D, Math.min(0.01D, this.yd + (this.random.nextFloat() - 0.5F) * 0.001F));
                    this.zd = Math.max(-0.02D, Math.min(0.02D, this.zd + (this.random.nextFloat() - 0.5F) * 0.002F));
                    move(this.xd, this.yd, this.zd); com.kingodogo.buildscape.particle.FireflyTracker.update(this, this.x, this.y, this.z);
                    float p = (float)this.age / this.lifetime;
                    this.alpha = Math.max(0.0F, Math.min(1.0F, p < 0.3F ? p / 0.3F : p > 0.7F ? (1.0F - p) / 0.3F : 1.0F));
                }
                @Override public void remove() { super.remove(); com.kingodogo.buildscape.particle.FireflyTracker.remove(this); }
                @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
                @Override public int getLightColor(float partialTick) { return 0xF000F0; }
            };
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> tintableHeartProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            {
                setSpriteFromAge(sprites); this.xd = dx; this.yd = dy == 0.0D ? 0.1D : dy; this.zd = dz;
                this.lifetime = 100; this.quadSize = 0.3F; this.hasPhysics = false;
                float[] color = PillarSparkleParticleLogic.configuredColorOrDefault("#FF0000");
                setColor(color[0], color[1], color[2]); this.alpha = 1.0F;
            }
            @Override public void tick() {
                super.tick(); this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { remove(); return; }
                move(this.xd, this.yd, this.zd); this.xd *= 0.99D; this.yd *= 0.99D; this.zd *= 0.99D;
                if (this.age > this.lifetime - 20) this.alpha = (this.lifetime - this.age) / 20.0F;
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
            @Override public int getLightColor(float partialTick) { return 0xF000F0; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> trailNoteProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            {
                this.lifetime = 60 + level.random.nextInt(40); this.gravity = 0.05F; this.hasPhysics = true;
                this.quadSize = 0.2F + level.random.nextFloat() * 0.1F; this.xd = dx; this.yd = dy; this.zd = dz;
                setSprite(sprites.get(level.random));
                int rgb = java.awt.Color.HSBtoRGB(level.random.nextFloat(), 0.8F, 0.9F);
                setColor(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
            }
            @Override public void tick() {
                super.tick();
                if (this.age > this.lifetime * 0.7F) this.alpha = 1.0F - (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> tintedSporeProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            {
                setSpriteFromAge(sprites); this.gravity = 0.0F;
                this.lifetime = 400 + level.random.nextInt(400); this.hasPhysics = true;
                float[] color = com.kingodogo.buildscape.particle.TintedParticleColorTracker.consumeRgb(x, y, z, 1.0F, 1.0F, 1.0F);
                setColor(color[0], color[1], color[2]);
            }
            @Override public void tick() {
                this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { remove(); return; }
                this.yd -= 0.0005D;
                if (this.level.random.nextInt(8) == 0) {
                    this.xd += (this.level.random.nextDouble() - 0.5D) * 0.001D;
                    this.zd += (this.level.random.nextDouble() - 0.5D) * 0.001D;
                    this.yd += (this.level.random.nextDouble() - 0.5D) * 0.0005D;
                }
                this.xd *= 0.999D; this.zd *= 0.999D; move(this.xd, this.yd, this.zd);
                if (this.age > this.lifetime - 60) this.alpha = (float)(this.lifetime - this.age) / 60.0F;
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> tintedDripProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            private final boolean floating = dy > -0.006D;
            {
                pickSprite(sprites); this.gravity = floating ? 0.0F : 0.02F;
                this.lifetime = floating ? 150 + level.random.nextInt(100) : 300 + level.random.nextInt(150);
                this.hasPhysics = true; this.xd = dx; this.yd = dy; this.zd = dz;
                float[] color = com.kingodogo.buildscape.particle.TintedParticleColorTracker.consumeRgb(x, y, z, 0.92F, 0.58F, 0.84F);
                setColor(color[0], color[1], color[2]);
            }
            @Override public void tick() {
                this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { remove(); return; }
                if (floating) {
                    this.xd = Math.max(-0.03D, Math.min(0.03D, this.xd + (this.random.nextDouble() - 0.5D) * 0.002D)) * 0.995D;
                    this.yd = Math.max(-0.03D, Math.min(0.03D, this.yd + (this.random.nextDouble() - 0.5D) * 0.002D)) * 0.995D;
                    this.zd = Math.max(-0.03D, Math.min(0.03D, this.zd + (this.random.nextDouble() - 0.5D) * 0.002D)) * 0.995D;
                } else { this.yd -= this.gravity; this.xd *= 0.98D; this.zd *= 0.98D; }
                move(this.xd, this.yd, this.zd);
                if (this.age > this.lifetime - 20) this.alpha = (float)(this.lifetime - this.age) / 20.0F;
                if (this.onGround && !floating) remove();
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> snowflakeProvider(
            net.minecraft.client.particle.SpriteSet sprites, boolean still) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            {
                setSpriteFromAge(sprites); pickSprite(sprites);
                this.gravity = still ? 0.0F : 0.05F;
                this.lifetime = 200 + level.random.nextInt(200);
                this.hasPhysics = true;
                this.quadSize = 0.1F + level.random.nextFloat() * 0.1F;
                this.xd = still ? dx : dx + (level.random.nextDouble() - 0.5D) * 0.02D;
                this.yd = dy;
                this.zd = still ? dz : dz + (level.random.nextDouble() - 0.5D) * 0.02D;
                setColor(1.0F, 1.0F, 1.0F);
                this.alpha = 0.8F + level.random.nextFloat() * 0.2F;
            }
            @Override public void tick() {
                super.tick();
                if (this.onGround) { this.remove(); return; }
                this.oRoll = this.roll; this.roll += 0.1F;
                if (this.age > this.lifetime * 0.7F) this.alpha = (1.0F - (this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F)) * 0.8F;
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> cascadeProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z) {
            {
                this.quadSize = 0.5F + level.random.nextFloat() * 0.4F;
                this.lifetime = 30 + level.random.nextInt(30); this.gravity = 0.005F; this.hasPhysics = false;
                this.xd = dx; this.yd = dy; this.zd = dz;
                setColor(1.0F, 1.0F, 1.0F); this.alpha = 0.9F; setSpriteFromAge(sprites);
            }
            @Override public void tick() {
                this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { this.remove(); return; }
                setSpriteFromAge(sprites);
                if (this.level.getFluidState(new net.minecraft.core.BlockPos((int)Math.floor(this.x), (int)Math.floor(this.y), (int)Math.floor(this.z))).is(net.minecraft.tags.FluidTags.WATER)) this.yd += 0.005F;
                else this.yd -= this.gravity;
                move(this.xd, this.yd, this.zd); this.xd *= 0.95D; this.zd *= 0.95D;
                float progress = (float)this.age / this.lifetime;
                if (progress > 0.6F) this.alpha = (1.0F - (progress - 0.6F) / 0.4F) * 0.9F;
            }
            @Override protected int getLightColor(float partialTick) { return 0xF000F0; }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> noxiousGasProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z) {
            {
                this.quadSize = 0.24F + level.random.nextFloat() * 0.18F;
                this.lifetime = 60 + level.random.nextInt(40); this.gravity = -0.003F; this.hasPhysics = true;
                this.xd = dx; this.yd = dy; this.zd = dz;
                setColor(1.0F, 1.0F, 1.0F); this.alpha = 0.8F; setSpriteFromAge(sprites);
            }
            @Override public void tick() {
                this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { this.remove(); return; }
                setSpriteFromAge(sprites); this.yd -= this.gravity; move(this.xd, this.yd, this.zd);
                this.xd *= 0.96D; this.yd *= 0.96D; this.zd *= 0.96D;
                float progress = (float)this.age / this.lifetime;
                if (progress > 0.5F) this.alpha = (1.0F - (progress - 0.5F) / 0.5F) * 0.8F;
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; }
        };
    }

    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> xpProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z, dx, dy, dz) {
            {
                setSpriteFromAge(sprites); pickSprite(sprites);
                this.quadSize = 0.08F + level.random.nextFloat() * 0.08F;
                this.lifetime = 12 + level.random.nextInt(12); this.gravity = 0.0F;
                this.xd = dx; this.yd = dy + 0.02D + level.random.nextDouble() * 0.02D; this.zd = dz;
                setColor(1.0F, 1.0F, 1.0F);
            }
            @Override public void tick() {
                this.xo = this.x; this.yo = this.y; this.zo = this.z;
                if (this.age++ >= this.lifetime) { this.remove(); return; }
                setSpriteFromAge(sprites); this.yd += 0.005D; move(this.xd, this.yd, this.zd);
                this.xd *= 0.85D; this.yd *= 0.85D; this.zd *= 0.85D;
            }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }
            @Override public int getLightColor(float partialTick) { return 0xF000F0; }
        };
    }
    private static net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> sulfurBubbleProvider(net.minecraft.client.particle.SpriteSet sprites) {
        return (type, level, x, y, z, dx, dy, dz) -> new TextureSheetParticle(level, x, y, z) {
            private final double yStart = this.yo, yEnd = this.yo + 3.0D;
            private final float sizeStart = 0.02F + 0.02F * this.random.nextFloat();
            private double yPrevious = y;
            {
                setSprite(sprites.get(level.random)); this.gravity = -0.04F; this.friction = 0.85F; setSize(0.02F, 0.02F);
                this.xd = dx * 0.2D + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
                this.zd = dz * 0.2D + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
                this.quadSize = this.sizeStart; this.lifetime = Integer.MAX_VALUE;
            }
            @Override public void tick() {
                super.tick();
                if (!this.removed && !this.level.getFluidState(new net.minecraft.core.BlockPos((int)Math.floor(this.x), (int)Math.floor(this.y), (int)Math.floor(this.z))).isSourceOfType(net.minecraft.world.level.material.Fluids.WATER)) remove();
                if (!this.removed && (this.y >= this.yEnd || this.y <= this.yPrevious)) remove();
                this.xd += wiggle(); this.zd += wiggle(); move(this.xd, 0.0D, this.zd);
                this.quadSize = this.sizeStart + (float)((this.y - this.yStart) / (this.yEnd - this.yStart)) * (0.15F - this.sizeStart); this.yPrevious = this.y;
            }
            private double wiggle() { return this.random.nextFloat() * 0.003F * (this.random.nextBoolean() ? 1 : -1) * 0.5D; }
            @Override public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }
        };
    }
}
