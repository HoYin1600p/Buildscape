package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase;
import com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload;
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
            BuildscapeCommon.init();
            com.kingodogo.buildscape.worldgen.ModBiomeModifications.register();
            Services.PLATFORM.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
            ((PlatformAdapterBase) Services.PLATFORM).registerExperienceCauldronInteractions(
                    net.minecraft.core.cauldron.BuildscapeCauldronRegistration::register);
            ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(
                    Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes"),
                    new net.minecraft.server.packs.resources.PreparableReloadListener() {
                        private final net.minecraft.server.packs.resources.PreparableReloadListener delegate =
                                Services.PLATFORM.createRecipeReloadListener();
                        @Override
                        public java.util.concurrent.CompletableFuture<Void> reload(SharedState state,
                                java.util.concurrent.Executor prepareExecutor, PreparationBarrier barrier,
                                java.util.concurrent.Executor applyExecutor) {
                            BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(state.get(
                                    net.fabricmc.fabric.api.resource.v1.DataResourceLoader.RECIPE_MANAGER_KEY));
                            return delegate.reload(state, prepareExecutor, barrier, applyExecutor);
                        }
                    });
            ResourceLoader.get(PackType.SERVER_DATA).addListenerOrdering(
                    net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys.Server.RECIPES,
                    Identifier.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes"));
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(server.getRecipeManager());
                com.kingodogo.buildscape.adapter.v26x.PacketFactory.setServer(server);
            });
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
                BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
                com.kingodogo.buildscape.adapter.v26x.PacketFactory.setServer(null);
                BuildscapeCommon.setServerFullyInitialized(false);
            });
            ServerLifecycleEvents.SERVER_STARTED.register(server -> BuildscapeCommon.setServerFullyInitialized(true));
            registerNetworking();
            com.kingodogo.buildscape.event.FabricGameplayEvents.register();
        });
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
