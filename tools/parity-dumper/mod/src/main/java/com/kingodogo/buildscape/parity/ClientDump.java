package com.kingodogo.buildscape.parity;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod.EventBusSubscriber(modid = ParityDumper.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientDump {
    private static volatile boolean pending;

    private ClientDump() {}

    @Mod.EventBusSubscriber(modid = ParityDumper.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ReloadRegistration {
        private ReloadRegistration() {}

        @SubscribeEvent
        public static void register(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener(new SimplePreparableReloadListener<Boolean>() {
                @Override
                protected Boolean prepare(ResourceManager manager, ProfilerFiller profiler) { return Boolean.TRUE; }

                @Override
                protected void apply(Boolean prepared, ResourceManager manager, ProfilerFiller profiler) { pending = true; }
            });
        }
    }

    @SubscribeEvent
    public static void tags(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) pending = true;
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.END || !pending || minecraft.getOverlay() != null) return;
        pending = false;
        DumpIO.captureTables(FMLPaths.GAMEDIR.get().resolve("parity-dump/client"), "client", ClientSnapshot::snapshot);
    }

}
