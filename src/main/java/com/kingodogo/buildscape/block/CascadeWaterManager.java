package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.kingodogo.buildscape.BuildScape;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class CascadeWaterManager {

    private static final Map<Long, AABBTicket> TICKETS = new HashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        // Clear all tickets when a world unloads to prevent stale data and memory leaks
        TICKETS.clear();
    }

    private static long key(Level level, BlockPos pos) {
        return ((long) level.dimension().location().hashCode() << 32) ^ pos.asLong();
    }

    public static void registerWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        long k = key(level, pos);
        
        // If ticket already exists, invalidate it before creating a new one to be safe
        AABBTicket old = TICKETS.remove(k);
        if (old != null) {
            old.invalidate();
        }

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
