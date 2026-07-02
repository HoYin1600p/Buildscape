package com.kingodogo.buildscape.block.vertical;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum ComplexFacing implements StringRepresentable {
    SOUTH_UP(Direction.SOUTH, Direction.UP),
    WEST_UP(Direction.WEST, Direction.UP),
    NORTH_UP(Direction.NORTH, Direction.UP),
    EAST_UP(Direction.EAST, Direction.UP),
    SOUTH_DOWN(Direction.SOUTH, Direction.DOWN),
    WEST_DOWN(Direction.WEST, Direction.DOWN),
    NORTH_DOWN(Direction.NORTH, Direction.DOWN),
    EAST_DOWN(Direction.EAST, Direction.DOWN),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    WEST_SOUTH(Direction.WEST, Direction.SOUTH),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    EAST_SOUTH(Direction.EAST, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),
    SOUTH_WEST(Direction.SOUTH, Direction.WEST),
    UP_WEST(Direction.UP, Direction.WEST),
    NORTH_WEST(Direction.NORTH, Direction.WEST),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    EAST_NORTH(Direction.EAST, Direction.NORTH),
    UP_NORTH(Direction.UP, Direction.NORTH),
    WEST_NORTH(Direction.WEST, Direction.NORTH),
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    NORTH_EAST(Direction.NORTH, Direction.EAST),
    UP_EAST(Direction.UP, Direction.EAST),
    SOUTH_EAST(Direction.SOUTH, Direction.EAST);

    private static final Map<String, ComplexFacing> BY_NAME = new HashMap<>();
    private static final ComplexFacing[][] BY_DIRECTIONS = new ComplexFacing[6][6];

    static {
        for (ComplexFacing facing : values()) {
            BY_NAME.put(facing.name, facing);
            BY_DIRECTIONS[facing.forward.ordinal()][facing.up.ordinal()] = facing;
        }
    }

    public static ComplexFacing byName(String name) {
        return BY_NAME.get(name);
    }

    public static ComplexFacing forFacing(Direction forward, Direction up) {
        if (forward == null || up == null || forward.getAxis() == up.getAxis()) {
            return null;
        }
        return BY_DIRECTIONS[forward.ordinal()][up.ordinal()];
    }

    public final Direction forward;
    public final Direction up;
    public final Direction left;
    public final Direction backward;
    public final Direction down;
    public final Direction right;
    private final String name;

    ComplexFacing(Direction forward, Direction up) {
        this.forward = forward;
        this.up = up;
        this.left = Direction.fromNormal(
                (up.getStepY() * forward.getStepZ()) - (up.getStepZ() * forward.getStepY()),
                (up.getStepZ() * forward.getStepX()) - (up.getStepX() * forward.getStepZ()),
                (up.getStepX() * forward.getStepY()) - (up.getStepY() * forward.getStepX()));
        this.backward = forward.getOpposite();
        this.down = up.getOpposite();
        this.right = this.left.getOpposite();
        this.name = forward.getSerializedName() + "_" + up.getSerializedName();
    }

    public ComplexFacing flipped() {
        return forFacing(this.up, this.forward);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
