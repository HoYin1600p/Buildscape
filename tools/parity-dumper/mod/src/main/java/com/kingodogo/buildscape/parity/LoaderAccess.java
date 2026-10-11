package com.kingodogo.buildscape.parity;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.Collection;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

final class LoaderAccess {
    private LoaderAccess() {}

    static void manifest(JsonObject manifest) { manifest.addProperty("forge", "40.3.12"); }

    static Field findField(Class<?> owner, String name) {
        return ObfuscationReflectionHelper.findField(owner, name);
    }

    static FoodProperties food(Item item, ItemStack stack) { return item.getFoodProperties(stack, null); }

    static Collection<CreativeModeTab> creativeTabs(Item item) { return item.getCreativeTabs(); }
}
