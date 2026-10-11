package com.kingodogo.buildscape;

import com.kingodogo.buildscape.event.ForgeGameplayEvents;
import com.kingodogo.buildscape.event.LoaderDispenserSetup;
import com.kingodogo.buildscape.network.ForgePacketFactory;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.recipe.framework.BuildScapeRecipeLoader;
import com.kingodogo.buildscape.registry.ForgeRegistryAdapter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BuildscapeCommon.MOD_ID)
public final class BuildscapeForge {
    public BuildscapeForge() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ((ForgeRegistryAdapter) Services.REGISTRY).attach(modBus);
        com.kingodogo.buildscape.adapter.v118x.BlockFactory.installExperienceFluidFactory(
                com.kingodogo.buildscape.fluid.ForgeExperienceFluidBlock::new);
        Services.PLATFORM.wrapRegistryAction(BuildscapeCommon::prepareRegistration);
        ((ForgePacketFactory) Services.PACKET_FACTORY).installChannel();
        modBus.addListener(BuildscapeForge::commonSetup);
        modBus.addListener(BuildscapeForge::registerAttributes);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::addReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::serverStopped);
        ForgeGameplayEvents.register();
        // BuildscapeForgeClient is discovered through a Dist.CLIENT-only mod-bus subscriber.
    }

    public static void init() {
        BuildscapeCommon.init();
        LoaderDispenserSetup.register();
        Services.PLATFORM.wrapRegistryAction(() -> {
            com.kingodogo.buildscape.block.CopperRodHandler.registerPoi(
                    net.minecraftforge.registries.GameData.getBlockStatePointOfInterestTypeMap());
            net.minecraftforge.fml.util.ObfuscationReflectionHelper.setPrivateValue(
                    net.minecraft.world.item.Item.class, net.minecraft.world.item.Items.POTION, 16, "f_41370_");
            net.minecraftforge.fml.util.ObfuscationReflectionHelper.setPrivateValue(
                    net.minecraft.world.item.Item.class, net.minecraft.world.item.Items.CAKE, 64, "f_41370_");
            java.util.Map<net.minecraft.sounds.SoundEvent, net.minecraft.world.item.RecordItem> records =
                    net.minecraftforge.fml.util.ObfuscationReflectionHelper.getPrivateValue(
                            net.minecraft.world.item.RecordItem.class, null, "f_43032_");
            if (records != null) {
                var registry = (ForgeRegistryAdapter) Services.REGISTRY;
                for (String path : java.util.List.of("music_disc_celebration", "music_disc_snowfall", "music_disc_builder")) {
                    var item = registry.getModItems().get(path);
                    var sound = registry.getModSounds().get(path);
                    if (item instanceof net.minecraft.world.item.RecordItem record && sound != null) records.put(sound, record);
                }
            }
        });
        com.kingodogo.buildscape.config.PillarParticleConfig.addConfigReloadCallback(remote -> {
            var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (!remote && server != null && server.isRunning()) {
                server.execute(() -> com.kingodogo.buildscape.network.PacketFactory.sendToAll(
                        new com.kingodogo.buildscape.network.SyncConfigPacket(
                                com.kingodogo.buildscape.config.PillarParticleConfig.get())));
            }
        });
    }

    private static void commonSetup(FMLCommonSetupEvent event) { event.enqueueWork(BuildscapeForge::init); }
    private static void registerAttributes(EntityAttributeCreationEvent event) {
        Services.PLATFORM.registerEntityAttributes(event::put);
    }
    private static void addReloadListeners(AddReloadListenerEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(event.getServerResources().getRecipeManager());
        event.addListener(Services.PLATFORM.createRecipeReloadListener());
    }
    private static void serverStarted(ServerStartedEvent event) {
        com.kingodogo.buildscape.adapter.v118x.PacketFactory.setServer(event.getServer());
        BuildscapeCommon.setServerFullyInitialized(true);
    }
    private static void serverStopped(ServerStoppedEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
        com.kingodogo.buildscape.adapter.v118x.PacketFactory.setServer(null);
        BuildscapeCommon.setServerFullyInitialized(false);
    }
}
