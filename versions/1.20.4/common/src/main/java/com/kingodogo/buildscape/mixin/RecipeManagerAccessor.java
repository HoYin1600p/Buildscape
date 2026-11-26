package com.kingodogo.buildscape.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor("byType")
    Multimap<RecipeType<?>, RecipeHolder<?>> buildscape$getByType();

    @Accessor("byType")
    void buildscape$setByType(Multimap<RecipeType<?>, RecipeHolder<?>> recipes);

    @Accessor("byName")
    Map<ResourceLocation, RecipeHolder<?>> buildscape$getByName();

    @Accessor("byName")
    void buildscape$setByName(Map<ResourceLocation, RecipeHolder<?>> recipes);
}
