package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.mixin.BeaconBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public final class BeaconScanContext {
    private static final ThreadLocal<BeaconBlockEntity> ACTIVE_BEACON = new ThreadLocal<>();
    private static final ThreadLocal<BlockPos> BLOCKING_POS = new ThreadLocal<>();

    private BeaconScanContext() {
    }

    public static void begin(BeaconBlockEntity beacon) {
        ACTIVE_BEACON.set(beacon);
        BLOCKING_POS.remove();
    }

    public static void end() {
        BLOCKING_POS.remove();
        ACTIVE_BEACON.remove();
    }

    public static void markBlocking(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        BeaconBlockEntity beacon = ACTIVE_BEACON.get();
        if (beacon == null || level.getBlockEntity(beaconPos) != beacon) {
            return;
        }

        BLOCKING_POS.set(pos.immutable());
        ((BeaconBeamHeightAccessor) beacon).buildscape$setBeamHeight(pos.getY() - beaconPos.getY());
        if (level instanceof Level actualLevel && !actualLevel.isClientSide) {
            ((BeaconBlockEntityAccessor) beacon).getBeamSections().clear();
        }
    }

    public static boolean blocksLight(BlockGetter level, BlockPos pos) {
        return ACTIVE_BEACON.get() != null && pos.equals(BLOCKING_POS.get());
    }
}
