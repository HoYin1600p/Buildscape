package net.minecraft.client.particle;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.FireworkExplosion;

public class FireworkParticles {
   public static class FlashProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public FlashProvider(final SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(final ColorParticleOption options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         FireworkParticles.OverlayParticle particle = new FireworkParticles.OverlayParticle(level, x, y, z, this.sprite.get(random));
         particle.setColor(options.getRed(), options.getGreen(), options.getBlue());
         particle.setAlpha(options.getAlpha());
         return particle;
      }
   }

   public static class OverlayParticle extends SingleQuadParticle {
      private OverlayParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
         super(level, x, y, z, sprite);
         this.lifetime = 4;
      }

      public SingleQuadParticle.Layer getLayer() {
         return SingleQuadParticle.Layer.TRANSLUCENT;
      }

      public void extract(final QuadParticleRenderState particleTypeRenderState, final Camera camera, final float partialTickTime) {
         this.setAlpha(0.6F - ((float)this.age + partialTickTime - 1.0F) * 0.25F * 0.5F);
         super.extract(particleTypeRenderState, camera, partialTickTime);
      }

      public float getQuadSize(final float a) {
         return 7.1F * Mth.sin((double)(((float)this.age + a - 1.0F) * 0.25F * (float)Math.PI));
      }
   }

   private static class SparkParticle extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean twinkle;
      private final ParticleEngine engine;
      private float fadeR;
      private float fadeG;
      private float fadeB;
      private boolean hasFade;

      private SparkParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za, final ParticleEngine engine, final SpriteSet sprites) {
         super(level, x, y, z, sprites, 0.1F);
         this.xd = xa;
         this.yd = ya;
         this.zd = za;
         this.engine = engine;
         this.quadSize *= 0.75F;
         this.lifetime = 48 + this.random.nextInt(12);
         this.setSpriteFromAge(sprites);
      }

      public void setTrail(final boolean trail) {
         this.trail = trail;
      }

      public void setTwinkle(final boolean twinkle) {
         this.twinkle = twinkle;
      }

      public void extract(final QuadParticleRenderState particleTypeRenderState, final Camera camera, final float partialTickTime) {
         if (!this.twinkle || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.extract(particleTypeRenderState, camera, partialTickTime);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
            FireworkParticles.SparkParticle sparkParticle = new FireworkParticles.SparkParticle(this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.engine, this.sprites);
            sparkParticle.setAlpha(0.99F);
            sparkParticle.setColor(this.rCol, this.gCol, this.bCol);
            sparkParticle.age = sparkParticle.lifetime / 2;
            if (this.hasFade) {
               sparkParticle.hasFade = true;
               sparkParticle.fadeR = this.fadeR;
               sparkParticle.fadeG = this.fadeG;
               sparkParticle.fadeB = this.fadeB;
            }

            sparkParticle.twinkle = this.twinkle;
            this.engine.add(sparkParticle);
         }

      }
   }

   public static class SparkProvider implements ParticleProvider {
      private final SpriteSet sprites;

      public SparkProvider(final SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         FireworkParticles.SparkParticle particle = new FireworkParticles.SparkParticle(level, x, y, z, xAux, yAux, zAux, Minecraft.getInstance().particleEngine, this.sprites);
         particle.setAlpha(0.99F);
         return particle;
      }
   }

   public static class Starter extends NoRenderParticle {
      private static final double[][] CREEPER_PARTICLE_COORDS = new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}};
      private static final double[][] STAR_PARTICLE_COORDS = new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}};
      private final boolean playSound;
      private int life;
      private final ParticleEngine engine;
      private final List explosions;
      private boolean twinkleDelay;

      public Starter(final ClientLevel level, final double x, final double y, final double z, final double xd, final double yd, final double zd, final ParticleEngine engine, final List explosions, final boolean playSound) {
         super(level, x, y, z);
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.engine = engine;
         this.playSound = playSound;
         if (explosions.isEmpty()) {
            throw new IllegalArgumentException("Cannot create firework starter with no explosions");
         } else {
            this.explosions = explosions;
            this.lifetime = explosions.size() * 2 - 1;

            for(FireworkExplosion explosion : explosions) {
               if (explosion.hasTwinkle()) {
                  this.twinkleDelay = true;
                  this.lifetime += 15;
                  break;
               }
            }

         }
      }

      public void tick() {
         if (this.life == 0 && this.playSound) {
            boolean farEffect = this.isFarAwayFromCamera();
            boolean largeExplosion = false;
            if (this.explosions.size() >= 3) {
               largeExplosion = true;
            } else {
               for(FireworkExplosion explosion : this.explosions) {
                  if (explosion.shape() == FireworkExplosion.Shape.LARGE_BALL) {
                     largeExplosion = true;
                     break;
                  }
               }
            }

            SoundEvent sound;
            if (largeExplosion) {
               sound = farEffect ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               sound = farEffect ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
            }

            this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if (this.life % 2 == 0 && this.life / 2 < this.explosions.size()) {
            int eIndex = this.life / 2;
            FireworkExplosion explosion = (FireworkExplosion)this.explosions.get(eIndex);
            boolean trail = explosion.hasTrail();
            boolean twinkle = explosion.hasTwinkle();
            IntList colors = explosion.colors();
            IntList fadeColors = explosion.fadeColors();
            if (colors.isEmpty()) {
               colors = IntList.of(DyeColor.BLACK.getFireworkColor());
            }

            switch (explosion.shape()) {
               case SMALL_BALL:
                  this.createParticleBall(0.25D, 2, colors, fadeColors, trail, twinkle);
                  break;
               case LARGE_BALL:
                  this.createParticleBall(0.5D, 4, colors, fadeColors, trail, twinkle);
                  break;
               case STAR:
                  this.createParticleShape(0.5D, STAR_PARTICLE_COORDS, colors, fadeColors, trail, twinkle, false);
                  break;
               case CREEPER:
                  this.createParticleShape(0.5D, CREEPER_PARTICLE_COORDS, colors, fadeColors, trail, twinkle, true);
                  break;
               case BURST:
                  this.createParticleBurst(colors, fadeColors, trail, twinkle);
            }

            int color = colors.getInt(0);
            this.engine.createParticle(ColorParticleOption.create(ParticleTypes.FLASH, color), this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }

         ++this.life;
         if (this.life > this.lifetime) {
            if (this.twinkleDelay && this.playSound) {
               boolean farEffect = this.isFarAwayFromCamera();
               SoundEvent sound = farEffect ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
               this.level.playLocalSound(this.x, this.y, this.z, sound, SoundSource.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }

      }

      private boolean isFarAwayFromCamera() {
         Minecraft instance = Minecraft.getInstance();
         return instance.gameRenderer.mainCamera().position().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
      }

      private void createParticle(final double x, final double y, final double z, final double xa, final double ya, final double za, final IntList rgbColors, final IntList fadeColors, final boolean trail, final boolean twinkle) {
         FireworkParticles.SparkParticle sparkParticle = (FireworkParticles.SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, x, y, z, xa, ya, za);
         sparkParticle.setTrail(trail);
         sparkParticle.setTwinkle(twinkle);
         sparkParticle.setAlpha(0.99F);
         sparkParticle.setColor(Util.getRandom((List)rgbColors, this.random));
         if (!fadeColors.isEmpty()) {
            sparkParticle.setFadeColor(Util.getRandom((List)fadeColors, this.random));
         }

      }

      private void createParticleBall(final double baseSpeed, final int steps, final IntList rgbColors, final IntList fadeColors, final boolean trail, final boolean twinkle) {
         double xx = this.x;
         double yy = this.y;
         double zz = this.z;

         for(int yStep = -steps; yStep <= steps; ++yStep) {
            for(int xStep = -steps; xStep <= steps; ++xStep) {
               for(int zStep = -steps; zStep <= steps; ++zStep) {
                  double xa = (double)xStep + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double ya = (double)yStep + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double za = (double)zStep + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double len = Math.sqrt(xa * xa + ya * ya + za * za) / baseSpeed + this.random.nextGaussian() * 0.05D;
                  this.createParticle(xx, yy, zz, xa / len, ya / len, za / len, rgbColors, fadeColors, trail, twinkle);
                  if (yStep != -steps && yStep != steps && xStep != -steps && xStep != steps) {
                     zStep += steps * 2 - 1;
                  }
               }
            }
         }

      }

      private void createParticleShape(final double baseSpeed, final double[][] coords, final IntList rgbColors, final IntList fadeColors, final boolean trail, final boolean twinkle, final boolean flat) {
         double sx = coords[0][0];
         double sy = coords[0][1];
         this.createParticle(this.x, this.y, this.z, sx * baseSpeed, sy * baseSpeed, 0.0D, rgbColors, fadeColors, trail, twinkle);
         float baseAngle = this.random.nextFloat() * (float)Math.PI;
         double angleMod = flat ? 0.034D : 0.34D;

         for(int angleStep = 0; angleStep < 3; ++angleStep) {
            double angle = (double)baseAngle + (double)((float)angleStep * (float)Math.PI) * angleMod;
            double ox = sx;
            double oy = sy;

            for(int c = 1; c < coords.length; ++c) {
               double tx = coords[c][0];
               double ty = coords[c][1];

               for(double subStep = 0.25D; subStep <= 1.0D; subStep += 0.25D) {
                  double xa = Mth.lerp(subStep, ox, tx) * baseSpeed;
                  double ya = Mth.lerp(subStep, oy, ty) * baseSpeed;
                  double za = xa * Math.sin(angle);
                  xa *= Math.cos(angle);

                  for(double flip = -1.0D; flip <= 1.0D; flip += 2.0D) {
                     this.createParticle(this.x, this.y, this.z, xa * flip, ya, za * flip, rgbColors, fadeColors, trail, twinkle);
                  }
               }

               ox = tx;
               oy = ty;
            }
         }

      }

      private void createParticleBurst(final IntList rgbColors, final IntList fadeColors, final boolean trail, final boolean twinkle) {
         double baseOffX = this.random.nextGaussian() * 0.05D;
         double baseOffZ = this.random.nextGaussian() * 0.05D;

         for(int i = 0; i < 70; ++i) {
            double xa = this.xd * 0.5D + this.random.nextGaussian() * 0.15D + baseOffX;
            double za = this.zd * 0.5D + this.random.nextGaussian() * 0.15D + baseOffZ;
            double ya = this.yd * 0.5D + this.random.nextDouble() * 0.5D;
            this.createParticle(this.x, this.y, this.z, xa, ya, za, rgbColors, fadeColors, trail, twinkle);
         }

      }
   }
}
