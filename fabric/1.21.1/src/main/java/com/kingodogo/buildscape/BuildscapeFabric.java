package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v121x.PacketFactory;
import com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class BuildscapeFabric implements ModInitializer {
    private static final com.kingodogo.buildscape.registry.StartupOnce STARTUP =
            new com.kingodogo.buildscape.registry.StartupOnce();

    @Override public void onInitialize() { init(); }

    public static void init() {
        STARTUP.run(() -> {
            Services.PLATFORM.wrapRegistryAction(BuildscapeCommon::init);
            com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
            com.kingodogo.buildscape.event.FabricGameplayEvents.register();
            com.kingodogo.buildscape.worldgen.ModBiomeModifications.register();
            Services.PLATFORM.registerEntityAttributes(FabricDefaultAttributeRegistry::register);
            registerNetworking();
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
                private final PreparableReloadListener delegate = Services.PLATFORM.createRecipeReloadListener();
                @Override public ResourceLocation getFabricId() {
                    return ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "dynamic_recipes");
                }
                @Override public java.util.Collection<ResourceLocation> getFabricDependencies() {
                    return java.util.List.of(ResourceLocation.withDefaultNamespace("recipes"));
                }
                @Override public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                        ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler,
                        Executor prepareExecutor, Executor applyExecutor) {
                    return delegate.reload(barrier, manager, preparationProfiler, reloadProfiler, prepareExecutor, applyExecutor);
                }
            });
            ServerLifecycleEvents.SERVER_STARTING.register(server -> {
                PacketFactory.setServer(server);
                BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(server.getRecipeManager());
            });
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                BuildscapeCommon.setServerFullyInitialized(true);
                com.kingodogo.buildscape.event.ModCommonEvents.onServerStarted(server);
            });
            ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                    com.kingodogo.buildscape.event.ModCommonEvents.onServerStopping());
            ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
                BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
                PacketFactory.setServer(null);
                BuildscapeCommon.setServerFullyInitialized(false);
            });
            net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
                com.kingodogo.buildscape.event.ModCommonEvents.onServerTick(server);
                com.kingodogo.buildscape.world.ModGameRules.syncIfChanged(server.overworld());
            });
            com.kingodogo.buildscape.config.PillarParticleConfig.addConfigReloadCallback(remote -> {
                var server = PacketFactory.getServer();
                if (!remote && server != null && server.isRunning()) server.execute(() ->
                        com.kingodogo.buildscape.network.PacketFactory.sendToAll(new com.kingodogo.buildscape.network.SyncConfigPacket(
                                com.kingodogo.buildscape.config.PillarParticleConfig.get())));
            });
        });
    }

    private static void registerNetworking() {
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(ResourceLocation.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            var codec = BuildscapeCustomPayload.codec(type);
            if (descriptor.direction() == PacketDirection.CLIENT_TO_SERVER) {
                PayloadTypeRegistry.playC2S().register(type, codec);
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                        com.kingodogo.buildscape.network.PacketFactory.handleServerbound(
                                descriptor.id(), payload.data(), context.player()));
            } else {
                PayloadTypeRegistry.playS2C().register(type, codec);
            }
        }
    }
}
