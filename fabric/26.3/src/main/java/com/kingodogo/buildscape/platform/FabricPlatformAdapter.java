package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformAdapter extends PlatformAdapterBase {
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
