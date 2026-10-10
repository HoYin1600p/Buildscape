package com.kingodogo.buildscape;

import net.neoforged.fml.common.Mod;

@Mod("buildscape")
public class BuildscapeNeoForge {
    public BuildscapeNeoForge() {
        init();
        tryRegisterPayloadHandlers();
        tryRegisterRecipeReloading();
        tryRegisterClient();
    }

    private void tryRegisterRecipeReloading() {
        try {
            Class<?> neoForgeClass = Class.forName("net.neoforged.neoforge.common.NeoForge");
            Object eventBus = neoForgeClass.getField("EVENT_BUS").get(null);
            java.lang.reflect.Method addListener = eventBus.getClass().getMethod("addListener", Class.class, java.util.function.Consumer.class);

            Class<?> addReloadListenerEventClass = Class.forName("net.neoforged.neoforge.event.AddReloadListenerEvent");
            java.util.function.Consumer<Object> reloadConsumer = event -> {
                try {
                    Object serverResources = event.getClass().getMethod("getServerResources").invoke(event);
                    Object recipeManager = serverResources.getClass().getMethod("getRecipeManager").invoke(serverResources);
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                            .setCurrentRecipeManager((net.minecraft.world.item.crafting.RecipeManager) recipeManager);
                    event.getClass().getMethod("addListener", net.minecraft.server.packs.resources.PreparableReloadListener.class)
                            .invoke(event, com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener());
                } catch (Throwable t) {
                    BuildscapeCommon.LOGGER.error("Failed to register NeoForge 26.3 dynamic recipe reload listener", t);
                }
            };
            addListener.invoke(eventBus, addReloadListenerEventClass, reloadConsumer);

            Class<?> serverStoppedEventClass = Class.forName("net.neoforged.neoforge.event.server.ServerStoppedEvent");
            java.util.function.Consumer<Object> stoppedConsumer = event -> {
                com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
            };
            addListener.invoke(eventBus, serverStoppedEventClass, stoppedConsumer);
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("NeoForge 26.3 recipe reloading hooks deferred or unavailable: {}", t.getMessage());
        }
    }

    private void tryRegisterPayloadHandlers() {
        try {
            Class<?> modBusClass = Class.forName("net.neoforged.bus.api.IEventBus");
            Class<?> contextClass = Class.forName("net.neoforged.fml.ModLoadingContext");
            Object context = contextClass.getMethod("get").invoke(null);
            Object activeContainer = contextClass.getMethod("getActiveContainer").invoke(context);
            Object bus = activeContainer.getClass().getMethod("getEventBus").invoke(activeContainer);
            if (bus != null) {
                Class<?> eventClass = Class.forName("net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent");
                java.lang.reflect.Method addListener = bus.getClass().getMethod("addListener", Class.class, java.util.function.Consumer.class);
                java.util.function.Consumer<Object> handler = event -> {
                    try {
                        Object registrar = event.getClass().getMethod("registrar", String.class).invoke(event, BuildscapeCommon.MOD_ID);
                        for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                            net.minecraft.resources.Identifier loc = net.minecraft.resources.Identifier.fromNamespaceAndPath(desc.id().getNamespace(), desc.id().getPath());
                            net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload> type =
                                    new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(loc);
                            net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload> codec =
                                    com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload.codec(type);

                            if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER) {
                                java.lang.reflect.Method playToServer = registrar.getClass().getMethod("playToServer",
                                        net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                                        net.minecraft.network.codec.StreamCodec.class,
                                        Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler"));
                                Object payloadHandler = java.lang.reflect.Proxy.newProxyInstance(
                                        Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler").getClassLoader(),
                                        new Class<?>[]{Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler")},
                                        (proxy, m, args) -> {
                                            if (m.getName().equals("handle") && args != null && args.length >= 2) {
                                                com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload p =
                                                        (com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload) args[0];
                                                Object ctx = args[1];
                                                java.lang.reflect.Method playerMethod = ctx.getClass().getMethod("player");
                                                Object playerObj = playerMethod.invoke(ctx);
                                                if (playerObj instanceof net.minecraft.server.level.ServerPlayer player) {
                                                    com.kingodogo.buildscape.network.PacketFactory.handleServerbound(desc.id(), p.data(), player);
                                                }
                                            }
                                            return null;
                                        }
                                );
                                playToServer.invoke(registrar, type, codec, payloadHandler);
                            } else {
                                java.lang.reflect.Method playToClient = registrar.getClass().getMethod("playToClient",
                                        net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type.class,
                                        net.minecraft.network.codec.StreamCodec.class,
                                        Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler"));
                                Object payloadHandler = java.lang.reflect.Proxy.newProxyInstance(
                                        Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler").getClassLoader(),
                                        new Class<?>[]{Class.forName("net.neoforged.neoforge.network.handling.IPayloadHandler")},
                                        (proxy, m, args) -> {
                                            if (m.getName().equals("handle") && args != null && args.length >= 2) {
                                                com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload p =
                                                        (com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload) args[0];
                                                Object ctx = args[1];
                                                java.lang.reflect.Method playerMethod = ctx.getClass().getMethod("player");
                                                Object playerObj = playerMethod.invoke(ctx);
                                                if (playerObj instanceof net.minecraft.world.entity.player.Player player) {
                                                    com.kingodogo.buildscape.network.PacketFactory.handleClientbound(desc.id(), p.data(), player);
                                                }
                                            }
                                            return null;
                                        }
                                );
                                playToClient.invoke(registrar, type, codec, payloadHandler);
                            }
                        }
                    } catch (Throwable t) {
                        BuildscapeCommon.LOGGER.warn("Failed to register NeoForge 26.3 payload: {}", t.getMessage());
                    }
                };
                addListener.invoke(bus, eventClass, handler);
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("NeoForge 26.3 payload handlers registration deferred or unavailable: {}", t.getMessage());
        }
    }

    public static void init() {
        BuildscapeCommon.init();
    }

    @SuppressWarnings("unchecked")
    private void tryRegisterClient() {
        try {
            Class<?> distEnum = Class.forName("net.neoforged.api.distmarker.Dist");
            Object clientDist = Enum.valueOf((Class<Enum>) distEnum, "CLIENT");
            Class<?> fmlEnv = Class.forName("net.neoforged.fml.loading.FMLEnvironment");
            Object currentDist = fmlEnv.getField("dist").get(null);
            if (clientDist.equals(currentDist)) {
                com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
                com.kingodogo.buildscape.particle.ParticleFactory.registerProviders();
                com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.warn("Failed to register NeoForge 26.3 client handlers: {}", t.getMessage());
        }
    }
}
