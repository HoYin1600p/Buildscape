package com.kingodogo.buildscape.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.core.NbtKeys;
import com.kingodogo.buildscape.feature.firework.CustomFireworkShapeRegistry;
import com.kingodogo.buildscape.feature.firework.FireworkTestCommand;
import com.kingodogo.buildscape.feature.firework.InfinitePhoenixFireworkStarItem;
import com.kingodogo.buildscape.feature.trophy.Trophies;
import com.kingodogo.buildscape.feature.trophy.TrophyDefinition;
import com.kingodogo.buildscape.registry.tab.TabOrder1;
import com.kingodogo.buildscape.registry.tab.TabOrder2;
import com.kingodogo.buildscape.registry.tab.TabOrder3;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ModCreativeModeTab {

    public static final CreativeModeTab BUILDSCAPE_TAB = new CreativeModeTab(BuildScape.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.BIT_OXIDIZED_COPPER_BLOCK.get());
        }

        private boolean isCustomFireworkVariant(ItemStack stack) {
            if (stack.is(Items.FIREWORK_STAR)) {
                CompoundTag explosion = stack.getTagElement(NbtKeys.EXPLOSION);
                return explosion != null
                        && CustomFireworkShapeRegistry.isCustomShape(explosion.getByte(NbtKeys.TYPE));
            }
            if (stack.is(Items.FIREWORK_ROCKET)) {
                CompoundTag fireworks = stack.getTagElement(NbtKeys.FIREWORKS);
                if (fireworks == null || !fireworks.contains(NbtKeys.EXPLOSIONS, 9)) return false;
                ListTag explosions = fireworks.getList(NbtKeys.EXPLOSIONS, 10);
                for (int i = 0; i < explosions.size(); i++) {
                    if (CustomFireworkShapeRegistry.isCustomShape(
                            explosions.getCompound(i).getByte(NbtKeys.TYPE))) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            NonNullList<ItemStack> rawList = NonNullList.create();
            addHardcodedItems(rawList);

            Set<Item> rawItems = Collections.newSetFromMap(new IdentityHashMap<>());
            for (ItemStack stack : rawList) {
                rawItems.add(stack.getItem());
            }

            NonNullList<ItemStack> others = NonNullList.create();
            super.fillItemList(others);
            for (ItemStack stack : others) {
                if (rawItems.add(stack.getItem())) {
                    rawList.add(stack);
                }
            }

            rawList = arrangeVariantItems(rawList);

            items.clear();
            Set<Item> added = new HashSet<>();

            for (ItemStack stack : rawList) {
                Item item = stack.getItem();
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                boolean customFireworkVariant = isCustomFireworkVariant(stack);
                if (id != null && !BuildScape.MODID.equals(id.getNamespace()) && !customFireworkVariant) {
                    continue;
                }
                if (!customFireworkVariant && added.contains(item)) continue;
                items.add(stack);
                if (!customFireworkVariant) added.add(item);
            }
            sortItems(items);
        }

        private NonNullList<ItemStack> arrangeVariantItems(NonNullList<ItemStack> input) {
            return input;
        }

        private String getVariantBasePath(String path) {
            if (path.startsWith("quartz_pillar_")) {
                return null;
            }
            if (path.endsWith("_vertical_slab")) {
                return path.substring(0, path.length() - "_vertical_slab".length());
            }
            if (path.endsWith("_stairs")) {
                return path.substring(0, path.length() - "_stairs".length());
            }
            if (path.endsWith("_slab")) {
                return path.substring(0, path.length() - "_slab".length());
            }
            if (path.endsWith("_wall")) {
                return path.substring(0, path.length() - "_wall".length());
            }
            return null;
        }

        private void arrangeVariantGroup(ItemStackOrder ordered, Map<String, ItemStack> buildscapeItems, String basePath) {
            if ("quartz_pillar".equals(basePath)) return;
            ItemStack parent = buildscapeItems.get(basePath);
            ItemStack stairs = buildscapeItems.get(basePath + "_stairs");
            ItemStack slab = buildscapeItems.get(basePath + "_slab");
            ItemStack verticalSlab = buildscapeItems.get(basePath + "_vertical_slab");
            ItemStack wall = buildscapeItems.get(basePath + "_wall");

            ItemStack anchor = firstPresent(parent, stairs, slab, verticalSlab, wall);
            if (anchor == null) return;

            if (parent != null && parent != anchor) {
                moveAfter(ordered, parent, anchor);
                anchor = parent;
            }
            if (stairs != null && stairs != anchor) {
                moveAfter(ordered, stairs, anchor);
                anchor = stairs;
            }
            if (slab != null && slab != anchor) {
                moveAfter(ordered, slab, anchor);
                anchor = slab;
            }
            if (verticalSlab != null && verticalSlab != anchor) {
                moveAfter(ordered, verticalSlab, anchor);
                anchor = verticalSlab;
            }
            if (wall != null && wall != anchor) {
                moveAfter(ordered, wall, anchor);
            }
        }

        private ItemStack firstPresent(ItemStack... stacks) {
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    return stack;
                }
            }
            return null;
        }

        private void moveAfter(ItemStackOrder ordered, ItemStack stack, ItemStack anchor) {
            if (anchor == null || stack == anchor) {
                return;
            }

            ordered.moveAfter(stack, anchor);
        }

        final class ItemStackOrder {
            private final Map<ItemStack, ItemStackNode> nodes = new IdentityHashMap<>();
            private ItemStackNode head;
            private ItemStackNode tail;

            private ItemStackOrder(List<ItemStack> stacks) {
                for (ItemStack stack : stacks) {
                    ItemStackNode node = new ItemStackNode(stack);
                    nodes.put(stack, node);

                    if (tail == null) {
                        head = node;
                    } else {
                        tail.next = node;
                        node.previous = tail;
                    }
                    tail = node;
                }
            }

            private void moveAfter(ItemStack stack, ItemStack anchor) {
                ItemStackNode node = nodes.get(stack);
                ItemStackNode anchorNode = nodes.get(anchor);
                if (node == null || node == anchorNode) {
                    return;
                }

                detach(node);
                if (anchorNode == null) {
                    append(node);
                    return;
                }

                ItemStackNode next = anchorNode.next;
                anchorNode.next = node;
                node.previous = anchorNode;
                node.next = next;

                if (next == null) {
                    tail = node;
                } else {
                    next.previous = node;
                }
            }

            private void detach(ItemStackNode node) {
                if (node.previous == null) {
                    head = node.next;
                } else {
                    node.previous.next = node.next;
                }

                if (node.next == null) {
                    tail = node.previous;
                } else {
                    node.next.previous = node.previous;
                }

                node.previous = null;
                node.next = null;
            }

            private void append(ItemStackNode node) {
                if (tail == null) {
                    head = node;
                } else {
                    tail.next = node;
                    node.previous = tail;
                }
                tail = node;
            }

            private NonNullList<ItemStack> toNonNullList() {
                NonNullList<ItemStack> output = NonNullList.create();
                for (ItemStackNode node = head; node != null; node = node.next) {
                    output.add(node.stack);
                }
                return output;
            }
        }

        final class ItemStackNode {
            private final ItemStack stack;
            private ItemStackNode previous;
            private ItemStackNode next;

            private ItemStackNode(ItemStack stack) {
                this.stack = stack;
            }
        }

    private void addHardcodedItems(@NotNull NonNullList<ItemStack> items) {
        addOrdered(items, TabOrder1.ITEMS);
        addOrdered(items, TabOrder2.ITEMS);
        addFireworkItems(items);
        addOrdered(items, TabOrder2.AFTER_FIREWORKS);
        addTrophyItems(items);
        addOrdered(items, TabOrder3.ITEMS);
    }

    private static void addOrdered(@NotNull NonNullList<ItemStack> items, List<Supplier<? extends ItemLike>> order) {
        for (Supplier<? extends ItemLike> entry : order) {
            items.add(new ItemStack(entry.get()));
        }
    }

    private void addFireworkItems(@NotNull NonNullList<ItemStack> items) {
            ItemStack cakeStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag cakeExp = cakeStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            cakeExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.CAKE_ID);
            cakeExp.putIntArray(NbtKeys.COLORS, new int[]{0xFFFDD0, 0x8B4513, 0xFF2D55});
            items.add(cakeStar);

            ItemStack crownStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag crownExp = crownStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            crownExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.CROWN_ID);
            crownExp.putIntArray(NbtKeys.COLORS, new int[]{0xFFD700, 0xFFFF77, 0xFF0044, 0x0066FF});
            items.add(crownStar);

            ItemStack trophyStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag trophyExp = trophyStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            trophyExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.TROPHY_ID);
            trophyExp.putIntArray(NbtKeys.COLORS, new int[]{0xFFD700, 0xFFFF88, 0x00FFFF});
            items.add(trophyStar);

            ItemStack treeStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag treeExp = treeStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            treeExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID);
            treeExp.putIntArray(NbtKeys.COLORS, new int[]{0x227733, 0xFF3030, 0xFFD700});
            items.add(treeStar);

            ItemStack presentsStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag presentsExp = presentsStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            presentsExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.PRESENTS_ID);
            presentsExp.putIntArray(NbtKeys.COLORS, new int[]{0xFF2233, 0xFFD700});
            items.add(presentsStar);

            ItemStack candyCaneStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag candyCaneExp = candyCaneStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            candyCaneExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.CANDY_CANE_ID);
            candyCaneExp.putIntArray(NbtKeys.COLORS, new int[]{0xFF0033});
            items.add(candyCaneStar);

            ItemStack snowflakeStar = new ItemStack(Items.FIREWORK_STAR);
            CompoundTag snowflakeExp = snowflakeStar.getOrCreateTagElement(NbtKeys.EXPLOSION);
            snowflakeExp.putByte(NbtKeys.TYPE, CustomFireworkShapeRegistry.SNOWFLAKE_ID);
            snowflakeExp.putIntArray(NbtKeys.COLORS, new int[]{0xFFFFFF, 0xE0F7FF, 0x5AC8FF});
            items.add(snowflakeStar);

            items.add(InfinitePhoenixFireworkStarItem.createDefaultStack());

            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.CAKE_ID,
                    new int[]{0xFFFDD0, 0x8B4513, 0xFF2D55}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.CROWN_ID,
                    new int[]{0xFFD700, 0xFFFF77, 0xFF0044, 0x0066FF}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.TROPHY_ID,
                    new int[]{0xFFD700, 0xFFFF88, 0x00FFFF}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.CHRISTMAS_TREE_ID,
                    new int[]{0x227733, 0xFF3030, 0xFFD700}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.PRESENTS_ID,
                    new int[]{0xFF2233, 0xFFD700}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.CANDY_CANE_ID,
                    new int[]{0xFF0033}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.PHOENIX_ID,
                    new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00}
            ));
            items.add(FireworkTestCommand.createCustomFireworkRocket(
                    CustomFireworkShapeRegistry.SNOWFLAKE_ID,
                    new int[]{0xFFFFFF, 0xE0F7FF, 0x5AC8FF}
            ));
    }

    private void addTrophyItems(@NotNull NonNullList<ItemStack> items) {
            for (TrophyDefinition trophyDef : Trophies.getAll()) {
                if (trophyDef.getItem() != null) {
                    items.add(new ItemStack(trophyDef.getItem()));
                }
            }
    }
    };

    public static void sortItems(NonNullList<ItemStack> items) {

    }
}
