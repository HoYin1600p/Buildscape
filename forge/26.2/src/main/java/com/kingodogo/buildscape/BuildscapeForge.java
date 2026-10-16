package com.kingodogo.buildscape;

import com.kingodogo.buildscape.event.ForgeGameplayEvents;
import com.kingodogo.buildscape.event.LoaderDispenserSetup;
import com.kingodogo.buildscape.network.ForgePacketFactory;
import com.kingodogo.buildscape.platform.ForgeBlockEntityData;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import com.kingodogo.buildscape.registry.ForgeRegistryAdapter;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(BuildscapeCommon.MOD_ID)
public final class BuildscapeForge {
    public BuildscapeForge(FMLJavaModLoadingContext context) {
        var group = context.getModBusGroup();
        com.kingodogo.buildscape.data.PersistentData.install(owner -> {
            if (owner instanceof net.minecraft.world.entity.Entity entity) return entity.getPersistentData();
            if (owner instanceof ForgeBlockEntityData blockEntity) return blockEntity.buildscape$getPersistentData();
            throw new IllegalArgumentException("Unsupported persistent data owner: " + owner.getClass().getName());
        });
        Services.PLATFORM.wrapRegistryAction(BuildscapeCommon::prepareRegistration);
        RegisterEvent.getBus(group).addListener(((ForgeRegistryAdapter) Services.REGISTRY)::onRegister);
        ((ForgePacketFactory) Services.PACKET_FACTORY).installChannel();
        FMLCommonSetupEvent.getBus(group).addListener(BuildscapeForge::commonSetup);
        EntityAttributeCreationEvent.BUS.addListener(event -> Services.PLATFORM.registerEntityAttributes(event::put));
        AddReloadListenerEvent.BUS.addListener(BuildscapeForge::addReloadListeners);
        ServerStartedEvent.BUS.addListener(BuildscapeForge::serverStarted);
        ServerStoppedEvent.BUS.addListener(BuildscapeForge::serverStopped);
        ForgeGameplayEvents.register();
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient()) BuildscapeForgeClient.register(group);
    }

    private static void commonSetup(FMLCommonSetupEvent event) { event.enqueueWork(BuildscapeForge::init); }
    public static void init() {
        BuildscapeCommon.init();
        LoaderDispenserSetup.register();
        ((com.kingodogo.buildscape.adapter.v26x.PlatformAdapterBase) Services.PLATFORM)
                .registerExperienceCauldronInteractions(
                        ((com.kingodogo.buildscape.mixin.forge262.CauldronDispatcherInvoker) (Object)
                                net.minecraft.core.cauldron.CauldronInteractions.EMPTY)::buildscape$put);
    }
    private static void addReloadListeners(AddReloadListenerEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(event.getServerResources().getRecipeManager());
        event.addListener(Services.PLATFORM.createRecipeReloadListener());
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
}
