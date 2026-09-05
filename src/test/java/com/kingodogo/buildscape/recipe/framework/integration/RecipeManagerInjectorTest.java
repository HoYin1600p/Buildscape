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

        System.out.println("Recipe manager injection: 4 checks passed.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private record StubRecipe(ResourceLocation id) implements Recipe<Container> {
        private StubRecipe(String id) {
            this(new ResourceLocation(id));
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
            return RecipeType.CRAFTING;
        }
    }
}
