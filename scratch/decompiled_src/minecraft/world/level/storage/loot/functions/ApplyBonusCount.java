package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   private static final Map FORMULAS = (Map)Stream.of(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.UniformBonusCount.TYPE).collect(Collectors.toMap(ApplyBonusCount.FormulaType::id, Function.identity()));
   private static final Codec FORMULA_TYPE_CODEC = Identifier.CODEC.comapFlatMap((location) -> {
      ApplyBonusCount.FormulaType type = (ApplyBonusCount.FormulaType)FORMULAS.get(location);
      return type != null ? DataResult.success(type) : DataResult.error(() -> "No formula type with id: '" + String.valueOf(location) + "'");
   }, ApplyBonusCount.FormulaType::id);
   private static final MapCodec FORMULA_CODEC = ExtraCodecs.dispatchOptionalValue("formula", "parameters", FORMULA_TYPE_CODEC, ApplyBonusCount.Formula::getType, ApplyBonusCount.FormulaType::codec);
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(Enchantment.CODEC.fieldOf("enchantment").forGetter((f) -> f.enchantment), FORMULA_CODEC.forGetter((f) -> f.formula))).apply(i, ApplyBonusCount::new));
   private final Holder enchantment;
   private final ApplyBonusCount.Formula formula;

   private ApplyBonusCount(final Optional condition, final Holder enchantment, final ApplyBonusCount.Formula formula) {
      super(condition);
      this.enchantment = enchantment;
      this.formula = formula;
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public Set getReferencedContextParams() {
      return Set.of(LootContextParams.TOOL);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      ItemInstance tool = (ItemInstance)context.getOptionalParameter(LootContextParams.TOOL);
      if (tool != null) {
         int level = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, tool);
         int newCount = this.formula.calculateNewCount(context.getRandom(), itemStack.getCount(), level);
         itemStack.setCount(newCount);
      }

      return itemStack;
   }

   public static LootItemConditionalFunction.Builder addBonusBinomialDistributionCount(final Holder enchantment, final float probability, final int extraRounds) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new ApplyBonusCount.BinomialWithBonusCount(extraRounds, probability)));
   }

   public static LootItemConditionalFunction.Builder addOreBonusCount(final Holder enchantment) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, ApplyBonusCount.OreDrops.INSTANCE));
   }

   public static LootItemConditionalFunction.Builder addUniformBonusCount(final Holder enchantment) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new ApplyBonusCount.UniformBonusCount(1)));
   }

   public static LootItemConditionalFunction.Builder addUniformBonusCount(final Holder enchantment, final int bonusMultiplier) {
      return simpleBuilder((conditions) -> new ApplyBonusCount(conditions, enchantment, new ApplyBonusCount.UniformBonusCount(bonusMultiplier)));
   }

   private static record BinomialWithBonusCount(int extraRounds, float probability) implements ApplyBonusCount.Formula {
      private static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.INT.fieldOf("extra").forGetter(ApplyBonusCount.BinomialWithBonusCount::extraRounds), Codec.FLOAT.fieldOf("probability").forGetter(ApplyBonusCount.BinomialWithBonusCount::probability)).apply(i, ApplyBonusCount.BinomialWithBonusCount::new));
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(Identifier.withDefaultNamespace("binomial_with_bonus_count"), CODEC);

      public int calculateNewCount(final RandomSource random, int count, final int level) {
         for(int i = 0; i < level + this.extraRounds; ++i) {
            if (random.nextFloat() < this.probability) {
               ++count;
            }
         }

         return count;
      }

      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }

   private interface Formula {
      int calculateNewCount(final RandomSource random, final int count, final int level);

      ApplyBonusCount.FormulaType getType();
   }

   private static record FormulaType(Identifier id, Codec codec) {
   }

   private static record OreDrops() implements ApplyBonusCount.Formula {
      public static final ApplyBonusCount.OreDrops INSTANCE = new ApplyBonusCount.OreDrops();
      public static final Codec CODEC = MapCodec.unitCodec(INSTANCE);
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(Identifier.withDefaultNamespace("ore_drops"), CODEC);

      public int calculateNewCount(final RandomSource random, final int count, final int level) {
         if (level > 0) {
            int bonus = random.nextInt(level + 2) - 1;
            if (bonus < 0) {
               bonus = 0;
            }

            return count * (bonus + 1);
         } else {
            return count;
         }
      }

      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }

   private static record UniformBonusCount(int bonusMultiplier) implements ApplyBonusCount.Formula {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(ApplyBonusCount.UniformBonusCount::bonusMultiplier)).apply(i, ApplyBonusCount.UniformBonusCount::new));
      public static final ApplyBonusCount.FormulaType TYPE = new ApplyBonusCount.FormulaType(Identifier.withDefaultNamespace("uniform_bonus_count"), CODEC);

      public int calculateNewCount(final RandomSource random, final int count, final int level) {
         return count + random.nextInt(this.bonusMultiplier * level + 1);
      }

      public ApplyBonusCount.FormulaType getType() {
         return TYPE;
      }
   }
}
