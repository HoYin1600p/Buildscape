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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class CascadeWaterManager {

    private static final Map<ResourceLocation, Map<BlockPos, AABBTicket>> TICKETS = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof Level level) {
            ResourceLocation dimension = level.dimension().location();
            Map<BlockPos, AABBTicket> dimensionTickets = TICKETS.remove(dimension);
            if (dimensionTickets != null) {
                dimensionTickets.values().forEach(AABBTicket::invalidate);
            }
        }
    }

    public static void registerWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        ResourceLocation dimension = level.dimension().location();
        
        Map<BlockPos, AABBTicket> dimensionTickets = TICKETS.computeIfAbsent(dimension, k -> new ConcurrentHashMap<>());
        
        // If ticket already exists, invalidate it before creating a new one to be safe
        AABBTicket old = dimensionTickets.remove(pos);
        if (old != null) {
            old.invalidate();
        }

        AABB aabb = new AABB(pos);
        AABBTicket ticket = FarmlandWaterManager.addAABBTicket(level, aabb);
        dimensionTickets.put(pos, ticket);
    }

    public static void removeWaterTicket(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        ResourceLocation dimension = level.dimension().location();
        Map<BlockPos, AABBTicket> dimensionTickets = TICKETS.get(dimension);
        if (dimensionTickets != null) {
            AABBTicket ticket = dimensionTickets.remove(pos);
            if (ticket != null) {
                ticket.invalidate();
            }
        }
    }
}
