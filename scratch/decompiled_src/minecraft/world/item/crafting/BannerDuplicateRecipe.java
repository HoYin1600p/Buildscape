package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerDuplicateRecipe extends CustomRecipe {
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Ingredient.CODEC.fieldOf("banner").forGetter((o) -> o.banner), ItemStackTemplate.CODEC.fieldOf("result").forGetter((o) -> o.result)).apply(i, BannerDuplicateRecipe::new));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, (o) -> o.banner, ItemStackTemplate.STREAM_CODEC, (o) -> o.result, BannerDuplicateRecipe::new);
   public static final RecipeSerializer SERIALIZER = new RecipeSerializer(MAP_CODEC, STREAM_CODEC);
   private final Ingredient banner;
   private final ItemStackTemplate result;

   public BannerDuplicateRecipe(final Ingredient banner, final ItemStackTemplate result) {
      this.banner = banner;
      this.result = result;
   }

   public boolean matches(final CraftingInput input, final Level level) {
      if (input.ingredientCount() != 2) {
         return false;
      } else {
         DyeColor color = null;
         boolean hasTarget = false;
         boolean hasSource = false;
         int slot = 0;

         while(true) {
            if (slot >= input.size()) {
               return hasSource && hasTarget;
            }

            ItemStack itemStack = input.getItem(slot);
            if (!itemStack.isEmpty()) {
               if (!this.banner.test(itemStack)) {
                  break;
               }

               Item patternCount = itemStack.getItem();
               if (!(patternCount instanceof BannerItem)) {
                  break;
               }

               BannerItem banner = (BannerItem)patternCount;
               if (color == null) {
                  color = banner.getColor();
               } else if (color != banner.getColor()) {
                  return false;
               }

               int patternCount = ((BannerPatternLayers)itemStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().size();
               if (patternCount > 6) {
                  return false;
               }

               if (patternCount > 0) {
                  if (hasSource) {
                     return false;
                  }

                  hasSource = true;
               } else {
                  if (hasTarget) {
                     return false;
                  }

                  hasTarget = true;
               }
            }

            ++slot;
         }

         return false;
      }
   }

   public ItemStack assemble(final CraftingInput input) {
      for(int slot = 0; slot < input.size(); ++slot) {
         ItemStack itemStack = input.getItem(slot);
         if (!itemStack.isEmpty()) {
            int patternCount = ((BannerPatternLayers)itemStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().size();
            if (patternCount > 0 && patternCount <= 6) {
               return TransmuteRecipe.createWithOriginalComponents(this.result, itemStack);
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList getRemainingItems(final CraftingInput input) {
      NonNullList result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

      for(int slot = 0; slot < result.size(); ++slot) {
         ItemStack itemStack = input.getItem(slot);
         if (!itemStack.isEmpty()) {
            ItemStackTemplate remainder = itemStack.getItem().getCraftingRemainder();
            if (remainder != null) {
               result.set(slot, remainder.create());
            } else if (!((BannerPatternLayers)itemStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)).layers().isEmpty()) {
               result.set(slot, itemStack.copyWithCount(1));
            }
         }
      }

      return result;
   }

   public RecipeSerializer getSerializer() {
      return SERIALIZER;
   }
}
