package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.block.MuffBlock;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class MuffBlockManager {
    private static final Set<BlockPos> ACTIVE_MUFFS = ConcurrentHashMap.newKeySet();
    private static final CommonId MUFF_BLOCK_ID = CommonId.of("buildscape", "muff_block");

    private MuffBlockManager() {}

    public static void register(BlockPos pos) {
        ACTIVE_MUFFS.add(pos.immutable());
    }

    public static Set<BlockPos> getActiveMuffs() {
        return ACTIVE_MUFFS;
    }

    public static void unregister(BlockPos pos) {
        ACTIVE_MUFFS.remove(pos);
    }

    public static void clear() {
        ACTIVE_MUFFS.clear();
    }

    public static boolean shouldMute(Level level, SoundSource source, double soundX, double soundY, double soundZ) {
        if (level == null || source == SoundSource.PLAYERS || source == SoundSource.MASTER) {
            return false;
        }
        for (BlockPos pos : ACTIVE_MUFFS) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Services.PLATFORM.getBlock(MUFF_BLOCK_ID)) && state.getValue(MuffBlock.POWERED)) {
                int radius = state.getValue(MuffBlock.RADIUS);
                double dx = Math.abs((double) pos.getX() + 0.5D - soundX);
                double dy = Math.abs((double) pos.getY() + 0.5D - soundY);
                double dz = Math.abs((double) pos.getZ() + 0.5D - soundZ);
                if (dx <= radius && dy <= radius && dz <= radius) {
                    return true;
                }
            }
        }
        return false;
    }
}
