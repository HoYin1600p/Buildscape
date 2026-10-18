package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.PillarIdManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID)
public class PillarRegistrySaveEvents {
    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld() instanceof ServerLevel) PillarIdManager.get().savePeriodic(true);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        PillarIdManager.get().savePeriodic(true);
    }
}
