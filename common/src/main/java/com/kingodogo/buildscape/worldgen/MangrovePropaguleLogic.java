package com.kingodogo.buildscape.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;

public final class MangrovePropaguleLogic {
    private MangrovePropaguleLogic() {}

    public static void place(List<BlockPos> logs, List<BlockPos> leaves, int requiredEmptyBlocks,
                             Predicate<BlockPos> isLeaf, Predicate<BlockPos> isReplaceable,
                             Predicate<BlockPos> isWater, BiConsumer<BlockPos, BlockState> setter,
                             BlockState air, IntUnaryOperator nextInt, IntFunction<BlockState> propagule) {
        if (leaves.isEmpty()) return;
        int targetCount = 2 + nextInt.applyAsInt(3);
        Set<BlockPos> existing = new HashSet<>();
        for (BlockPos pos : leaves) if (isLeaf.test(pos)) existing.add(pos.immutable());
        Set<BlockPos> supported = MangroveLeafSupport.findSupportedLeaves(logs, existing);
        for (BlockPos pos : existing) if (!supported.contains(pos)) setter.accept(pos, air);
        List<BlockPos> candidates = new ArrayList<>(supported);
        for (int i = candidates.size() - 1; i > 0; i--) {
            int other = nextInt.applyAsInt(i + 1);
            BlockPos value = candidates.get(i);
            candidates.set(i, candidates.get(other));
            candidates.set(other, value);
        }
        int placed = 0;
        for (BlockPos leaf : candidates) {
            if (placed >= targetCount) break;
            BlockPos below = leaf.below();
            if (!isReplaceable.test(below)) continue;
            boolean clear = true;
            for (int i = 1; i <= requiredEmptyBlocks; i++) if (!isReplaceable.test(below.below(i))) {
                clear = false;
                break;
            }
            if (!clear) continue;
            int age = nextInt.applyAsInt(5);
            setter.accept(below, propagule.apply(age).setValue(
                    com.kingodogo.buildscape.block.MangrovePropaguleBlock.WATERLOGGED, isWater.test(below)));
            placed++;
        }
    }
}
