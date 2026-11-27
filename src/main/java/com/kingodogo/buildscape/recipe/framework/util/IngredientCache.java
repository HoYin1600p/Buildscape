package com.kingodogo.buildscape.recipe.framework.util;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IngredientCache {

    private static final Map<Item, Ingredient> ITEM_INGREDIENT_CACHE = new ConcurrentHashMap<>();
    private static final Map<TagKey<Item>, Ingredient> TAG_INGREDIENT_CACHE = new ConcurrentHashMap<>();

    public static Ingredient get(ItemLike item) {
        if (item == null) {
            return Ingredient.of();
        }
        return ITEM_INGREDIENT_CACHE.computeIfAbsent(item.asItem(), Ingredient::of);
    }

    public static Ingredient get(TagKey<Item> tag) {
        if (tag == null) {
            return Ingredient.of();
        }
        return TAG_INGREDIENT_CACHE.computeIfAbsent(tag, key -> {
            String value = key.location().toString();
            String[] parts = value.split(":", 2);
            return Services.PLATFORM.createTagIngredient(new CommonId(
                    parts.length == 2 ? parts[0] : "minecraft",
                    parts.length == 2 ? parts[1] : parts[0]));
        });
    }

    public static Ingredient ofItems(ItemLike... items) {
        if (items == null || items.length == 0) {
            return Ingredient.of();
        }
        if (items.length == 1) {
            return get(items[0]);
        }
        return Ingredient.of(items);
    }

    public static void clear() {
        ITEM_INGREDIENT_CACHE.clear();
        TAG_INGREDIENT_CACHE.clear();
    }
}
