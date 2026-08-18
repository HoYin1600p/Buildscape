package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record MultiplyValue(LevelBasedValue factor) implements EnchantmentValueEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("factor").forGetter(MultiplyValue::factor)).apply(i, MultiplyValue::new));

   public float process(final int enchantmentLevel, final RandomSource random, final float inputValue) {
      return inputValue * this.factor.calculate(enchantmentLevel);
   }

   public MapCodec codec() {
      return CODEC;
   }
}
