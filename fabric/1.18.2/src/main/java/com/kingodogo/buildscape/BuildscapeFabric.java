package com.kingodogo.buildscape;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.network.FabricPacketFactory;
import com.kingodogo.buildscape.adapter.v118x.PacketFactory;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class BuildscapeFabric implements ModInitializer {
    private static final com.kingodogo.buildscape.registry.StartupOnce STARTUP = new com.kingodogo.buildscape.registry.StartupOnce();
    @Override public void onInitialize() { init(); }

    public static void init() {
        STARTUP.run(() -> {
            // Reserve the final slot before shared v118x code constructs its tab.
            ((net.fabricmc.fabric.impl.item.group.ItemGroupExtensions)
                    net.minecraft.world.item.CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
            Services.PLATFORM.wrapRegistryAction(com.kingodogo.buildscape.fluid.FabricExperienceFluid::register);
            Services.PLATFORM.wrapRegistryAction(BuildscapeCommon::init);
            Services.PLATFORM.wrapRegistryAction(() -> {
                com.kingodogo.buildscape.block.CopperRodHandler.registerPoi(
                        com.kingodogo.buildscape.mixin.fabric.PoiMapAccessor.buildscape$getTypes());
                ((com.kingodogo.buildscape.mixin.fabric.ItemStackSizeAccessor)net.minecraft.world.item.Items.POTION).buildscape$setMaxStackSize(16);
                ((com.kingodogo.buildscape.mixin.fabric.ItemStackSizeAccessor)net.minecraft.world.item.Items.CAKE).buildscape$setMaxStackSize(64);
            });
            com.kingodogo.buildscape.event.FabricGameplayEvents.register();
            com.kingodogo.buildscape.event.LoaderDispenserSetup.register();
            Services.PLATFORM.registerEntityAttributes((type, attributes) ->
                    FabricDefaultAttributeRegistry.register(type, new net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder() {
                        @Override public net.minecraft.world.entity.ai.attributes.AttributeSupplier build() {
                            return attributes;
                        }
                    }));
            ((FabricPacketFactory) Services.PACKET_FACTORY).installReceivers();
            ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
                private final PreparableReloadListener delegate = Services.PLATFORM.createRecipeReloadListener();
                @Override public ResourceLocation getFabricId() { return new ResourceLocation("buildscape", "dynamic_recipes"); }
                @Override public java.util.Collection<ResourceLocation> getFabricDependencies() {
                    return java.util.List.of(new ResourceLocation("minecraft", "recipes"));
                }
                @Override public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                        ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler, Executor prepareExecutor, Executor applyExecutor) {
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
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> com.kingodogo.buildscape.event.ModCommonEvents.onServerStopping());
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
}
