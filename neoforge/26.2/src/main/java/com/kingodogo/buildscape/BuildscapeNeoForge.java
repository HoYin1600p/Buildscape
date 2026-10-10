package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import com.kingodogo.buildscape.registry.NeoForgeRegistryAdapter;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(BuildscapeCommon.MOD_ID)
public class BuildscapeNeoForge {
    private static final net.neoforged.neoforge.registries.DeferredRegister<net.neoforged.neoforge.attachment.AttachmentType<?>> ATTACHMENT_TYPES =
            net.neoforged.neoforge.registries.DeferredRegister.create(
                    net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BuildscapeCommon.MOD_ID);
    private static final net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.attachment.AttachmentType<?>,
            net.neoforged.neoforge.attachment.AttachmentType<net.minecraft.nbt.CompoundTag>> PERSISTENT_DATA =
            ATTACHMENT_TYPES.register(com.kingodogo.buildscape.data.PersistentData.ATTACHMENT_PATH,
                    () -> net.neoforged.neoforge.attachment.AttachmentType
                            .builder(() -> new net.minecraft.nbt.CompoundTag())
                            .serialize(com.kingodogo.buildscape.data.PersistentData.MAP_CODEC)
                            .build());

    public BuildscapeNeoForge(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
        com.kingodogo.buildscape.data.PersistentData.install(owner ->
                ((net.neoforged.neoforge.attachment.IAttachmentHolder) owner).getData(PERSISTENT_DATA.get()));
        BuildscapeCommon.prepareRegistration();
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) ->
                com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(event.getServer().overworld()));
        modBus.addListener(((NeoForgeRegistryAdapter) Services.REGISTRY)::onRegister);
        modBus.addListener(BuildscapeNeoForge::commonSetup);
        modBus.addListener(BuildscapeNeoForge::registerPayloads);
        modBus.addListener(BuildscapeNeoForge::registerAttributes);
        modBus.addListener(BuildscapeNeoForge::registerCauldronInteractions);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForge::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForge::serverStarted);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForge::serverStopped);
        com.kingodogo.buildscape.event.NeoForgeGameplayEvents.register();
        if (FMLEnvironment.getDist().isClient()) BuildscapeNeoForgeClient.register(modBus);
    }

    public static void init() {
        BuildscapeCommon.init();
        com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
    }
    private static void commonSetup(FMLCommonSetupEvent event) { event.enqueueWork(BuildscapeNeoForge::init); }
    private static void registerAttributes(EntityAttributeCreationEvent event) {
        Services.PLATFORM.registerEntityAttributes(event::put);
    }
    private static void registerCauldronInteractions(RegisterCauldronInteractionEvent.Interaction event) {
        ((PlatformAdapterBase) Services.PLATFORM).registerExperienceCauldronInteractions((item, interaction) ->
                event.register(Identifier.withDefaultNamespace("empty"), item, interaction));
    }
    private static void addReloadListeners(AddServerReloadListenersEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(event.getServerResources().getRecipeManager());
        event.addListener(Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes"),
                Services.PLATFORM.createRecipeReloadListener());
    }
    private static void serverStarted(ServerStartedEvent event) {
        com.kingodogo.buildscape.adapter.v26x.PacketFactory.setServer(event.getServer());
        BuildscapeCommon.setServerFullyInitialized(true);
    }
    private static void serverStopped(ServerStoppedEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
        com.kingodogo.buildscape.adapter.v26x.PacketFactory.setServer(null);
        BuildscapeCommon.setServerFullyInitialized(false);
    }
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(Identifier.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            var codec = BuildscapeCustomPayload.codec(type);
            if (descriptor.direction() == PacketDirection.CLIENT_TO_SERVER) {
                registrar.playToServer(type, codec, (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        com.kingodogo.buildscape.network.PacketFactory.handleServerbound(descriptor.id(), payload.data(), player);
                    }
                });
            } else {
                registrar.playToClient(type, codec, (payload, context) ->
                        com.kingodogo.buildscape.network.PacketFactory.handleClientbound(descriptor.id(), payload.data(), context.player()));
            }
        }
    }
}
