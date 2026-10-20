package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.feature.firework.CustomFireworkStarRecipe;
import com.kingodogo.buildscape.feature.firework.InfinitePhoenixFireworkStarRecipe;
import com.kingodogo.buildscape.recipe.ClearShulkerFiltersRecipe;
import com.kingodogo.buildscape.recipe.ConfettiConfigureRecipe;
import com.kingodogo.buildscape.recipe.HazeBushDyeRecipe;
import com.kingodogo.buildscape.recipe.ShapedDurabilityRecipe;
import com.kingodogo.buildscape.recipe.ShapelessDurabilityRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

/**
 * Deferred register for the mod's custom recipe serializers (durability recipes, haze bush dye, confetti configure,
 * shulker filter clearing and custom firework stars).
 */
public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            BuildScape.MODID);

    public static final RegistryObject<RecipeSerializer<ShapedDurabilityRecipe>> SHAPED_DURABILITY_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "shaped_durability",
                    () -> ShapedDurabilityRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<ShapelessDurabilityRecipe>> SHAPELESS_DURABILITY_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "shapeless_durability",
                    () -> ShapelessDurabilityRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<HazeBushDyeRecipe>> HAZE_BUSH_DYE_RECIPE = RECIPE_SERIALIZERS
            .register("haze_bush_dye", () -> HazeBushDyeRecipe.SERIALIZER);

    public static final RegistryObject<SimpleRecipeSerializer<ConfettiConfigureRecipe>> CONFETTI_CONFIGURE_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "confetti_configure",
                    () -> new SimpleRecipeSerializer<>(ConfettiConfigureRecipe::new));

    public static final RegistryObject<SimpleRecipeSerializer<ClearShulkerFiltersRecipe>> CLEAR_SHULKER_FILTERS_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "clear_shulker_filters",
                    () -> new SimpleRecipeSerializer<>(ClearShulkerFiltersRecipe::new));

    public static final RegistryObject<SimpleRecipeSerializer<CustomFireworkStarRecipe>> CUSTOM_FIREWORK_STAR_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "custom_firework_star",
                    () -> new SimpleRecipeSerializer<>(CustomFireworkStarRecipe::new));

    public static final RegistryObject<SimpleRecipeSerializer<InfinitePhoenixFireworkStarRecipe>> INFINITE_PHOENIX_FIREWORK_STAR_RECIPE = RECIPE_SERIALIZERS
            .register(
                    "infinite_phoenix_firework_star",
                    () -> new SimpleRecipeSerializer<>(InfinitePhoenixFireworkStarRecipe::new));

}
