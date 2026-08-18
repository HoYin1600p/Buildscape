package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class BlastingRecipe extends AbstractCookingRecipe {
   public static final MapCodec MAP_CODEC = cookingMapCodec(BlastingRecipe::new, 100);
   public static final StreamCodec STREAM_CODEC = cookingStreamCodec(BlastingRecipe::new);
   public static final RecipeSerializer SERIALIZER = new RecipeSerializer(MAP_CODEC, STREAM_CODEC);

   public BlastingRecipe(final Recipe.CommonInfo commonInfo, final AbstractCookingRecipe.CookingBookInfo bookInfo, final Ingredient ingredient, final ItemStackTemplate result, final float experience, final int cookingTime) {
      super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
   }

   protected Item furnaceIcon() {
      return Items.BLAST_FURNACE;
   }

   public RecipeSerializer getSerializer() {
      return SERIALIZER;
   }

   public RecipeType getType() {
      return RecipeType.BLASTING;
   }

   public RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BLOCKS:
            var10000 = RecipeBookCategories.BLAST_FURNACE_BLOCKS;
            break;
         case FOOD:
         case MISC:
            var10000 = RecipeBookCategories.BLAST_FURNACE_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
