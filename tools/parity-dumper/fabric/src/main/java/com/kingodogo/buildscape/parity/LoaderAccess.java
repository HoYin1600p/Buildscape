package com.kingodogo.buildscape.parity;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

final class LoaderAccess {
    private LoaderAccess() {}

    static void manifest(JsonObject manifest) {
        manifest.addProperty("loader", "fabric");
        manifest.addProperty("fabric", "0.16.14");
    }

    static Field findField(Class<?> owner, String name) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    static FoodProperties food(Item item, ItemStack stack) { return item.getFoodProperties(); }

    static Collection<CreativeModeTab> creativeTabs(Item item) {
        CreativeModeTab tab = item.getItemCategory();
        return tab == null ? List.of() : List.of(tab);
    }
}
