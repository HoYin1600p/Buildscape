package com.kingodogo.buildscape.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

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
    // Low bits enable categories; the matching high bits apply shift-click exclusions.
    public static final int STRICT_SHIFT = 3;
    public static final int FILTER_SINGLE_TEXTURE = 1 << 6;
    public static final int FILTER_MATCH_SHAPE = 1 << 7;
    public static final int FILTER_MODIFIERS = FILTER_SINGLE_TEXTURE | FILTER_MATCH_SHAPE;
    public static final int FILTER_STATE_MASK = FILTER_ALL | FILTER_ALL << STRICT_SHIFT | FILTER_MODIFIERS;

    private static final Map<Item, BlockColor> REGISTRY = new LinkedHashMap<>();
    private static final List<BlockColor> ALL_BLOCKS = new ArrayList<>();

    private ColorGradientSolver() {
    }

    public static boolean isCandidateBlock(Item item) {
        if (!(item instanceof BlockItem blockItem)) return false;
        Block block = blockItem.getBlock();
        ResourceLocation id = block.getRegistryName();
        if (isCreativeOnly(id) || isWaxed(id)) return false;

        try {
            BlockState state = block.defaultBlockState();
            return !state.isAir() && state.getFluidState().isEmpty()
                    && state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO) >= 0.0F;
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
        ShapeFamily shape = requiredShape(filterMask, target);
        List<BlockColor> candidates = candidates(filterMask, shape);
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

        ShapeFamily shape = requiredShape(filterMask, anchors);
        List<BlockColor> candidates = candidates(filterMask, shape);
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

    private static List<BlockColor> candidates(int filterMask, ShapeFamily requiredShape) {
        int enabled = filterMask & FILTER_ALL;
        if (enabled == 0) return new ArrayList<>();
        if ((filterMask & FILTER_MATCH_SHAPE) != 0 && requiredShape == null) return new ArrayList<>();
        List<BlockColor> result = new ArrayList<>();
        synchronized (ColorGradientSolver.class) {
            for (BlockColor color : ALL_BLOCKS) {
                if (!matchesFilter(color.item, color.categories, filterMask)) continue;
                if ((filterMask & FILTER_SINGLE_TEXTURE) != 0 && !color.singleTexture) continue;
                if ((filterMask & FILTER_MATCH_SHAPE) != 0 && shapeFor(color.item) != requiredShape) continue;
                result.add(color);
            }
        }
        return result;
    }

    public static boolean matchesFilter(Item item, int filterState) {
        return matchesFilter(item, categoriesFor(item), filterState);
    }

    private static boolean matchesFilter(Item item, int physicalCategories, int filterState) {
        if (!(item instanceof BlockItem blockItem)) return false;
        if (!isCandidateBlock(item)) return false;
        int enabled = filterState & FILTER_ALL;
        if (enabled == 0) return false;
        if (isShiftExcluded(blockItem.getBlock(), filterState >>> STRICT_SHIFT & FILTER_ALL)) return false;
        Block block = blockItem.getBlock();
        String path = registryPath(block);
        if (enabled == FILTER_ALL) return true;
        return switch (enabled) {
            case FILTER_SOLID -> physicalCategories == FILTER_SOLID
                    && !path.contains("wallpaper_flat") && !path.endsWith("_rose_vines");
            case FILTER_TRANSPARENT -> isTransparentOnly(block, path, physicalCategories);
            case FILTER_NON_FULL -> isNonFullOnly(block, path, physicalCategories);
            case FILTER_SOLID | FILTER_TRANSPARENT -> isSolidTransparent(block, path);
            case FILTER_TRANSPARENT | FILTER_NON_FULL -> isTransparentNonFull(block, path);
            case FILTER_SOLID | FILTER_NON_FULL -> isSolidNonFull(path);
            default -> false;
        };
    }

    private static ShapeFamily requiredShape(int filterMask, ItemStack input) {
        return (filterMask & FILTER_MATCH_SHAPE) == 0 || input == null || input.isEmpty()
                ? null : shapeFor(input.getItem());
    }

    private static ShapeFamily requiredShape(int filterMask, List<ItemStack> inputs) {
        if ((filterMask & FILTER_MATCH_SHAPE) == 0) return null;
        ShapeFamily required = null;
        for (ItemStack input : inputs) {
            if (input == null || input.isEmpty()) continue;
            ShapeFamily shape = shapeFor(input.getItem());
            if (shape == null || required != null && required != shape) return null;
            required = shape;
        }
        return required;
    }

    private static ShapeFamily shapeFor(Item item) {
        if (!(item instanceof BlockItem blockItem)) return null;
        Block block = blockItem.getBlock();
        String path = registryPath(block);

        if (path.contains("glass_pane") || path.endsWith("_pane")) return ShapeFamily.PANE;
        if (path.contains("leaf_layer") || path.contains("carpet_layer") || path.endsWith("_layer")) {
            return ShapeFamily.LAYER;
        }
        if (path.contains("vertical_slab")) return ShapeFamily.VERTICAL_SLAB;
        if (block instanceof TrapDoorBlock) return ShapeFamily.TRAPDOOR;
        if (block instanceof DoorBlock) return ShapeFamily.DOOR;
        if (block instanceof FenceGateBlock) return ShapeFamily.FENCE_GATE;
        if (block instanceof FenceBlock) return ShapeFamily.FENCE;
        if (block instanceof SlabBlock) return ShapeFamily.SLAB;
        if (block instanceof StairBlock) return ShapeFamily.STAIR;
        if (block instanceof WallBlock) return ShapeFamily.WALL;
        if (block instanceof ButtonBlock) return ShapeFamily.BUTTON;
        if (block instanceof BasePressurePlateBlock) return ShapeFamily.PRESSURE_PLATE;
        if (block instanceof ChestBlock || block instanceof EnderChestBlock || path.endsWith("_chest")) {
            return ShapeFamily.CHEST;
        }
        if (block instanceof BedBlock) return ShapeFamily.BED;
        if (block instanceof BannerBlock || block instanceof WallBannerBlock) return ShapeFamily.BANNER;
        if (block instanceof CandleBlock || path.contains("candle")) return ShapeFamily.CANDLE;
        if (block instanceof SignBlock || block instanceof WallSignBlock || path.endsWith("_sign")) {
            return ShapeFamily.SIGN;
        }
        if (block instanceof LeavesBlock || path.contains("leaves")) return ShapeFamily.LEAVES;
        if (block instanceof LadderBlock || path.contains("ladder")) return ShapeFamily.LADDER;
        if (block instanceof AbstractGlassBlock || path.contains("glass")) return ShapeFamily.GLASS;
        if (path.endsWith("_carpet")) return ShapeFamily.CARPET;
        if (path.contains("overlay")) return ShapeFamily.OVERLAY;
        if (block instanceof FlowerBlock || path.contains("flower") || path.contains("blossom")) {
            return ShapeFamily.FLOWER;
        }
        if (path.contains("foliage") || path.contains("hedge")) return ShapeFamily.FOLIAGE;
        if (path.equals("chain") || path.endsWith("_chain")) return ShapeFamily.CHAIN;
        if (path.contains("ornament")) return ShapeFamily.ORNAMENT;
        if (path.endsWith("_star") || path.startsWith("star_")) return ShapeFamily.STAR;
        if (path.contains("string_light")) return ShapeFamily.STRING_LIGHT;
        if (path.contains("glow_lights") || path.contains("light_block") || path.contains("bulb")
                || path.contains("lamp") || path.contains("froglight") || path.equals("shroomlight")) {
            return ShapeFamily.LIGHT_BLOCK;
        }
        if (path.contains("vine")) return ShapeFamily.VINE;
        if (path.contains("amethyst_cluster") || path.endsWith("_cluster")) return ShapeFamily.CLUSTER;
        if (path.contains("mesh") || path.endsWith("_bars")) return ShapeFamily.MESH;
        if (path.contains("decorated_pot")) return ShapeFamily.DECORATED;
        if (path.contains("item_frame")) return ShapeFamily.ITEM_FRAME;
        if (path.contains("stocking")) return ShapeFamily.STOCKING;
        if (path.contains("cushion")) return ShapeFamily.CUSHION;
        if (path.contains("jar")) return ShapeFamily.JAR;

        try {
            BlockState state = block.defaultBlockState();
            if (Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))) {
                return ShapeFamily.FULL_BLOCK;
            }
        } catch (RuntimeException ignored) {
            // Unknown custom shapes do not participate in Match Shape.
        }
        return null;
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
        return categoriesFor(item, full, transparent);
    }

    public static int categoriesFor(Item item, boolean full, boolean transparent) {
        if (!(item instanceof BlockItem blockItem)) return 0;
        Block block = blockItem.getBlock();
        String path = registryPath(block);
        int physicalCategories = 0;
        if (full && !transparent) physicalCategories |= FILTER_SOLID;
        if (transparent) physicalCategories |= FILTER_TRANSPARENT;
        if (!full) physicalCategories |= FILTER_NON_FULL;
        if (path.contains("wallpaper_flat")) return FILTER_NON_FULL;
        if (isTransparentNonFull(block, path)) return FILTER_TRANSPARENT | FILTER_NON_FULL;
        if (isSolidTransparent(block, path)) return FILTER_SOLID | FILTER_TRANSPARENT;
        if (isSolidNonFull(path)) return FILTER_SOLID | FILTER_NON_FULL;
        if (isNonFullOnly(block, path, physicalCategories)) return FILTER_NON_FULL;
        if (isTransparentOnly(block, path, physicalCategories)) {
            return FILTER_TRANSPARENT;
        }
        return full ? FILTER_SOLID : FILTER_NON_FULL;
    }

    private static boolean isTransparentOnly(Block block, String path, int physicalCategories) {
        if (isTransparentNonFull(block, path) || isSolidTransparent(block, path)) return false;
        return physicalCategories == FILTER_TRANSPARENT && (block instanceof AbstractGlassBlock
                || block instanceof LeavesBlock
                || path.contains("glass") || path.contains("grate") || path.contains("leaves")
                || path.equals("slime_block") || path.equals("honey_block")
                || path.equals("ice") || path.equals("packed_ice") || path.equals("blue_ice")
                || path.equals("frosted_ice") || path.equals("icicle_block"));
    }

    private static boolean isNonFullOnly(Block block, String path, int physicalCategories) {
        if (isTransparentNonFull(block, path) || isSolidNonFull(path)) return false;
        return block instanceof SlabBlock || block instanceof StairBlock || block instanceof WallBlock
                || block instanceof BasePressurePlateBlock || block instanceof ButtonBlock
                || block instanceof FenceBlock || block instanceof FenceGateBlock || block instanceof CandleBlock
                || block instanceof BedBlock || block instanceof DoorBlock || block instanceof TrapDoorBlock
                || block instanceof TorchBlock || block instanceof WallTorchBlock || block instanceof LanternBlock
                || block instanceof CampfireBlock || block instanceof EndRodBlock || block instanceof BaseRailBlock
                || block instanceof WebBlock || block instanceof LadderBlock || block instanceof ChestBlock
                || block instanceof EnderChestBlock || block instanceof IronBarsBlock || block instanceof BannerBlock
                || block instanceof WallBannerBlock || block instanceof SignBlock || block instanceof WallSignBlock
                || block instanceof FlowerPotBlock || block instanceof BushBlock || block instanceof ConduitBlock
                || block instanceof RepeaterBlock || block instanceof HopperBlock || block instanceof LecternBlock
                || block instanceof TripWireBlock || block instanceof LeverBlock || block instanceof AnvilBlock
                || block instanceof StonecutterBlock || block instanceof GrindstoneBlock
                || block instanceof EnchantmentTableBlock || block instanceof SculkSensorBlock
                || path.contains("decorated_pot") || path.contains("sculk_shrieker")
                || path.contains("vertical_slab") || path.contains("spike") || path.contains("overlay")
                || path.contains("hedge") || path.contains("foliage") || path.contains("candle")
                || path.contains("stocking") || path.contains("item_frame")
                || path.contains("smoke_vent") || path.contains("wallpaper_flat") || path.contains("copper_rod")
                || path.contains("glow_lights") || path.endsWith("_rose_vines")
                || path.contains("mesh") || path.contains("carpet_layer") || path.endsWith("_carpet")
                || path.endsWith("_bars") || path.endsWith("_star") || path.endsWith("_chest")
                || path.endsWith("_sign") || path.endsWith("_pot") || path.contains("amethyst_cluster")
                || physicalCategories == FILTER_NON_FULL;
    }

    private static boolean isSolidTransparent(Block block, String path) {
        return block instanceof RedstoneLampBlock
                || block instanceof DropperBlock || block instanceof DispenserBlock || block instanceof NoteBlock
                || block instanceof BeaconBlock
                || path.contains("bulb") || path.contains("festive_lamp") || path.contains("froglight")
                || path.equals("shroomlight") || path.equals("target") || path.equals("observer")
                || path.equals("jack_o_lantern");
    }

    private static boolean isTransparentNonFull(Block block, String path) {
        return block instanceof StainedGlassPaneBlock || path.contains("glass_pane")
                || path.contains("leaf_layer") || path.contains("ornament")
                || path.contains("string_light") || path.equals("icicle");
    }

    private static boolean isSolidNonFull(String path) {
        return path.equals("soul_sand") || path.equals("mud") || path.equals("farmland");
    }

    private static boolean isShiftExcluded(Block block, int strictMask) {
        if (strictMask == 0) return false;
        String path = registryPath(block);
        if ((strictMask & FILTER_SOLID) != 0 && (block instanceof BarrelBlock
                || block instanceof TntBlock || isOre(block, path) || path.contains("workbench")
                || path.contains("sack") || path.contains("big_book")
                || path.contains("shulker_box") || path.contains("bookshelf") || path.contains("steel_fan")
                || path.contains("muff_block"))) return true;
        if ((strictMask & FILTER_TRANSPARENT) != 0 && (isSolidTransparent(block, path)
                || path.contains("ornament") || path.contains("string_light"))) return true;
        return (strictMask & FILTER_NON_FULL) != 0 && (block instanceof ConduitBlock
                || block instanceof RepeaterBlock || block instanceof HopperBlock || block instanceof LecternBlock
                || block instanceof TripWireBlock || block instanceof LeverBlock || block instanceof AnvilBlock
                || block instanceof StonecutterBlock || block instanceof GrindstoneBlock
                || block instanceof EnchantmentTableBlock || block instanceof SculkSensorBlock
                || path.contains("decorated_pot") || path.contains("sculk_shrieker")
                || path.endsWith("_star") || path.endsWith("_head") || path.contains("mob_head") || path.contains("_skull")
                || path.contains("scaffolding") || path.contains("daylight_detector")
                || path.contains("tripwire")
                || path.equals("comparator") || path.equals("bell")
                || path.contains("stocking") || path.contains("item_frame") || path.contains("smoke_vent")
                || path.contains("wallpaper_flat"));
    }

    private static boolean isOre(Block block, String path) {
        return block instanceof RedStoneOreBlock || block.defaultBlockState().is(Tags.Blocks.ORES)
                || path.contains("_ore") || path.equals("ancient_debris");
    }

    private static String registryPath(Block block) {
        ResourceLocation id = block.getRegistryName();
        return id == null ? "" : id.getPath();
    }

    private static int normalizeCategories(int categories) {
        int normalized = categories & FILTER_ALL;
        return normalized == 0 ? FILTER_NON_FULL : normalized;
    }

    public static boolean isCreativeOnly(ResourceLocation id) {
        if (id == null) return false;
        String path = id.getPath();
        return path.contains("bedrock") || path.equals("barrier") || path.contains("command_block")
                || path.contains("structure_block") || path.contains("structure_void")
                || path.contains("jigsaw") || path.equals("spawner") || path.equals("end_portal_frame")
                || path.contains("infested") || path.equals("budding_amethyst")
                || path.startsWith("cascade_block") || path.equals("mirror_block")
                || path.equals("reinforced_deepslate")
                || path.equals("petrified_oak_slab") || path.contains("test_instance_block")
                || path.equals("test_block") || path.equals("trial_spawner")
                || path.equals("moving_piston") || path.equals("light");
    }

    private static boolean isWaxed(ResourceLocation id) {
        return id != null && id.getPath().startsWith("waxed_");
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

    private enum ShapeFamily {
        FULL_BLOCK,
        GLASS,
        PANE,
        CARPET,
        LAYER,
        OVERLAY,
        CHEST,
        DOOR,
        TRAPDOOR,
        WALL,
        SLAB,
        STAIR,
        BUTTON,
        PRESSURE_PLATE,
        VERTICAL_SLAB,
        FENCE,
        FENCE_GATE,
        FLOWER,
        FOLIAGE,
        CHAIN,
        ORNAMENT,
        STAR,
        STRING_LIGHT,
        LIGHT_BLOCK,
        VINE,
        BED,
        BANNER,
        CANDLE,
        CLUSTER,
        SIGN,
        LEAVES,
        MESH,
        LADDER,
        DECORATED,
        ITEM_FRAME,
        STOCKING,
        CUSHION,
        JAR
    }

    public static final class BlockColor {
        public final Item item;
        public final int r;
        public final int g;
        public final int b;
        public final int categories;
        public final boolean singleTexture;
        public final float L;
        public final float a;
        public final float bStar;

        public BlockColor(Item item, int r, int g, int b, int categories) {
            this(item, r, g, b, categories, false);
        }

        public BlockColor(Item item, int r, int g, int b, int categories, boolean singleTexture) {
            this.item = item;
            this.r = r;
            this.g = g;
            this.b = b;
            this.categories = normalizeCategories(categories);
            this.singleTexture = singleTexture;
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
            this.singleTexture = false;
            this.L = l;
            this.a = a;
            this.bStar = bStar;
        }

        private static BlockColor target(float l, float a, float bStar) {
            return new BlockColor(l, a, bStar);
        }
    }
}
