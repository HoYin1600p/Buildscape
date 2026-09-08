package com.kingodogo.buildscape.worldgen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;

public final class MangroveLeafSupportTest {
    public static void main(String[] args) {
        Set<BlockPos> leaves = new HashSet<>();
        for (int x = 1; x <= 7; x++) leaves.add(new BlockPos(x, 0, 0));
        Set<BlockPos> supported = MangroveLeafSupport.findSupportedLeaves(List.of(BlockPos.ZERO), leaves);
        if (supported.size() != 6 || supported.contains(new BlockPos(7, 0, 0))) {
            throw new AssertionError("Only leaves within six face-connected steps may support propagules");
        }
        leaves.remove(new BlockPos(3, 0, 0));
        supported = MangroveLeafSupport.findSupportedLeaves(List.of(BlockPos.ZERO), leaves);
        if (supported.size() != 2) throw new AssertionError("A gap must break the support path");
        supported = MangroveLeafSupport.findSupportedLeaves(List.of(BlockPos.ZERO), Set.of(new BlockPos(1, 1, 0)));
        if (!supported.isEmpty()) throw new AssertionError("Diagonal contact does not prevent leaf decay");
        supported = MangroveLeafSupport.findSupportedLeaves(List.of(BlockPos.ZERO, new BlockPos(8, 0, 0)), leaves);
        if (supported.size() != leaves.size()) throw new AssertionError("Each branch supplies its own leaves");
        System.out.println("Mangrove leaf support: 4 checks passed.");
    }
}
