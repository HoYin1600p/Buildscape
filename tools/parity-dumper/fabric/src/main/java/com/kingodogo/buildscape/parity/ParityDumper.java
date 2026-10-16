package com.kingodogo.buildscape.parity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public final class ParityDumper implements ModInitializer {
    public static final String MOD_ID = "buildscape_parity_dumper";

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> DumpIO.capture(
                FabricLoader.getInstance().getGameDir().resolve("parity-dump/server"),
                "server", () -> RegistryDump.server(server)));
    }
}
