package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;

public record CookingFuel(ResolvableNumber burnTime, ResolvableNumber speedMultiplier) {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(ResolvableNumber.CODEC.fieldOf("burn_time").forGetter(CookingFuel::burnTime), ResolvableNumber.CODEC.fieldOf("speed_multiplier").forGetter(CookingFuel::speedMultiplier)).apply(i, CookingFuel::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ResolvableNumber.STREAM_CODEC, CookingFuel::burnTime, ResolvableNumber.STREAM_CODEC, CookingFuel::speedMultiplier, CookingFuel::new);

   public CookingFuel(final ResourceKey burnTime, final ResourceKey speedMultiplier) {
      this(ResolvableNumber.fromKey(burnTime), ResolvableNumber.fromKey(speedMultiplier));
   }
}
