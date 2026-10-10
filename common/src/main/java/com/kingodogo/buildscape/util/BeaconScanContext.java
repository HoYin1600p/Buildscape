package com.kingodogo.buildscape.util;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class BeaconScanContext {
    private static final ThreadLocal<BeaconBlockEntity> ACTIVE_BEACON = new ThreadLocal<>();
    private static final Map<Level, Map<BlockPos, CachedHeight>> CLIENT_HEIGHTS = new WeakHashMap<>();
    private static final CommonId TINTED_ORNAMENT_ID = new CommonId("buildscape", "tinted_glass_ornament");
    private static final CommonId BIG_TINTED_ORNAMENT_ID = new CommonId("buildscape", "big_tinted_glass_ornament");

    private BeaconScanContext() {
    }

    public static void begin(BeaconBlockEntity beacon) {
        ACTIVE_BEACON.set(beacon);
    }

    public static void end() {
        ACTIVE_BEACON.remove();
    }

    public static void markBlocking(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        BeaconBlockEntity beacon = ACTIVE_BEACON.get();
        if (beacon == null || level.getBlockEntity(beaconPos) != beacon) {
            return;
        }
        ((BeaconBeamHeightAccessor) beacon).buildscape$markBeamBlocked(pos.getY() - beaconPos.getY());
    }

    public static synchronized void confirm(Level level, BlockPos beaconPos, int height) {
        if (!level.isClientSide()) {
            return;
        }
        Map<BlockPos, CachedHeight> heights = CLIENT_HEIGHTS.computeIfAbsent(level, ignored -> new HashMap<>());
        heights.put(beaconPos.immutable(), new CachedHeight(height, level.getGameTime() + 20L));
    }

    public static synchronized int confirmedHeight(Level level, BlockPos beaconPos) {
        long gameTime = level.getGameTime();
        Map<BlockPos, CachedHeight> heights = CLIENT_HEIGHTS.computeIfAbsent(level, ignored -> new HashMap<>());
        CachedHeight cached = heights.get(beaconPos);
        if (cached != null && cached.validUntil > gameTime) {
            return cached.height;
        }
        int height = scanHeight(level, beaconPos);
        heights.put(beaconPos.immutable(), new CachedHeight(height, gameTime + 20L));
        return height;
    }

    private static int scanHeight(Level level, BlockPos beaconPos) {
        int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, beaconPos.getX(), beaconPos.getZ());
        Block tintedOrnament = Services.PLATFORM.getBlock(TINTED_ORNAMENT_ID);
        Block bigTintedOrnament = Services.PLATFORM.getBlock(BIG_TINTED_ORNAMENT_ID);
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos();
        for (int y = beaconPos.getY() + 1; y <= top; y++) {
            scanPos.set(beaconPos.getX(), y, beaconPos.getZ());
            BlockState state = level.getBlockState(scanPos);
            if ((tintedOrnament != null && state.is(tintedOrnament))
                    || (bigTintedOrnament != null && state.is(bigTintedOrnament))) {
                return y - beaconPos.getY();
            }
        }
        return BeaconBeamScanState.UNLIMITED;
    }

    private record CachedHeight(int height, long validUntil) {
    }
}
