package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer, Validatable {
   protected final Optional condition;
   protected final Optional modifier;

   protected LootPoolEntryContainer(final Optional condition, final Optional modifier) {
      this.condition = condition;
      this.modifier = modifier;
   }

   protected static Products.P2 commonFields(final RecordCodecBuilder.Instance i) {
      return i.group(LootItemCondition.CODEC.optionalFieldOf("condition").forGetter((e) -> e.condition), LootItemFunctions.CODEC.optionalFieldOf("modifier").forGetter((e) -> e.modifier));
   }

   public void validate(final ValidationContext output) {
      Validatable.validateHolder(output, "condition", this.condition);
      Validatable.validateHolder(output, "modifier", this.modifier);
   }

   protected abstract boolean expandRaw(final LootContext context, final Consumer output);

   public final boolean expand(final LootContext context, final Consumer output) {
      return !this.canRun(context) ? false : this.expandRaw(context, this.adjustOutput(output));
   }

   private Consumer adjustOutput(final Consumer output) {
      return this.modifier.isEmpty() ? output : (rawEntry) -> output.accept(new LootPoolEntry() {
            {
               Objects.requireNonNull(LootPoolEntryContainer.this);
            }

            public int getWeight(final float luck) {
               return rawEntry.getWeight(luck);
            }

            public void createItemStack(final Consumer output, final LootContext context) {
               rawEntry.createItemStack(LootItemFunction.decorate(LootPoolEntryContainer.this.modifier, output, context), context);
            }
         });
   }

   private boolean canRun(final LootContext context) {
      return this.condition.isEmpty() || ((LootItemCondition)((Holder)this.condition.get()).value()).test(context);
   }

   public abstract MapCodec codec();

   public abstract static class Builder implements ConditionUserBuilder, FunctionUserBuilder {
      private final ImmutableList.Builder conditions = ImmutableList.builder();
      private final ImmutableList.Builder modifiers = ImmutableList.builder();

      protected abstract LootPoolEntryContainer.Builder getThis();

      public LootPoolEntryContainer.Builder when(final Holder condition) {
         this.conditions.add(condition);
         return this.getThis();
      }

      public LootPoolEntryContainer.Builder apply(final Holder function) {
         this.modifiers.add(function);
         return this.getThis();
      }

      public final LootPoolEntryContainer.Builder unwrap() {
         return this.getThis();
      }

      protected Optional getCondition() {
         return ConditionUserBuilder.buildCondition(this.conditions.build());
      }

      protected Optional getModifier() {
         return FunctionUserBuilder.buildFunction(this.modifiers.build());
      }

      public AlternativesEntry.Builder otherwise(final LootPoolEntryContainer.Builder other) {
         return new AlternativesEntry.Builder(this, other);
      }

      public EntryGroup.Builder append(final LootPoolEntryContainer.Builder other) {
         return new EntryGroup.Builder(this, other);
      }

      public SequentialEntry.Builder then(final LootPoolEntryContainer.Builder other) {
         return new SequentialEntry.Builder(this, other);
      }

      public abstract LootPoolEntryContainer build();
   }
}
