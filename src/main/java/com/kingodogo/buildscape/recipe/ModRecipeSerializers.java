package com.kingodogo.buildscape.recipe;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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

}
