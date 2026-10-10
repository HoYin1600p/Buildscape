package com.kingodogo.buildscape;

import net.fabricmc.api.ModInitializer;

public class BuildscapeFabric implements ModInitializer {
    public BuildscapeFabric() {
        init();
    }

    @Override
    public void onInitialize() {
        init();
    }

    public static void init() {
        BuildscapeCommon.init();
        registerRecipeReloading();
        registerNetworking();
    }

    private static boolean recipeReloadingRegistered;

    private static synchronized void registerRecipeReloading() {
        if (recipeReloadingRegistered) return;
        recipeReloadingRegistered = true;
        try {
            net.minecraft.server.packs.resources.PreparableReloadListener baseListener = com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener();
            Class<?> listenerType = Class.forName("net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener");
            Object listener = java.lang.reflect.Proxy.newProxyInstance(listenerType.getClassLoader(),
                    new Class<?>[]{listenerType}, (proxy, method, args) -> {
                        if (method.getName().equals("getFabricId")) {
                            return net.minecraft.resources.Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes");
                        }
                        if (method.getName().equals("reload")) {
                            return method.invoke(baseListener, args);
                        }
                        return null;
                    });
            Class<?> helperType = Class.forName("net.fabricmc.fabric.api.resource.ResourceManagerHelper");
            Object helper = helperType.getMethod("get", net.minecraft.server.packs.PackType.class)
                    .invoke(null, net.minecraft.server.packs.PackType.SERVER_DATA);
            helperType.getMethod("registerReloadListener", listenerType).invoke(helper, listener);

            Class<?> events = Class.forName("net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents");
            registerLifecycleCallback(events.getField("SERVER_STARTING").get(null), server -> {
                try {
                    Object manager = server.getClass().getMethod("getRecipeManager").invoke(server);
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                            .setCurrentRecipeManager((net.minecraft.world.item.crafting.RecipeManager) manager);
                } catch (ReflectiveOperationException e) {
                    BuildscapeCommon.LOGGER.error("Failed to obtain Fabric server recipe manager", e);
                }
            });
            registerLifecycleCallback(events.getField("SERVER_STOPPED").get(null), server ->
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null));
        } catch (ReflectiveOperationException e) {
            BuildscapeCommon.LOGGER.error("Failed to attach Fabric dynamic recipe lifecycle hooks", e);
        }
    }

    private static void registerLifecycleCallback(Object event, java.util.function.Consumer<Object> callback)
            throws ReflectiveOperationException {
        java.lang.reflect.Method register = java.util.Arrays.stream(event.getClass().getMethods())
                .filter(method -> method.getName().equals("register") && method.getParameterCount() == 1)
                .findFirst().orElseThrow();
        Class<?> callbackType = register.getParameterTypes()[0];
        Object proxy = java.lang.reflect.Proxy.newProxyInstance(callbackType.getClassLoader(), new Class<?>[]{callbackType},
                (ignored, method, args) -> {
                    if (args != null && args.length == 1) callback.accept(args[0]);
                    return null;
                });
        register.invoke(event, proxy);
    }

    private static void registerNetworking() {
        try {
            Class<?> payloadRegistryClass = Class.forName("net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry");
            Object playC2S = payloadRegistryClass.getMethod("playC2S").invoke(null);
            Object playS2C = payloadRegistryClass.getMethod("playS2C").invoke(null);
            java.lang.reflect.Method registerC2S = playC2S.getClass().getMethod("register",
                    net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                    net.minecraft.network.codec.StreamCodec.class);
            java.lang.reflect.Method registerS2C = playS2C.getClass().getMethod("register",
                    net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                    net.minecraft.network.codec.StreamCodec.class);

            Class<?> serverNetworking = Class.forName("net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking");
            Class<?> serverReceiverClass = Class.forName("net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayPayloadHandler");
            java.lang.reflect.Method registerServerReceiver = serverNetworking.getMethod("registerGlobalReceiver",
                    net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                    serverReceiverClass);

            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.fromNamespaceAndPath(desc.id().getNamespace(), desc.id().getPath());
                net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload> type =
                        new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(loc);
                net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload> codec =
                        com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload.codec(type);

                if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER) {
                    registerC2S.invoke(playC2S, type, codec);
                    Object handler = java.lang.reflect.Proxy.newProxyInstance(
                            serverReceiverClass.getClassLoader(),
                            new Class<?>[]{serverReceiverClass},
                            (proxy, method, args) -> {
                                if (method.getName().equals("receive") && args != null && args.length >= 2) {
                                    com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload payload =
                                            (com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload) args[0];
                                    Object ctx = args[1];
                                    java.lang.reflect.Method playerMethod = ctx.getClass().getMethod("player");
                                    Object playerObj = playerMethod.invoke(ctx);
                                    if (playerObj instanceof net.minecraft.server.level.ServerPlayer player) {
                                        com.kingodogo.buildscape.network.PacketFactory.handleServerbound(desc.id(), payload.data(), player);
                                    }
                                }
                                return null;
                            }
                    );
                    registerServerReceiver.invoke(null, type, handler);
                } else {
                    registerS2C.invoke(playS2C, type, codec);
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Fabric 26.2 networking registration deferred or unavailable: {}", t.getMessage());
        }
    }
}
