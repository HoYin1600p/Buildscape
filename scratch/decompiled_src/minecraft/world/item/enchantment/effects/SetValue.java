package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record SetValue(LevelBasedValue value) implements EnchantmentValueEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(SetValue::value)).apply(i, SetValue::new));

   public float process(final int enchantmentLevel, final RandomSource random, final float inputValue) {
      return this.value.calculate(enchantmentLevel);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
