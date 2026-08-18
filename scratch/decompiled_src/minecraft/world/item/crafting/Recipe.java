package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface Recipe {
   Codec DIRECT_CODEC = BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
   Codec KEY_CODEC = ResourceKey.codec(Registries.RECIPE);
   StreamCodec STREAM_CODEC = ByteBufCodecs.registry(Registries.RECIPE_SERIALIZER).dispatch(Recipe::getSerializer, RecipeSerializer::streamCodec);
   Codec CODEC = RegistryCodecs.holder(Registries.RECIPE, DIRECT_CODEC);
   Codec LIST_CODEC = RegistryCodecs.holderSet(Registries.RECIPE, DIRECT_CODEC);

   boolean matches(RecipeInput input, Level level);

   ItemStack assemble(RecipeInput input);

   default boolean isSpecial() {
      return false;
   }

   boolean showNotification();

   String group();

   RecipeSerializer getSerializer();

   RecipeType getType();

   PlacementInfo placementInfo();

   default List display() {
      return List.of();
   }

   RecipeBookCategory recipeBookCategory();

   public interface BookInfo {
      Object category();

      String group();

      static MapCodec mapCodec(final Codec categoryCodec, final Object defaultCategory, final Recipe.BookInfo.Constructor constructor) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(categoryCodec.optionalFieldOf("category", defaultCategory).forGetter(Recipe.BookInfo::category), Codec.STRING.optionalFieldOf("group", "").forGetter(Recipe.BookInfo::group)).apply(i, constructor));
      }

      static StreamCodec streamCodec(final StreamCodec categoryCodec, final Recipe.BookInfo.Constructor constructor) {
         return StreamCodec.composite(categoryCodec, Recipe.BookInfo::category, ByteBufCodecs.STRING_UTF8, Recipe.BookInfo::group, constructor);
      }

      @FunctionalInterface
      public interface Constructor extends BiFunction {
      }
   }

   public static record CommonInfo(boolean showNotification) {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(Recipe.CommonInfo::showNotification)).apply(i, Recipe.CommonInfo::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, Recipe.CommonInfo::showNotification, Recipe.CommonInfo::new);
   }
}
