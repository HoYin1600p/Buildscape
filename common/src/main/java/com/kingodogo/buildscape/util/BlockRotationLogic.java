package com.kingodogo.buildscape.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
public final class BlockRotationLogic {

    private BlockRotationLogic() {}

    public enum RotationDirection {
        CLOCKWISE,
        COUNTER_CLOCKWISE,
        FLIP
    }
    @SuppressWarnings("unchecked")
    public static BlockState rotate(BlockState state, Direction playerFacing, RotationDirection rotationDir) {
        BlockState newState = state;
        if (rotationDir == RotationDirection.FLIP && state.hasProperty(BlockStateProperties.HALF)) {
            Half current = state.getValue(BlockStateProperties.HALF);
            return state.setValue(BlockStateProperties.HALF, current == Half.TOP ? Half.BOTTOM : Half.TOP);
        }
        if (rotationDir == RotationDirection.FLIP && state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
            SlabType current = state.getValue(BlockStateProperties.SLAB_TYPE);
            if (current != SlabType.DOUBLE) {
                return state.setValue(BlockStateProperties.SLAB_TYPE, current == SlabType.TOP ? SlabType.BOTTOM : SlabType.TOP);
            }
        }
        Property<?> rawFacing = state.getBlock().getStateDefinition().getProperty("facing");
        if (rawFacing instanceof Property<?> && rawFacing.getValueClass() == Direction.class) {
            Property<Direction> facingProp = (Property<Direction>) rawFacing;
            Direction currentFacing = state.getValue(facingProp);
            Direction nextFacing = switch (rotationDir) {
                case CLOCKWISE -> currentFacing.getClockWise(playerFacing.getAxis() == Direction.Axis.Y ? Direction.Axis.Y : playerFacing.getAxis());
                case COUNTER_CLOCKWISE -> currentFacing.getCounterClockWise(playerFacing.getAxis() == Direction.Axis.Y ? Direction.Axis.Y : playerFacing.getAxis());
                case FLIP -> currentFacing.getOpposite();
            };
            if (facingProp.getPossibleValues().contains(nextFacing)) {
                return state.setValue(facingProp, nextFacing);
            }
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction current = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction next = switch (rotationDir) {
                case CLOCKWISE -> current.getClockWise();
                case COUNTER_CLOCKWISE -> current.getCounterClockWise();
                case FLIP -> current.getOpposite();
            };
            return state.setValue(BlockStateProperties.HORIZONTAL_FACING, next);
        }
        if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis current = state.getValue(BlockStateProperties.AXIS);
            Direction.Axis next = switch (current) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.X;
            };
            return state.setValue(BlockStateProperties.AXIS, next);
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            Direction.Axis current = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            Direction.Axis next = current == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, next);
        }
        if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
            int current = state.getValue(BlockStateProperties.ROTATION_16);
            int step = rotationDir == RotationDirection.COUNTER_CLOCKWISE ? -1 : 1;
            int next = (current + step + 16) % 16;
            return state.setValue(BlockStateProperties.ROTATION_16, next);
        }
        Rotation rot = switch (rotationDir) {
            case CLOCKWISE -> Rotation.CLOCKWISE_90;
            case COUNTER_CLOCKWISE -> Rotation.COUNTERCLOCKWISE_90;
            case FLIP -> Rotation.CLOCKWISE_180;
        };
        return state.rotate(rot);
    }
}
