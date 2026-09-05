package com.kingodogo.buildscape.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public final class TreeChopTraversal {

    private TreeChopTraversal() {
    }

    public static Set<BlockPos> collect(BlockPos startPos, Predicate<BlockPos> matches, int limit) {
        Set<BlockPos> visited = new LinkedHashSet<>();
        if (startPos == null || matches == null || limit <= 0 || !matches.test(startPos)) {
            return visited;
        }

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> examined = new HashSet<>();
        visited.add(startPos.immutable());
        queue.add(startPos.immutable());
        examined.add(startPos.immutable());

        while (!queue.isEmpty() && visited.size() < limit) {
            BlockPos current = queue.removeFirst();
            for (int dx = -1; dx <= 1 && visited.size() < limit; dx++) {
                for (int dy = -1; dy <= 1 && visited.size() < limit; dy++) {
                    for (int dz = -1; dz <= 1 && visited.size() < limit; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        BlockPos neighbor = current.offset(dx, dy, dz).immutable();
                        if (examined.add(neighbor) && matches.test(neighbor)) {
                            visited.add(neighbor);
                            queue.addLast(neighbor);
                        }
                    }
                }
            }
        }
        return visited;
    }
}
