package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SulfurBubbleParticle extends TextureSheetParticle {
   private final double yStart;
   private final double yEnd;
   private final float sizeStart;
   private double yPrev;

   private SulfurBubbleParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double za, final TextureAtlasSprite sprite) {
      super(level, x, y, z);
      this.setSprite(sprite);
      this.gravity = -0.04F;
      this.friction = 0.85F;
      this.setSize(0.02F, 0.02F);
      this.xd = xa * 0.2D + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.zd = za * 0.2D + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.sizeStart = 0.02F + 0.02F * this.random.nextFloat();
      this.quadSize = this.sizeStart;
      this.lifetime = Integer.MAX_VALUE;
      this.yStart = this.yo;
      this.yEnd = this.yo + 4.0D - 1.0D;
      this.yPrev = y;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.removed && !this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).isSourceOfType(Fluids.WATER)) {
         this.remove();
      }

      if (!this.removed && this.y >= this.yEnd) {
         this.remove();
      }

      if (!this.removed && this.y <= this.yPrev) {
         this.remove();
      }

      this.xd += this.randomHorizontalWiggling();
      this.zd += this.randomHorizontalWiggling();
      this.move(this.xd, 0.0D, this.zd);
      float travelProgress = (float)((this.y - this.yStart) / (this.yEnd - this.yStart));
      this.quadSize = this.sizeStart + travelProgress * (0.15F - this.sizeStart);
      this.yPrev = this.y;
   }

   private double randomHorizontalWiggling() {
      return (double)(this.random.nextFloat() * 0.003F * (float)(this.random.nextBoolean() ? 1 : -1)) * 0.5D;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         this.sprite = sprite;
      }

      @Nullable
      @Override
      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux) {
         Random random = level.getRandom();
         return new SulfurBubbleParticle(level, x, y, z, xAux, zAux, this.sprite.get(random));
      }
   }
}
