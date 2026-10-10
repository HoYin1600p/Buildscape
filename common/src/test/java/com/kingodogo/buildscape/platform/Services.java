package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.registry.IRegistryAdapter;
import com.kingodogo.buildscape.registry.RegistrySupplier;
import com.kingodogo.buildscape.registry.SimpleRegistrySupplier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Headless stand-in for the real service holder (test classes shadow the main class on the test classpath).
 * The platform adapters only exist inside a loader, so this gives tests inert implementations: platform queries
 * answer false/0/null, and the registry adapter just records the blocks that ModBlocks registers so a test can
 * build them from the mod's own definitions.
 */
public class Services {
    /** Block registrations in registration order, filled when ModBlocks initialises. */
    public static final List<RegistrySupplier<?>> REGISTERED_BLOCKS = Collections.synchronizedList(new ArrayList<>());

    /** Item, entity and sound registrations in registration order (recorded like the blocks). */
    public static final List<RegistrySupplier<?>> REGISTERED_ITEMS = Collections.synchronizedList(new ArrayList<>());
    public static final List<RegistrySupplier<?>> REGISTERED_ENTITIES = Collections.synchronizedList(new ArrayList<>());
    public static final List<RegistrySupplier<?>> REGISTERED_SOUNDS = Collections.synchronizedList(new ArrayList<>());

    public static final IPlatformAdapter PLATFORM = (IPlatformAdapter) Proxy.newProxyInstance(
            Services.class.getClassLoader(), new Class<?>[]{IPlatformAdapter.class}, (proxy, method, args) -> {
                // Item properties get their registry id (and so their description key) exactly as the 26.x adapter does it.
                if (method.getName().equals("prepareItemProperties")) {
                    return com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase.itemProperties(
                            (com.kingodogo.buildscape.util.CommonId) args[0], (net.minecraft.world.item.Item.Properties) args[1]);
                }
                if (method.getName().equals("prepareBlockItemProperties")) {
                    return com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase.itemProperties(
                            (com.kingodogo.buildscape.util.CommonId) args[0], (net.minecraft.world.item.Item.Properties) args[1])
                            .useBlockDescriptionPrefix();
                }
                // The special item classes need the loader adapter; a plain item with the same properties has the same description key.
                if (method.getName().startsWith("create") && method.getName().endsWith("Item")
                        && args != null) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof net.minecraft.world.item.Item.Properties p) {
                            return i > 0 && args[0] instanceof net.minecraft.world.level.block.Block b
                                    ? new net.minecraft.world.item.BlockItem(b, p)
                                    : new net.minecraft.world.item.Item(p);
                        }
                    }
                }
                if (method.getName().equals("createBlockEntityType")) {
                    // Same construction as the real adapter, so block entities can be built headless.
                    @SuppressWarnings("unchecked")
                    var isValid = (java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState>) args[1];
                    return blockEntityType((BlockEntityFactory<?>) args[0], isValid);
                }
                return defaultValue(method.getReturnType());
            });

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity>
    net.minecraft.world.level.block.entity.BlockEntityType<T> blockEntityType(
            BlockEntityFactory<T> factory, java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState> isValid) {
        return new net.minecraft.world.level.block.entity.BlockEntityType<>(factory::create, java.util.Set.of()) {
            @Override
            public boolean isValid(net.minecraft.world.level.block.state.BlockState state) {
                return isValid.test(state);
            }
        };
    }
    public static final IRegistryAdapter REGISTRY = (IRegistryAdapter) Proxy.newProxyInstance(
            Services.class.getClassLoader(), new Class<?>[]{IRegistryAdapter.class}, (proxy, method, args) -> {
                switch (method.getName()) {
                    case "registerBlock" -> {
                        RegistrySupplier<?> supplier = new SimpleRegistrySupplier<>("buildscape", (String) args[0], (Supplier<?>) args[1]);
                        REGISTERED_BLOCKS.add(supplier);
                        return supplier;
                    }
                    case "getRegisteredBlocks" -> {
                        Collection<RegistrySupplier<?>> blocks = REGISTERED_BLOCKS;
                        return blocks;
                    }
                    case "getRegisteredItems" -> {
                        return REGISTERED_ITEMS;
                    }
                    case "getRegisteredEntities" -> {
                        return REGISTERED_ENTITIES;
                    }
                    default -> {
                        if (method.getName().startsWith("register")) {
                            RegistrySupplier<?> supplier = new SimpleRegistrySupplier<>("buildscape", (String) args[0], (Supplier<?>) args[1]);
                            switch (method.getName()) {
                                case "registerItem" -> REGISTERED_ITEMS.add(supplier);
                                case "registerEntity" -> REGISTERED_ENTITIES.add(supplier);
                                case "registerSound" -> REGISTERED_SOUNDS.add(supplier);
                                default -> { }
                            }
                            return supplier;
                        }
                        return defaultValue(method.getReturnType());
                    }
                }
            });
    public static final IBlockFactory BLOCK_FACTORY = inert(IBlockFactory.class);
    public static final com.kingodogo.buildscape.network.IPacketFactory PACKET_FACTORY =
            inert(com.kingodogo.buildscape.network.IPacketFactory.class);

    @SuppressWarnings("unchecked")
    private static <T> T inert(Class<T> type) {
        return (T) Proxy.newProxyInstance(Services.class.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> defaultValue(method.getReturnType()));
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        return 0;
    }

    public static <T> T load(Class<T> clazz) {
        throw new IllegalStateException("No service providers in headless tests: " + clazz.getName());
    }
}
