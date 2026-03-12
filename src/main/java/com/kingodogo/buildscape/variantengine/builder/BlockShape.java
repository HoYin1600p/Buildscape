package com.kingodogo.buildscape.variantengine.builder;

import com.kingodogo.buildscape.variantengine.block.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class BlockShape implements StringRepresentable, Comparable<BlockShape>, Predicate<Block> {

    private static final Map<String, BlockShape> NAME_TO_SHAPE = new HashMap<>();
    private static final List<BlockShape> SHAPES = new ArrayList<>();
    private static final List<BlockShape> VALUES = Collections.unmodifiableList(SHAPES);

    public static final BlockShape BASE = new BlockShape(b -> true, "base", 1f, true);
    public static final BlockShape STAIRS = new BlockShape(com.kingodogo.buildscape.variantengine.util.BlockDetectionUtil::isStair, "stairs", 1f, true);
    public static final BlockShape SLAB = new BlockShape(com.kingodogo.buildscape.variantengine.util.BlockDetectionUtil::isSlab, "slab", 0.5f, true);
    public static final BlockShape VERTICAL_SLAB = new BlockShape(b -> b instanceof VerticalSlabBlock, "vertical_slab", 0.5f, true);
    public static final BlockShape VERTICAL_STAIRS = new BlockShape(b -> b instanceof VerticalStairsBlock, "vertical_stairs", 1f, true);
    public static final BlockShape QUARTER_PIECE = new BlockShape(b -> b instanceof QuarterPieceBlock, "quarter_piece", 0.25f, true);
    public static final BlockShape VERTICAL_QUARTER_PIECE = new BlockShape(b -> b instanceof VerticalQuarterPieceBlock, "vertical_quarter_piece", 0.25f, true);
    public static final BlockShape FENCE = new BlockShape(b -> b instanceof FenceBlock, "fence", 1f, false);
    public static final BlockShape FENCE_GATE = new BlockShape(b -> b instanceof FenceGateBlock, "fence_gate", 1f, false);
    public static final BlockShape WALL = new BlockShape(b -> b instanceof WallBlock, "wall", 1f, false);
    public static final BlockShape BUTTON = new BlockShape(b -> b instanceof ButtonBlock, "button", 1 / 3f, false);
    public static final BlockShape PRESSURE_PLATE = new BlockShape(b -> b instanceof PressurePlateBlock, "pressure_plate", 1 / 3f, false);
    public final Predicate<Block> blockPredicate;
    public final float logicalCompleteness;
    public final boolean isConstruction;
    public final int id;
    private final @NotNull String name;

    public BlockShape(Predicate<Block> blockPredicate, @NotNull String name, float logicalCompleteness, boolean isConstruction) {
        this.blockPredicate = blockPredicate;
        this.name = name;
        this.logicalCompleteness = logicalCompleteness;
        this.isConstruction = isConstruction;
        this.id = SHAPES.size();
        SHAPES.add(this);
        NAME_TO_SHAPE.put(name, this);
    }

    @Nullable
    public static BlockShape getShapeOf(Block block) {
        if (block instanceof ExtShapeBlockInterface e) {
            BlockShape shape = e.getBlockShape();
            if (shape != null) return shape;
        }
        for (BlockShape shape : BlockShape.values()) {
            if (shape.test(block)) return shape;
        }
        return null;
    }

    public static List<BlockShape> values() {
        return VALUES;
    }

    public static BlockShape byName(String name) {
        return NAME_TO_SHAPE.get(name);
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name;
    }

    public String asString() {
        return name;
    }

    @Override
    public boolean test(Block block) {
        return blockPredicate.test(block);
    }

    @Override
    public int compareTo(@NotNull BlockShape o) {
        return Integer.compare(this.id, o.id);
    }
}
