package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

import java.util.HashMap;
import java.util.Map;

public class CascadeWaterManager {

    private static final Map<Long, AABBTicket> TICKETS = new HashMap<>();

    private static long key(Level level, BlockPos pos) {
        return ((long) level.dimension().location().hashCode() << 32) ^ pos.asLong();
    }

    public static void registerWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        long k = key(level, pos);
        if (TICKETS.containsKey(k)) return;
        AABB aabb = new AABB(pos);
        AABBTicket ticket = FarmlandWaterManager.addAABBTicket(level, aabb);
        TICKETS.put(k, ticket);
    }

    public static void removeWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        long k = key(level, pos);
        AABBTicket ticket = TICKETS.remove(k);
        if (ticket != null) {
            ticket.invalidate();
        }
    }
}
