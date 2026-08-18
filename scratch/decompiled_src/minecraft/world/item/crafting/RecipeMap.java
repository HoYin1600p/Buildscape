package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class RecipeMap {
   public static final RecipeMap EMPTY = new RecipeMap(ImmutableMultimap.of(), Map.of());
   private final Multimap byType;
   private final Map byKey;

   private RecipeMap(final Multimap byType, final Map byKey) {
      this.byType = byType;
      this.byKey = byKey;
   }

   public static RecipeMap create(final HolderLookup recipes) {
      ImmutableMultimap.Builder byType = ImmutableMultimap.builder();
      ImmutableMap.Builder byKey = ImmutableMap.builder();
      recipes.listElements().forEach((recipe) -> {
         RecipeHolder legacyRecipeHolder = new RecipeHolder(recipe.key(), (Recipe)recipe.value());
         byType.put(((Recipe)recipe.value()).getType(), legacyRecipeHolder);
         byKey.put(recipe.key(), legacyRecipeHolder);
      });
      return new RecipeMap(byType.build(), byKey.build());
   }

   public Collection byType(final RecipeType type) {
      return this.byType.get(type);
   }

   public Collection values() {
      return this.byKey.values();
   }

   public @Nullable RecipeHolder byKey(final ResourceKey recipeId) {
      return (RecipeHolder)this.byKey.get(recipeId);
   }

   public Stream getRecipesFor(final RecipeType type, final RecipeInput container, final Level level) {
      return container.isEmpty() ? Stream.empty() : this.byType(type).stream().filter((r) -> r.value().matches(container, level));
   }
}
