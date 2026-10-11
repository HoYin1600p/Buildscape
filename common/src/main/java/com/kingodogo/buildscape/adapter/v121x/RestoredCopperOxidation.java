package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.block.CopperChestBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;

/** Oxidation for copper families which have no vanilla equivalent on 1.21.1. */
public final class RestoredCopperOxidation {
    private RestoredCopperOxidation() {}

    public static void tryOxidize(Level level, BlockPos pos, BlockState state) {
        CommonId id = Services.PLATFORM.getBlockId(state.getBlock());
        if (id == null || !id.getNamespace().equals("buildscape")) return;
        String path = id.getPath();
        if (path.startsWith("waxed_") || path.startsWith("oxidized_")) return;
        String next = path.startsWith("weathered_") ? "oxidized_" + path.substring(10)
                : path.startsWith("exposed_") ? "weathered_" + path.substring(8) : "exposed_" + path;
        if (level.random.nextFloat() >= 0.05688889F) return;
        Block target = Services.PLATFORM.getBlock(new CommonId("buildscape", next));
        if (target == null || target == Blocks.AIR) return;
        BlockState replacement = copyProperties(state, target.defaultBlockState());
        if (target instanceof LightningRodBlock && replacement.getValue(LightningRodBlock.POWERED)) {
            level.scheduleTick(pos, target, 8);
        }
        if (state.getBlock() instanceof CopperChestBlock && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos partner = pos.relative(ChestBlock.getConnectedDirection(state));
            BlockState partnerState = level.getBlockState(partner);
            if (partnerState.getBlock() == state.getBlock()) {
                level.setBlock(pos, replacement, 18);
                level.setBlock(partner, copyProperties(partnerState, target.defaultBlockState()), 18);
                level.updateNeighborsAt(pos, target);
                level.updateNeighborsAt(partner, target);
                level.updateNeighbourForOutputSignal(pos, target);
                level.updateNeighbourForOutputSignal(partner, target);
                return;
            }
            replacement = replacement.setValue(ChestBlock.TYPE, ChestType.SINGLE);
        }
        level.setBlock(pos, replacement, 3);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (Property property : from.getProperties()) {
            if (to.hasProperty(property)) to = to.setValue(property, from.getValue(property));
        }
        return to;
    }
}
