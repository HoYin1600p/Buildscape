package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v263.PlatformAdapterBase;
import com.kingodogo.buildscape.adapter.v263.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class BuildscapeFabric implements ModInitializer {
    @Override public void onInitialize() { init(); }
    private static final com.kingodogo.buildscape.registry.StartupOnce STARTUP = new com.kingodogo.buildscape.registry.StartupOnce();

    public static void init() {
        STARTUP.run(() -> {
            net.fabricmc.fabric.api.attachment.v1.AttachmentType<net.minecraft.nbt.CompoundTag> persistentData =
                    net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry.<net.minecraft.nbt.CompoundTag>builder()
                            .initializer(net.minecraft.nbt.CompoundTag::new)
                            .persistent(com.kingodogo.buildscape.data.PersistentData.CODEC)
                            .buildAndRegister(Identifier.fromNamespaceAndPath(
                                    BuildscapeCommon.MOD_ID, com.kingodogo.buildscape.data.PersistentData.ATTACHMENT_PATH));
            com.kingodogo.buildscape.data.PersistentData.install(owner ->
                    ((net.fabricmc.fabric.api.attachment.v1.AttachmentTarget) owner).getAttachedOrCreate(persistentData));
            BuildscapeCommon.init();
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server ->
                    com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld()));
            com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
            com.kingodogo.buildscape.worldgen.ModBiomeModifications.register();
            Services.PLATFORM.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
            ((PlatformAdapterBase) Services.PLATFORM).registerExperienceCauldronInteractions(
                    net.minecraft.core.cauldron.BuildscapeCauldronRegistration::register);
            ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(
                    Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes"),
                    Services.PLATFORM.createRecipeReloadListener());
            // 26.3 builds a fresh RecipeManager per data reload and no longer exposes it to reload
            // listeners, so the new manager is taken from the finished resources (and from the server on
            // first start). Recipes are injected into it, then its derived tables are rebuilt.
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                ((PlatformAdapterBase) Services.PLATFORM).registerDeferredServerData(server);
                useRecipeManager(server, server.getRecipeManager());
                com.kingodogo.buildscape.adapter.v263.PacketFactory.setServer(server);
            });
            ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resources, success) -> {
                if (!success) return;
                ((PlatformAdapterBase) Services.PLATFORM).registerDeferredServerData(server);
                useRecipeManager(server, server.getRecipeManager());
            });
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
                BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
                com.kingodogo.buildscape.adapter.v263.PacketFactory.setServer(null);
                BuildscapeCommon.setServerFullyInitialized(false);
            });
            ServerLifecycleEvents.SERVER_STARTED.register(server -> BuildscapeCommon.setServerFullyInitialized(true));
            registerNetworking();
            com.kingodogo.buildscape.event.FabricGameplayEvents.register();
        });
    }

    private static void useRecipeManager(net.minecraft.server.MinecraftServer server,
            net.minecraft.world.item.crafting.RecipeManager manager) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(manager);
        // The server finalized the manager before this point; rebuild the recipe-book, property and
        // stonecutter tables so they include the injected recipes.
        manager.finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    }

    private static void registerNetworking() {
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(Identifier.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            var codec = BuildscapeCustomPayload.codec(type);
            if (descriptor.direction() == PacketDirection.CLIENT_TO_SERVER) {
                PayloadTypeRegistry.serverboundPlay().register(type, codec);
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                        com.kingodogo.buildscape.network.PacketFactory.handleServerbound(
                                descriptor.id(), payload.data(), context.player()));
            } else {
                PayloadTypeRegistry.clientboundPlay().register(type, codec);
            }
        }
    }
}
