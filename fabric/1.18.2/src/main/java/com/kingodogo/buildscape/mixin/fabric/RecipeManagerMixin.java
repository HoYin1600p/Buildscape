package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void buildscape$recipes(java.util.Map<ResourceLocation, JsonElement> recipes, ResourceManager manager,
            ProfilerFiller profiler, CallbackInfo ci) {
        com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager((RecipeManager)(Object)this);
    }
}
