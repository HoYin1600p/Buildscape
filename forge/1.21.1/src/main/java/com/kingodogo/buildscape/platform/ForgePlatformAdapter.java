package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase;

public class ForgePlatformAdapter extends PlatformAdapterBase {
    @Override
    public String getPlatformName() {
        return "Forge 1.21.1";
    }

    @Override
    public boolean isModLoaded(String modId) {
        try {
            Class<?> clazz = Class.forName("net.minecraftforge.fml.ModList");
            Object instance = clazz.getMethod("get").invoke(null);
            return (boolean) clazz.getMethod("isLoaded", String.class).invoke(instance, modId);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public boolean isClient() {
        try {
            Class<?> clazz = Class.forName("net.minecraftforge.fml.loading.FMLEnvironment");
            Object dist = clazz.getField("dist").get(null);
            return (boolean) dist.getClass().getMethod("isClient").invoke(dist);
        } catch (Throwable t) {
            return false;
        }
    }
}
