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
    public BuildscapeForge(FMLJavaModLoadingContext context) {
        var modBus = context.getModEventBus();
        com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.installFactories(
                () -> new com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.Source() {
                    @Override public boolean canConvertToSource(net.minecraft.world.level.material.FluidState state,
                            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return false; }
                    @Override public net.minecraftforge.fluids.FluidType getFluidType() {
                        return net.minecraftforge.common.ForgeMod.WATER_TYPE.get();
                    }
                },
                () -> new com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.Flowing() {
                    @Override public boolean canConvertToSource(net.minecraft.world.level.material.FluidState state,
                            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) { return false; }
                    @Override public net.minecraftforge.fluids.FluidType getFluidType() {
                        return net.minecraftforge.common.ForgeMod.WATER_TYPE.get();
                    }
                });
        Services.PLATFORM.wrapRegistryAction(BuildscapeCommon::prepareRegistration);
        modBus.addListener(((ForgeRegistryAdapter) Services.REGISTRY)::onRegister);
        ((ForgePacketFactory) Services.PACKET_FACTORY).installChannel();
        modBus.addListener(BuildscapeForge::commonSetup);
        modBus.addListener(BuildscapeForge::registerAttributes);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::addReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::serverStarted);
        MinecraftForge.EVENT_BUS.addListener(BuildscapeForge::serverStopped);
        ForgeGameplayEvents.register();
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            BuildscapeForgeClient.register(modBus);
        }
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BuildscapeForge::init);
    }

    public static void init() {
        BuildscapeCommon.init();
        LoaderDispenserSetup.register();
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        Services.PLATFORM.registerEntityAttributes(event::put);
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(event.getServerResources().getRecipeManager());
        event.addListener(Services.PLATFORM.createRecipeReloadListener());
    }

    private static void serverStarted(ServerStartedEvent event) {
        com.kingodogo.buildscape.adapter.v121x.PacketFactory.setServer(event.getServer());
        BuildscapeCommon.setServerFullyInitialized(true);
    }

    private static void serverStopped(ServerStoppedEvent event) {
        BuildScapeRecipeLoader.INSTANCE.setCurrentRecipeManager(null);
        com.kingodogo.buildscape.adapter.v121x.PacketFactory.setServer(null);
        BuildscapeCommon.setServerFullyInitialized(false);
    }
}
