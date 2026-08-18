package net.minecraft.world.item.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RecipePropertySet {
   public static final ResourceKey TYPE_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("recipe_property_set"));
   public static final ResourceKey SMITHING_BASE = registerVanilla("smithing_base");
   public static final ResourceKey SMITHING_TEMPLATE = registerVanilla("smithing_template");
   public static final ResourceKey SMITHING_ADDITION = registerVanilla("smithing_addition");
   public static final ResourceKey FURNACE_INPUT = registerVanilla("furnace_input");
   public static final ResourceKey BLAST_FURNACE_INPUT = registerVanilla("blast_furnace_input");
   public static final ResourceKey SMOKER_INPUT = registerVanilla("smoker_input");
   public static final ResourceKey CAMPFIRE_INPUT = registerVanilla("campfire_input");
   public static final ResourceKey BREWING_INPUTS = registerVanilla("brewing_input");
   public static final ResourceKey BREWING_REAGENTS = registerVanilla("brewing_reagent");
   public static final StreamCodec STREAM_CODEC = Item.STREAM_CODEC.apply(ByteBufCodecs.list()).map((holders) -> new RecipePropertySet(Set.copyOf(holders)), (propertySet) -> List.copyOf(propertySet.items));
   public static final RecipePropertySet EMPTY = new RecipePropertySet(Set.of());
   private final Set items;

   private RecipePropertySet(final Set items) {
      this.items = items;
   }

   private static ResourceKey registerVanilla(final String name) {
      return ResourceKey.create(TYPE_KEY, Identifier.withDefaultNamespace(name));
   }

   public boolean test(final ItemStack itemStack) {
      return this.items.contains(itemStack.typeHolder());
   }

   public static RecipePropertySet create(final Collection ingredients) {
      Set items = (Set)ingredients.stream().flatMap(Ingredient::items).collect(Collectors.toUnmodifiableSet());
      return new RecipePropertySet(items);
   }
}
