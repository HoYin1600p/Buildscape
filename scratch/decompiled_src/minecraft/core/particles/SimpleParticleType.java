package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;

public class SimpleParticleType extends ParticleType implements ParticleOptions {
   private final MapCodec codec = MapCodec.unit(this::getType);
   private final StreamCodec streamCodec = StreamCodec.unit(this);

   protected SimpleParticleType(final boolean overrideLimiter) {
      super(overrideLimiter);
   }

   public SimpleParticleType getType() {
      return this;
   }

   public MapCodec codec() {
      return this.codec;
   }

   public StreamCodec streamCodec() {
      return this.streamCodec;
   }
}
