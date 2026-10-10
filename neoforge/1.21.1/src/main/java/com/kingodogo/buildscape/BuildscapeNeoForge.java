package com.kingodogo.buildscape;

import net.neoforged.fml.common.Mod;

@Mod("buildscape")
public class BuildscapeNeoForge {
    public BuildscapeNeoForge(net.neoforged.bus.api.IEventBus modEventBus) {
        com.kingodogo.buildscape.registry.NeoForgeRegistryAdapter.setEventBus(modEventBus);
        init();
        registerPayloadHandlers(modEventBus);
        registerServerRecipeReloading();
        modEventBus.addListener(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent.class, event -> {
            com.kingodogo.buildscape.platform.Services.PLATFORM.registerEntityAttributes(event::put);
        });
        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            modEventBus.addListener(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent.class, event -> {
                com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
            });
            modEventBus.addListener(net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent.class, event -> {
                com.kingodogo.buildscape.particle.ParticleFactory.registerProviders();
            });
            modEventBus.addListener(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers.class, event -> {
                com.kingodogo.buildscape.platform.Services.PLATFORM.registerRenderers();
            });
            modEventBus.addListener(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions.class, event -> {
                com.kingodogo.buildscape.platform.Services.PLATFORM.registerLayerDefinitions((layer, defSupplier) -> {
                    if (layer instanceof net.minecraft.client.model.geom.ModelLayerLocation mll && defSupplier != null) {
                        event.registerLayerDefinition(mll, () -> (net.minecraft.client.model.geom.builders.LayerDefinition) defSupplier.get());
                    }
                });
            });
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.client.event.ClientTickEvent.Post.class, event -> {
                com.kingodogo.buildscape.client.ClientEvents.onClientTick();
            });
            com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
        }
    }

    private void registerServerRecipeReloading() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.AddReloadListenerEvent.class, event -> {
            com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                    .setCurrentRecipeManager(event.getServerResources().getRecipeManager());
            event.addListener(com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener());
        });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.server.ServerStoppedEvent.class, event -> {
            com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                    .setCurrentRecipeManager(null);
        });
    }

    private void registerPayloadHandlers(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent.class, event -> {
            net.neoforged.neoforge.network.registration.PayloadRegistrar registrar = event.registrar(BuildscapeCommon.MOD_ID);
            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
                net.minecraft.resources.ResourceLocation loc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(desc.id().getNamespace(), desc.id().getPath());
                net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload> type =
                        new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(loc);
                net.minecraft.network.codec.StreamCodec<net.minecraft.network.FriendlyByteBuf, com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload> codec =
                        com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload.codec(type);

                if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER) {
                    registrar.playToServer(type, codec, (payload, context) -> {
                        context.enqueueWork(() -> {
                            if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                                com.kingodogo.buildscape.network.PacketFactory.handleServerbound(desc.id(), payload.data(), player);
                            }
                        });
                    });
                } else {
                    registrar.playToClient(type, codec, (payload, context) -> {
                        context.enqueueWork(() -> {
                            com.kingodogo.buildscape.network.PacketFactory.handleClientbound(desc.id(), payload.data(), context.player());
                        });
                    });
                }
            }
        });
    }

    public static void init() {
        BuildscapeCommon.init();
    }
}
