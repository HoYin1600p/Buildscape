package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

public abstract class ParticleType {
   private final boolean overrideLimiter;

   protected ParticleType(final boolean overrideLimiter) {
      this.overrideLimiter = overrideLimiter;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public abstract MapCodec codec();

   public abstract StreamCodec streamCodec();
}
