package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class AliasResolver {

    private final Map<String, String> aliases = new HashMap<>();

    public AliasResolver() {
        aliases.put("BS:", "buildscape:");
        aliases.put("MC:", "minecraft:");
        aliases.put("F:", "forge:");
    }

    public void registerAliases(Map<String, String> newAliases) {
        if (newAliases != null) {
            aliases.putAll(newAliases);
        }
    }

    public String resolveString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        boolean isTag = input.startsWith("#");
        if (isTag) {
            input = input.substring(1);
        }

        if (aliases.containsKey(input)) {
            input = aliases.get(input);
        }

        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            String prefix = entry.getKey();
            if (prefix.endsWith(":") && input.startsWith(prefix)) {
                input = entry.getValue() + input.substring(prefix.length());
                break;
            }
        }

        if (!input.contains(":")) {
            input = "buildscape:" + input;
        }

        return isTag ? "#" + input : input;
    }

    public Ingredient resolveIngredient(String rawSpec) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return Ingredient.EMPTY;
        }

        if (rawSpec.startsWith("[") && rawSpec.endsWith("]")) {
            String inner = rawSpec.substring(1, rawSpec.length() - 1);
            String[] parts = inner.split(",");
            java.util.List<Ingredient> ingredients = new java.util.ArrayList<>();
            for (String part : parts) {
                Ingredient ing = resolveIngredient(part.trim());
                if (!ing.isEmpty()) {
                    ingredients.add(ing);
                }
            }
            return ingredients.isEmpty() ? Ingredient.EMPTY : Ingredient.merge(ingredients);
        }

        String resolved = resolveString(rawSpec);

        if (resolved.startsWith("#")) {
            String tagId = resolved.substring(1);
            TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(tagId));
            return IngredientCache.get(tagKey);
        }

        ResourceLocation loc = new ResourceLocation(resolved);
        Item item = ForgeRegistries.ITEMS.getValue(loc);
        if (item != null && ForgeRegistries.ITEMS.containsKey(loc)) {
            return IngredientCache.get(item);
        }

        return Ingredient.EMPTY;
    }

    public Item resolveItem(String rawSpec) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return null;
        }
        String resolved = resolveString(rawSpec);
        ResourceLocation loc = new ResourceLocation(resolved);
        return ForgeRegistries.ITEMS.getValue(loc);
    }
}
