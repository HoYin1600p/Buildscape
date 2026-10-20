package com.kingodogo.buildscape.recipe.framework.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public final class RecipeManagerInjectorTest {

    @Test
    public void mergeKeepsExistingRecipesAndOrder() {
        Recipe<?> vanilla = new StubRecipe("minecraft:shared");
        Recipe<?> originalBuildscape = new StubRecipe("buildscape:shared");
        Recipe<?> replacementBuildscape = new StubRecipe("buildscape:shared");
        Recipe<?> addedBuildscape = new StubRecipe("buildscape:added");

        Map<ResourceLocation, Recipe<?>> merged = RecipeManagerInjector.mergeById(
                List.of(vanilla, originalBuildscape),
                List.of(replacementBuildscape, addedBuildscape));

        require(merged.size() == 3, "Unexpected merged recipe count");
        require(merged.get(vanilla.getId()) == vanilla, "Vanilla recipe was not preserved");
        require(merged.get(originalBuildscape.getId()) == originalBuildscape,
                "An existing recipe was overwritten by a Buildscape recipe with the same ID");
        require(merged.get(addedBuildscape.getId()) == addedBuildscape, "New Buildscape recipe was not added");

        PlainRecipe vanillaPlain = new PlainRecipe("crafting", "minecraft:shared");
        PlainRecipe originalPlain = new PlainRecipe("crafting", "buildscape:shared");
        PlainRecipe replacementPlain = new PlainRecipe("crafting", "buildscape:shared");
        PlainRecipe addedPlain = new PlainRecipe("crafting", "buildscape:added");
        PlainRecipe smelting = new PlainRecipe("smelting", "minecraft:smelting");
        RecipeManagerInjector.Indexes<String, String, PlainRecipe> indexes = RecipeManagerInjector.mergeIndexesGeneric(
                Map.of(
                        "crafting", Map.of(vanillaPlain.id(), vanillaPlain, originalPlain.id(), originalPlain),
                        "smelting", Map.of(smelting.id(), smelting)),
                Map.of(vanillaPlain.id(), vanillaPlain, originalPlain.id(), originalPlain, smelting.id(), smelting),
                List.of(replacementPlain, addedPlain), PlainRecipe::type, PlainRecipe::id);

        require(indexes.byType().get("smelting").get(smelting.id()) == smelting,
                "Unrelated recipe type was not preserved");
        require(indexes.byType().get("crafting").get(originalPlain.id()) == originalPlain,
                "Typed recipe index replaced an existing ID");
        require(indexes.byName().get(originalPlain.id()) == originalPlain,
                "Named recipe index replaced an existing ID");
        require(indexes.byName().get(addedPlain.id()) == addedPlain,
                "Named recipe index did not include the new recipe");

        PlainRecipe disabledPlain = new PlainRecipe("crafting", "buildscape:disabled");
        RecipeManagerInjector.Indexes<String, String, PlainRecipe> withDisabled = RecipeManagerInjector
                .mergeIndexesGeneric(
                        Map.of("crafting", Map.of(vanillaPlain.id(), vanillaPlain)),
                        Map.of(vanillaPlain.id(), vanillaPlain),
                        List.of(disabledPlain, addedPlain), PlainRecipe::type, PlainRecipe::id,
                        java.util.Set.of(disabledPlain.id()));
        require(!withDisabled.byName().containsKey(disabledPlain.id()), "Disabled recipe was injected by name");
        require(!withDisabled.byType().get("crafting").containsKey(disabledPlain.id()),
                "Disabled recipe was injected by type");
        require(withDisabled.byName().get(addedPlain.id()) == addedPlain, "Enabled recipe was not injected");
        require(withDisabled.byName().get(vanillaPlain.id()) == vanillaPlain, "Existing recipe was lost");

        // Iteration order decides which of two matching recipes wins, so it must be insertion order:
        // existing recipes first, in their order, then the injected ones.
        java.util.LinkedHashMap<String, PlainRecipe> orderedExisting = new java.util.LinkedHashMap<>();
        List<String> expectedOrder = new java.util.ArrayList<>();
        for (int i = 0; i < 40; i++) {
            PlainRecipe recipe = new PlainRecipe("crafting", "pack:r" + (39 - i));
            orderedExisting.put(recipe.id(), recipe);
            expectedOrder.add(recipe.id());
        }
        List<PlainRecipe> injected = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            injected.add(new PlainRecipe("crafting", "buildscape:n" + (9 - i)));
            expectedOrder.add("buildscape:n" + (9 - i));
        }
        RecipeManagerInjector.Indexes<String, String, PlainRecipe> ordered = RecipeManagerInjector.mergeIndexesGeneric(
                Map.of("crafting", orderedExisting), orderedExisting, injected, PlainRecipe::type, PlainRecipe::id);
        require(new java.util.ArrayList<>(ordered.byName().keySet()).equals(expectedOrder),
                "Named recipe index lost insertion order");
        require(new java.util.ArrayList<>(ordered.byType().get("crafting").keySet()).equals(expectedOrder),
                "Typed recipe index lost insertion order");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private record PlainRecipe(String type, String id) {
    }

    private record StubRecipe(ResourceLocation id, RecipeType<?> type) implements Recipe<Container> {
        private StubRecipe(String id) {
            this(new ResourceLocation(id), null);
        }

        @Override
        public boolean matches(Container container, Level level) {
            return false;
        }

        @Override
        public ItemStack assemble(Container container) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return false;
        }

        @Override
        public ItemStack getResultItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return null;
        }

        @Override
        public RecipeType<?> getType() {
            return type;
        }
    }
}
