package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record GeyserParticleOptions(ParticleType type, int waterBlocks) implements ParticleOptions {
   public static MapCodec codec(final ParticleType type) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.POSITIVE_INT.fieldOf("water_blocks").forGetter((o) -> o.waterBlocks)).apply(i, (waterBlocks) -> new GeyserParticleOptions(type, waterBlocks)));
   }

   public static StreamCodec streamCodec(final ParticleType type) {
      return StreamCodec.composite(ByteBufCodecs.INT, (o) -> o.waterBlocks, (waterBlocks) -> new GeyserParticleOptions(type, waterBlocks));
   }

   public ParticleType getType() {
      return this.type;
   }
}
