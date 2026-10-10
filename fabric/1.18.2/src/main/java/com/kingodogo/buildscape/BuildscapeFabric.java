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

    private static void registerNetworking() {
        try {
            Class<?> serverNetworking = Class.forName("net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking");
            Class<?> handlerType = Class.forName("net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking$PlayChannelHandler");
            java.lang.reflect.Method registerMethod = java.util.Arrays.stream(serverNetworking.getMethods())
                    .filter(m -> m.getName().equals("registerGlobalReceiver") && m.getParameterCount() == 2)
                    .findFirst().orElse(null);
            if (registerMethod == null) return;

            Class<?> idClass = registerMethod.getParameterTypes()[0];
            java.lang.reflect.Constructor<?> idCtor = idClass.getConstructor(String.class, String.class);

            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER) {
                    Object idObj = idCtor.newInstance(desc.id().getNamespace(), desc.id().getPath());
                    Object handlerProxy = java.lang.reflect.Proxy.newProxyInstance(
                            handlerType.getClassLoader(),
                            new Class<?>[]{handlerType},
                            (proxy, method, args) -> {
                                if (method.getName().equals("receive") && args != null && args.length >= 4) {
                                    Object playerObj = args[1];
                                    Object bufObj = args[3];
                                    if (playerObj instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                        if (bufObj instanceof net.minecraft.network.FriendlyByteBuf friendlyBuf) {
                                            com.kingodogo.buildscape.network.PacketFactory.handleServerbound(desc.id(), friendlyBuf, serverPlayer);
                                        } else if (bufObj instanceof io.netty.buffer.ByteBuf rawBuf) {
                                            com.kingodogo.buildscape.network.PacketFactory.handleServerbound(desc.id(), new net.minecraft.network.FriendlyByteBuf(rawBuf), serverPlayer);
                                        }
                                    }
                                }
                                return null;
                            }
                    );
                    registerMethod.invoke(null, idObj, handlerProxy);
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.warn("Failed to register Fabric 1.18.2 server networking receivers: {}", t.getMessage());
        }
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
                            return new net.minecraft.resources.ResourceLocation(BuildscapeCommon.MOD_ID, "dynamic_recipes");
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
}
