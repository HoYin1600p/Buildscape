package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.compat.vertical.VerticalRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        // Tagging logic for static variants would go here.
        // Dynamic horizontal/vertical conversions are handled at runtime via VerticalResourcePack.
    }
}
