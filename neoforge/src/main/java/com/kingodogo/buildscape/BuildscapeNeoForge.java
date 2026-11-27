package com.kingodogo.buildscape;

import net.neoforged.fml.common.Mod;

@Mod("buildscape")
public class BuildscapeNeoForge {
    public BuildscapeNeoForge(net.neoforged.bus.api.IEventBus modEventBus) {
        com.kingodogo.buildscape.adapter.ExperienceFluids.installFactories(
                () -> new com.kingodogo.buildscape.adapter.ExperienceFluids.Source() {
                    @Override public boolean canConvertToSource(net.minecraft.world.level.material.FluidState state,
                            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return false; }
                    @Override public net.neoforged.neoforge.fluids.FluidType getFluidType() {
                        return net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value();
                    }
                },
                () -> new com.kingodogo.buildscape.adapter.ExperienceFluids.Flowing() {
                    @Override public boolean canConvertToSource(net.minecraft.world.level.material.FluidState state,
                            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return false; }
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
            com.kingodogo.buildscape.adapter.PacketFactory.setServer(event.getServer());
            BuildscapeCommon.setServerFullyInitialized(true);
        });
        com.kingodogo.buildscape.event.NeoForgeGameplayEvents.register();
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) BuildscapeNeoForgeClient.register(modEventBus);
    }
    private void registerServerRecipeReloading() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.AddReloadListenerEvent event) -> {
            event.addListener(com.kingodogo.buildscape.platform.Services.PLATFORM.createRecipeReloadListener(
                    com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.forReload(
                            event.getServerResources().getRecipeManager())));
        });
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent event) -> {
            com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader.INSTANCE
                    .setCurrentRecipeManager(null);
            com.kingodogo.buildscape.adapter.PacketFactory.setServer(null);
            BuildscapeCommon.setServerFullyInitialized(false);
        });
    }

    private void registerPayloadHandlers(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent event) -> {
            net.neoforged.neoforge.network.registration.IPayloadRegistrar registrar = event.registrar(com.kingodogo.buildscape.network.ModPackets.PROTOCOL_NAME);
            record RawPayload(net.minecraft.resources.ResourceLocation id, byte[] data) implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
                @Override public void write(net.minecraft.network.FriendlyByteBuf out) { out.writeBytes(data); }
            }
            for (com.kingodogo.buildscape.network.IPacketFactory.PacketDescriptor desc : com.kingodogo.buildscape.platform.Services.PACKET_FACTORY.getRegisteredDescriptors()) {
                net.minecraft.resources.ResourceLocation loc = new net.minecraft.resources.ResourceLocation(desc.id().getNamespace(), desc.id().getPath());
                registrar.play(loc, reader -> {
                    byte[] bytes = new byte[reader.readableBytes()];
                    reader.readBytes(bytes);
                    return new RawPayload(loc, bytes);
                }, (payload, context) -> {
                    net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.data()));
                    context.workHandler().submitAsync(() -> {
                        if (desc.direction() == com.kingodogo.buildscape.network.PacketDirection.CLIENT_TO_SERVER) {
                            context.player().ifPresent(p -> {
                                if (p instanceof net.minecraft.server.level.ServerPlayer sp) {
                                    com.kingodogo.buildscape.platform.Services.PACKET_FACTORY.handleServerbound(desc.id(), buf, sp);
                                }
                            });
                        } else {
                            com.kingodogo.buildscape.platform.Services.PACKET_FACTORY.handleClientbound(desc.id(), buf, context.player().orElse(null));
                        }
                    });
                });
            }
        });
    }

    public static void init() {
        BuildscapeCommon.init();
        com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
    }
}
