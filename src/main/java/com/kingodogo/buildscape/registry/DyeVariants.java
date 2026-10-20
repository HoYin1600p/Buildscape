package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.core.DyeColors;
import com.kingodogo.buildscape.registry.blocks.BlocksRegister;
import com.kingodogo.buildscape.registry.blocks.DecorBlocks;
import com.kingodogo.buildscape.registry.items.ItemsRegister;
import com.kingodogo.buildscape.registry.items.MasonryItems;
import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The per-colour registry entries named {@code <colour>_festive_stocking} and {@code <colour>_item_frame}, found by
 * {@link DyeColors} instead of by a hand-written 16-way switch in every class that needs one.
 */
public final class DyeVariants {
    private static final Map<DyeColors, Supplier<Block>> STOCKING_BLOCKS = byColor(BlocksRegister.blocks(),
            "_festive_stocking");
    private static final Map<DyeColors, Supplier<Item>> STOCKING_ITEMS = byColor(ItemsRegister.items(),
            "_festive_stocking");
    private static final Map<DyeColors, Supplier<Item>> ITEM_FRAMES = byColor(ItemsRegister.items(), "_item_frame");

    private DyeVariants() {}

    private static <T> Map<DyeColors, Supplier<T>> byColor(DeferredRegister<T> register, String suffix) {
        Map<String, RegistryObject<T>> byPath = new HashMap<>();
        for (RegistryObject<T> entry : register.getEntries()) {
            byPath.put(entry.getId().getPath(), entry);
        }
        Map<DyeColors, Supplier<T>> result = new EnumMap<>(DyeColors.class);
        for (DyeColors color : DyeColors.values()) {
            String path = color.serializedName() + suffix;
            result.put(color, Objects.requireNonNull(byPath.get(path), () -> "not registered: " + path));
        }
        return result;
    }

    /** The festive stocking block of a colour name; the plain stocking for null or any other name. */
    public static Block festiveStockingBlock(String color) {
        DyeColors dye = DyeColors.fromName(color);
        return (dye == null ? DecorBlocks.FESTIVE_STOCKING : STOCKING_BLOCKS.get(dye)).get();
    }

    /** The festive stocking item of a colour name; the plain stocking for any other name. */
    public static Item festiveStockingItem(String color) {
        DyeColors dye = DyeColors.fromName(color);
        return (dye == null ? WoodItems.FESTIVE_STOCKING : STOCKING_ITEMS.get(dye)).get();
    }

    /** The coloured item frame item of a colour name, {@code invisible} included; white for any other name. */
    public static Item itemFrameItem(String color) {
        if ("invisible".equals(color)) {
            return MasonryItems.INVISIBLE_ITEM_FRAME.get();
        }
        DyeColors dye = DyeColors.fromName(color);
        return (dye == null ? MasonryItems.WHITE_ITEM_FRAME : ITEM_FRAMES.get(dye)).get();
    }
}
