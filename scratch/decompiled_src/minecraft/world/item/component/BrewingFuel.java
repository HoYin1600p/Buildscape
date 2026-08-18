package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;

public record BrewingFuel(ResolvableNumber uses, ResolvableNumber speedMultiplier) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ResolvableNumber.CODEC.fieldOf("uses").forGetter(BrewingFuel::uses), ResolvableNumber.CODEC.fieldOf("speed_multiplier").forGetter(BrewingFuel::speedMultiplier)).apply(i, BrewingFuel::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ResolvableNumber.STREAM_CODEC, BrewingFuel::uses, ResolvableNumber.STREAM_CODEC, BrewingFuel::speedMultiplier, BrewingFuel::new);

   public BrewingFuel(final ResourceKey uses, final ResourceKey speedMultiplier) {
      this(ResolvableNumber.fromKey(uses), ResolvableNumber.fromKey(speedMultiplier));
   }
}
