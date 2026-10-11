package com.kingodogo.buildscape;

import net.neoforged.fml.common.Mod;

@Mod("buildscape")
public class BuildscapeNeoForge {
    public BuildscapeNeoForge(net.neoforged.bus.api.IEventBus modEventBus) {
        com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.installFactories(
                () -> new com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.Source() {
                    @Override public net.neoforged.neoforge.fluids.FluidType getFluidType() {
                        return net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value();
                    }
                },
                () -> new com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.Flowing() {
                    @Override public net.neoforged.neoforge.fluids.FluidType getFluidType() {
                        return net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value();
                    }
                });
        com.kingodogo.buildscape.BuildscapeCommon.prepareRegistration();
        modEventBus.addListener(((com.kingodogo.buildscape.registry.NeoForgeRegistryAdapter)
                com.kingodogo.buildscape.platform.Services.REGISTRY)::onRegister);
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(BuildscapeNeoForge::init));
        registerPayloadHandlers(modEventBus);
        registerServerRecipeReloading();
        modEventBus.addListener((net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) ->
                com.kingodogo.buildscape.platform.Services.PLATFORM.registerEntityAttributes(event::put));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStartedEvent event) -> {
            com.kingodogo.buildscape.adapter.v121x.PacketFactory.setServer(event.getServer());
            BuildscapeCommon.setServerFullyInitialized(true);
        });
        com.kingodogo.buildscape.event.NeoForgeGameplayEvents.register();
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) BuildscapeNeoForgeClient.register(modEventBus);
    }
    private void registerServerRecipeReloading() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.AddReloadListenerEvent event) -> {
            com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                    .setCurrentRecipeManager(event.getServerResources().getRecipeManager());
            event.addListener(com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener());
        });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent event) -> {
            com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                    .setCurrentRecipeManager(null);
            com.kingodogo.buildscape.adapter.v121x.PacketFactory.setServer(null);
            BuildscapeCommon.setServerFullyInitialized(false);
        });
    }

    private void registerPayloadHandlers(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) -> {
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
        com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
    }
}
