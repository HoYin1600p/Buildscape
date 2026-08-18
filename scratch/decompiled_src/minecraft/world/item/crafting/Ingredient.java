package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.HolderSetCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate, StackedContents.IngredientInfo {
   public static final StreamCodec CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map(Ingredient::new, (i) -> i.values);
   public static final StreamCodec OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map((ingredient) -> ingredient.size() == 0 ? Optional.empty() : Optional.of(new Ingredient(ingredient)), (ingredient) -> (HolderSet)ingredient.map((i) -> i.values).orElse(HolderSet.empty()));
   public static final Codec NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
   public static final Codec CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, (i) -> i.values);
   private final HolderSet values;

   private Ingredient(final HolderSet values) {
      values.unwrap().ifRight((directValues) -> {
         if (directValues.isEmpty()) {
            throw new UnsupportedOperationException("Ingredients can't be empty");
         } else if (directValues.contains(Items.AIR.builtInRegistryHolder())) {
            throw new UnsupportedOperationException("Ingredient can't contain air");
         }
      });
      this.values = values;
   }

   public static boolean testOptionalIngredient(final Optional ingredient, final ItemStack stack) {
      return ingredient.map((value) -> value.test(stack)).orElseGet(stack::isEmpty);
   }

   /** @deprecated */
   @Deprecated
   public Stream items() {
      return this.values.stream();
   }

   public boolean isEmpty() {
      return this.values.size() == 0;
   }

   public boolean test(final ItemStack input) {
      return input.is(this.values);
   }

   public boolean acceptsItem(final Holder item) {
      return this.values.contains(item);
   }

   public boolean equals(final Object o) {
      if (o instanceof Ingredient other) {
         return Objects.equals(this.values, other.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(this.values);
   }

   public static Ingredient of(final ItemLike itemLike) {
      return new Ingredient(HolderSet.direct(itemLike.asItem().builtInRegistryHolder()));
   }

   public static Ingredient of(final ItemLike... items) {
      return of(Arrays.stream(items));
   }

   public static Ingredient of(final Stream stream) {
      return new Ingredient(HolderSet.direct(stream.map((e) -> e.asItem().builtInRegistryHolder()).toList()));
   }

   public static Ingredient of(final HolderSet tag) {
      return new Ingredient(tag);
   }

   public SlotDisplay display() {
      return new SlotDisplay.TagSlotDisplay(this.values);
   }

   public static SlotDisplay optionalIngredientToDisplay(final Optional ingredient) {
      return (SlotDisplay)ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
   }

   private static SlotDisplay displayForSingleItem(final Holder item) {
      SlotDisplay inputDisplay = new SlotDisplay.ItemSlotDisplay(item);
      ItemStackTemplate remainderStack = ((Item)item.value()).getCraftingRemainder();
      if (remainderStack != null) {
         SlotDisplay remainderDisplay = new SlotDisplay.ItemStackSlotDisplay(remainderStack);
         return new SlotDisplay.WithRemainder(inputDisplay, remainderDisplay);
      } else {
         return inputDisplay;
      }
   }

   public Optional getSingleItem() {
      return this.values.size() != 1 ? Optional.empty() : Optional.of(this.values.get(0));
   }
}
