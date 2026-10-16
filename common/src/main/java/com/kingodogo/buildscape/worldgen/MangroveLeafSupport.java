package com.kingodogo.buildscape.worldgen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

final class MangroveLeafSupport {
    private MangroveLeafSupport() {}

    static Set<BlockPos> findSupportedLeaves(Collection<BlockPos> logs, Set<BlockPos> leaves) {
        Set<BlockPos> supported = new HashSet<>();
        Collection<BlockPos> frontier = logs;
        for (int distance = 1; distance <= 6 && !frontier.isEmpty(); distance++) {
            Set<BlockPos> next = new HashSet<>();
            for (BlockPos pos : frontier) {
                for (Direction direction : Direction.values()) {
                    BlockPos adjacent = pos.relative(direction);
                    if (leaves.contains(adjacent) && supported.add(adjacent)) {
                        next.add(adjacent);
                    }
                }
            }
            frontier = next;
        }
        return supported;
    }
}
