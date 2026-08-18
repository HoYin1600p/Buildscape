package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public abstract class AbstractCookingRecipe extends SingleItemRecipe {
   protected final AbstractCookingRecipe.CookingBookInfo bookInfo;
   private final float experience;
   private final int cookingTime;

   public AbstractCookingRecipe(final Recipe.CommonInfo commonInfo, final AbstractCookingRecipe.CookingBookInfo bookInfo, final Ingredient ingredient, final ItemStackTemplate result, final float experience, final int cookingTime) {
      super(commonInfo, ingredient, result);
      this.bookInfo = bookInfo;
      this.experience = experience;
      this.cookingTime = cookingTime;
   }

   public abstract RecipeSerializer getSerializer();

   public abstract RecipeType getType();

   public float experience() {
      return this.experience;
   }

   public int cookingTime() {
      return this.cookingTime;
   }

   public CookingBookCategory category() {
      return this.bookInfo.category;
   }

   public String group() {
      return this.bookInfo.group;
   }

   protected abstract Item furnaceIcon();

   public List display() {
      return List.of(new FurnaceRecipeDisplay(this.input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.ItemStackSlotDisplay(this.result()), new SlotDisplay.ItemSlotDisplay(this.furnaceIcon()), this.cookingTime, this.experience));
   }

   public static MapCodec cookingMapCodec(final AbstractCookingRecipe.Factory factory, final int defaultCookingTime) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(Recipe.CommonInfo.MAP_CODEC.forGetter((o) -> o.commonInfo), AbstractCookingRecipe.CookingBookInfo.MAP_CODEC.forGetter((o) -> o.bookInfo), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), ItemStackTemplate.CODEC.fieldOf("result").forGetter(SingleItemRecipe::result), Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractCookingRecipe::experience), Codec.INT.optionalFieldOf("cookingtime", defaultCookingTime).forGetter(AbstractCookingRecipe::cookingTime)).apply(i, factory::create));
   }

   public static StreamCodec cookingStreamCodec(final AbstractCookingRecipe.Factory factory) {
      return StreamCodec.composite(Recipe.CommonInfo.STREAM_CODEC, (o) -> o.commonInfo, AbstractCookingRecipe.CookingBookInfo.STREAM_CODEC, (o) -> o.bookInfo, Ingredient.CONTENTS_STREAM_CODEC, SingleItemRecipe::input, ItemStackTemplate.STREAM_CODEC, SingleItemRecipe::result, ByteBufCodecs.FLOAT, AbstractCookingRecipe::experience, ByteBufCodecs.INT, AbstractCookingRecipe::cookingTime, factory::create);
   }

   public static record CookingBookInfo(CookingBookCategory category, String group) implements Recipe.BookInfo {
      public static final MapCodec MAP_CODEC = Recipe.BookInfo.mapCodec(CookingBookCategory.CODEC, CookingBookCategory.MISC, AbstractCookingRecipe.CookingBookInfo::new);
      public static final StreamCodec STREAM_CODEC = Recipe.BookInfo.streamCodec(CookingBookCategory.STREAM_CODEC, AbstractCookingRecipe.CookingBookInfo::new);
   }

   @FunctionalInterface
   public interface Factory {
      AbstractCookingRecipe create(Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo cbookInfotegory, Ingredient ingredient, ItemStackTemplate result, float experience, int cookingTime);
   }
}
