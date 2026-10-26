package com.kingodogo.buildscape.recipe.framework;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeReloadBindingPortTest {
    @Test
    void managerArrivingAfterPreparationCompletesOnlyItsOwnReload() {
        var firstResources = resources();
        var secondResources = resources();
        var first = BuildScapeRecipeLoader.forResourceReload(firstResources);
        var second = BuildScapeRecipeLoader.forResourceReload(secondResources);
        first.applyRecipes(java.util.List.of());
        second.applyRecipes(java.util.List.of());
        var firstManager = new RecipeManager();
        var secondManager = new RecipeManager();
        BuildScapeRecipeLoader.bindResourceManager(secondResources, secondManager);
        BuildScapeRecipeLoader.bindResourceManager(firstResources, firstManager);
        assertSame(firstManager, first.getCurrentRecipeManager());
        assertSame(secondManager, second.getCurrentRecipeManager());
        assertNull(BuildScapeRecipeLoader.forResourceReload(firstResources).getCurrentRecipeManager());
    }

    @Test
    void managerArrivingBeforePreparationIsNotReusedByTheNextReload() {
        var resources = resources();
        var manager = new RecipeManager();
        BuildScapeRecipeLoader.bindResourceManager(resources, manager);
        var reload = BuildScapeRecipeLoader.forResourceReload(resources);
        assertSame(manager, reload.getCurrentRecipeManager());
        reload.applyRecipes(java.util.List.of());
        assertNull(BuildScapeRecipeLoader.forResourceReload(resources).getCurrentRecipeManager());
    }

    private static ResourceManager resources() {
        return (ResourceManager) java.lang.reflect.Proxy.newProxyInstance(ResourceManager.class.getClassLoader(),
                new Class<?>[]{ResourceManager.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    case "toString" -> "test resources";
                    default -> throw new AssertionError(method.getName());
                });
    }

    @Test
    void overlappingReloadsKeepTheirOwnManagers() {
        var first = new RecipeManager();
        var second = new RecipeManager();
        var firstReload = BuildScapeRecipeLoader.forReload(first);
        var secondReload = BuildScapeRecipeLoader.forReload(second);
        assertNotSame(firstReload, secondReload);
        assertSame(first, firstReload.getCurrentRecipeManager());
        assertSame(second, secondReload.getCurrentRecipeManager());
    }

    @Test
    void boundReloadCannotBeRetargetedByAnotherReload() {
        var first = new RecipeManager();
        var reload = BuildScapeRecipeLoader.forReload(first);
        assertThrows(IllegalStateException.class, () -> reload.setCurrentRecipeManager(new RecipeManager()));
        assertSame(first, reload.getCurrentRecipeManager());
        assertThrows(NullPointerException.class, () -> BuildScapeRecipeLoader.forReload(null));
    }
}
