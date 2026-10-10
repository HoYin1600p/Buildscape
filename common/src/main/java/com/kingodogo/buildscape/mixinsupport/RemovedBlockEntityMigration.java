package com.kingodogo.buildscape.mixinsupport;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/** Keeps saved-data APIs behind the active VersionCluster factory. */
public final class RemovedBlockEntityMigration {
    private RemovedBlockEntityMigration() {}

    public static void load(BlockPos pos, BlockState state, CompoundTag tag, Object lookup,
            Consumer<BlockEntity> result) {
        MixinFactory.get().loadRemovedBlockEntity(pos, state, tag, lookup, result);
    }
}
