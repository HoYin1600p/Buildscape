package com.kingodogo.buildscape.recipe.framework.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public final class RecipeManagerInjectorTest {

    public static void main(String[] args) {
        Recipe<?> vanilla = new StubRecipe("minecraft:shared");
        Recipe<?> originalBuildscape = new StubRecipe("buildscape:shared");
        Recipe<?> replacementBuildscape = new StubRecipe("buildscape:shared");
        Recipe<?> addedBuildscape = new StubRecipe("buildscape:added");

        Map<ResourceLocation, Recipe<?>> merged = RecipeManagerInjector.mergeById(
                List.of(vanilla, originalBuildscape),
                List.of(replacementBuildscape, addedBuildscape));

        require(merged.size() == 3, "Unexpected merged recipe count");
        require(merged.get(vanilla.getId()) == vanilla, "Vanilla recipe was not preserved");
        require(merged.get(replacementBuildscape.getId()) == replacementBuildscape,
                "Buildscape recipe did not override the matching ID");
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
        require(indexes.byType().get("crafting").get(replacementPlain.id()) == replacementPlain,
                "Typed recipe index did not replace the matching ID");
        require(indexes.byName().get(replacementPlain.id()) == replacementPlain,
                "Named recipe index did not replace the matching ID");
        require(indexes.byName().get(addedPlain.id()) == addedPlain,
                "Named recipe index did not include the new recipe");

        System.out.println("Recipe manager injection: 8 checks passed.");
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
