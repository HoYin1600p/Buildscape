package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(
                    ForgeRegistries.RECIPE_SERIALIZERS,
                    BuildScape.MODID
            );

    public static final RegistryObject<
            RecipeSerializer<ShapedDurabilityRecipe>
            > SHAPED_DURABILITY_RECIPE = RECIPE_SERIALIZERS.register(
            "shaped_durability",
            () -> ShapedDurabilityRecipe.SERIALIZER
    );

    public static final RegistryObject<
            RecipeSerializer<ShapelessDurabilityRecipe>
            > SHAPELESS_DURABILITY_RECIPE = RECIPE_SERIALIZERS.register(
            "shapeless_durability",
            () -> ShapelessDurabilityRecipe.SERIALIZER
    );

    public static final RegistryObject<
            SimpleRecipeSerializer<ConfettiConfigureRecipe>
            > CONFETTI_CONFIGURE_RECIPE = RECIPE_SERIALIZERS.register(
            "confetti_configure",
            () -> new SimpleRecipeSerializer<>(ConfettiConfigureRecipe::new)
    );

    public static final RegistryObject<
            SimpleRecipeSerializer<ClearShulkerFiltersRecipe>
            > CLEAR_SHULKER_FILTERS_RECIPE = RECIPE_SERIALIZERS.register(
            "clear_shulker_filters",
            () -> new SimpleRecipeSerializer<>(ClearShulkerFiltersRecipe::new)
    );

    public static final RegistryObject<
            SimpleRecipeSerializer<CustomFireworkStarRecipe>
            > CUSTOM_FIREWORK_STAR_RECIPE = RECIPE_SERIALIZERS.register(
            "custom_firework_star",
            () -> new SimpleRecipeSerializer<>(CustomFireworkStarRecipe::new)
    );

    public static final RegistryObject<
            SimpleRecipeSerializer<InfinitePhoenixFireworkStarRecipe>
            > INFINITE_PHOENIX_FIREWORK_STAR_RECIPE = RECIPE_SERIALIZERS.register(
            "infinite_phoenix_firework_star",
            () -> new SimpleRecipeSerializer<>(InfinitePhoenixFireworkStarRecipe::new)
    );

}
