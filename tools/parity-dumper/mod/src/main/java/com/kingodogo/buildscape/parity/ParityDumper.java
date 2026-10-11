package com.kingodogo.buildscape.parity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(ParityDumper.MOD_ID)
public final class ParityDumper {
    public static final String MOD_ID = "buildscape_parity_dumper";

    public ParityDumper() {
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
    }

    private void serverStarted(ServerStartedEvent event) {
        DumpIO.capture(FMLPaths.GAMEDIR.get().resolve("parity-dump/server"),
                "server", () -> RegistryDump.server(event.getServer()));
    }
}
