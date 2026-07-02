package com.kingodogo.buildscape.block.vertical;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public final class VerticalStairShapeState implements Comparable<VerticalStairShapeState>, StringRepresentable {
    public static final int COUNT = ComplexFacing.values().length * VerticalStairShape.values().length;

    private static final VerticalStairShapeState[] VALUES = new VerticalStairShapeState[COUNT];
    private static final Map<String, VerticalStairShapeState> BY_NAME = new HashMap<>(COUNT);

    static {
        for (ComplexFacing facing : ComplexFacing.values()) {
            for (VerticalStairShape shape : VerticalStairShape.values()) {
                VerticalStairShapeState state = new VerticalStairShapeState(facing, shape);
                VALUES[ordinal(facing, shape)] = state;
                BY_NAME.put(state.name, state);
            }
        }
    }

    public static int ordinal(ComplexFacing facing, VerticalStairShape shape) {
        return (facing.ordinal() * VerticalStairShape.values().length) + shape.ordinal();
    }

    public static VerticalStairShapeState of(ComplexFacing facing, VerticalStairShape shape) {
        if (facing == null || shape == null) {
            return null;
        }
        return VALUES[ordinal(facing, shape)];
    }

    public static VerticalStairShapeState byName(String name) {
        return BY_NAME.get(name);
    }

    public static VerticalStairShapeState[] values() {
        return VALUES.clone();
    }

    public static VerticalStairShapeState transform(VerticalStairShapeState state, DirectionTransformer transformer) {
        return of(ComplexFacing.forFacing(transformer.apply(state.facing.forward), transformer.apply(state.facing.up)), state.shape);
    }

    public final ComplexFacing facing;
    public final VerticalStairShape shape;
    public final int ordinal;
    private final String name;

    private VerticalStairShapeState(ComplexFacing facing, VerticalStairShape shape) {
        this.facing = facing;
        this.shape = shape;
        this.ordinal = ordinal(facing, shape);
        this.name = facing.getSerializedName() + "_" + shape.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(VerticalStairShapeState other) {
        return Integer.compare(this.ordinal, other.ordinal);
    }

    @FunctionalInterface
    public interface DirectionTransformer {
        Direction apply(Direction direction);
    }
}
