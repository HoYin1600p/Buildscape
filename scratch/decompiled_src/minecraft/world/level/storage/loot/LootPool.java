package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool implements Validatable {
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter((p) -> p.entries), LootItemCondition.CODEC.optionalFieldOf("condition").forGetter((p) -> p.condition), LootItemFunctions.CODEC.optionalFieldOf("modifier").forGetter((p) -> p.modifier), NumberProviders.CODEC.fieldOf("rolls").forGetter((p) -> p.rolls), NumberProviders.CODEC.optionalFieldOf("bonus_rolls", ConstantValue.exactly(0.0F)).forGetter((p) -> p.bonusRolls)).apply(i, LootPool::new));
   private final List entries;
   private final Optional condition;
   private final Optional modifier;
   private final Holder rolls;
   private final Holder bonusRolls;

   private LootPool(final List entries, final Optional condition, final Optional modifier, final Holder rolls, final Holder bonusRolls) {
      this.entries = entries;
      this.condition = condition;
      this.modifier = modifier;
      this.rolls = rolls;
      this.bonusRolls = bonusRolls;
   }

   private void addRandomItem(final Consumer result, final LootContext context) {
      RandomSource random = context.getRandom();
      List validEntries = Lists.newArrayList();
      MutableInt totalWeight = new MutableInt();

      for(LootPoolEntryContainer entry : this.entries) {
         entry.expand(context, (e) -> {
            int weight = e.getWeight(context.getLuck());
            if (weight > 0) {
               validEntries.add(e);
               totalWeight.add(weight);
            }

         });
      }

      int entryCount = validEntries.size();
      if (totalWeight.intValue() != 0 && entryCount != 0) {
         if (entryCount == 1) {
            ((LootPoolEntry)validEntries.get(0)).createItemStack(result, context);
         } else {
            int index = random.nextInt(totalWeight.intValue());

            for(LootPoolEntry entry : validEntries) {
               index -= entry.getWeight(context.getLuck());
               if (index < 0) {
                  entry.createItemStack(result, context);
                  return;
               }
            }

         }
      }
   }

   public void addRandomItems(final Consumer result, final LootContext context) {
      if (!this.condition.isPresent() || ((LootItemCondition)((Holder)this.condition.get()).value()).test(context)) {
         Consumer decoratedConsumer = LootItemFunction.decorate(this.modifier, result, context);
         int count = ((NumberProvider)this.rolls.value()).getInt(context) + Mth.floor(((NumberProvider)this.bonusRolls.value()).getFloat(context) * context.getLuck());

         for(int i = 0; i < count; ++i) {
            this.addRandomItem(decoratedConsumer, context);
         }

      }
   }

   public void validate(final ValidationContext output) {
      Validatable.validateHolder(output, "condition", this.condition);
      Validatable.validateHolder(output, "modifier", this.modifier);
      Validatable.validate(output, "entries", this.entries);
      Validatable.validateHolder(output, "rolls", this.rolls);
      Validatable.validateHolder(output, "bonus_rolls", this.bonusRolls);
   }

   public static LootPool.Builder lootPool() {
      return new LootPool.Builder();
   }

   public static class Builder implements FunctionUserBuilder, ConditionUserBuilder {
      private final ImmutableList.Builder entries = ImmutableList.builder();
      private final ImmutableList.Builder conditions = ImmutableList.builder();
      private final ImmutableList.Builder functions = ImmutableList.builder();
      private Holder rolls = ConstantValue.exactly(1.0F);
      private Holder bonusRolls = ConstantValue.exactly(0.0F);

      public LootPool.Builder setRolls(final Holder rolls) {
         this.rolls = rolls;
         return this;
      }

      public LootPool.Builder unwrap() {
         return this;
      }

      public LootPool.Builder setBonusRolls(final Holder bonusRolls) {
         this.bonusRolls = bonusRolls;
         return this;
      }

      public LootPool.Builder add(final LootPoolEntryContainer.Builder entry) {
         this.entries.add(entry.build());
         return this;
      }

      public LootPool.Builder addAll(final List entries) {
         for(LootPoolEntryContainer.Builder entry : entries) {
            this.add(entry);
         }

         return this;
      }

      public LootPool.Builder when(final Holder condition) {
         this.conditions.add(condition);
         return this;
      }

      public LootPool.Builder apply(final Holder function) {
         this.functions.add(function);
         return this;
      }

      public LootPool build() {
         return new LootPool(this.entries.build(), ConditionUserBuilder.buildCondition(this.conditions.build()), FunctionUserBuilder.buildFunction(this.functions.build()), this.rolls, this.bonusRolls);
      }
   }
}
