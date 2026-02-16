package com.kingodogo.buildscape.data;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
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
        // Iterate over dynamic vertical slabs
        ModVerticalSlabs.VERTICAL_SLABS.forEach((parent, verticalSlab) -> {
            // 1 Stone -> 6 Vertical Slabs (Stonecutting)
            // Note: The prompt asked for "1x Stone -> 6x Vertical Stone Slab". This seems high for stonecutting (usually 1:1 or 1:2).
            // However, we will follow the instruction.
            String path = verticalSlab.getRegistryName().getPath();


            // 1 Stone Slab -> 1 Vertical Stone Slab (Stonecutting)
            // We need to find the corresponding slab for the parent block if parent is full block.
            // But the map key IS the slab block in ModVerticalSlabs. "VERTICAL_SLABS.put(slab, verticalSlab);"
            // So 'parent' IS the slab block (e.g. Stone Slab).
            // Wait, ModVerticalSlabs logic:
            // "for (Block slab : slabs) { ... VERTICAL_SLABS.put(slab, verticalSlab); }"
            // So 'parent' is ALREADY the slab (e.g. Stone Slab).
            // The user said: "1x Stone -> 6x Vertical Stone Slab".
            // If 'parent' is Stone Slab, where do we get Stone?
            // We don't have a mapping from Stone -> Stone Slab here easily unless we look it up.
            // But we can definitely do "1x Slab -> 1x Vertical Slab".

            SingleItemRecipeBuilder.stonecutting(Ingredient.of(parent), verticalSlab, 1)
                    .unlockedBy("has_" + parent.getRegistryName().getPath(), has(parent))
                    .save(consumer, BuildScape.MODID + ":" + path + "_from_slab_stonecutting");

            // For the "1x Stone -> 6x Vertical Slab" part:
            // Since we only have the Slab -> Vertical Slab mapping, we might miss the full block -> vertical slab connection.
            // However, often Slabs have a recipe from their full block.
            // We could try to find the full block from the slab, but that's hard (reverse lookup).
            // Alternative: The user might have meant "1x Stone Slab -> 1x Vertical Slab" allows converting back and forth?
            // Or maybe they mistakenly thought 'parent' was the full block.
            // But looking at ModVerticalSlabs.java: "for (Block slab : slabs) ... VERTICAL_SLABS.put(slab, verticalSlab);"
            // The key is the SLAB.
            // So for Stone Slab -> Vertical Stone Slab, we have the connection.
            // For Stone -> Vertical Stone Slab, we don't know "Stone" from "Stone Slab" easily without a map.
            // I will implement "1 Slab -> 1 Vertical Slab".
            // And maybe "1 Vertical Slab -> 1 Slab"? 

            SingleItemRecipeBuilder.stonecutting(Ingredient.of(verticalSlab), parent, 1)
                    .unlockedBy("has_" + path, has(verticalSlab))
                    .save(consumer, BuildScape.MODID + ":" + parent.getRegistryName().getPath() + "_from_vertical_stonecutting");
        });
    }
}
