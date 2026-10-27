package com.kingodogo.buildscape.client.particle;

import com.kingodogo.buildscape.particle.SmokeColorRegistry;
import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, value = Dist.CLIENT)
public final class ParticleCacheEvents {
    private ParticleCacheEvents() {}

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        SmokeColorRegistry.cleanupOldEntries();
        TintedDripParticle.cleanupOldEntries();
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isClientSide()) {
            clear();
        }
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        clear();
    }

    private static void clear() {
        HazeMask.clearCaches();
        SmokeColorRegistry.clearColorCache();
        TintedDripParticle.clearColorCache();
        CherryParticle.clearColorCache();
        CakeParticle.clearColorCache();
        TrailNoteParticle.clearColorCache();
        TintableHeartParticle.clearColorCache();
        FireflyParticle.clearActiveParticles();
    }
}
