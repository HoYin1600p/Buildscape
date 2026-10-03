package com.kingodogo.buildscape.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HazeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float rotSpeed;
    private final float maxAlpha;
    private final float baseSize;

    protected HazeParticle(ClientLevel level, double x, double y, double z,
                           double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.baseSize = 2.0F + level.random.nextFloat() * 1.5F;
        this.quadSize = this.baseSize;
        this.lifetime = 240 + level.random.nextInt(120);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.xd = (level.random.nextFloat() - 0.5D) * 0.0003D;
        this.yd = 0.0D;
        this.zd = (level.random.nextFloat() - 0.5D) * 0.0003D;

        this.rotSpeed = (level.random.nextFloat() - 0.5F) * 0.0006F;
        this.roll = level.random.nextFloat() * ((float) Math.PI * 2.0F);
        this.oRoll = this.roll;

        if (xSpeed > 0.01 || ySpeed > 0.01 || zSpeed > 0.01) {
            float var = (level.random.nextFloat() - 0.5F) * 0.03F;
            float r = Mth.clamp((float) xSpeed + var, 0.0F, 1.0F);
            float g = Mth.clamp((float) ySpeed + var, 0.0F, 1.0F);
            float b = Mth.clamp((float) zSpeed + var, 0.0F, 1.0F);
            this.setColor(r, g, b);
        } else {
            float tint = 0.94F + level.random.nextFloat() * 0.06F;
            this.setColor(tint, tint, tint);
        }

        this.maxAlpha = 0.32F + level.random.nextFloat() * 0.12F;
        this.alpha = 0.0F;

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        this.move(this.xd, this.yd, this.zd);

        this.xd *= 0.99;
        this.zd *= 0.99;

        float progress = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseSize * (1.0F + progress * 0.20F);

        if (progress < 0.20F) {
            this.alpha = (progress / 0.20F) * this.maxAlpha;
        } else if (progress > 0.65F) {
            this.alpha = (1.0F - (progress - 0.65F) / 0.35F) * this.maxAlpha;
        } else {
            this.alpha = this.maxAlpha;
        }

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        float px = (float)(Mth.lerp((double)partialTick, this.xo, this.x) - camPos.x());
        float py = (float)(Mth.lerp((double)partialTick, this.yo, this.y) - camPos.y());
        float pz = (float)(Mth.lerp((double)partialTick, this.zo, this.z) - camPos.z());

        float size = this.getQuadSize(partialTick);
        float angle = Mth.lerp(partialTick, this.oRoll, this.roll);

        float cos = Mth.cos(angle) * size;
        float sin = Mth.sin(angle) * size;

        float ux = cos;
        float uz = sin;
        float vx = -sin;
        float vz = cos;

        Vector3f p0 = new Vector3f(px - ux - vx, py, pz - uz - vz);
        Vector3f p1 = new Vector3f(px + ux - vx, py, pz + uz - vz);
        Vector3f p2 = new Vector3f(px + ux + vx, py, pz + uz + vz);
        Vector3f p3 = new Vector3f(px - ux + vx, py, pz - uz + vz);

        int light = this.getLightColor(partialTick);
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        // Top Face (visible looking down)
        consumer.vertex(p3.x(), p3.y(), p3.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p2.x(), p2.y(), p2.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p1.x(), p1.y(), p1.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p0.x(), p0.y(), p0.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

        // Bottom Face (visible looking up)
        consumer.vertex(p0.x(), p0.y(), p0.z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p1.x(), p1.y(), p1.z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p2.x(), p2.y(), p2.z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(p3.x(), p3.y(), p3.z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HazeParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
