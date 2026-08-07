package com.kingodogo.buildscape.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Nine-slot color and gradient solver. The logical server uses MapColor as a
 * safe fallback; clients replace that catalog with colors sampled from baked
 * models when the Builder's Workbench is opened.
 */
public final class ColorGradientSolver {
    public static final int FILTER_SOLID = 1;
    public static final int FILTER_TRANSPARENT = 1 << 1;
    public static final int FILTER_NON_FULL = 1 << 2;
    public static final int FILTER_ALL = FILTER_SOLID | FILTER_TRANSPARENT | FILTER_NON_FULL;

    private static final Map<Item, BlockColor> REGISTRY = new LinkedHashMap<>();
    private static final List<BlockColor> ALL_BLOCKS = new ArrayList<>();

    private ColorGradientSolver() {
    }

    public static boolean isCandidateBlock(Item item) {
        if (!(item instanceof BlockItem blockItem)) return false;
        Block block = blockItem.getBlock();
        ResourceLocation id = block.getRegistryName();
        if (isCreativeOnly(id)) return false;

        try {
            BlockState state = block.defaultBlockState();
            return !state.isAir() && state.getFluidState().isEmpty();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    public static synchronized void registerDynamicColor(Item item, int colorHex) {
        registerDynamicColor(item, colorHex, categoriesFor(item));
    }

    public static synchronized void registerDynamicColor(Item item, int colorHex, int categories) {
        if (!isCandidateBlock(item)) return;
        int r = colorHex >> 16 & 255;
        int g = colorHex >> 8 & 255;
        int b = colorHex & 255;
        BlockColor color = new BlockColor(item, r, g, b, normalizeCategories(categories));
        REGISTRY.put(item, color);
        ALL_BLOCKS.removeIf(existing -> existing.item == item);
        ALL_BLOCKS.add(color);
    }

    public static synchronized void replaceDynamicColors(Collection<BlockColor> colors) {
        REGISTRY.clear();
        ALL_BLOCKS.clear();
        for (BlockColor color : colors) {
            if (color != null && isCandidateBlock(color.item)) {
                REGISTRY.put(color.item, color);
                ALL_BLOCKS.add(color);
            }
        }
    }

    private static synchronized void ensureRegistryPopulated() {
        if (!ALL_BLOCKS.isEmpty()) return;
        for (ResourceLocation id : Registry.ITEM.keySet()) {
            Item item = Registry.ITEM.get(id);
            if (!isCandidateBlock(item)) continue;
            Block block = ((BlockItem) item).getBlock();
            int color;
            try {
                color = block.defaultBlockState().getMapColor(null, null).col;
            } catch (RuntimeException ignored) {
                color = 0x808080;
            }
            registerDynamicColor(item, color == 0 ? 0x808080 : color, categoriesFor(item));
        }
    }

    public static List<ItemStack> solveColorPicker(ItemStack target, int filterMask, int[] offsets) {
        ensureRegistryPopulated();
        List<ItemStack> result = emptyResult();
        if (target.isEmpty()) return result;

        BlockColor targetColor = resolveColor(target);
        List<BlockColor> candidates = candidates(filterMask);
        if (candidates.isEmpty()) return result;
        candidates.sort(byDistance(targetColor));

        Item[] defaults = chooseUniqueDefaults(candidates, 9, Set.of());
        for (int slot = 0; slot < 9; slot++) {
            List<BlockColor> available = excludingOtherDefaults(candidates, defaults, slot, Set.of());
            if (available.isEmpty()) available = candidates;
            BlockColor chosen = available.get(offset(offsets, slot, available.size()));
            result.set(slot, new ItemStack(chosen.item));
        }
        return result;
    }

    public static List<ItemStack> solveColorPickerWithInventory(ItemStack target, List<ItemStack> inventory,
                                                                 int[] offsets) {
        ensureRegistryPopulated();
        Set<Item> availableItems = new HashSet<>();
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) availableItems.add(stack.getItem());
        }

        List<ItemStack> result = emptyResult();
        if (target.isEmpty() || availableItems.isEmpty()) return result;
        BlockColor targetColor = resolveColor(target);
        List<BlockColor> candidates = new ArrayList<>();
        synchronized (ColorGradientSolver.class) {
            for (BlockColor color : ALL_BLOCKS) {
                if (availableItems.contains(color.item)) candidates.add(color);
            }
        }
        if (candidates.isEmpty()) return result;
        candidates.sort(byDistance(targetColor));

        Item[] defaults = chooseUniqueDefaults(candidates, 9, Set.of());
        for (int slot = 0; slot < 9; slot++) {
            List<BlockColor> available = excludingOtherDefaults(candidates, defaults, slot, Set.of());
            if (available.isEmpty()) available = candidates;
            result.set(slot, new ItemStack(available.get(offset(offsets, slot, available.size())).item));
        }
        return result;
    }

    public static List<ItemStack> solveGradient(ItemStack start, ItemStack end, int filterMask, int[] offsets) {
        List<ItemStack> anchors = emptyResult();
        anchors.set(0, start);
        anchors.set(8, end);
        return solveGradient(anchors, filterMask, offsets);
    }

    /**
     * Solves each interval between occupied anchor slots independently. Slots
     * outside the first and last anchor remain empty, and every anchor is copied
     * exactly into the output at its input position.
     */
    public static List<ItemStack> solveGradient(List<ItemStack> anchors, int filterMask, int[] offsets) {
        ensureRegistryPopulated();
        List<ItemStack> result = emptyResult();
        if (anchors == null || anchors.size() < 9) return result;

        List<Integer> anchorSlots = new ArrayList<>();
        Set<Item> anchorItems = new HashSet<>();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack anchor = anchors.get(slot);
            if (anchor != null && !anchor.isEmpty()) {
                anchorSlots.add(slot);
                anchorItems.add(anchor.getItem());
                result.set(slot, anchor.copy());
            }
        }
        if (anchorSlots.size() < 2) return emptyResult();

        BlockColor[] targets = new BlockColor[9];
        for (int segment = 0; segment < anchorSlots.size() - 1; segment++) {
            int startSlot = anchorSlots.get(segment);
            int endSlot = anchorSlots.get(segment + 1);
            BlockColor startColor = resolveColor(anchors.get(startSlot));
            BlockColor endColor = resolveColor(anchors.get(endSlot));
            int width = endSlot - startSlot;
            for (int slot = startSlot + 1; slot < endSlot; slot++) {
                float t = (float) (slot - startSlot) / width;
                targets[slot] = BlockColor.target(
                        lerp(startColor.L, endColor.L, t),
                        lerp(startColor.a, endColor.a, t),
                        lerp(startColor.bStar, endColor.bStar, t));
            }
        }

        List<BlockColor> candidates = candidates(filterMask);
        if (candidates.isEmpty()) return result;

        Item[] defaults = new Item[9];
        Set<Item> used = new HashSet<>(anchorItems);
        for (int slot = 0; slot < 9; slot++) {
            if (targets[slot] == null) continue;
            List<BlockColor> sorted = sortedForTarget(candidates, targets[slot]);
            BlockColor chosen = firstUnused(sorted, used);
            if (chosen == null && !sorted.isEmpty()) chosen = sorted.get(0);
            if (chosen != null) {
                defaults[slot] = chosen.item;
                used.add(chosen.item);
            }
        }

        for (int slot = 0; slot < 9; slot++) {
            if (targets[slot] == null) continue;
            List<BlockColor> sorted = sortedForTarget(candidates, targets[slot]);
            List<BlockColor> available = excludingOtherDefaults(sorted, defaults, slot, anchorItems);
            if (available.isEmpty()) {
                available = new ArrayList<>();
                for (BlockColor color : sorted) {
                    if (!anchorItems.contains(color.item)) available.add(color);
                }
            }
            if (!available.isEmpty()) {
                result.set(slot, new ItemStack(available.get(offset(offsets, slot, available.size())).item));
            }
        }
        return result;
    }

    private static List<BlockColor> candidates(int filterMask) {
        if ((filterMask & FILTER_ALL) == 0) return new ArrayList<>();
        List<BlockColor> result = new ArrayList<>();
        synchronized (ColorGradientSolver.class) {
            for (BlockColor color : ALL_BLOCKS) {
                if ((color.categories & filterMask) != 0) result.add(color);
            }
        }
        return result;
    }

    private static Comparator<BlockColor> byDistance(BlockColor target) {
        return Comparator.comparingDouble(color -> labDistance(
                target.L, target.a, target.bStar, color.L, color.a, color.bStar));
    }

    private static List<BlockColor> sortedForTarget(List<BlockColor> candidates, BlockColor target) {
        List<BlockColor> sorted = new ArrayList<>(candidates);
        sorted.sort(byDistance(target));
        return sorted;
    }

    private static Item[] chooseUniqueDefaults(List<BlockColor> candidates, int count, Set<Item> excluded) {
        Item[] defaults = new Item[count];
        Set<Item> used = new HashSet<>(excluded);
        for (int slot = 0; slot < count; slot++) {
            BlockColor chosen = firstUnused(candidates, used);
            if (chosen == null && !candidates.isEmpty()) chosen = candidates.get(slot % candidates.size());
            if (chosen != null) {
                defaults[slot] = chosen.item;
                used.add(chosen.item);
            }
        }
        return defaults;
    }

    private static BlockColor firstUnused(List<BlockColor> candidates, Set<Item> used) {
        for (BlockColor candidate : candidates) {
            if (!used.contains(candidate.item)) return candidate;
        }
        return null;
    }

    private static List<BlockColor> excludingOtherDefaults(List<BlockColor> candidates, Item[] defaults,
                                                            int currentSlot, Set<Item> additionallyExcluded) {
        Set<Item> excluded = new HashSet<>(additionallyExcluded);
        for (int slot = 0; slot < defaults.length; slot++) {
            if (slot != currentSlot && defaults[slot] != null) excluded.add(defaults[slot]);
        }
        List<BlockColor> result = new ArrayList<>();
        for (BlockColor candidate : candidates) {
            if (!excluded.contains(candidate.item)) result.add(candidate);
        }
        return result;
    }

    private static int offset(int[] offsets, int slot, int size) {
        if (size <= 0 || offsets == null || slot < 0 || slot >= offsets.length) return 0;
        return Math.floorMod(offsets[slot], size);
    }

    private static List<ItemStack> emptyResult() {
        return new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
    }

    private static BlockColor resolveColor(ItemStack stack) {
        BlockColor registered;
        synchronized (ColorGradientSolver.class) {
            registered = REGISTRY.get(stack.getItem());
        }
        if (registered != null) return registered;
        if (stack.getItem() instanceof BlockItem blockItem) {
            int color;
            try {
                color = blockItem.getBlock().defaultBlockState().getMapColor(null, null).col;
            } catch (RuntimeException ignored) {
                color = 0x808080;
            }
            if (color == 0) color = 0x808080;
            return new BlockColor(stack.getItem(), color >> 16 & 255, color >> 8 & 255, color & 255,
                    categoriesFor(stack.getItem()));
        }
        return new BlockColor(stack.getItem(), 128, 128, 128, FILTER_SOLID);
    }

    public static int categoriesFor(Item item) {
        if (!(item instanceof BlockItem blockItem)) return 0;
        Block block = blockItem.getBlock();
        BlockState state = block.defaultBlockState();
        boolean full;
        try {
            full = Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        } catch (RuntimeException ignored) {
            full = false;
        }
        ResourceLocation id = block.getRegistryName();
        String path = id == null ? "" : id.getPath();
        boolean transparent = block instanceof AbstractGlassBlock
                || block instanceof HalfTransparentBlock
                || block instanceof StainedGlassPaneBlock
                || block instanceof LeavesBlock
                || path.contains("glass")
                || path.contains("ice");

        int categories = 0;
        if (full && !transparent) categories |= FILTER_SOLID;
        if (transparent) categories |= FILTER_TRANSPARENT;
        if (!full) categories |= FILTER_NON_FULL;
        return normalizeCategories(categories);
    }

    private static int normalizeCategories(int categories) {
        int normalized = categories & FILTER_ALL;
        return normalized == 0 ? FILTER_NON_FULL : normalized;
    }

    public static boolean isCreativeOnly(ResourceLocation id) {
        if (id == null) return false;
        String path = id.getPath();
        return path.equals("bedrock") || path.equals("barrier") || path.contains("command_block")
                || path.contains("structure_block") || path.contains("structure_void")
                || path.contains("jigsaw") || path.equals("spawner") || path.contains("portal")
                || path.equals("moving_piston") || path.equals("light");
    }

    private static float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }

    private static double labDistance(float l1, float a1, float b1, float l2, float a2, float b2) {
        float dl = l1 - l2;
        float da = a1 - a2;
        float db = b1 - b2;
        return dl * dl + da * da + db * db;
    }

    private static float[] rgbToLab(int red, int green, int blue) {
        float r = srgbToLinear(red / 255.0f);
        float g = srgbToLinear(green / 255.0f);
        float b = srgbToLinear(blue / 255.0f);
        float x = (r * 0.4124564f + g * 0.3575761f + b * 0.1804375f) / 0.95047f;
        float y = r * 0.2126729f + g * 0.7151522f + b * 0.0721750f;
        float z = (r * 0.0193339f + g * 0.1191920f + b * 0.9503041f) / 1.08883f;
        x = labF(x);
        y = labF(y);
        z = labF(z);
        return new float[]{116.0f * y - 16.0f, 500.0f * (x - y), 200.0f * (y - z)};
    }

    private static float srgbToLinear(float value) {
        return value <= 0.04045f ? value / 12.92f
                : (float) Math.pow((value + 0.055f) / 1.055f, 2.4);
    }

    private static float labF(float value) {
        float delta = 6.0f / 29.0f;
        return value > delta * delta * delta ? (float) Math.cbrt(value)
                : value / (3 * delta * delta) + 4.0f / 29.0f;
    }

    public static final class BlockColor {
        public final Item item;
        public final int r;
        public final int g;
        public final int b;
        public final int categories;
        public final float L;
        public final float a;
        public final float bStar;

        public BlockColor(Item item, int r, int g, int b, int categories) {
            this.item = item;
            this.r = r;
            this.g = g;
            this.b = b;
            this.categories = normalizeCategories(categories);
            float[] lab = rgbToLab(r, g, b);
            this.L = lab[0];
            this.a = lab[1];
            this.bStar = lab[2];
        }

        private BlockColor(float l, float a, float bStar) {
            this.item = null;
            this.r = 0;
            this.g = 0;
            this.b = 0;
            this.categories = 0;
            this.L = l;
            this.a = a;
            this.bStar = bStar;
        }

        private static BlockColor target(float l, float a, float bStar) {
            return new BlockColor(l, a, bStar);
        }
    }
}
