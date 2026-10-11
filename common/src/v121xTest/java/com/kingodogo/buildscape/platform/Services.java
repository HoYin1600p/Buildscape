package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.registry.BaseRegistryAdapter;
import com.kingodogo.buildscape.registry.IRegistryAdapter;

import java.lang.reflect.Proxy;

/** Descriptor-only service fixture: records real registration queues without constructing game objects. */
public final class Services {
    private Services() {}
    public static boolean isVersionCluster(String cluster) { return "v121x".equals(cluster); }
    public static final IRegistryAdapter REGISTRY = new BaseRegistryAdapter("buildscape") {};
    public static final IPlatformAdapter PLATFORM = (IPlatformAdapter) Proxy.newProxyInstance(
            Services.class.getClassLoader(), new Class<?>[]{IPlatformAdapter.class}, (proxy, method, args) -> {
                if (method.getName().equals("wrapRegistryAction")) {
                    ((Runnable) args[0]).run();
                    return null;
                }
                if (method.getReturnType() == boolean.class) return false;
                if (method.getReturnType() == int.class) return 0;
                if (method.getReturnType() == long.class) return 0L;
                if (method.getReturnType() == float.class) return 0F;
                if (method.getReturnType() == double.class) return 0D;
                return null;
            });
    public static final IBlockFactory BLOCK_FACTORY = (IBlockFactory) Proxy.newProxyInstance(
            Services.class.getClassLoader(), new Class<?>[]{IBlockFactory.class}, (proxy, method, args) -> {
                throw new AssertionError("Registration descriptor tests must not construct blocks");
            });
    public static final com.kingodogo.buildscape.network.IPacketFactory PACKET_FACTORY =
            (com.kingodogo.buildscape.network.IPacketFactory) Proxy.newProxyInstance(Services.class.getClassLoader(),
                    new Class<?>[]{com.kingodogo.buildscape.network.IPacketFactory.class}, (proxy, method, args) -> {
                        throw new AssertionError("Registration descriptor tests must not construct packets");
                    });
    public static <T> T load(Class<T> type) {
        throw new AssertionError("No loader services in descriptor tests: " + type.getName());
    }
}
