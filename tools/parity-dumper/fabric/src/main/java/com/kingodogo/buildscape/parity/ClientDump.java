package com.kingodogo.buildscape.parity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class ClientDump implements ClientModInitializer {
    private static volatile boolean pending;

    public static void requestSnapshot() { pending = true; }

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return new ResourceLocation(ParityDumper.MOD_ID, "snapshot");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) { requestSnapshot(); }
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> requestSnapshot());
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if (!pending || minecraft.getOverlay() != null) return;
            pending = false;
            DumpIO.captureTables(FabricLoader.getInstance().getGameDir().resolve("parity-dump/client"),
                    "client", ClientSnapshot::snapshot);
        });
    }
}
