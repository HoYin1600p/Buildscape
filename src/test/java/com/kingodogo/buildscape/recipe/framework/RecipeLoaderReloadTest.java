package com.kingodogo.buildscape.recipe.framework;

import net.minecraft.world.item.crafting.RecipeManager;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class RecipeLoaderReloadTest {
    @Test
    public void everyReloadGetsItsOwnListenerBoundToItsOwnManager() {
        RecipeManager first = new RecipeManager();
        RecipeManager second = new RecipeManager();
        BuildScapeRecipeLoader a = BuildScapeRecipeLoader.forReload(first);
        BuildScapeRecipeLoader b = BuildScapeRecipeLoader.forReload(second);
        assertNotSame(a, b);
        assertSame(first, a.recipeManager());
        assertSame(second, b.recipeManager());
    }
}
