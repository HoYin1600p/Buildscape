package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public final class ModCreativeTabs {
    public static final CommonId TAB_ID = CommonId.of(BuildscapeCommon.MOD_ID, "buildscape_tab");
    public static final String TAB_TRANSLATION_KEY = "itemGroup.buildscape";
    private static final List<String> ORDERED_ITEM_IDS = loadOrderedItemIds();

    private ModCreativeTabs() {}
    public static CreativeModeTab createTab(Map<String, Item> modItems) {
        return Services.PLATFORM.createCreativeTab(
                TAB_ID,
                TAB_TRANSLATION_KEY,
                () -> {
                    Item iconItem = modItems.get("bit_oxidized_copper_block");
                    return new ItemStack(iconItem != null ? iconItem : modItems.values().iterator().next());
                },
                ORDERED_ITEM_IDS,
                modItems
        );
    }
    private static List<String> loadOrderedItemIds() {
        List<String> list = new ArrayList<>(3200);
        try (InputStream in = ModCreativeTabs.class.getResourceAsStream("/data/buildscape/creative_tab_order.txt")) {
            if (in != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String clean = line.trim();
                        if (!clean.isEmpty() && !clean.startsWith("#")) {
                            list.add(clean);
                        }
                    }
                }
            }
        } catch (Exception e) {
            BuildscapeCommon.LOGGER.warn("Failed to read creative tab item list", e);
        }
        List<String> haze = List.of("white", "light_gray", "gray", "black", "brown", "red", "orange", "yellow",
                "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink");
        list.removeIf(id -> id.endsWith("_haze_bush") || id.equals("ice_crystal"));
        int hazeIndex = list.indexOf("yellow_monets") + 1;
        for (String color : haze) list.add(hazeIndex++, color + "_haze_bush");
        list.add(list.indexOf("packed_icicle_block") + 1, "ice_crystal");
        return list;
    }
}
