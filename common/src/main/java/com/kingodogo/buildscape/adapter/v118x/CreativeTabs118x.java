package com.kingodogo.buildscape.adapter.v118x;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import com.kingodogo.buildscape.item.InfinitePhoenixFireworkStarItem;
import com.kingodogo.buildscape.trophy.Trophies;
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
                ResourceLocation key = new ResourceLocation("buildscape", id);
                Item item = Registry.ITEM.containsKey(key) ? Registry.ITEM.get(key) : modItems.get(id);
                if (item != null && added.add(item)) {
                    raw.add(new ItemStack(item));
                    if (id.equals("music_disc_builder")) {
                        addFireworks(raw);
                        added.add(InfinitePhoenixFireworkStarItem.createDefaultStack().getItem());
                    }
                    if (id.equals("festive_wandering_homemaker_spawn_egg")) {
                        for (var trophy : Trophies.getAll()) {
                            Item trophyItem = trophy.getItem();
                            if (trophyItem != null && added.add(trophyItem)) raw.add(new ItemStack(trophyItem));
                        }
                    }
                }
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
                if (id != null && ("buildscape".equals(id.getNamespace())
                        || stack.is(Items.FIREWORK_STAR) || stack.is(Items.FIREWORK_ROCKET))) stacks.add(stack);
            }
        }
    };

    private CreativeTabs118x() {}

    private static void addFireworks(NonNullList<ItemStack> stacks) {
        byte[] shapes = {5, 6, 7, 8, 9, 10, 11, 12};
        int[][] colors = {
                {0xFFFDD0, 0x8B4513, 0xFF2D55}, {0xFFD700, 0xFFFF77, 0xFF0044, 0x0066FF},
                {0xFFD700, 0xFFFF88, 0x00FFFF}, {0x227733, 0xFF3030, 0xFFD700},
                {0xFF2233, 0xFFD700}, {0xFF0033}, {0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00},
                {0xFFFFFF, 0xE0F7FF, 0x5AC8FF}
        };
        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] == 11) continue;
            ItemStack star = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag explosion = star.getOrCreateTagElement("Explosion");
            explosion.putByte("Type", shapes[i]);
            explosion.putIntArray("Colors", colors[i]);
            stacks.add(star);
        }
        stacks.add(InfinitePhoenixFireworkStarItem.createDefaultStack());
        for (int i = 0; i < shapes.length; i++) {
            ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
            CompoundTag explosion = new CompoundTag();
            explosion.putByte("Type", shapes[i]);
            explosion.putIntArray("Colors", colors[i]);
            explosion.putBoolean("Flicker", true);
            explosion.putBoolean("Trail", true);
            ListTag explosions = new ListTag();
            explosions.add(explosion);
            CompoundTag fireworks = rocket.getOrCreateTagElement("Fireworks");
            fireworks.put("Explosions", explosions);
            fireworks.putByte("Flight", (byte) 1);
            stacks.add(rocket);
        }
    }

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
