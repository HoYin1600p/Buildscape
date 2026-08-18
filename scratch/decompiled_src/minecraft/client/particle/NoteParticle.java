package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class NoteParticle extends SingleQuadParticle {
   private NoteParticle(final ClientLevel level, final double x, final double y, final double z, final double color, final TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0D, 0.0D, 0.0D, sprite);
      this.friction = 0.66F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd *= (double)0.01F;
      this.yd *= (double)0.01F;
      this.zd *= (double)0.01F;
      this.yd += 0.2D;
      this.rCol = Math.max(0.0F, Mth.sin((double)(((float)color + 0.0F) * ((float)Math.PI * 2F))) * 0.65F + 0.35F);
      this.gCol = Math.max(0.0F, Mth.sin((double)(((float)color + 0.33333334F) * ((float)Math.PI * 2F))) * 0.65F + 0.35F);
      this.bCol = Math.max(0.0F, Mth.sin((double)(((float)color + 0.6666667F) * ((float)Math.PI * 2F))) * 0.65F + 0.35F);
      this.quadSize *= 1.5F;
      this.lifetime = 6;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public float getQuadSize(final float a) {
      return this.quadSize * Mth.clamp(((float)this.age + a) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new NoteParticle(level, x, y, z, xAux, this.sprite.get(random));
      }
   }
}
