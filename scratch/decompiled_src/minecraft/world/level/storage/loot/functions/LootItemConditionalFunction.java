package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootItemConditionalFunction implements LootItemFunction {
   protected final Optional condition;

   protected LootItemConditionalFunction(final Optional condition) {
      this.condition = condition;
   }

   public abstract MapCodec codec();

   protected static Products.P1 commonFields(final RecordCodecBuilder.Instance i) {
      return i.group(LootItemCondition.CODEC.optionalFieldOf("condition").forGetter((f) -> f.condition));
   }

   public final ItemStack apply(final ItemStack itemStack, final LootContext context) {
      return !this.condition.isEmpty() && !((LootItemCondition)((Holder)this.condition.get()).value()).test(context) ? itemStack : this.run(itemStack, context);
   }

   protected abstract ItemStack run(final ItemStack itemStack, final LootContext context);

   public void validate(final ValidationContext context) {
      LootItemFunction.super.validate(context);
      Validatable.validateHolder(context, "condition", this.condition);
   }

   protected static LootItemConditionalFunction.Builder simpleBuilder(final Function constructor) {
      return new LootItemConditionalFunction.DummyBuilder(constructor);
   }

   public abstract static class Builder implements LootItemFunction.Builder, ConditionUserBuilder {
      private final ImmutableList.Builder conditions = ImmutableList.builder();

      public LootItemConditionalFunction.Builder when(final Holder condition) {
         this.conditions.add(condition);
         return this.getThis();
      }

      public final LootItemConditionalFunction.Builder unwrap() {
         return this.getThis();
      }

      protected abstract LootItemConditionalFunction.Builder getThis();

      protected Optional getCondition() {
         return ConditionUserBuilder.buildCondition(this.conditions.build());
      }
   }

   private static final class DummyBuilder extends LootItemConditionalFunction.Builder {
      private final Function constructor;

      public DummyBuilder(final Function constructor) {
         this.constructor = constructor;
      }

      protected LootItemConditionalFunction.DummyBuilder getThis() {
         return this;
      }

      public LootItemFunction build() {
         return (LootItemFunction)this.constructor.apply(this.getCondition());
      }
   }
}
