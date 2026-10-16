package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformAdapter extends PlatformAdapterBase {
    @Override
    public java.nio.file.Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }
    @Override
    public java.nio.file.Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getPlatformName() {
        return "Fabric 26.3";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
