package com.kingodogo.buildscape.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public final class MangrovePlacerLogic {
    private MangrovePlacerLogic() {}

    public static void createFoliage(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            BlockPos centerPos,
            int foliageRadius,
            int actualFoliageHeight,
            int leafPlacementAttempts,
            Function<BlockPos, BlockState> foliageStateGetter,
            Function<BlockPos, BlockState> trunkStateGetter,
            DoubleSupplier randomDouble,
            Supplier<Float> randomFloat,
            Consumer<List<BlockPos>> shuffleList
    ) {
        Set<BlockPos> placedLeaves = new HashSet<>();

        for (int attempt = 0; attempt < leafPlacementAttempts; attempt++) {
            double angle1 = randomDouble.getAsDouble() * 2 * Math.PI;
            double angle2 = randomDouble.getAsDouble() * Math.PI;
            double radius = foliageRadius * Math.sqrt(randomDouble.getAsDouble());

            radius += (randomDouble.getAsDouble() - 0.5) * 0.5;
            radius = Math.max(0, Math.min(radius, foliageRadius + 1));

            int x = (int) (centerPos.getX() + radius * Math.sin(angle2) * Math.cos(angle1));
            int y = (int) (centerPos.getY() + radius * Math.cos(angle2) * (actualFoliageHeight / (double) foliageRadius));
            int z = (int) (centerPos.getZ() + radius * Math.sin(angle2) * Math.sin(angle1));

            y = Math.max(centerPos.getY(), Math.min(centerPos.getY() + actualFoliageHeight - 1, y));

            BlockPos leafPos = new BlockPos(x, y, z);
            if (leafPos.equals(centerPos)) {
                continue;
            }

            if (isConnectedToLogOrLeaf(level, leafPos, placedLeaves, trunkStateGetter, foliageStateGetter)) {
                if (level.isStateAtPosition(leafPos, BlockState::isAir)) {
                    blockSetter.accept(leafPos, foliageStateGetter.apply(leafPos));
                    placedLeaves.add(leafPos);
                }
            }
        }

        List<BlockPos> candidates = new ArrayList<>();
        for (int yOffset = -1; yOffset < actualFoliageHeight + 1; yOffset++) {
            int currentY = centerPos.getY() + yOffset;
            int searchRadius = foliageRadius + 2;

            for (int xOffset = -searchRadius; xOffset <= searchRadius; xOffset++) {
                for (int zOffset = -searchRadius; zOffset <= searchRadius; zOffset++) {
                    BlockPos leafPos = new BlockPos(centerPos.getX() + xOffset, currentY, centerPos.getZ() + zOffset);
                    if (level.isStateAtPosition(leafPos, BlockState::isAir) && !placedLeaves.contains(leafPos)) {
                        int adjacentLeaves = countAdjacentLeaves(level, leafPos, placedLeaves, foliageStateGetter);
                        if (adjacentLeaves >= 4 && randomFloat.get() < 0.6F) {
                            candidates.add(leafPos);
                        }
                    }
                }
            }
        }

        shuffleList.accept(candidates);
        for (BlockPos candidate : candidates) {
            if (level.isStateAtPosition(candidate, BlockState::isAir)
                    && isConnectedToLogOrLeaf(level, candidate, placedLeaves, trunkStateGetter, foliageStateGetter)) {
                blockSetter.accept(candidate, foliageStateGetter.apply(candidate));
                placedLeaves.add(candidate);
            }
        }

        List<BlockPos> extensionCandidates = new ArrayList<>(placedLeaves);
        shuffleList.accept(extensionCandidates);

        for (int i = 0; i < Math.min(extensionCandidates.size() / 4, 20); i++) {
            BlockPos baseLeaf = extensionCandidates.get(i);

            for (int dir = 0; dir < 6; dir++) {
                if (randomFloat.get() < 0.3F) continue;

                BlockPos extensionPos = baseLeaf.relative(Direction.from3DDataValue(dir));
                int distFromCenter = (int) Math.sqrt(
                        (extensionPos.getX() - centerPos.getX()) * (extensionPos.getX() - centerPos.getX()) +
                                (extensionPos.getZ() - centerPos.getZ()) * (extensionPos.getZ() - centerPos.getZ()) +
                                (extensionPos.getY() - centerPos.getY()) * (extensionPos.getY() - centerPos.getY())
                );

                if (distFromCenter <= foliageRadius + 2 &&
                        !placedLeaves.contains(extensionPos) &&
                        level.isStateAtPosition(extensionPos, BlockState::isAir) &&
                        isConnectedToLogOrLeaf(level, extensionPos, placedLeaves, trunkStateGetter, foliageStateGetter)) {
                    blockSetter.accept(extensionPos, foliageStateGetter.apply(extensionPos));
                    placedLeaves.add(extensionPos);
                    break;
                }
            }
        }
    }

    public static boolean isConnectedToLogOrLeaf(
            LevelSimulatedReader level,
            BlockPos pos,
            Set<BlockPos> placedLeaves,
            Function<BlockPos, BlockState> trunkStateGetter,
            Function<BlockPos, BlockState> foliageStateGetter
    ) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos checkPos = pos.offset(dx, dy, dz);

                    if (level.isStateAtPosition(checkPos, state ->
                            state.is(net.minecraft.tags.BlockTags.LOGS) ||
                                    state.getBlock() == trunkStateGetter.apply(checkPos).getBlock())) {
                        return true;
                    }
                    if (placedLeaves.contains(checkPos)) {
                        return true;
                    }
                    if (level.isStateAtPosition(checkPos, state ->
                            state.getBlock() == foliageStateGetter.apply(checkPos).getBlock())) {
                        return true;
                    }
                }
            }
        }

        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -6; dz <= 6; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    int distanceSq = dx * dx + dy * dy + dz * dz;
                    if (distanceSq > 36) continue;

                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (level.isStateAtPosition(checkPos, state ->
                            state.is(net.minecraft.tags.BlockTags.LOGS) ||
                                    state.getBlock() == trunkStateGetter.apply(checkPos).getBlock())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int countAdjacentLeaves(
            LevelSimulatedReader level,
            BlockPos pos,
            Set<BlockPos> placedLeaves,
            Function<BlockPos, BlockState> foliageStateGetter
    ) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    if (placedLeaves.contains(checkPos)) {
                        count++;
                    } else if (level.isStateAtPosition(checkPos, state ->
                            state.getBlock() == foliageStateGetter.apply(checkPos).getBlock())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static void placeTrunk(
            LevelSimulatedReader level,
            BlockPos startPos,
            int freeTreeHeight,
            int elevationShift,
            int extraHeight,
            float placeBranchPerLogProbability,
            int branchSteps,
            int branchLength,
            int branchDirectionIndex,
            Supplier<Float> randomFloat,
            BiPredicate<LevelSimulatedReader, BlockPos> placeLogFunc,
            Consumer<BlockPos> addFoliageAttachment
    ) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int elevatedStartY = startPos.getY() + elevationShift;
        int trunkHeight = freeTreeHeight + extraHeight;

        for (int i = 0; i < trunkHeight; i++) {
            int y = elevatedStartY + i;
            mutablePos.set(startPos.getX(), y, startPos.getZ());

            if (placeLogFunc.test(level, mutablePos)) {
                if (i > 0 && randomFloat.get() < placeBranchPerLogProbability) {
                    placeBranch(level, mutablePos, branchSteps, branchLength, branchDirectionIndex, placeLogFunc, addFoliageAttachment);
                }
            }
        }

        addFoliageAttachment.accept(new BlockPos(startPos.getX(), elevatedStartY + trunkHeight, startPos.getZ()));
    }

    public static void placeBranch(
            LevelSimulatedReader level,
            BlockPos branchStart,
            int branchSteps,
            int branchLength,
            int branchDirectionIndex,
            BiPredicate<LevelSimulatedReader, BlockPos> placeLogFunc,
            Consumer<BlockPos> addFoliageAttachment
    ) {
        if (branchSteps <= 0 || branchLength <= 0) return;

        Direction[] horizontalDirections = {
                Direction.NORTH,
                Direction.SOUTH,
                Direction.EAST,
                Direction.WEST
        };
        Direction branchDirection = horizontalDirections[Math.abs(branchDirectionIndex) % horizontalDirections.length];

        BlockPos.MutableBlockPos currentPos = branchStart.mutable();
        BlockPos.MutableBlockPos branchEnd = branchStart.mutable();

        for (int i = 0; i < branchLength; i++) {
            branchEnd.move(branchDirection);
            if (placeLogFunc.test(level, branchEnd)) {
                currentPos.set(branchEnd);
            } else {
                break;
            }
        }

        for (int i = 0; i < branchSteps; i++) {
            branchEnd.move(Direction.UP);
            if (placeLogFunc.test(level, branchEnd)) {
                currentPos.set(branchEnd);
                addFoliageAttachment.accept(currentPos.immutable());
            } else {
                break;
            }
        }
    }
}
