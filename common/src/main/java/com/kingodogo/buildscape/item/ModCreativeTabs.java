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
    private static final List<String> ORDERED_ITEM_IDS = loadOrderedItemIds();

    private ModCreativeTabs() {}
    public static CreativeModeTab createTab(Map<String, Item> modItems) {
        return Services.PLATFORM.createCreativeTab(
                TAB_ID,
                "itemGroup.buildscape",
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
        } catch (Exception ignored) {}
        return list;
    }
}
