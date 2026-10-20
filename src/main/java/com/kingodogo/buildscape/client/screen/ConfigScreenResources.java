package com.kingodogo.buildscape.client.screen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/** A reload generation avoids holding stale translated item names or font measurements. */
@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ConfigScreenResources {
    private static volatile long generation;
    private ConfigScreenResources() {}
    static long generation() {
        return generation;
    }

    @SubscribeEvent
    public static void register(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) resources -> generation++);
    }
}
