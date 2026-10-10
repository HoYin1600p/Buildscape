package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class CascadeWaterManager {

    private static final Map<ResourceKey<Level>, Set<BlockPos>> WATER_SOURCES = new ConcurrentHashMap<>();
    public static void registerWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        WATER_SOURCES.computeIfAbsent(level.dimension(), k -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }
    public static void removeWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        Set<BlockPos> positions = WATER_SOURCES.get(level.dimension());
        if (positions != null) {
            positions.remove(pos);
        }
    }
    public static boolean isNearCascadeWater(Level level, BlockPos pos) {
        Set<BlockPos> positions = WATER_SOURCES.get(level.dimension());
        if (positions == null || positions.isEmpty()) return false;
        for (BlockPos source : positions) {
            if (Math.abs(source.getX() - pos.getX()) <= 4 && Math.abs(source.getZ() - pos.getZ()) <= 4 && (source.getY() == pos.getY() || source.getY() == pos.getY() + 1)) {
                return true;
            }
        }
        return false;
    }
}
