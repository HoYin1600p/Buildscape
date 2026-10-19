package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class PillarRegistrySaveEvents {
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld() instanceof ServerLevel) PillarIdManager.get().savePeriodic(false);
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() instanceof ServerLevel
                && event.getChunk() instanceof LevelChunk chunk) {
            PillarIdManager.get().trackLoadedChunk(chunk, true);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld() instanceof ServerLevel
                && event.getChunk() instanceof LevelChunk chunk) {
            PillarIdManager.get().trackLoadedChunk(chunk, false);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PillarIdManager.get().savePeriodic(true);
    }
}
