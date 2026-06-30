package com.kingodogo.buildscape.block.vertical;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum VerticalStairShape implements StringRepresentable {
    STRAIGHT("straight", false, false),
    INNER_FRONT_LEFT("inner_front_left", false, false),
    INNER_FRONT_RIGHT("inner_front_right", false, false),
    INNER_TOP_LEFT("inner_top_left", true, false),
    INNER_TOP_RIGHT("inner_top_right", true, false),
    INNER_BOTH_LEFT("inner_both_left", true, true),
    INNER_BOTH_RIGHT("inner_both_right", true, true),
    OUTER_BACK_LEFT("outer_back_left", false, false),
    OUTER_BACK_RIGHT("outer_back_right", false, false),
    OUTER_BOTTOM_LEFT("outer_bottom_left", true, false),
    OUTER_BOTTOM_RIGHT("outer_bottom_right", true, false),
    OUTER_BOTH_LEFT("outer_both_left", true, true),
    OUTER_BOTH_RIGHT("outer_both_right", true, true),
    OUTER_BACK_LEFT_BOTTOM_RIGHT("outer_back_left_bottom_right", true, true),
    OUTER_BACK_RIGHT_BOTTOM_LEFT("outer_back_right_bottom_left", true, true);

    private final String name;
    public final boolean isVertical;
    public final boolean isMixed;

    VerticalStairShape(String name, boolean isVertical, boolean isMixed) {
        this.name = name;
        this.isVertical = isVertical;
        this.isMixed = isMixed;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public VoxelShape getVoxelShape(ComplexFacing facing) {
        VoxelShape shape = Shapes.empty();
        for (Octant octant : this.getOctants()) {
            shape = Shapes.or(shape, octantShape(facing, octant.right, octant.top, octant.front));
        }
        return shape;
    }

    private List<Octant> getOctants() {
        List<Octant> octants = new ArrayList<>(8);
        addBottom(octants);

        switch (this) {
            case INNER_FRONT_LEFT, INNER_TOP_LEFT, INNER_BOTH_LEFT -> {
                addTopFront(octants);
                octants.add(new Octant(false, true, false));
            }
            case INNER_FRONT_RIGHT, INNER_TOP_RIGHT, INNER_BOTH_RIGHT -> {
                addTopFront(octants);
                octants.add(new Octant(true, true, false));
            }
            case OUTER_BACK_LEFT, OUTER_BOTTOM_LEFT, OUTER_BOTH_LEFT -> octants.add(new Octant(true, true, true));
            case OUTER_BACK_RIGHT, OUTER_BOTTOM_RIGHT, OUTER_BOTH_RIGHT -> octants.add(new Octant(false, true, true));
            case OUTER_BACK_LEFT_BOTTOM_RIGHT -> octants.add(new Octant(false, true, true));
            case OUTER_BACK_RIGHT_BOTTOM_LEFT -> octants.add(new Octant(true, true, true));
            case STRAIGHT -> addTopFront(octants);
        }

        return octants;
    }

    private static void addBottom(List<Octant> octants) {
        octants.add(new Octant(false, false, false));
        octants.add(new Octant(true, false, false));
        octants.add(new Octant(false, false, true));
        octants.add(new Octant(true, false, true));
    }

    private static void addTopFront(List<Octant> octants) {
        octants.add(new Octant(false, true, true));
        octants.add(new Octant(true, true, true));
    }

    private static VoxelShape octantShape(ComplexFacing facing, boolean right, boolean top, boolean front) {
        Bounds bounds = new Bounds();
        apply(bounds, facing.right, right);
        apply(bounds, facing.up, top);
        apply(bounds, facing.forward, front);
        return Block.box(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ);
    }

    private static void apply(Bounds bounds, Direction direction, boolean positiveHalf) {
        double min = positiveHalf ? 8.0D : 0.0D;
        double max = positiveHalf ? 16.0D : 8.0D;
        if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            min = 16.0D - max;
            max = min + 8.0D;
        }

        switch (direction.getAxis()) {
            case X -> {
                bounds.minX = min;
                bounds.maxX = max;
            }
            case Y -> {
                bounds.minY = min;
                bounds.maxY = max;
            }
            case Z -> {
                bounds.minZ = min;
                bounds.maxZ = max;
            }
        }
    }

    private record Octant(boolean right, boolean top, boolean front) {
    }

    private static final class Bounds {
        private double minX;
        private double minY;
        private double minZ;
        private double maxX;
        private double maxY;
        private double maxZ;
    }
}
