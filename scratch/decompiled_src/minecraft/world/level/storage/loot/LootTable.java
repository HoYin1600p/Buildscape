package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class LootTable implements Validatable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec KEY_CODEC = ResourceKey.codec(Registries.LOOT_TABLE);
   public static final ContextKeySet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   public static final long RANDOMIZE_SEED = 0L;
   public static final Codec DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create((i) -> i.group(LootContextParamSets.CODEC.lenientOptionalFieldOf("type", DEFAULT_PARAM_SET).forGetter((t) -> t.paramSet), Identifier.CODEC.optionalFieldOf("random_sequence").forGetter((t) -> t.randomSequence), LootPool.CODEC.listOf().optionalFieldOf("pools", List.of()).forGetter((t) -> t.pools), LootItemFunctions.CODEC.optionalFieldOf("modifier").forGetter((t) -> t.modifier)).apply(i, LootTable::new)));
   public static final Codec CODEC = RegistryCodecs.holder(Registries.LOOT_TABLE, DIRECT_CODEC);
   public static final Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.LOOT_TABLE, DIRECT_CODEC);
   public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, Optional.empty(), List.of(), Optional.empty());
   private final ContextKeySet paramSet;
   private final Optional randomSequence;
   private final List pools;
   private final Optional modifier;

   private LootTable(final ContextKeySet paramSet, final Optional randomSequence, final List pools, final Optional modifier) {
      this.paramSet = paramSet;
      this.randomSequence = randomSequence;
      this.pools = pools;
      this.modifier = modifier;
   }

   public static Consumer createStackSplitter(final ServerLevel level, final Consumer output) {
      return (result) -> {
         if (result.isItemEnabled(level.enabledFeatures())) {
            if (result.getCount() < result.getMaxStackSize()) {
               output.accept(result);
            } else {
               int count = result.getCount();

               while(count > 0) {
                  ItemStack copy = result.copyWithCount(Math.min(result.getMaxStackSize(), count));
                  count -= copy.getCount();
                  output.accept(copy);
               }
            }

         }
      };
   }

   public void getRandomItemsRaw(final LootParams params, final Consumer output) {
      this.getRandomItemsRaw((new LootContext.Builder(params)).create(this.randomSequence), output);
   }

   public void getRandomItemsRaw(final LootContext context, final Consumer output) {
      LootContext.VisitedEntry breadcrumb = LootContext.createVisitedEntry(this);
      if (context.pushVisitedElement(breadcrumb)) {
         Consumer decoratedOutput = LootItemFunction.decorate(this.modifier, output, context);

         for(LootPool pool : this.pools) {
            pool.addRandomItems(decoratedOutput, context);
         }

         context.popVisitedElement(breadcrumb);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void getRandomItems(final LootParams params, final long optionalLootTableSeed, final Consumer output) {
      this.getRandomItemsRaw((new LootContext.Builder(params)).withOptionalRandomSeed(optionalLootTableSeed).create(this.randomSequence), createStackSplitter(params.getLevel(), output));
   }

   public void getRandomItems(final LootParams params, final Consumer output) {
      this.getRandomItemsRaw(params, createStackSplitter(params.getLevel(), output));
   }

   public void getRandomItems(final LootContext context, final Consumer output) {
      this.getRandomItemsRaw(context, createStackSplitter(context.getLevel(), output));
   }

   public ObjectArrayList getRandomItems(final LootParams params, final RandomSource randomSource) {
      return this.getRandomItems((new LootContext.Builder(params)).withOptionalRandomSource(randomSource).create(this.randomSequence));
   }

   public ObjectArrayList getRandomItems(final LootParams params, final long optionalLootTableSeed) {
      return this.getRandomItems((new LootContext.Builder(params)).withOptionalRandomSeed(optionalLootTableSeed).create(this.randomSequence));
   }

   public ObjectArrayList getRandomItems(final LootParams params) {
      return this.getRandomItems((new LootContext.Builder(params)).create(this.randomSequence));
   }

   private ObjectArrayList getRandomItems(final LootContext context) {
      ObjectArrayList result = new ObjectArrayList();
      this.getRandomItems(context, result::add);
      return result;
   }

   public ContextKeySet getParamSet() {
      return this.paramSet;
   }

   public void validate(final ValidationContext context) {
      Validatable.validate(context, "pools", this.pools);
      Validatable.validateHolder(context, "modifier", this.modifier);
   }

   public void fill(final Container container, final LootParams params, final long optionalRandomSeed) {
      LootContext context = (new LootContext.Builder(params)).withOptionalRandomSeed(optionalRandomSeed).create(this.randomSequence);
      ObjectArrayList itemStacks = this.getRandomItems(context);
      RandomSource random = context.getRandom();
      List availableSlots = this.getAvailableSlots(container, random);
      this.shuffleAndSplitItems(itemStacks, availableSlots.size(), random);
      ObjectListIterator var9 = itemStacks.iterator();

      while(var9.hasNext()) {
         ItemStack itemStack = (ItemStack)var9.next();
         if (availableSlots.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemStack.isEmpty()) {
            container.setItem(availableSlots.remove(availableSlots.size() - 1), ItemStack.EMPTY);
         } else {
            container.setItem(availableSlots.remove(availableSlots.size() - 1), itemStack);
         }
      }

   }

   private void shuffleAndSplitItems(final ObjectArrayList result, final int availableSlots, final RandomSource random) {
      List splittableItems = Lists.newArrayList();
      Iterator iterator = result.iterator();

      while(iterator.hasNext()) {
         ItemStack itemStack = (ItemStack)iterator.next();
         if (itemStack.isEmpty()) {
            iterator.remove();
         } else if (itemStack.getCount() > 1) {
            splittableItems.add(itemStack);
            iterator.remove();
         }
      }

      while(availableSlots - result.size() - splittableItems.size() > 0 && !splittableItems.isEmpty()) {
         ItemStack itemStack = (ItemStack)splittableItems.remove(Mth.nextInt(random, 0, splittableItems.size() - 1));
         int remove = Mth.nextInt(random, 1, itemStack.getCount() / 2);
         ItemStack copy = itemStack.split(remove);
         if (itemStack.getCount() > 1 && random.nextBoolean()) {
            splittableItems.add(itemStack);
         } else {
            result.add(itemStack);
         }

         if (copy.getCount() > 1 && random.nextBoolean()) {
            splittableItems.add(copy);
         } else {
            result.add(copy);
         }
      }

      result.addAll(splittableItems);
      Util.shuffle(result, random);
   }

   private List getAvailableSlots(final Container container, final RandomSource random) {
      ObjectArrayList slots = new ObjectArrayList();

      for(int i = 0; i < container.getContainerSize(); ++i) {
         if (container.getItem(i).isEmpty()) {
            slots.add(i);
         }
      }

      Util.shuffle(slots, random);
      return slots;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   public static class Builder implements FunctionUserBuilder {
      private final ImmutableList.Builder pools = ImmutableList.builder();
      private final ImmutableList.Builder functions = ImmutableList.builder();
      private ContextKeySet paramSet = LootTable.DEFAULT_PARAM_SET;
      private Optional randomSequence = Optional.empty();

      public LootTable.Builder withPool(final LootPool.Builder pool) {
         this.pools.add(pool.build());
         return this;
      }

      public LootTable.Builder setParamSet(final ContextKeySet paramSet) {
         this.paramSet = paramSet;
         return this;
      }

      public LootTable.Builder setRandomSequence(final Identifier key) {
         this.randomSequence = Optional.of(key);
         return this;
      }

      public LootTable.Builder apply(final Holder function) {
         this.functions.add(function);
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.randomSequence, this.pools.build(), FunctionUserBuilder.buildFunction(this.functions.build()));
      }
   }
}
