package com.kingodogo.buildscape.feature.sculk;

import com.kingodogo.buildscape.registry.blocks.CopperBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SculkVeinBlock extends GlowLichenBlock {
    public SculkVeinBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static boolean regrow(ServerLevel level, BlockPos pos) {
        return regrow(level, pos, null);
    }

    public static boolean regrow(ServerLevel level, BlockPos pos, Set<Direction> facings) {
        boolean changed = false;
        for (Direction face : facings == null ? Arrays.asList(Direction.values()) : facings) {
            changed |= placeFace(level, pos, face);
        }
        return changed;
    }

    public static boolean spread(ServerLevel level, BlockPos pos, Random random) {
        BlockState state = level.getBlockState(pos);
        boolean sculk = state.is(CopperBlocks.SCULK.get());
        if (!sculk && !state.is(CopperBlocks.SCULK_VEIN.get())) {
            return false;
        }
        List<Direction> faces = Arrays.asList(Direction.values());
        List<Direction> directions = Arrays.asList(Direction.values());
        Collections.shuffle(faces, random);
        Collections.shuffle(directions, random);
        boolean changed = false;
        for (Direction face : faces) {
            if (!sculk && !state.getValue(getFaceProperty(face))) {
                continue;
            }
            for (Direction direction : directions) {
                if (direction.getAxis() == face.getAxis()) {
                    continue;
                }
                if (placeFace(level, pos, direction)) {
                    changed = true;
                    continue;
                }
                BlockPos adjacent = pos.relative(direction);
                if (placeFace(level, adjacent, face)) {
                    changed = true;
                    continue;
                }
                if (level.getBlockState(adjacent).getCollisionShape(level, adjacent).isEmpty()
                        && placeFace(level, adjacent.relative(face), direction.getOpposite())) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static boolean placeFace(ServerLevel level, BlockPos pos, Direction face) {
        BlockPos supportPos = pos.relative(face);
        if (!level.hasChunkAt(pos) || !level.hasChunkAt(supportPos) || level.isOutsideBuildHeight(pos)) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !state.is(Blocks.WATER) && !state.is(CopperBlocks.SCULK_VEIN.get())) {
            return false;
        }
        BlockState support = level.getBlockState(supportPos);
        if (support.is(CopperBlocks.SCULK.get()) || support.is(CopperBlocks.SCULK_CATALYST.get())
                || support.is(CopperBlocks.SCULK_SENSOR.get()) || support.is(CopperBlocks.SCULK_SHRIEKER.get())
                || support.is(Blocks.SCULK_SENSOR)
                || support.is(Blocks.MOVING_PISTON)
                || !support.isFaceSturdy(level, supportPos, face.getOpposite())) {
            return false;
        }
        BlockState vein = state.is(CopperBlocks.SCULK_VEIN.get())
                ? state
                : CopperBlocks.SCULK_VEIN.get().defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, state.getFluidState().is(Fluids.WATER));
        BooleanProperty property = getFaceProperty(face);
        return !vein.getValue(property) && level.setBlock(pos, vein.setValue(property, true), 3);
    }

    public static void discharge(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(CopperBlocks.SCULK_VEIN.get())) {
            return;
        }
        BlockState remaining = state;
        boolean anyFace = false;
        for (Direction face : Direction.values()) {
            BooleanProperty property = getFaceProperty(face);
            if (remaining.getValue(property) && level.hasChunkAt(pos.relative(face))
                    && level.getBlockState(pos.relative(face)).is(CopperBlocks.SCULK.get())) {
                remaining = remaining.setValue(property, false);
            }
            anyFace |= remaining.getValue(property);
        }
        if (!anyFace) {
            remaining = state.getValue(BlockStateProperties.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState()
                    : Blocks.AIR.defaultBlockState();
        }
        if (remaining != state) {
            level.setBlock(pos, remaining, 3);
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {}
}
