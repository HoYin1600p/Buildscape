package net.minecraft.world.item.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public record RecipeHolder(ResourceKey id, Recipe value) {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.RECIPE), RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         if (obj instanceof RecipeHolder) {
            RecipeHolder holder = (RecipeHolder)obj;
            if (this.id == holder.id) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public String toString() {
      return this.id.toString();
   }
}
