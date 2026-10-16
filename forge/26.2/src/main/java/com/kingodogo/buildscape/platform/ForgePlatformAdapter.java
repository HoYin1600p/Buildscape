package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;

public class ForgePlatformAdapter extends PlatformAdapterBase {
    @Override
    public <V> void register(net.minecraft.core.Registry<V> registry,
                             com.kingodogo.buildscape.util.CommonId id, V value) {
        ((com.kingodogo.buildscape.registry.ForgeRegistryAdapter)
                com.kingodogo.buildscape.platform.Services.REGISTRY).enqueue(registry, id, value);
    }

    @Override
    public String getPlatformName() {
        return "Forge 26.2";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return net.minecraftforge.fml.ModList.isLoaded(modId);
    }

    @Override
    public java.nio.file.Path getGameDir() {
        return net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get();
    }
    @Override
    public java.nio.file.Path getConfigDir() {
        return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient();
    }
}

