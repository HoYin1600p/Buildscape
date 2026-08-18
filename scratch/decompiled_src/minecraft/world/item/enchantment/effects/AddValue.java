package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record AddValue(LevelBasedValue value) implements EnchantmentValueEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(AddValue::value)).apply(i, AddValue::new));

   public float process(final int enchantmentLevel, final RandomSource random, final float inputValue) {
      return inputValue + this.value.calculate(enchantmentLevel);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
