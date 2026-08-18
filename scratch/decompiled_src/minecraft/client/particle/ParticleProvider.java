package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public interface ParticleProvider {
   @Nullable Particle createParticle(ParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

   public interface Sprite {
      @Nullable SingleQuadParticle createParticle(ParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
   }
}
