package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.registry.IRegistryAdapter;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class Services {
    public static final IPlatformAdapter PLATFORM = load(IPlatformAdapter.class);
    public static final IRegistryAdapter REGISTRY = load(IRegistryAdapter.class);
    public static final IBlockFactory BLOCK_FACTORY = load(IBlockFactory.class);
    public static final com.kingodogo.buildscape.network.IPacketFactory PACKET_FACTORY = load(com.kingodogo.buildscape.network.IPacketFactory.class);

    public static <T> T load(Class<T> clazz) {
        List<T> providers = ServiceLoader.load(clazz).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
        if (providers.size() != 1) {
            String providerNames = providers.stream()
                    .map(provider -> provider.getClass().getName())
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Expected exactly one provider for " + clazz.getName()
                    + " but found " + providers.size() + (providerNames.isEmpty() ? "" : ": " + providerNames));
        }
        return providers.get(0);
    }
}
