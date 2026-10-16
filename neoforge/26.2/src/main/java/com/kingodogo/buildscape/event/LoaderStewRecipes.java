package com.kingodogo.buildscape.event;

import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;

/** Prepare the result before normal or shift-click crafting moves it into an inventory. */
public final class LoaderStewRecipes {
    private LoaderStewRecipes() {}

    public static void prepare(ModifyRecipeJsonsEvent event) {
        event.getRecipeJsons().putIfAbsent(Identifier.fromNamespaceAndPath("buildscape", "suspicious_stew_from_frost_rose"),
                JsonParser.parseString("""
                        {
                          "type": "minecraft:crafting_shapeless",
                          "group": "suspicious_stew",
                          "ingredients": ["minecraft:bowl", "minecraft:brown_mushroom", "minecraft:red_mushroom", "buildscape:frost_rose"],
                          "result": {
                            "id": "minecraft:suspicious_stew",
                            "components": {"minecraft:custom_data": {"FrostRoseStew": 1}}
                          }
                        }
                        """));
    }
}
