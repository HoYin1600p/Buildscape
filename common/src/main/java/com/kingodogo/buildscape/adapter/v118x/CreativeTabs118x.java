package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import com.kingodogo.buildscape.util.CommonId;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** Legacy tabs exist before items are constructed, because each item owns its tab. */
public final class CreativeTabs118x {
    private static List<String> orderedIds = List.of();
    private static Map<String, Item> modItems = Map.of();
    private static Supplier<ItemStack> icon = () -> new ItemStack(
            Registry.ITEM.get(new ResourceLocation("buildscape", "bit_oxidized_copper_block")));
    private static final CreativeModeTab TAB = new CreativeModeTab(CreativeModeTab.TABS.length, "buildscape") {
        @Override
        public ItemStack makeIcon() {
            return icon.get();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> stacks) {
            NonNullList<ItemStack> raw = NonNullList.create();
            Set<Item> added = new HashSet<>();
            for (String id : orderedIds) {
                Item item = modItems.get(id);
                if (item != null && added.add(item)) raw.add(new ItemStack(item));
            }
            // Main appends items omitted from its hardcoded order in registry order.
            NonNullList<ItemStack> others = NonNullList.create();
            super.fillItemList(others);
            for (ItemStack stack : others) {
                if (added.add(stack.getItem())) raw.add(stack);
            }
            stacks.clear();
            for (ItemStack stack : raw) {
                ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
                if (id != null && "buildscape".equals(id.getNamespace())) stacks.add(stack);
            }
        }
    };

    private CreativeTabs118x() {}

    public static CreativeModeTab configure(Supplier<ItemStack> iconSupplier,
                                            List<String> itemOrder, Map<String, Item> items) {
        icon = iconSupplier;
        orderedIds = List.copyOf(itemOrder);
        modItems = items;
        return TAB;
    }

    public static CreativeModeTab tabFor(CommonId id) {
        String path = id.getPath();
        if (path.endsWith("_vertical_slab")) {
            String base = path.substring(0, path.length() - "_vertical_slab".length());
            if (Registry.ITEM.containsKey(new ResourceLocation("minecraft", base))
                    || Registry.ITEM.containsKey(new ResourceLocation("minecraft", base + "_slab"))) {
                return base.endsWith("_wool") || base.equals("crying_obsidian") || base.equals("tinted_glass")
                        ? CreativeModeTab.TAB_DECORATIONS : CreativeModeTab.TAB_BUILDING_BLOCKS;
            }
        }
        return TAB;
    }
}
