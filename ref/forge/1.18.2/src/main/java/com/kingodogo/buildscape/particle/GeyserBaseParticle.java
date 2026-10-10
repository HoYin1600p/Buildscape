package com.kingodogo.buildscape.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class GeyserBaseParticle extends BaseAshSmokeParticle {

   private GeyserBaseParticle(final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final int waterBlocks, final float burstImpulseBase, final SpriteSet sprites) {
      super(level, x, y, z, burstImpulseBase + 0.25F * (float)waterBlocks, burstImpulseBase + 0.25F * (float)waterBlocks, burstImpulseBase + 0.25F * (float)waterBlocks, xAux, yAux, zAux, 3.0F + 0.125F * (float)waterBlocks, sprites, 0.0F, 0, 0.0F, true);
      this.friction = 0.725F;
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.yd = Math.abs(this.yd);
      float lifetimeFactor = 0.8F + 0.2F * level.getRandom().nextFloat();
      this.lifetime = (int)(25.0F * lifetimeFactor);
      this.setSize(3.0F, 3.0F);
      this.setPos(x, y, z);
   }

   public static class Provider implements ParticleProvider<GeyserBaseParticleOptions> {
      private final SpriteSet sprites;

      public Provider(final SpriteSet sprites) {
         this.sprites = sprites;
      }

      @Nullable
      @Override
      public Particle createParticle(final GeyserBaseParticleOptions options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux) {
         Random random = level.getRandom();
         double randomX = x + (double)((random.nextFloat() - 0.5F) * 0.5F);
         double randomY = y + (double)((random.nextFloat() - 0.5F) * 0.5F) + 0.2D;
         double randomZ = z + (double)((random.nextFloat() - 0.5F) * 0.5F);
         return new GeyserBaseParticle(level, randomX, randomY, randomZ, xAux, yAux, zAux, options.getWaterBlocks(), options.getBurstImpulseBase(), this.sprites);
      }
   }
}
