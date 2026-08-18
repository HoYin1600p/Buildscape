package net.minecraft.world.item.crafting;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RecipeManager implements RecipeAccess {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map RECIPE_PROPERTY_SETS = Map.of(RecipePropertySet.SMITHING_ADDITION, (RecipeManager.IngredientExtractor)(recipe) -> {
      Optional var10000;
      if (recipe instanceof SmithingRecipe smithingRecipe) {
         var10000 = smithingRecipe.additionIngredient();
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   }, RecipePropertySet.SMITHING_BASE, (RecipeManager.IngredientExtractor)(recipe) -> {
      Optional var10000;
      if (recipe instanceof SmithingRecipe smithingRecipe) {
         var10000 = Optional.of(smithingRecipe.baseIngredient());
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   }, RecipePropertySet.SMITHING_TEMPLATE, (RecipeManager.IngredientExtractor)(recipe) -> {
      Optional var10000;
      if (recipe instanceof SmithingRecipe smithingRecipe) {
         var10000 = smithingRecipe.templateIngredient();
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   }, RecipePropertySet.FURNACE_INPUT, forSingleInput(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, forSingleInput(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, forSingleInput(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, forSingleInput(RecipeType.CAMPFIRE_COOKING), RecipePropertySet.BREWING_INPUTS, (RecipeManager.IngredientExtractor)(recipe) -> {
      Optional var10000;
      if (recipe instanceof BrewingRecipe brewingRecipe) {
         var10000 = Optional.of(brewingRecipe.getInput().ingredient());
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   }, RecipePropertySet.BREWING_REAGENTS, (RecipeManager.IngredientExtractor)(recipe) -> {
      Optional var10000;
      if (recipe instanceof BrewingRecipe brewingRecipe) {
         var10000 = Optional.of(brewingRecipe.getReagent().ingredient());
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   });
   private final RecipeMap recipes;
   private Map propertySets = Map.of();
   private SelectableRecipe.SingleInputSet stonecutterRecipes = SelectableRecipe.SingleInputSet.empty();
   private List allDisplays = List.of();
   private Map recipeToDisplay = Map.of();
   private final Collection learnableRecipes;

   public RecipeManager(final HolderLookup.Provider registries) {
      HolderLookup.RegistryLookup recipeRegistries = registries.lookupOrThrow(Registries.RECIPE);
      this.recipes = RecipeMap.create(recipeRegistries);
      this.learnableRecipes = (Collection)this.recipes.values().stream().filter((r) -> !r.value().isSpecial()).collect(Collectors.toUnmodifiableList());
   }

   public void finalizeRecipeLoading(final FeatureFlagSet enabledFlags) {
      List stonecutterRecipes = new ArrayList();
      List propertySetCollectors = RECIPE_PROPERTY_SETS.entrySet().stream().map((e) -> new RecipeManager.IngredientCollector((ResourceKey)e.getKey(), (RecipeManager.IngredientExtractor)e.getValue())).toList();
      this.recipes.values().forEach((recipeHolder) -> {
         Recipe recipe = recipeHolder.value();
         if (!recipe.isSpecial() && recipe.placementInfo().isImpossibleToPlace()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", recipeHolder.id().identifier());
         } else {
            propertySetCollectors.forEach((c) -> c.accept(recipe));
            if (recipe instanceof StonecutterRecipe) {
               StonecutterRecipe stonecutterRecipe = (StonecutterRecipe)recipe;
               if (isIngredientEnabled(enabledFlags, stonecutterRecipe.input()) && stonecutterRecipe.resultDisplay().isEnabled(enabledFlags)) {
                  stonecutterRecipes.add(new SelectableRecipe.SingleInputEntry(stonecutterRecipe.input(), new SelectableRecipe(stonecutterRecipe.resultDisplay(), Optional.of(recipeHolder))));
               }
            }

         }
      });
      this.propertySets = (Map)propertySetCollectors.stream().collect(Collectors.toUnmodifiableMap((c) -> c.key, (c) -> c.asPropertySet(enabledFlags)));
      this.stonecutterRecipes = new SelectableRecipe.SingleInputSet(stonecutterRecipes);
      this.allDisplays = unpackRecipeInfo(this.recipes.values(), enabledFlags);
      this.recipeToDisplay = (Map)this.allDisplays.stream().collect(Collectors.groupingBy((r) -> r.parent.id(), IdentityHashMap::new, Collectors.toList()));
   }

   private static List filterDisabled(final FeatureFlagSet enabledFlags, final List ingredients) {
      ingredients.removeIf((e) -> !isIngredientEnabled(enabledFlags, e));
      return ingredients;
   }

   private static boolean isIngredientEnabled(final FeatureFlagSet enabledFlags, final Ingredient ingredient) {
      return ingredient.items().allMatch((i) -> ((Item)i.value()).isEnabled(enabledFlags));
   }

   public Optional getRecipeFor(final RecipeType type, final RecipeInput input, final Level level, final @Nullable ResourceKey recipeHint) {
      RecipeHolder hintedRecipe = recipeHint != null ? this.byKeyTyped(type, recipeHint) : null;
      return this.getRecipeFor(type, input, level, hintedRecipe);
   }

   public Optional getRecipeFor(final RecipeType type, final RecipeInput input, final Level level, final @Nullable RecipeHolder recipeHint) {
      return recipeHint != null && recipeHint.value().matches(input, level) ? Optional.of(recipeHint) : this.getRecipeFor(type, input, level);
   }

   public Optional getRecipeFor(final RecipeType type, final RecipeInput input, final Level level) {
      return this.recipes.getRecipesFor(type, input, level).findFirst();
   }

   public Optional byKey(final ResourceKey recipeId) {
      return Optional.ofNullable(this.recipes.byKey(recipeId));
   }

   private @Nullable RecipeHolder byKeyTyped(final RecipeType type, final ResourceKey recipeId) {
      RecipeHolder recipe = this.recipes.byKey(recipeId);
      return recipe != null && recipe.value().getType().equals(type) ? recipe : null;
   }

   public Map getSynchronizedItemProperties() {
      return this.propertySets;
   }

   public SelectableRecipe.SingleInputSet getSynchronizedStonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public RecipePropertySet propertySet(final ResourceKey id) {
      return (RecipePropertySet)this.propertySets.getOrDefault(id, RecipePropertySet.EMPTY);
   }

   public SelectableRecipe.SingleInputSet stonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public Collection getRecipes() {
      return this.recipes.values();
   }

   public Collection getLearnableRecipes() {
      return this.learnableRecipes;
   }

   public RecipeManager.@Nullable ServerDisplayInfo getRecipeFromDisplay(final RecipeDisplayId id) {
      int index = id.index();
      return index >= 0 && index < this.allDisplays.size() ? (RecipeManager.ServerDisplayInfo)this.allDisplays.get(index) : null;
   }

   public void listDisplaysForRecipe(final ResourceKey id, final Consumer output) {
      List recipes = (List)this.recipeToDisplay.get(id);
      if (recipes != null) {
         recipes.forEach((e) -> output.accept(e.display));
      }

   }

   public static RecipeManager.CachedCheck createCheck(final RecipeType type) {
      return new RecipeManager.CachedCheck() {
         private @Nullable ResourceKey lastRecipe;

         public Optional getRecipeFor(final RecipeInput input, final ServerLevel level) {
            RecipeManager recipeManager = level.recipeAccess();
            Optional result = recipeManager.getRecipeFor(type, input, level, this.lastRecipe);
            if (result.isPresent()) {
               RecipeHolder unpackedResult = (RecipeHolder)result.get();
               this.lastRecipe = unpackedResult.id();
               return Optional.of(unpackedResult);
            } else {
               return Optional.empty();
            }
         }
      };
   }

   private static List unpackRecipeInfo(final Iterable recipes, final FeatureFlagSet enabledFeatures) {
      List result = new ArrayList();
      Object2IntMap recipeGroups = new Object2IntOpenHashMap();

      for(RecipeHolder recipeHolder : recipes) {
         Recipe recipe = recipeHolder.value();
         OptionalInt groupId;
         if (recipe.group().isEmpty()) {
            groupId = OptionalInt.empty();
         } else {
            groupId = OptionalInt.of(recipeGroups.computeIfAbsent(recipe.group(), (idx) -> recipeGroups.size()));
         }

         Optional placementCheck;
         if (recipe.isSpecial()) {
            placementCheck = Optional.empty();
         } else {
            placementCheck = Optional.of(recipe.placementInfo().ingredients());
         }

         for(RecipeDisplay recipeDisplay : recipe.display()) {
            if (recipeDisplay.isEnabled(enabledFeatures)) {
               int nextDisplayId = result.size();
               RecipeDisplayId id = new RecipeDisplayId(nextDisplayId);
               RecipeDisplayEntry entry = new RecipeDisplayEntry(id, recipeDisplay, groupId, recipe.recipeBookCategory(), placementCheck);
               result.add(new RecipeManager.ServerDisplayInfo(entry, recipeHolder));
            }
         }
      }

      return result;
   }

   private static RecipeManager.IngredientExtractor forSingleInput(final RecipeType type) {
      return (recipe) -> {
         Optional var10000;
         if (recipe.getType() == type && recipe instanceof SingleItemRecipe singleItemRecipe) {
            var10000 = Optional.of(singleItemRecipe.input());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      };
   }

   public interface CachedCheck {
      Optional getRecipeFor(RecipeInput input, ServerLevel level);
   }

   public static class IngredientCollector implements Consumer {
      private final ResourceKey key;
      private final RecipeManager.IngredientExtractor extractor;
      private final List ingredients = new ArrayList();

      protected IngredientCollector(final ResourceKey key, final RecipeManager.IngredientExtractor extractor) {
         this.key = key;
         this.extractor = extractor;
      }

      public void accept(final Recipe recipe) {
         this.extractor.apply(recipe).ifPresent(this.ingredients::add);
      }

      public RecipePropertySet asPropertySet(final FeatureFlagSet enabledFeatures) {
         return RecipePropertySet.create(RecipeManager.filterDisabled(enabledFeatures, this.ingredients));
      }
   }

   @FunctionalInterface
   public interface IngredientExtractor {
      Optional apply(Recipe recipe);
   }

   public static record ServerDisplayInfo(RecipeDisplayEntry display, RecipeHolder parent) {
   }
}
