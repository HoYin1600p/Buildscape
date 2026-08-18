package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.util.Mth;

public class DustParticleBase extends SingleQuadParticle {
   private final SpriteSet sprites;

   protected DustParticleBase(final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final ScalableParticleOptionsBase options, final SpriteSet sprites) {
      super(level, x, y, z, xAux, yAux, zAux, sprites.first());
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = sprites;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      this.quadSize *= 0.75F * options.getScale();
      int baseLifetime = (int)(8.0D / (this.random.nextDouble() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)baseLifetime * options.getScale(), 1.0F);
      this.setSpriteFromAge(sprites);
   }

   protected float randomizeColor(final float color, final float baseFactor) {
      return (this.random.nextFloat() * 0.2F + 0.8F) * color * baseFactor;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public float getQuadSize(final float a) {
      return this.quadSize * Mth.clamp(((float)this.age + a) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}
