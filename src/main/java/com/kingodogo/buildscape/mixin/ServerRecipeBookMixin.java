package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.core.RecipeIdMigration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ServerRecipeBook.class)
public abstract class ServerRecipeBookMixin {
    @Redirect(method = "loadRecipes", require = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;byKey(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"))
    private Optional<? extends Recipe<?>> buildscape$migrateRecipeId(RecipeManager manager, ResourceLocation id) {
        Optional<? extends Recipe<?>> recipe = manager.byKey(id);
        if (recipe.isPresent()) {
            return recipe;
        }
        ResourceLocation migrated = RecipeIdMigration.translate(id);
        return id.equals(migrated) ? recipe : manager.byKey(migrated);
    }
}
