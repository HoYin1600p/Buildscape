package com.kingodogo.buildscape.util;

import net.minecraft.core.BlockPos;
import org.junit.Test;

import java.util.Set;

public final class TreeChopTraversalTest {

    @Test
    public void traversalHonoursLimitAndPredicate() {
        BlockPos origin = BlockPos.ZERO;
        Set<BlockPos> capped = TreeChopTraversal.collect(origin, pos -> true, 200);
        require(capped.size() == 200, "Traversal did not enforce the exact 200-block limit");

        Set<BlockPos> line = TreeChopTraversal.collect(
                origin,
                pos -> pos.getY() == 0 && pos.getZ() == 0 && pos.getX() >= 0 && pos.getX() < 12,
                200);
        require(line.size() == 12, "Traversal did not collect the connected line");
        require(line.contains(new BlockPos(11, 0, 0)), "Traversal omitted a connected endpoint");

        Set<BlockPos> rejected = TreeChopTraversal.collect(origin, pos -> false, 200);
        require(rejected.isEmpty(), "Traversal accepted a rejected starting block");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
