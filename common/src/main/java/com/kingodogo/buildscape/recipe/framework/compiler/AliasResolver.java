package com.kingodogo.buildscape.recipe.framework.compiler;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.util.IngredientCache;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.Map;

public class AliasResolver {

    private final Map<String, String> aliases = new HashMap<>();

    public AliasResolver() {
        aliases.put("BS:", "buildscape:");
        aliases.put("MC:", "minecraft:");
        aliases.put("F:", "c:");
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
            return Ingredient.of();
        }

        if (rawSpec.startsWith("[") && rawSpec.endsWith("]")) {
            String inner = rawSpec.substring(1, rawSpec.length() - 1);
            String[] parts = inner.split(",");
            java.util.List<net.minecraft.world.level.ItemLike> items = new java.util.ArrayList<>();
            for (String part : parts) {
                Item item = resolveItem(part.trim());
                if (item != null && item != net.minecraft.world.item.Items.AIR) {
                    items.add(item);
                }
            }
            return items.isEmpty() ? Ingredient.of() : Ingredient.of(items.toArray(new net.minecraft.world.level.ItemLike[0]));
        }

        String resolved = resolveString(rawSpec);

        if (resolved.startsWith("#")) {
            String tagId = resolved.substring(1);
            String[] split = tagId.split(":", 2);
            String ns = split.length > 1 ? split[0] : "minecraft";
            String path = split.length > 1 ? split[1] : split[0];
            return Services.PLATFORM.createTagIngredient(new CommonId(ns, path));
        }

        String[] split = resolved.split(":", 2);
        String ns = split.length > 1 ? split[0] : "minecraft";
        String path = split.length > 1 ? split[1] : split[0];
        Item item = Services.PLATFORM.getItem(new CommonId(ns, path));
        if (item != null && item != net.minecraft.world.item.Items.AIR) {
            return IngredientCache.get(item);
        }

        return Ingredient.of();
    }

    public Item resolveItem(String rawSpec) {
        if (rawSpec == null || rawSpec.isEmpty()) {
            return null;
        }
        String resolved = resolveString(rawSpec);
        String[] split = resolved.split(":", 2);
        String ns = split.length > 1 ? split[0] : "minecraft";
        String path = split.length > 1 ? split[1] : split[0];
        return Services.PLATFORM.getItem(new CommonId(ns, path));
    }
}
