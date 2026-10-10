package com.kingodogo.buildscape.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class MangroveRootLogic {
    private MangroveRootLogic() {}

    public static void place(List<BlockPos> logs, float randomSkewChance,
                             Predicate<BlockPos> isTerrain, Predicate<BlockPos> canPlace,
                             BiConsumer<BlockPos, BlockState> setter, Supplier<BlockState> rootState,
                             IntUnaryOperator nextInt, java.util.function.DoubleSupplier nextFloat) {
        if (logs.isEmpty()) return;
        BlockPos base = logs.stream().min(Comparator.comparingInt(BlockPos::getY)).orElse(logs.get(0));
        int ground = findGroundLevel(base, isTerrain);
        for (Direction direction : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            int horizontalDistance = 2 + nextInt.applyAsInt(4);
            List<BlockPos> horizontal = new ArrayList<>(horizontalDistance);
            for (int i = 1; i <= horizontalDistance; i++) {
                BlockPos root = new BlockPos(base.getX() + direction.getStepX() * i, base.getY(),
                        base.getZ() + direction.getStepZ() * i);
                horizontal.add(root);
                placeRoot(root, canPlace, setter, rootState);
            }
            BlockPos last = horizontal.get(horizontal.size() - 1);
            for (int y = 1; y <= last.getY() - ground; y++) placeRoot(last.below(y), canPlace, setter, rootState);
            if (horizontal.size() >= 2 && nextFloat.getAsDouble() < randomSkewChance) {
                BlockPos secondLast = horizontal.get(horizontal.size() - 2);
                int depth = Math.min(2 + nextInt.applyAsInt(2), secondLast.getY() - ground);
                for (int y = 1; y <= depth; y++) placeRoot(secondLast.below(y), canPlace, setter, rootState);
            }
        }
    }

    private static int findGroundLevel(BlockPos start, Predicate<BlockPos> isTerrain) {
        BlockPos.MutableBlockPos cursor = start.mutable();
        while (cursor.getY() > -64) {
            if (isTerrain.test(cursor)) return cursor.getY();
            cursor.move(Direction.DOWN);
        }
        return Math.max(start.getY() - 3, -64);
    }

    private static void placeRoot(BlockPos pos, Predicate<BlockPos> canPlace,
                                  BiConsumer<BlockPos, BlockState> setter, Supplier<BlockState> state) {
        if (canPlace.test(pos)) setter.accept(pos, state.get());
    }
}
