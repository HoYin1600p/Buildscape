package com.kingodogo.buildscape;

import net.minecraftforge.fml.common.Mod;

@Mod("buildscape")
public class BuildscapeForge {
    public BuildscapeForge() {
        init();
        registerServerRecipeReloading();
        registerNetworking();
        tryRegisterClient();
    }

    public static void init() {
        BuildscapeCommon.init();
    }

    private void registerNetworking() {
        try {
            Class<?> netRegistryClass = Class.forName("net.minecraftforge.network.NetworkRegistry");
            Class<?> channelClass = Class.forName("net.minecraftforge.network.simple.SimpleChannel");
            Class<?> netDirectionClass = Class.forName("net.minecraftforge.network.NetworkDirection");
            Object playToServerDir = Enum.valueOf((Class<Enum>) netDirectionClass, "PLAY_TO_SERVER");
            Object playToClientDir = Enum.valueOf((Class<Enum>) netDirectionClass, "PLAY_TO_CLIENT");

            String protocol = "1.0";
            java.util.function.Supplier<String> protocolSupplier = () -> protocol;
            java.util.function.Predicate<String> clientCheck = protocol::equals;
            java.util.function.Predicate<String> serverCheck = protocol::equals;

            Object channel = netRegistryClass.getMethod("newSimpleChannel",
                    net.minecraft.resources.Identifier.class,
                    java.util.function.Supplier.class,
                    java.util.function.Predicate.class,
                    java.util.function.Predicate.class
            ).invoke(null, net.minecraft.resources.Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "main"),
                    protocolSupplier, clientCheck, serverCheck);

            int packetId = 0;
            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                final int currentId = packetId++;
                final com.kingodogo.buildscape.util.CommonId packetCommonId = desc.id();
                final boolean isC2S = desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER;
                Object directionEnum = isC2S ? playToServerDir : playToClientDir;

                java.lang.reflect.Method messageBuilder = channelClass.getMethod("messageBuilder", Class.class, int.class, netDirectionClass);
                Object builder = messageBuilder.invoke(channel, com.kingodogo.buildscape.network.CommonPacket.class, currentId, directionEnum);

                java.util.function.BiConsumer<com.kingodogo.buildscape.network.CommonPacket, net.minecraft.network.FriendlyByteBuf> encoder =
                        (pkt, buf) -> pkt.write(buf);
                java.util.function.Function<net.minecraft.network.FriendlyByteBuf, byte[]> decoder =
                        buf -> {
                            byte[] bytes = new byte[buf.readableBytes()];
                            buf.readBytes(bytes);
                            return bytes;
                        };
                java.util.function.BiConsumer<byte[], java.util.function.Supplier<Object>> consumer =
                        (data, ctxSupplier) -> {
                            Object ctx = ctxSupplier.get();
                            try {
                                java.lang.reflect.Method enqueueWork = ctx.getClass().getMethod("enqueueWork", Runnable.class);
                                enqueueWork.invoke(ctx, (Runnable) () -> {
                                    try {
                                        if (isC2S) {
                                            Object sender = ctx.getClass().getMethod("getSender").invoke(ctx);
                                            if (sender instanceof net.minecraft.server.level.ServerPlayer player) {
                                                com.kingodogo.buildscape.network.PacketFactory.handleServerbound(packetCommonId, data, player);
                                            }
                                        } else {
                                            com.kingodogo.buildscape.network.PacketFactory.handleClientbound(packetCommonId, data, null);
                                        }
                                    } catch (Throwable t) {
                                        BuildscapeCommon.LOGGER.error("Failed to process Forge 26.3 packet {}", packetCommonId, t);
                                    }
                                });
                                ctx.getClass().getMethod("setPacketHandled", boolean.class).invoke(ctx, true);
                            } catch (Throwable t) {
                                BuildscapeCommon.LOGGER.error("Failed to enqueue Forge 26.3 packet handler for {}", packetCommonId, t);
                            }
                        };

                builder.getClass().getMethod("encoder", java.util.function.BiConsumer.class).invoke(builder, encoder);
                builder.getClass().getMethod("decoder", java.util.function.Function.class).invoke(builder, decoder);
                builder.getClass().getMethod("consumer", java.util.function.BiConsumer.class).invoke(builder, consumer);
                builder.getClass().getMethod("add").invoke(builder);
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Forge 26.3 networking registration deferred or unavailable: {}", t.getMessage());
        }
    }

    private void registerServerRecipeReloading() {
        try {
            Object bus = Class.forName("net.minecraftforge.common.MinecraftForge").getField("EVENT_BUS").get(null);
            java.lang.reflect.Method addListener = bus.getClass().getMethod("addListener", java.util.function.Consumer.class);
            java.util.function.Consumer<Object> reloadListener = event -> {
                if (!"net.minecraftforge.event.AddReloadListenerEvent".equals(event.getClass().getName())) return;
                try {
                    Object serverResources = event.getClass().getMethod("getServerResources").invoke(event);
                    Object recipeManager = serverResources.getClass().getMethod("getRecipeManager").invoke(serverResources);
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                            .setCurrentRecipeManager((net.minecraft.world.item.crafting.RecipeManager) recipeManager);
                    event.getClass().getMethod("addListener", net.minecraft.server.packs.resources.PreparableReloadListener.class)
                            .invoke(event, com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener());
                } catch (ReflectiveOperationException e) {
                    BuildscapeCommon.LOGGER.error("Failed to register the dynamic recipe reload listener", e);
                }
            };
            java.util.function.Consumer<Object> stopListener = event -> {
                if ("net.minecraftforge.event.server.ServerStoppedEvent".equals(event.getClass().getName())) {
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                            .setCurrentRecipeManager(null);
                }
            };
            addListener.invoke(bus, reloadListener);
            addListener.invoke(bus, stopListener);
        } catch (ReflectiveOperationException e) {
            BuildscapeCommon.LOGGER.error("Failed to attach Forge dynamic recipe lifecycle hooks", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void tryRegisterClient() {
        try {
            Class<?> distEnum = Class.forName("net.minecraftforge.api.distmarker.Dist");
            Object clientDist = Enum.valueOf((Class<Enum>) distEnum, "CLIENT");
            Class<?> fmlEnv = Class.forName("net.minecraftforge.fml.loading.FMLEnvironment");
            Object currentDist = fmlEnv.getField("dist").get(null);
            if (clientDist.equals(currentDist)) {
                Class<?> fmlContext = Class.forName("net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext");
                Object context = fmlContext.getMethod("get").invoke(null);
                Object bus = fmlContext.getMethod("getModEventBus").invoke(context);
                java.lang.reflect.Method addListener = bus.getClass().getMethod("addListener", java.util.function.Consumer.class);
                java.util.function.Consumer<Object> consumer = evt -> {
                    try {
                        java.lang.reflect.Method enqueueWork = evt.getClass().getMethod("enqueueWork", Runnable.class);
                        enqueueWork.invoke(evt, (Runnable) () -> {
                            com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
                            com.kingodogo.buildscape.particle.ParticleFactory.registerProviders();
                            com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
                        });
                    } catch (Throwable t) {
                        BuildscapeCommon.LOGGER.warn("Failed to initialize Forge client menu screens: {}", t.getMessage());
                    }
                };
                addListener.invoke(bus, consumer);

                try {
                    Object forgeBus = Class.forName("net.minecraftforge.common.MinecraftForge").getField("EVENT_BUS").get(null);
                    java.lang.reflect.Method addForgeListener = forgeBus.getClass().getMethod("addListener", java.util.function.Consumer.class);
                    java.util.function.Consumer<Object> tickConsumer = tickEvt -> {
                        if (tickEvt.getClass().getName().contains("ClientTickEvent")) {
                            com.kingodogo.buildscape.client.ClientEvents.onClientTick();
                        }
                    };
                    addForgeListener.invoke(forgeBus, tickConsumer);
                } catch (Throwable t) {
                    BuildscapeCommon.LOGGER.debug("Forge client tick event hook deferred: {}", t.getMessage());
                }
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Forge client initialization skipped: {}", t.getMessage());
        }
    }
}
