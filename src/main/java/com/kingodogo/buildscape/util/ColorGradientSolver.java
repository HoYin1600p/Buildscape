package com.kingodogo.buildscape.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Server-safe Lab-space gradient engine.
 * <p>
 * Colors default to each block's MapColor. registerDynamicColor remains as an
 * integration point for a future explicit color-data source.
 * <p>
 * Gradient solving:
 * solveGradient(start, end, filterMode)
 * → interpolates in CIE-Lab space for perceptually uniform transitions
 * → enforces uniqueness so every slot is a different block
 * -> applies the selected availability filter
 */
public class ColorGradientSolver {

    // ── Inner record ─────────────────────────────────────────────────────────

    private static final Map<Item, BlockColor> REGISTRY = new LinkedHashMap<>();

    // ── Registry ─────────────────────────────────────────────────────────────
    private static final List<BlockColor> ALL_BLOCKS = new ArrayList<>();

    public static boolean isSlab(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return false;
        return bi.getBlock() instanceof net.minecraft.world.level.block.SlabBlock;
    }

    // ── Shape helpers ────────────────────────────────────────────────────────

    public static boolean isStair(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bi)) return false;
        return bi.getBlock() instanceof net.minecraft.world.level.block.StairBlock;
    }

    /**
     * Returns true if this item represents a placeable structural block that
     * makes sense for colour-gradient comparisons.
     */
    public static boolean isStructuralBlock(Item item) {
        if (!(item instanceof BlockItem blockItem)) return false;
        Block block = blockItem.getBlock();
        BlockState state;
        try {
            state = block.defaultBlockState();
        } catch (Exception e) {
            return false;
        }

        // Exclude liquids and non-solid / replaceable materials
        if (state.getMaterial().isLiquid()) return false;
        if (!state.getMaterial().isSolid() || state.getMaterial().isReplaceable()) return false;

        // Blacklist non-structural block classes
        if (block instanceof net.minecraft.world.level.block.ButtonBlock ||
                block instanceof net.minecraft.world.level.block.PressurePlateBlock ||
                block instanceof net.minecraft.world.level.block.SignBlock ||
                block instanceof net.minecraft.world.level.block.WallSignBlock ||
                block instanceof net.minecraft.world.level.block.TorchBlock ||
                block instanceof net.minecraft.world.level.block.WallTorchBlock ||
                block instanceof net.minecraft.world.level.block.FlowerBlock ||
                block instanceof net.minecraft.world.level.block.TallFlowerBlock ||
                block instanceof net.minecraft.world.level.block.SaplingBlock ||
                block instanceof net.minecraft.world.level.block.CropBlock ||
                block instanceof net.minecraft.world.level.block.BushBlock ||
                block instanceof net.minecraft.world.level.block.StemBlock ||
                block instanceof net.minecraft.world.level.block.BannerBlock ||
                block instanceof net.minecraft.world.level.block.WallBannerBlock ||
                block instanceof net.minecraft.world.level.block.FlowerPotBlock ||
                block instanceof net.minecraft.world.level.block.DoorBlock ||
                block instanceof net.minecraft.world.level.block.BellBlock ||
                block instanceof net.minecraft.world.level.block.DiodeBlock) {
            return false;
        }

        // Registry-path based exclusions (covers modded variants without naming classes)
        ResourceLocation rl = block.getRegistryName();
        if (rl != null) {
            String path = rl.getPath();
            return !path.equals("redstone_wire") && !path.equals("repeater") &&
                    !path.equals("comparator") && !path.contains("_wire") &&
                    !path.contains("lever") && !path.contains("tripwire") &&
                    !path.contains("_rail") && !path.contains("piston") &&
                    !path.contains("dispenser") && !path.contains("dropper") &&
                    !path.contains("hopper") && !path.contains("observer") &&
                    !path.contains("daylight") && !path.contains("conduit");
        }

        return true;
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Registers an explicit RGB override for a structural block.
     */
    public static synchronized void registerDynamicColor(Item item, int colorHex) {
        if (!isStructuralBlock(item)) return;

        int r = (colorHex >> 16) & 0xFF;
        int g = (colorHex >> 8) & 0xFF;
        int b = colorHex & 0xFF;

        BlockColor bc = new BlockColor(item, r, g, b);
        REGISTRY.put(item, bc);

        ALL_BLOCKS.removeIf(existing -> existing.item == item);
        ALL_BLOCKS.add(bc);
    }

    /**
     * Lazily builds the registry from MapColor on the logical server.
     */
    private static synchronized void ensureRegistryPopulated() {
        if (!ALL_BLOCKS.isEmpty()) return;
        for (ResourceLocation rl : net.minecraft.core.Registry.ITEM.keySet()) {
            Item item = net.minecraft.core.Registry.ITEM.get(rl);
            if (item instanceof BlockItem && isStructuralBlock(item)) {
                int col = ((BlockItem) item).getBlock().defaultBlockState()
                        .getMapColor(null, null).col;
                if (col != 0) registerDynamicColor(item, col);
            }
        }
    }

    /**
     * Builds a 9-element gradient between startStack (slot 0) and endStack (slot 8).
     *
     * @param filterMode 0=All, 1=Filtered, 2=Survival+
     */
    public static List<ItemStack> solveGradient(ItemStack startStack, ItemStack endStack, int filterMode, int[] offsets) {
        ensureRegistryPopulated();

        List<ItemStack> path = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));

        // --- Edge cases -------------------------------------------------------
        if (startStack.isEmpty() && endStack.isEmpty()) return path;
        if (startStack.isEmpty()) {
            path.set(8, endStack.copy());
            return path;
        }
        if (endStack.isEmpty()) {
            path.set(0, startStack.copy());
            return path;
        }

        // --- Resolve colors for start/end ------------------------------------
        BlockColor startColor = resolveColor(startStack);
        BlockColor endColor = resolveColor(endStack);

        boolean targetIsSlab = isSlab(startStack) || isSlab(endStack);
        boolean targetIsStair = isStair(startStack) || isStair(endStack);

        // --- Build filtered candidate list -----------------------------------
        List<BlockColor> candidates;
        synchronized (ColorGradientSolver.class) {
            candidates = new ArrayList<>(ALL_BLOCKS.size());
            for (BlockColor bc : ALL_BLOCKS) {
                if (matchesFilter(bc.item, filterMode)) {
                    ItemStack cStack = new ItemStack(bc.item);
                    if (targetIsSlab && !isSlab(cStack)) continue;
                    if (targetIsStair && !isStair(cStack)) continue;
                    if (!targetIsSlab && !targetIsStair && filterMode != 0 && (isSlab(cStack) || isStair(cStack)))
                        continue;
                    candidates.add(bc);
                }
            }
        }
        if (candidates.isEmpty()) {
            synchronized (ColorGradientSolver.class) {
                candidates = new ArrayList<>(ALL_BLOCKS);
            }
        }

        // Fill fixed edges
        path.set(0, startStack.copy());
        path.set(8, endStack.copy());

        // 1. Establish baseline default blocks (offsets = 0) to ensure global uniqueness
        Item[] defaults = new Item[9];
        defaults[0] = startStack.getItem();
        defaults[8] = endStack.getItem();
        Set<Item> defaultUsed = new HashSet<>();
        defaultUsed.add(startStack.getItem());
        defaultUsed.add(endStack.getItem());

        for (int i = 1; i < 8; i++) {
            float t = i / 8.0f;
            float tL = lerp(startColor.L, endColor.L, t);
            float ta = lerp(startColor.a, endColor.a, t);
            float tb = lerp(startColor.bStar, endColor.bStar, t);

            List<BlockColor> sortedCandidates = new ArrayList<>(candidates);
            sortedCandidates.sort(Comparator.comparingDouble(bc -> labDistance(tL, ta, tb, bc.L, bc.a, bc.bStar)));

            BlockColor chosen = null;
            for (BlockColor bc : sortedCandidates) {
                if (!defaultUsed.contains(bc.item)) {
                    chosen = bc;
                    break;
                }
            }
            if (chosen == null && !sortedCandidates.isEmpty()) {
                chosen = sortedCandidates.get(0);
            }
            if (chosen != null) {
                defaults[i] = chosen.item;
                defaultUsed.add(chosen.item);
            }
        }

        // 2. Solve each slot with its local offset, excluding default items chosen by other slots
        for (int i = 1; i < 8; i++) {
            float t = i / 8.0f;
            float tL = lerp(startColor.L, endColor.L, t);
            float ta = lerp(startColor.a, endColor.a, t);
            float tb = lerp(startColor.bStar, endColor.bStar, t);

            List<BlockColor> sortedCandidates = new ArrayList<>(candidates);
            sortedCandidates.sort(Comparator.comparingDouble(bc -> labDistance(tL, ta, tb, bc.L, bc.a, bc.bStar)));

            Set<Item> excluded = new HashSet<>();
            excluded.add(startStack.getItem());
            excluded.add(endStack.getItem());
            for (int j = 1; j < 8; j++) {
                if (j != i && defaults[j] != null) {
                    excluded.add(defaults[j]);
                }
            }

            List<BlockColor> available = new ArrayList<>();
            for (BlockColor bc : sortedCandidates) {
                if (!excluded.contains(bc.item)) {
                    available.add(bc);
                }
            }
            if (available.isEmpty()) {
                available = sortedCandidates;
            }

            int skipCount = offsets[i] % available.size();
            BlockColor chosen = available.get(skipCount);
            if (chosen != null) {
                path.set(i, new ItemStack(chosen.item));
            }
        }

        return path;
    }

    // ── Main gradient solver ──────────────────────────────────────────────────

    private static BlockColor resolveColor(ItemStack stack) {
        BlockColor bc = REGISTRY.get(stack.getItem());
        if (bc == null && stack.getItem() instanceof BlockItem bi) {
            int col = bi.getBlock().defaultBlockState().getMapColor(null, null).col;
            int r = (col >> 16) & 0xFF, g = (col >> 8) & 0xFF, b = col & 0xFF;
            bc = new BlockColor(stack.getItem(), r, g, b);
        }
        if (bc == null) bc = new BlockColor(stack.getItem(), 128, 128, 128);
        return bc;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * CIE-Lab euclidean distance (Delta-E approximation).
     */
    private static double labDistance(float L1, float a1, float b1,
                                      float L2, float a2, float b2) {
        float dL = L1 - L2, da = a1 - a2, db = b1 - b2;
        return dL * dL + da * da + db * db;
    }

    public static boolean isCreativeOnly(ResourceLocation rl) {
        if (rl == null) return false;
        String path = rl.getPath();
        return path.equals("bedrock") || path.equals("barrier") || path.contains("command_block")
                || path.contains("structure_block") || path.contains("jigsaw") || path.equals("spawner")
                || path.contains("portal") || path.equals("moving_piston");
    }

    // ── Shape filter ──────────────────────────────────────────────────────────

    public static boolean isOre(ResourceLocation rl) {
        if (rl == null) return false;
        String path = rl.getPath();
        return path.contains("_ore") || path.contains("raw_") || path.contains("ancient_debris");
    }

    public static boolean isWorkstation(ResourceLocation rl) {
        if (rl == null) return false;
        String path = rl.getPath();
        return path.contains("crafting_table") || path.contains("furnace") || path.contains("smoker")
                || path.contains("blast_furnace") || path.contains("fletching_table") || path.contains("cartography_table")
                || path.contains("smithing_table") || path.contains("loom") || path.contains("grindstone")
                || path.contains("stonecutter") || path.contains("anvil") || path.contains("enchanting_table")
                || path.contains("brewing_stand") || path.contains("builders_workbench") || path.contains("workbench");
    }

    public static boolean isLightBlock(ResourceLocation rl) {
        if (rl == null) return false;
        String path = rl.getPath();
        return path.contains("glowstone") || path.contains("lantern") || path.contains("sea_lantern")
                || path.contains("shroomlight") || path.contains("torch") || path.contains("campfire")
                || path.contains("glow_lichen") || path.contains("redstone_lamp") || path.contains("jack_o_lantern")
                || path.contains("froglight") || path.equals("light");
    }

    public static boolean isRedstoneComponent(ResourceLocation rl) {
        if (rl == null) return false;
        String path = rl.getPath();
        return path.contains("redstone_block") || path.contains("repeater") || path.contains("comparator")
                || path.contains("observer") || path.contains("dispenser") || path.contains("dropper")
                || path.contains("hopper") || path.contains("piston") || path.contains("note_block")
                || path.contains("jukebox") || path.contains("target") || path.contains("tnt")
                || path.contains("tripwire") || path.contains("daylight_detector") || path.contains("lectern")
                || path.contains("redstone_wire");
    }

    private static boolean matchesFilter(Item item, int filterMode) {
        if (!(item instanceof BlockItem bi)) return false;
        Block block = bi.getBlock();
        ResourceLocation rl = block.getRegistryName();
        if (rl == null) return false;

        // All filters exclude creative-only blocks
        if (isCreativeOnly(rl)) return false;

        if (filterMode == 1) { // Filtered
            return !isOre(rl) && !isWorkstation(rl) && !isLightBlock(rl) && !isRedstoneComponent(rl);
        } else if (filterMode == 2) { // Survival+
            return !isWorkstation(rl);
        }
        return true; // All
    }

    private static float lerp(float start, float end, float t) {
        return start + t * (end - start);
    }

    // ── Math helpers ──────────────────────────────────────────────────────────

    /**
     * Converts sRGB [0-255] to CIE-Lab.
     * Lab[0] = L* (0-100), Lab[1] = a* (~-128..127), Lab[2] = b* (~-128..127)
     */
    private static float[] rgbToLab(int ri, int gi, int bi) {
        // Step 1: sRGB → linear RGB
        float r = srgbToLinear(ri / 255.0f);
        float g = srgbToLinear(gi / 255.0f);
        float b = srgbToLinear(bi / 255.0f);

        // Step 2: linear RGB → XYZ (D65 illuminant)
        float X = r * 0.4124564f + g * 0.3575761f + b * 0.1804375f;
        float Y = r * 0.2126729f + g * 0.7151522f + b * 0.0721750f;
        float Z = r * 0.0193339f + g * 0.1191920f + b * 0.9503041f;

        // Normalise by D65 white point
        X /= 0.95047f;
        // Y stays /= 1.00000
        Z /= 1.08883f;

        // Step 3: XYZ → Lab
        X = labF(X);
        Y = labF(Y);
        Z = labF(Z);

        float L = 116.0f * Y - 16.0f;
        float a = 500.0f * (X - Y);
        float bStar = 200.0f * (Y - Z);

        return new float[]{L, a, bStar};
    }

    private static float srgbToLinear(float c) {
        return c <= 0.04045f ? c / 12.92f : (float) Math.pow((c + 0.055f) / 1.055f, 2.4);
    }

    private static float labF(float t) {
        final float delta = 6.0f / 29.0f;
        return t > delta * delta * delta
                ? (float) Math.cbrt(t)
                : t / (3 * delta * delta) + 4.0f / 29.0f;
    }

    public static List<ItemStack> solveColorPicker(ItemStack target, int filterMode, int[] offsets) {
        ensureRegistryPopulated();
        List<ItemStack> path = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        if (target.isEmpty()) return path;

        BlockColor targetColor = resolveColor(target);

        boolean targetIsSlab = isSlab(target);
        boolean targetIsStair = isStair(target);

        List<BlockColor> candidates = new ArrayList<>();
        synchronized (ColorGradientSolver.class) {
            for (BlockColor bc : ALL_BLOCKS) {
                if (matchesFilter(bc.item, filterMode)) {
                    ItemStack cStack = new ItemStack(bc.item);
                    if (targetIsSlab && !isSlab(cStack)) continue;
                    if (targetIsStair && !isStair(cStack)) continue;
                    if (!targetIsSlab && !targetIsStair && filterMode != 0 && (isSlab(cStack) || isStair(cStack)))
                        continue;
                    candidates.add(bc);
                }
            }
        }
        if (candidates.isEmpty()) {
            synchronized (ColorGradientSolver.class) {
                candidates = new ArrayList<>(ALL_BLOCKS);
            }
        }

        candidates.sort(Comparator.comparingDouble(bc -> labDistance(targetColor.L, targetColor.a, targetColor.bStar, bc.L, bc.a, bc.bStar)));

        // 1. Establish default baseline (offset = 0)
        Item[] defaults = new Item[9];
        Set<Item> defaultUsed = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            BlockColor chosen = null;
            for (BlockColor bc : candidates) {
                if (!defaultUsed.contains(bc.item)) {
                    chosen = bc;
                    break;
                }
            }
            if (chosen == null && !candidates.isEmpty()) {
                chosen = candidates.get(0);
            }
            if (chosen != null) {
                defaults[i] = chosen.item;
                defaultUsed.add(chosen.item);
            }
        }

        // 2. Solve each slot with its offset, excluding defaults of other slots
        for (int i = 0; i < 9; i++) {
            Set<Item> excluded = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                if (j != i && defaults[j] != null) {
                    excluded.add(defaults[j]);
                }
            }

            List<BlockColor> available = new ArrayList<>();
            for (BlockColor bc : candidates) {
                if (!excluded.contains(bc.item)) {
                    available.add(bc);
                }
            }
            if (available.isEmpty()) {
                available = candidates;
            }

            int skipCount = offsets[i] % available.size();
            BlockColor chosen = available.get(skipCount);
            if (chosen != null) {
                path.set(i, new ItemStack(chosen.item));
            }
        }
        return path;
    }

    public static List<ItemStack> solveColorPickerWithInventory(ItemStack target, List<ItemStack> inventory, int[] offsets) {
        ensureRegistryPopulated();
        List<ItemStack> path = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        if (target.isEmpty()) return path;

        BlockColor targetColor = resolveColor(target);

        boolean targetIsSlab = isSlab(target);
        boolean targetIsStair = isStair(target);

        Set<Item> invItems = new HashSet<>();
        for (ItemStack s : inventory) {
            if (!s.isEmpty()) {
                invItems.add(s.getItem());
            }
        }

        List<BlockColor> candidates = new ArrayList<>();
        synchronized (ColorGradientSolver.class) {
            for (BlockColor bc : ALL_BLOCKS) {
                if (invItems.contains(bc.item)) {
                    ItemStack cStack = new ItemStack(bc.item);
                    if (targetIsSlab && !isSlab(cStack)) continue;
                    if (targetIsStair && !isStair(cStack)) continue;
                    if (!targetIsSlab && !targetIsStair && (isSlab(cStack) || isStair(cStack))) continue;
                    candidates.add(bc);
                }
            }
        }
        if (candidates.isEmpty()) {
            synchronized (ColorGradientSolver.class) {
                candidates = new ArrayList<>(ALL_BLOCKS);
            }
        }

        candidates.sort(Comparator.comparingDouble(bc -> labDistance(targetColor.L, targetColor.a, targetColor.bStar, bc.L, bc.a, bc.bStar)));

        // 1. Establish default baseline (offset = 0)
        Item[] defaults = new Item[9];
        Set<Item> defaultUsed = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            BlockColor chosen = null;
            for (BlockColor bc : candidates) {
                if (!defaultUsed.contains(bc.item)) {
                    chosen = bc;
                    break;
                }
            }
            if (chosen == null && !candidates.isEmpty()) {
                chosen = candidates.get(0);
            }
            if (chosen != null) {
                defaults[i] = chosen.item;
                defaultUsed.add(chosen.item);
            }
        }

        // 2. Solve each slot with its offset, excluding defaults of other slots
        for (int i = 0; i < 9; i++) {
            Set<Item> excluded = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                if (j != i && defaults[j] != null) {
                    excluded.add(defaults[j]);
                }
            }

            List<BlockColor> available = new ArrayList<>();
            for (BlockColor bc : candidates) {
                if (!excluded.contains(bc.item)) {
                    available.add(bc);
                }
            }
            if (available.isEmpty()) {
                available = candidates;
            }

            int skipCount = offsets[i] % available.size();
            BlockColor chosen = available.get(skipCount);
            if (chosen != null) {
                path.set(i, new ItemStack(chosen.item));
            }
        }
        return path;
    }

    public static class BlockColor {
        public final Item item;
        public final int r, g, b;
        /**
         * CIE-Lab coordinates, computed once at registration time.
         */
        public final float L, a, bStar;

        public BlockColor(Item item, int r, int g, int b) {
            this.item = item;
            this.r = r;
            this.g = g;
            this.b = b;
            float[] lab = rgbToLab(r, g, b);
            this.L = lab[0];
            this.a = lab[1];
            this.bStar = lab[2];
        }
    }
}
