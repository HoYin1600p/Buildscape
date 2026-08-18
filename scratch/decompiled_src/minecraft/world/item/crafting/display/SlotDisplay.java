package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public interface SlotDisplay {
   Codec CODEC = BuiltInRegistries.SLOT_DISPLAY.byNameCodec().dispatch(SlotDisplay::type, SlotDisplay.Type::codec);
   StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.SLOT_DISPLAY).dispatch(SlotDisplay::type, SlotDisplay.Type::streamCodec);

   Stream resolve(ContextMap context, DisplayContentsFactory builder);

   SlotDisplay.Type type();

   default boolean isEnabled(final FeatureFlagSet enabledFeatures) {
      return true;
   }

   default List resolveForStacks(final ContextMap context) {
      return this.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).toList();
   }

   default ItemStack resolveForFirstStack(final ContextMap context) {
      return (ItemStack)this.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
   }

   private static Stream applyDemoTransformation(final ContextMap context, final DisplayContentsFactory factory, final SlotDisplay firstDisplay, final SlotDisplay secondDisplay, final RandomSource randomSource, final BinaryOperator operation) {
      if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
         List firstItems = firstDisplay.resolveForStacks(context);
         if (firstItems.isEmpty()) {
            return Stream.empty();
         } else {
            List secondItems = secondDisplay.resolveForStacks(context);
            return secondItems.isEmpty() ? Stream.empty() : Stream.generate(() -> {
               ItemStack first = (ItemStack)Util.getRandom(firstItems, randomSource);
               ItemStack second = (ItemStack)Util.getRandom(secondItems, randomSource);
               return (ItemStack)operation.apply(first, second);
            }).limit(256L).filter((s) -> !s.isEmpty()).limit(16L).map(stacks::forStack);
         }
      } else {
         return Stream.empty();
      }
   }

   private static Stream applyDemoTransformation(final ContextMap context, final DisplayContentsFactory factory, final SlotDisplay firstDisplay, final SlotDisplay secondDisplay, final BinaryOperator operation) {
      if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
         List firstItems = firstDisplay.resolveForStacks(context);
         if (firstItems.isEmpty()) {
            return Stream.empty();
         } else {
            List secondItems = secondDisplay.resolveForStacks(context);
            if (secondItems.isEmpty()) {
               return Stream.empty();
            } else {
               int cycle = firstItems.size() * secondItems.size();
               return IntStream.range(0, cycle).mapToObj((index) -> {
                  int firstItemCount = firstItems.size();
                  int firstItemIndex = index % firstItemCount;
                  int secondItemIndex = index / firstItemCount;
                  ItemStack first = (ItemStack)firstItems.get(firstItemIndex);
                  ItemStack second = (ItemStack)secondItems.get(secondItemIndex);
                  return (ItemStack)operation.apply(first, second);
               }).filter((s) -> !s.isEmpty()).limit(16L).map(stacks::forStack);
            }
         }
      } else {
         return Stream.empty();
      }
   }

   public static class AnyFuel implements SlotDisplay {
      public static final SlotDisplay.AnyFuel INSTANCE = new SlotDisplay.AnyFuel();
      public static final MapCodec MAP_CODEC = MapCodec.unit(INSTANCE);
      public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      private AnyFuel() {
      }

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public String toString() {
         return "<any fuel>";
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
            RegistryAccess registries = (RegistryAccess)context.get(SlotDisplayContext.REGISTRIES);
            if (registries != null) {
               return registries.lookupOrThrow(Registries.ITEM).componentLookup().findAll(DataComponents.COOKING_FUEL).stream().map(stacks::forStack);
            }
         }

         return Stream.empty();
      }
   }

   public static record Composite(List contents) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(SlotDisplay.Composite::contents)).apply(i, SlotDisplay.Composite::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), SlotDisplay.Composite::contents, SlotDisplay.Composite::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         return this.contents.stream().flatMap((d) -> d.resolve(context, factory));
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return this.contents.stream().allMatch((c) -> c.isEnabled(enabledFeatures));
      }
   }

   public static record DyedSlotDemo(SlotDisplay dye, SlotDisplay target) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("dye").forGetter(SlotDisplay.DyedSlotDemo::dye), SlotDisplay.CODEC.fieldOf("target").forGetter(SlotDisplay.DyedSlotDemo::target)).apply(i, SlotDisplay.DyedSlotDemo::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SlotDisplay.DyedSlotDemo::dye, SlotDisplay.STREAM_CODEC, SlotDisplay.DyedSlotDemo::target, SlotDisplay.DyedSlotDemo::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         BinaryOperator transformation = (target, dye) -> {
            DyeColor dyeValue = (DyeColor)dye.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            return DyedItemColor.applyDyes(target.copy(), List.of(dyeValue));
         };
         return SlotDisplay.applyDemoTransformation(context, factory, this.target, this.dye, transformation);
      }
   }

   public static class Empty implements SlotDisplay {
      public static final SlotDisplay.Empty INSTANCE = new SlotDisplay.Empty();
      public static final MapCodec MAP_CODEC = MapCodec.unit(INSTANCE);
      public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      private Empty() {
      }

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public String toString() {
         return "<empty>";
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         return Stream.empty();
      }
   }

   public static record ItemSlotDisplay(Holder item) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Item.CODEC.fieldOf("item").forGetter(SlotDisplay.ItemSlotDisplay::item)).apply(i, SlotDisplay.ItemSlotDisplay::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Item.STREAM_CODEC, SlotDisplay.ItemSlotDisplay::item, SlotDisplay.ItemSlotDisplay::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public ItemSlotDisplay(final Item item) {
         this(item.builtInRegistryHolder());
      }

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
            return Stream.of(stacks.forStack(this.item));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return ((Item)this.item.value()).isEnabled(enabledFeatures);
      }
   }

   public static class ItemStackContentsFactory implements DisplayContentsFactory.ForStacks {
      public static final SlotDisplay.ItemStackContentsFactory INSTANCE = new SlotDisplay.ItemStackContentsFactory();

      public ItemStack forStack(final ItemStack stack) {
         return stack;
      }
   }

   public static record ItemStackSlotDisplay(ItemStackTemplate stack) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ItemStackTemplate.CODEC.fieldOf("item").forGetter(SlotDisplay.ItemStackSlotDisplay::stack)).apply(i, SlotDisplay.ItemStackSlotDisplay::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, SlotDisplay.ItemStackSlotDisplay::stack, SlotDisplay.ItemStackSlotDisplay::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
            return Stream.of(stacks.forStack(this.stack.create()));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return ((Item)this.stack.item().value()).isEnabled(enabledFeatures);
      }
   }

   public static record OnlyWithComponent(SlotDisplay source, DataComponentType component) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("contents").forGetter(SlotDisplay.OnlyWithComponent::source), DataComponentType.CODEC.fieldOf("component").forGetter(SlotDisplay.OnlyWithComponent::component)).apply(i, SlotDisplay.OnlyWithComponent::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SlotDisplay.OnlyWithComponent::source, DataComponentType.STREAM_CODEC, SlotDisplay.OnlyWithComponent::component, SlotDisplay.OnlyWithComponent::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public Stream resolve(final ContextMap context, final DisplayContentsFactory builder) {
         if (builder instanceof DisplayContentsFactory.ForStacks stacks) {
            return this.source.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).filter((s) -> s.has(this.component)).map(stacks::forStack);
         } else {
            return Stream.empty();
         }
      }

      public SlotDisplay.Type type() {
         return TYPE;
      }
   }

   public static record SmithingTrimDemoSlotDisplay(SlotDisplay base, SlotDisplay material, Holder pattern) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("base").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::base), SlotDisplay.CODEC.fieldOf("material").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(SlotDisplay.SmithingTrimDemoSlotDisplay::pattern)).apply(i, SlotDisplay.SmithingTrimDemoSlotDisplay::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SlotDisplay.SmithingTrimDemoSlotDisplay::base, SlotDisplay.STREAM_CODEC, SlotDisplay.SmithingTrimDemoSlotDisplay::material, TrimPattern.STREAM_CODEC, SlotDisplay.SmithingTrimDemoSlotDisplay::pattern, SlotDisplay.SmithingTrimDemoSlotDisplay::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         RandomSource randomSource = RandomSource.createThreadLocalInstance((long)System.identityHashCode(this));
         BinaryOperator transformation = (base, material) -> SmithingTrimRecipe.applyTrim(base, material, this.pattern);
         return SlotDisplay.applyDemoTransformation(context, factory, this.base, this.material, randomSource, transformation);
      }
   }

   public static record TagSlotDisplay(HolderSet tag) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(RegistryCodecs.holderSet(Registries.ITEM).fieldOf("tag").forGetter(SlotDisplay.TagSlotDisplay::tag)).apply(i, SlotDisplay.TagSlotDisplay::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.ITEM), SlotDisplay.TagSlotDisplay::tag, SlotDisplay.TagSlotDisplay::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
            return this.tag.stream().map(stacks::forStack);
         } else {
            return Stream.empty();
         }
      }
   }

   public static record Type(MapCodec codec, StreamCodec streamCodec) {
   }

   public static record WithAnyPotion(SlotDisplay display) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("contents").forGetter(SlotDisplay.WithAnyPotion::display)).apply(i, SlotDisplay.WithAnyPotion::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SlotDisplay.WithAnyPotion::display, SlotDisplay.WithAnyPotion::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks stacks) {
            List displayItems = this.display.resolveForStacks(context);
            Optional potions = Optional.ofNullable((RegistryAccess)context.get(SlotDisplayContext.REGISTRIES)).flatMap((r) -> r.lookup(Registries.POTION));
            return potions.stream().flatMap(HolderLookup::listElements).flatMap((potion) -> {
               PotionContents potionContents = new PotionContents(potion);
               return displayItems.stream().map((item) -> {
                  ItemStack itemCopy = item.copy();
                  itemCopy.set(DataComponents.POTION_CONTENTS, potionContents);
                  return stacks.forStack(itemCopy);
               });
            });
         } else {
            return Stream.empty();
         }
      }
   }

   public static record WithRemainder(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("input").forGetter(SlotDisplay.WithRemainder::input), SlotDisplay.CODEC.fieldOf("remainder").forGetter(SlotDisplay.WithRemainder::remainder)).apply(i, SlotDisplay.WithRemainder::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SlotDisplay.WithRemainder::input, SlotDisplay.STREAM_CODEC, SlotDisplay.WithRemainder::remainder, SlotDisplay.WithRemainder::new);
      public static final SlotDisplay.Type TYPE = new SlotDisplay.Type(MAP_CODEC, STREAM_CODEC);

      public SlotDisplay.Type type() {
         return TYPE;
      }

      public Stream resolve(final ContextMap context, final DisplayContentsFactory factory) {
         if (factory instanceof DisplayContentsFactory.ForRemainders remainders) {
            List resolvedRemainders = this.remainder.resolve(context, factory).toList();
            return this.input.resolve(context, factory).map((input) -> remainders.addRemainder(input, resolvedRemainders));
         } else {
            return this.input.resolve(context, factory);
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return this.input.isEnabled(enabledFeatures) && this.remainder.isEnabled(enabledFeatures);
      }
   }
}
