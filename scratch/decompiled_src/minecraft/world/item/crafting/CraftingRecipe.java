package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public interface CraftingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.CRAFTING;
   }

   RecipeSerializer getSerializer();

   CraftingBookCategory category();

   default NonNullList getRemainingItems(final CraftingInput input) {
      return defaultCraftingReminder(input);
   }

   static NonNullList defaultCraftingReminder(final CraftingInput input) {
      NonNullList result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

      for(int slot = 0; slot < result.size(); ++slot) {
         Item item = input.getItem(slot).getItem();
         ItemStackTemplate remainder = item.getCraftingRemainder();
         result.set(slot, remainder != null ? remainder.create() : ItemStack.EMPTY);
      }

      return result;
   }

   default RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BUILDING:
            var10000 = RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            break;
         case EQUIPMENT:
            var10000 = RecipeBookCategories.CRAFTING_EQUIPMENT;
            break;
         case REDSTONE:
            var10000 = RecipeBookCategories.CRAFTING_REDSTONE;
            break;
         case MISC:
            var10000 = RecipeBookCategories.CRAFTING_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static record CraftingBookInfo(CraftingBookCategory category, String group) implements Recipe.BookInfo {
      public static final MapCodec MAP_CODEC = Recipe.BookInfo.mapCodec(CraftingBookCategory.CODEC, CraftingBookCategory.MISC, CraftingRecipe.CraftingBookInfo::new);
      public static final StreamCodec STREAM_CODEC = Recipe.BookInfo.streamCodec(CraftingBookCategory.STREAM_CODEC, CraftingRecipe.CraftingBookInfo::new);
   }
}
