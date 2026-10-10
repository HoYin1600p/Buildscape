package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.particle.GeyserBaseParticleOptions;
import com.kingodogo.buildscape.particle.GeyserParticleOptions;
import com.kingodogo.buildscape.particle.ModParticles;
import com.kingodogo.buildscape.particle.PillarSparkleParticleLogic;
import com.kingodogo.buildscape.particle.TintedParticleColorTracker;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public final class ParticleFactory {

    private static final int[] CONFETTI_COLORS = {0xFF0000, 0x00FFFF, 0x1919EA, 0x3CDFFF, 0xFFFF00, 0xFF5C00, 0xBFFE00, 0x39FF14, 0xF686B7, 0xAB87FF, 0xFF00FF};

    private ParticleFactory() {}

    public interface Registrar {
        <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory);
        <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider);
    }
    private static Registrar registrar;
    private static boolean registered;

    public static void registerProviders(Registrar target) {
        registrar = java.util.Objects.requireNonNull(target);
        registerProviders();
    }

    private static <T extends ParticleOptions> void register(ParticleType<T> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        registrar.register(type, factory);
    }

    private static <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
        registrar.registerDirect(type, provider);
    }

    public static void registerProviders() {
        if (registered) return;
        java.util.Objects.requireNonNull(registrar, "Particle providers must be registered through the loader hook");
        registerAllProviders();
        registered = true;
    }

    private static void registerAllProviders() {
        register(ModParticles.GLOW_LIME_SPARKLE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                private final TextureAtlasSprite baseSprite = sprites.first();
                private int currentFrame;
                {
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
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }
        );

        register(ModParticles.TINTED_DRIP_FALL.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                private final boolean isFloating = dy > -0.006D;
                {
                    setSpriteFromAge(sprites);
                    this.gravity = isFloating ? 0.0F : 0.02F;
                    this.lifetime = isFloating ? 150 + random.nextInt(100) : 300 + random.nextInt(150);
                    this.hasPhysics = true;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    float[] c = TintedParticleColorTracker.consumeRgb(x, y, z, 0.92F, 0.58F, 0.84F);
                    setColor(c[0], c[1], c[2]);
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    if (isFloating) {
                        this.xd += (random.nextDouble() - 0.5D) * 0.002D;
                        this.yd += (random.nextDouble() - 0.5D) * 0.002D;
                        this.zd += (random.nextDouble() - 0.5D) * 0.002D;
                        this.xd = Math.max(-0.03D, Math.min(0.03D, this.xd)) * 0.995D;
                        this.yd = Math.max(-0.03D, Math.min(0.03D, this.yd)) * 0.995D;
                        this.zd = Math.max(-0.03D, Math.min(0.03D, this.zd)) * 0.995D;
                    } else {
                        this.yd -= this.gravity;
                        this.xd *= 0.98D; this.zd *= 0.98D;
                    }
                    this.move(this.xd, this.yd, this.zd);
                    if (this.age > this.lifetime - 20) {
                        this.alpha = (float) (this.lifetime - this.age) / 20.0F;
                    }
                    if (this.onGround && !isFloating) { this.remove(); }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.TINTED_SPORE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.0F;
                    this.lifetime = 400 + random.nextInt(400);
                    this.hasPhysics = true;
                    float[] c = TintedParticleColorTracker.consumeRgb(x, y, z, 1.0F, 1.0F, 1.0F);
                    setColor(c[0], c[1], c[2]);
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { remove(); return; }
                    this.yd -= 0.0005D;
                    if (random.nextInt(8) == 0) {
                        this.xd += (random.nextDouble() - 0.5D) * 0.001D;
                        this.zd += (random.nextDouble() - 0.5D) * 0.001D;
                        this.yd += (random.nextDouble() - 0.5D) * 0.0005D;
                    }
                    this.xd *= 0.999D; this.zd *= 0.999D; move(this.xd, this.yd, this.zd);
                    if (this.age > this.lifetime - 60) this.alpha = (float)(this.lifetime - this.age) / 60.0F;
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.SNOWFLAKE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.05F;
                    this.lifetime = 200 + random.nextInt(200);
                    this.hasPhysics = true;
                    this.quadSize = 0.1F + random.nextFloat() * 0.1F;
                    this.xd = dx + (random.nextDouble() - 0.5) * 0.02;
                    this.yd = dy;
                    this.zd = dz + (random.nextDouble() - 0.5) * 0.02;
                    this.alpha = 0.8F + random.nextFloat() * 0.2F;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.onGround) { this.remove(); return; }
                    this.oRoll = this.roll;
                    this.roll += 0.1F;
                    if (this.age > this.lifetime * 0.7F) {
                        this.alpha = (1.0F - (float)(this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F)) * 0.8F;
                    }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.SNOWFLAKE_STILL.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.0F;
                    this.lifetime = 200 + random.nextInt(200);
                    this.hasPhysics = true;
                    this.quadSize = 0.1F + random.nextFloat() * 0.1F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    setColor(1.0F, 1.0F, 1.0F);
                    this.alpha = 0.8F + random.nextFloat() * 0.2F;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.onGround) { this.remove(); return; }
                    this.oRoll = this.roll;
                    this.roll += 0.1F;
                    if (this.age > this.lifetime * 0.7F) {
                        this.alpha = (1.0F - (float)(this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F)) * 0.8F;
                    }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.CONFETTI.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                private final float rotationSpeed;
                {
                    setSpriteFromAge(sprites);
                    int rgb = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.length)];
                    setColor(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
                    this.gravity = 0.05F + random.nextFloat() * 0.04F;
                    this.lifetime = 70 + random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.08F + random.nextFloat() * 0.12F;
                    this.roll = random.nextFloat() * (float)(Math.PI * 2.0D);
                    this.oRoll = this.roll;
                    this.rotationSpeed = (random.nextFloat() - 0.5F) * 0.5F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    this.alpha = 1.0F;
                }
                @Override public void tick() {
                    super.tick();
                    this.oRoll = this.roll;
                    this.roll += this.rotationSpeed;
                    this.xd *= 0.98D; this.zd *= 0.98D;
                    if (this.age % 5 == 0) { this.xd += (random.nextDouble() - 0.5D) * 0.01D; this.zd += (random.nextDouble() - 0.5D) * 0.01D; }
                    if (this.age > this.lifetime * 0.8F) this.alpha = 1.0F - (float)(this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F);
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.TINTABLE_HEART.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.xd = dx; this.yd = dy == 0.0D ? 0.1D : dy; this.zd = dz;
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
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }
        );

        register(ModParticles.CAKE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = 0.05F;
                    this.lifetime = 60 + random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.2F + random.nextFloat() * 0.1F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.age > this.lifetime * 0.7F) {
                        this.alpha = 1.0F - (float)(this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
                    }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.CHERRY.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                private final float rotationSpeed = (random.nextFloat() - 0.5F) * 0.1F;
                {
                    this.lifetime = 60 + random.nextInt(40);
                    this.gravity = 0.05F;
                    this.hasPhysics = true;
                    this.roll = random.nextFloat() * ((float)Math.PI * 2.0F);
                    this.oRoll = this.roll;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    int spriteIndex = random.nextInt(6) + (random.nextBoolean() ? 6 : 0);
                    setSprite(sprites.get(spriteIndex, 12));
                    this.quadSize *= 0.5F;
                    setColor(1.0F, 1.0F, 1.0F);
                }
                @Override public void tick() {
                    super.tick();
                    this.oRoll = this.roll; this.roll += this.rotationSpeed;
                    if (this.age > this.lifetime * 0.7F) this.alpha = 1.0F - (float)(this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.BUBBLE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.gravity = -0.01F;
                    this.lifetime = 60 + random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.1F + random.nextFloat() * 0.1F;
                    this.xd = dx + (random.nextDouble() - 0.5) * 0.02;
                    this.yd = dy;
                    this.zd = dz + (random.nextDouble() - 0.5) * 0.02;
                    this.alpha = 0.8F + random.nextFloat() * 0.2F;
                }
                @Override public void tick() {
                    super.tick();
                    this.oRoll = this.roll;
                    this.roll += 0.05F;
                    if (this.age > this.lifetime * 0.8F) {
                        this.alpha = (1.0F - (float)(this.age - this.lifetime * 0.8F) / (this.lifetime * 0.2F)) * 0.6F;
                    }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }
        );

        register(ModParticles.TRAIL_NOTE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.get(random)) {
                {
                    this.gravity = 0.05F;
                    this.lifetime = 60 + random.nextInt(40);
                    this.hasPhysics = true;
                    this.quadSize = 0.2F + random.nextFloat() * 0.1F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    float hue = random.nextFloat();
                    int rgb = java.awt.Color.HSBtoRGB(hue, 0.8F, 0.9F);
                    setColor(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F);
                }
                @Override public void tick() {
                    super.tick();
                    if (this.age > this.lifetime * 0.7F) {
                        this.alpha = 1.0F - (float)(this.age - this.lifetime * 0.7F) / (this.lifetime * 0.3F);
                    }
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.COLORED_SMOKE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    this.quadSize = 0.3F;
                    this.lifetime = 200 + random.nextInt(100);
                    this.gravity = 3.0E-6F;
                    this.hasPhysics = true;
                    this.xd = dx; this.yd = dy; this.zd = dz;
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
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.CASCADE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.quadSize = 0.5F + random.nextFloat() * 0.4F;
                    this.lifetime = 30 + random.nextInt(30);
                    this.gravity = 0.005F;
                    this.hasPhysics = false;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    setColor(1.0F, 1.0F, 1.0F); this.alpha = 0.9F;
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    setSpriteFromAge(sprites);
                    if (this.level.getFluidState(net.minecraft.core.BlockPos.containing(this.x, this.y, this.z)).is(net.minecraft.tags.FluidTags.WATER)) this.yd += 0.005F;
                    else this.yd -= this.gravity;
                    move(this.xd, this.yd, this.zd); this.xd *= 0.95D; this.zd *= 0.95D;
                    float progress = (float)this.age / this.lifetime;
                    if (progress > 0.6F) this.alpha = (1.0F - (progress - 0.6F) / 0.4F) * 0.9F;
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }
        );

        register(ModParticles.NOXIOUS_GAS.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.quadSize = 0.24F + random.nextFloat() * 0.18F;
                    this.lifetime = 60 + random.nextInt(40);
                    this.gravity = -0.003F; this.hasPhysics = true;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    setColor(1.0F, 1.0F, 1.0F); this.alpha = 0.8F;
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    setSpriteFromAge(sprites); this.yd -= this.gravity; move(this.xd, this.yd, this.zd);
                    this.xd *= 0.96D; this.yd *= 0.96D; this.zd *= 0.96D;
                    float progress = (float)this.age / this.lifetime;
                    if (progress > 0.5F) this.alpha = (1.0F - (progress - 0.5F) / 0.5F) * 0.8F;
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.COPPER_FIRE_FLAME.get(), FlameParticle.Provider::new);

        register(ModParticles.FIREFLY.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> {
            if (!com.kingodogo.buildscape.particle.FireflyTracker.canSpawn(level, x, y, z)) return null;
            return new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.get(random)) {
                {
                    this.gravity = 0.0F;
                    this.lifetime = 200 + random.nextInt(101);
                    this.hasPhysics = false;
                    this.quadSize = 0.08F + random.nextFloat() * 0.05F;
                    this.xd = dx; this.yd = dy; this.zd = dz;
                    setColor(1.0F, 0.92F + random.nextFloat() * 0.06F, 0.55F + random.nextFloat() * 0.15F);
                    this.alpha = 0.0F;
                    com.kingodogo.buildscape.particle.FireflyTracker.register(this, level, x, y, z);
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    this.xd = Math.max(-0.02D, Math.min(0.02D, this.xd + (random.nextFloat() - 0.5F) * 0.002F));
                    this.yd = Math.max(-0.01D, Math.min(0.01D, this.yd + (random.nextFloat() - 0.5F) * 0.001F));
                    this.zd = Math.max(-0.02D, Math.min(0.02D, this.zd + (random.nextFloat() - 0.5F) * 0.002F));
                    this.move(this.xd, this.yd, this.zd);
                    com.kingodogo.buildscape.particle.FireflyTracker.update(this, this.x, this.y, this.z);
                    float p = (float)this.age / (float)this.lifetime;
                    this.alpha = Math.max(0.0F, Math.min(1.0F, p < 0.3F ? p / 0.3F : p > 0.7F ? (1.0F - p) / 0.3F : 1.0F));
                }
                @Override public void remove() { super.remove(); com.kingodogo.buildscape.particle.FireflyTracker.remove(this); }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.TRANSLUCENT; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }; }
        );

        register(ModParticles.SULFUR_BUBBLES.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, sprites.get(random)) {
                private final double yStart = this.yo, yEnd = this.yo + 3.0D;
                private final float sizeStart = 0.02F + 0.02F * random.nextFloat();
                private double yPrevious = y;
                {
                    this.gravity = -0.04F; this.friction = 0.85F; setSize(0.02F, 0.02F);
                    this.xd = dx * 0.2D + (random.nextFloat() * 2.0F - 1.0F) * 0.02F;
                    this.zd = dz * 0.2D + (random.nextFloat() * 2.0F - 1.0F) * 0.02F;
                    this.quadSize = this.sizeStart; this.lifetime = Integer.MAX_VALUE;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.isAlive() && !this.level.getFluidState(net.minecraft.core.BlockPos.containing(this.x, this.y, this.z)).isSourceOfType(net.minecraft.world.level.material.Fluids.WATER)) remove();
                    if (this.isAlive() && (this.y >= this.yEnd || this.y <= this.yPrevious)) remove();
                    this.xd += wiggle(); this.zd += wiggle(); move(this.xd, 0.0D, this.zd);
                    this.quadSize = this.sizeStart + (float)((this.y - this.yStart) / (this.yEnd - this.yStart)) * (0.15F - this.sizeStart); this.yPrevious = this.y;
                }
                private double wiggle() { return random.nextFloat() * 0.003F * (random.nextBoolean() ? 1 : -1) * 0.5D; }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.OPAQUE; }
            }
        );

        registerDirect(ModParticles.GEYSER.get(),
            (type, level, x, y, z, dx, dy, dz, random) -> new NoRenderParticle(level, x, y, z, dx, dy, dz) {
                private final int waterBlocks = type.getWaterBlocks();
                {
                    this.lifetime = 20;
                }
                @Override public void tick() {
                    super.tick();
                    if (this.age % 2 == 0) {
                        GeyserBaseParticleOptions base =
                                new GeyserBaseParticleOptions(ModParticles.GEYSER_BASE.get(), this.waterBlocks, 1.5F);
                        for (int i = 0; i < 2; ++i) this.level.addAlwaysVisibleParticle(base, true, this.x, this.y, this.z, dx, dy, dz);
                    }
                    GeyserParticleOptions plume =
                            new GeyserParticleOptions(ModParticles.GEYSER_PLUME.get(), this.waterBlocks);
                    for (int i = 0; i < this.waterBlocks + 2; ++i) {
                        this.level.addAlwaysVisibleParticle(plume, true, this.x, this.y, this.z, dx, dy, dz);
                    }
                    if (this.age % 10 == 0) {
                        GeyserBaseParticleOptions poof =
                                new GeyserBaseParticleOptions(ModParticles.GEYSER_POOF.get(), this.waterBlocks, 2.0F);
                        for (int i = 0; i < 20; ++i) {
                            this.level.addAlwaysVisibleParticle(poof, true, this.x, this.y, this.z, dx, dy, dz);
                        }
                    }
                }
            }
        );

        register(ModParticles.GEYSER_BASE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new BaseAshSmokeParticle(
                    level, x + (random.nextFloat() - 0.5F) * 0.5F, y + (random.nextFloat() - 0.5F) * 0.5F + 0.2D, z + (random.nextFloat() - 0.5F) * 0.5F,
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    dx, dy, dz,
                    3.0F + 0.125F * (float)type.getWaterBlocks(),
                    sprites, 0.0F, 0, 0.0F, true) {
                {
                    this.friction = 0.725F;
                    setColor(1.0F, 1.0F, 1.0F);
                    this.yd = Math.abs(this.yd);
                    this.lifetime = (int)(25.0F * (0.8F + 0.2F * random.nextFloat()));
                    setSize(3.0F, 3.0F);
                }
                @Override public SingleQuadParticle.Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.GEYSER_POOF.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new BaseAshSmokeParticle(
                    level, x + (random.nextFloat() - 0.5F) * 0.5F, y + (random.nextFloat() - 0.5F) * 0.5F + 0.2D, z + (random.nextFloat() - 0.5F) * 0.5F,
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    type.getBurstImpulseBase() + 0.25F * (float)type.getWaterBlocks(),
                    dx, dy, dz,
                    3.0F + 0.125F * (float)type.getWaterBlocks(),
                    sprites, 0.0F, 0, 0.0F, true) {
                {
                    this.friction = 0.725F;
                    setColor(1.0F, 1.0F, 1.0F);
                    this.yd = Math.abs(this.yd);
                    this.lifetime = (int)(25.0F * (0.8F + 0.2F * random.nextFloat()));
                    setSize(3.0F, 3.0F);
                }
                @Override public SingleQuadParticle.Layer getLayer() { return Layer.TRANSLUCENT; }
            }
        );

        register(ModParticles.GEYSER_PLUME.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> {
            double px = x + (random.nextFloat() - 0.5F) * 0.2F, py = y + random.nextFloat(), pz = z + (random.nextFloat() - 0.5F) * 0.2F;
            return new SingleQuadParticle(level, px, py, pz, dx, dy, dz, sprites.first()) {
                private final int plumeHeight = 5 * Math.max(1, type.getWaterBlocks());
                private final double startY = py;
                private final double maxY = py + (double)plumeHeight - 1.0D;
                private final float horizontalSprayX = (random.nextFloat() - 0.5F) * 0.2F;
                private final float horizontalSprayZ = (random.nextFloat() - 0.5F) * 0.2F;
                private final float initialPropulsion = (type.getWaterBlocks() == 1 ? 1.5F : 1.0F) * (float)plumeHeight * 1.45F;
                private final float minSize = this.quadSize * 0.75F * (2.0F + (float)plumeHeight / 8.0F);
                private final float maxSize = this.quadSize * 0.75F * (3.0F + (float)plumeHeight / 8.0F);
                private boolean done;
                {
                    this.hasPhysics = true;
                    this.lifetime = plumeHeight * 5;
                    this.yd = 0.0D;
                    this.friction = 1.0F;
                    this.gravity = -this.initialPropulsion;
                    this.quadSize = this.minSize;
                    setSpriteFromAge(sprites);
                }
                @Override public void tick() {
                    super.tick();
                    if (!this.done && (this.yd < 0.0D || this.y > this.maxY || this.y == this.yo)) {
                        this.lifetime = Math.min(this.lifetime, this.age + 5);
                        this.friction = 0.0F;
                        this.done = true;
                    }
                    double yProgress = Math.max(0.0, Math.min(1.0, (this.y - this.startY) / (this.maxY - this.startY)));
                    this.gravity = this.initialPropulsion * (float)Math.pow(yProgress, 3.0) * 0.12F;
                    this.xd = yProgress * this.horizontalSprayX;
                    this.zd = yProgress * this.horizontalSprayZ;
                    setSpriteFromAge(sprites);
                    this.quadSize = this.minSize + (float)yProgress * (this.maxSize - this.minSize);
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.OPAQUE; }
            }; }
        );

        register(ModParticles.XP_PARTICLE.get(), sprites ->
            (type, level, x, y, z, dx, dy, dz, random) -> new SingleQuadParticle(level, x, y, z, dx, dy, dz, sprites.first()) {
                {
                    setSpriteFromAge(sprites);
                    this.quadSize = 0.08F + random.nextFloat() * 0.08F;
                    this.lifetime = 12 + random.nextInt(12); this.gravity = 0.0F;
                    this.xd = dx; this.yd = dy + 0.02D + random.nextDouble() * 0.02D; this.zd = dz;
                    setColor(1.0F, 1.0F, 1.0F);
                }
                @Override public void tick() {
                    this.xo = this.x; this.yo = this.y; this.zo = this.z;
                    if (this.age++ >= this.lifetime) { this.remove(); return; }
                    setSpriteFromAge(sprites); this.yd += 0.005D; move(this.xd, this.yd, this.zd);
                    this.xd *= 0.85D; this.yd *= 0.85D; this.zd *= 0.85D;
                }
                @Override public ParticleRenderType getGroup() { return ParticleRenderType.SINGLE_QUADS; }
                @Override protected Layer getLayer() { return Layer.OPAQUE; }
                @Override protected int getLightCoords(float partialTick) { return 0xF000F0; }
            }
        );
    }
}
