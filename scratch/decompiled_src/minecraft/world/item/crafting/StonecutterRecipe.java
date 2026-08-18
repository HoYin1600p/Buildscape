package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;

public class StonecutterRecipe extends SingleItemRecipe {
   public static final MapCodec MAP_CODEC = simpleMapCodec(StonecutterRecipe::new);
   public static final StreamCodec STREAM_CODEC = simpleStreamCodec(StonecutterRecipe::new);
   public static final RecipeSerializer SERIALIZER = new RecipeSerializer(MAP_CODEC, STREAM_CODEC);

   public StonecutterRecipe(final Recipe.CommonInfo commonInfo, final Ingredient ingredient, final ItemStackTemplate result) {
      super(commonInfo, ingredient, result);
   }

   public RecipeType getType() {
      return RecipeType.STONECUTTING;
   }

   public RecipeSerializer getSerializer() {
      return SERIALIZER;
   }

   public String group() {
      return "";
   }

   public List display() {
      return List.of(new StonecutterRecipeDisplay(this.input().display(), this.resultDisplay(), new SlotDisplay.ItemSlotDisplay(Items.STONECUTTER)));
   }

   public SlotDisplay resultDisplay() {
      return new SlotDisplay.ItemStackSlotDisplay(this.result());
   }

   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.STONECUTTER;
   }
}
