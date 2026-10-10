package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformAdapter extends PlatformAdapterBase {
    @Override
    public String getPlatformName() {
        return "Fabric 1.21.1";
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
