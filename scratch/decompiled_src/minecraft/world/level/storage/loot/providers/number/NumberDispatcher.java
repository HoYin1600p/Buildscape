package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record NumberDispatcher(List cases, Holder defaultValue) implements NumberProvider {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(NumberDispatcher.Case.CODEC.listOf().fieldOf("cases").forGetter((n) -> n.cases), NumberProviders.CODEC.optionalFieldOf("default", ConstantValue.exactly(0.0F)).forGetter((n) -> n.defaultValue)).apply(i, NumberDispatcher::new));

   private NumberProvider selectValue(final LootContext context) {
      for(NumberDispatcher.Case aCase : this.cases) {
         if (aCase.test(context)) {
            return (NumberProvider)aCase.numberProvider.value();
         }
      }

      return (NumberProvider)this.defaultValue.value();
   }

   public float getFloat(final LootContext context) {
      return this.selectValue(context).getFloat(context);
   }

   public int getInt(final LootContext context) {
      return this.selectValue(context).getInt(context);
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validate(context, "cases", this.cases);
      Validatable.validateHolder(context, "default", this.defaultValue);
   }

   public MapCodec codec() {
      return MAP_CODEC;
   }

   public static record Case(Holder condition, Holder numberProvider) implements Validatable {
      public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.fieldOf("condition").forGetter((c) -> c.condition), NumberProviders.CODEC.fieldOf("number_provider").forGetter((c) -> c.numberProvider)).apply(i, NumberDispatcher.Case::new));

      public boolean test(final LootContext context) {
         return ((LootItemCondition)this.condition.value()).test(context);
      }

      public void validate(final ValidationContext context) {
         Validatable.validateHolder(context, "condition", this.condition);
         Validatable.validateHolder(context, "number_provider", this.numberProvider);
      }
   }
}
