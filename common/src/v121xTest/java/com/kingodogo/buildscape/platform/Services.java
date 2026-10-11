package com.kingodogo.buildscape.platform;

import com.kingodogo.buildscape.block.IBlockFactory;
import com.kingodogo.buildscape.registry.BaseRegistryAdapter;
import com.kingodogo.buildscape.registry.IRegistryAdapter;

import java.lang.reflect.Proxy;

/** Loader-free registration fixture with bounded registry access for block parity construction. */
public final class Services {
    private Services() {}
    public static boolean isVersionCluster(String cluster) { return "v121x".equals(cluster); }
    public static final IRegistryAdapter REGISTRY = new BaseRegistryAdapter("buildscape") {};
    public static final java.util.Map<String, net.minecraft.world.level.block.Block> PARITY_BLOCKS = new java.util.LinkedHashMap<>();
    public static final IPlatformAdapter PLATFORM = (IPlatformAdapter) Proxy.newProxyInstance(
            Services.class.getClassLoader(), new Class<?>[]{IPlatformAdapter.class}, (proxy, method, args) -> {
                if (method.getName().equals("wrapRegistryAction")) {
                    withIntrusiveHolders((Runnable) args[0]);
                    return null;
                }
                if (method.getName().equals("createTrophyBlock")) {
                    var definition = (com.kingodogo.buildscape.trophy.TrophyDefinition) args[0];
                    return new com.kingodogo.buildscape.trophy.TrophyBlock(definition,
                            net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE)
                                    .strength(definition.getHardness(), definition.getResistance())
                                    .sound(definition.getSoundType()).lightLevel(state -> definition.getLightEmission())
                                    .emissiveRendering((state, getter, pos) -> true).noOcclusion()) {
                        @Override protected com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.HorizontalDirectionalBlock> codec() {
                            return com.mojang.serialization.MapCodec.<com.kingodogo.buildscape.trophy.TrophyBlock>unit(() -> this);
                        }
                    };
                }
                if (method.getName().equals("createSoundEvent")) {
                    var id = (com.kingodogo.buildscape.util.CommonId) args[0];
                    return net.minecraft.sounds.SoundEvent.createVariableRangeEvent(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
                }
                if (method.getName().equals("getBlock")) {
                    var id = (com.kingodogo.buildscape.util.CommonId) args[0];
                    if (id.getNamespace().equals("buildscape")) return PARITY_BLOCKS.get(id.getPath());
                    return net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath()));
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
    private static void withIntrusiveHolders(Runnable action) throws ReflectiveOperationException {
        // Preserve every map and frozen flag, including during recursive dependency construction.
        var type = net.minecraft.core.MappedRegistry.class;
        var holders = type.getDeclaredField("unregisteredIntrusiveHolders");
        var frozen = type.getDeclaredField("frozen");
        holders.setAccessible(true);
        frozen.setAccessible(true);
        Object[] registries = {net.minecraft.core.registries.BuiltInRegistries.BLOCK,
                net.minecraft.core.registries.BuiltInRegistries.ITEM,
                net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE,
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE,
                net.minecraft.core.registries.BuiltInRegistries.FLUID};
        Object[] previous = new Object[registries.length];
        boolean[] wasFrozen = new boolean[registries.length];
        int opened = 0;
        try {
            for (int i = 0; i < registries.length; i++) {
                previous[i] = holders.get(registries[i]);
                wasFrozen[i] = frozen.getBoolean(registries[i]);
                opened = i + 1;
                if (previous[i] == null) holders.set(registries[i], new java.util.IdentityHashMap<>());
                frozen.setBoolean(registries[i], false);
            }
            action.run();
        } finally {
            for (int i = opened - 1; i >= 0; i--) {
                holders.set(registries[i], previous[i]);
                frozen.setBoolean(registries[i], wasFrozen[i]);
            }
        }
    }
    public static <T> T load(Class<T> type) {
        throw new AssertionError("No loader services in descriptor tests: " + type.getName());
    }
}



