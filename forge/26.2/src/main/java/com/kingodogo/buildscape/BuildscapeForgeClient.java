package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v26x.GuiProvider;
import com.kingodogo.buildscape.adapter.v26x.ParticleFactory;
import com.kingodogo.buildscape.adapter.v26x.RenderFactory;
import com.kingodogo.buildscape.menu.ModMenuTypes;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

import java.util.function.Function;

final class BuildscapeForgeClient {
    private BuildscapeForgeClient() {}
    static void register(BusGroup group) {
        net.minecraftforge.common.MinecraftForge.registerConfigScreen(
                com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks::createConfigScreen);
        com.kingodogo.buildscape.adapter.v26x.client.SpecialItemRenderers.register(
                com.kingodogo.buildscape.mixin.forge262.SpecialModelRenderersAccessor.buildscape$getMapper()::put);
        net.minecraftforge.client.event.RegisterKeyMappingsEvent.BUS.addListener(BuildscapeForgeClient::keys);
        FMLClientSetupEvent.getBus(group).addListener(BuildscapeForgeClient::setup);
        RegisterParticleProvidersEvent.BUS.addListener(BuildscapeForgeClient::particles);
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(BuildscapeForgeClient::renderers);
        net.minecraftforge.client.event.AddGuiOverlayLayersEvent.BUS.addListener(BuildscapeForgeClient::guiLayers);
        net.minecraftforge.client.event.RegisterColorHandlersEvent.Block.BUS.addListener(BuildscapeForgeClient::blockColors);
        net.minecraftforge.client.event.ModelEvent.ModifyBakingResult.BUS.addListener(BuildscapeForgeClient::itemColors);
        net.minecraftforge.client.event.ModelEvent.BakeFluidModels.BUS.addListener(BuildscapeForgeClient::fluidModels);
        net.minecraftforge.client.event.ViewportEvent.ComputeFogColor.BUS.addListener(BuildscapeForgeClient::fogColor);
        net.minecraftforge.client.event.ViewportEvent.RenderFog.BUS.addListener(BuildscapeForgeClient::fogDistances);
        ClientTickEvent.Post.BUS.addListener(BuildscapeForgeClient::tick);
        net.minecraftforge.client.event.InputEvent.Key.BUS.addListener(BuildscapeForgeClient::keyInput);
        net.minecraftforge.client.event.InputEvent.MouseButton.Post.BUS.addListener(BuildscapeForgeClient::mouseInput);
        net.minecraftforge.client.event.InputEvent.MouseScrollingEvent.BUS.addListener(BuildscapeForgeClient::scroll);
        net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered.BUS.addListener(BuildscapeForgeClient::interaction);
        net.minecraftforge.client.event.RenderHighlightEvent.Block.BUS.addListener(BuildscapeForgeClient::highlight);
        net.minecraftforge.client.event.ViewportEvent.ComputeFov.BUS.addListener(BuildscapeForgeClient::fov);
        net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles.BUS.addListener(BuildscapeForgeClient::camera);
        net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut.BUS.addListener(BuildscapeForgeClient::disconnect);
        net.minecraftforge.event.level.LevelEvent.Unload.BUS.addListener(BuildscapeForgeClient::unload);
    }
    private static void blockColors(net.minecraftforge.client.event.RegisterColorHandlersEvent.Block event) {
        com.kingodogo.buildscape.adapter.v26x.client.FoliageColors.register(event.getBlockColors());
    }
    private static void fluidModels(net.minecraftforge.client.event.ModelEvent.BakeFluidModels event) {
        var model = com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidModel.create()
                .bake(event.materials(), () -> "buildscape:experience");
        event.register(com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.still(), model);
        event.register(com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.flowing(), model);
    }
    private static void fogColor(net.minecraftforge.client.event.ViewportEvent.ComputeFogColor event) {
        if (com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidFog.isExperienceFluid(
                event.getCamera(), net.minecraft.client.Minecraft.getInstance().level)) {
            event.setRed(0.3F);
            event.setGreen(0.9F);
            event.setBlue(0.1F);
        }
    }
    private static boolean fogDistances(net.minecraftforge.client.event.ViewportEvent.RenderFog event) {
        if (com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidFog.isExperienceFluid(
                event.getCamera(), net.minecraft.client.Minecraft.getInstance().level)) {
            com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidFog.distances(event.getData());
            event.setNearPlaneDistance(0.5F);
            event.setFarPlaneDistance(12.0F);
            return true;
        }
        return false;
    }
    private static void itemColors(net.minecraftforge.client.event.ModelEvent.ModifyBakingResult event) {
        event.getResults().itemStackModels().replaceAll(
                com.kingodogo.buildscape.adapter.v26x.client.FoliageColors::tintItem);
    }
    private static void keys(net.minecraftforge.client.event.RegisterKeyMappingsEvent event) {
        com.kingodogo.buildscape.client.ModKeyBinds.register(event::register);
    }
    private static void keyInput(net.minecraftforge.client.event.InputEvent.Key event) {
        com.kingodogo.buildscape.client.ClientEvents.onKeyInput(event.getKey(), event.getAction());
    }
    private static void mouseInput(net.minecraftforge.client.event.InputEvent.MouseButton.Post event) {
        if (event.getButton() == 1 && event.getAction() == 1) com.kingodogo.buildscape.client.ClientEvents.onRightClick();
    }
    private static boolean scroll(net.minecraftforge.client.event.InputEvent.MouseScrollingEvent event) {
        if (!com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isScreenOpen()
                && com.kingodogo.buildscape.client.ZoomHandler.isZooming()) {
            com.kingodogo.buildscape.client.ZoomHandler.handleScroll(event.getDeltaY());
            return true;
        }
        return false;
    }
    private static boolean interaction(net.minecraftforge.client.event.InputEvent.InteractionKeyMappingTriggered event) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (event.isAttack()) com.kingodogo.buildscape.client.BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
        if (event.isAttack() && !com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isScreenOpen()
                && com.kingodogo.buildscape.client.TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            event.setSwingHand(true);
            return true;
        }
        return false;
    }
    private static void highlight(net.minecraftforge.client.event.RenderHighlightEvent.Block event) {
        if (com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.extractBlockHighlight(
                event.getCamera(), event.getTarget(), event.getLevelRenderState())) {
            event.setCustomRenderer((collector, pose, state) ->
                    com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.collectBlockHighlight(pose, collector, state));
        }
    }
    private static void fov(net.minecraftforge.client.event.ViewportEvent.ComputeFov event) {
        event.setFOV(event.getFOV() * com.kingodogo.buildscape.client.ZoomHandler.getZoomLevel());
    }
    private static void camera(net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        float[] rotation = com.kingodogo.buildscape.client.ZoomHandler.getSmoothedRotation(event.getYaw(), event.getPitch());
        event.setYaw(rotation[0]);
        event.setPitch(rotation[1]);
    }
    private static void disconnect(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        com.kingodogo.buildscape.client.ClientEvents.onClientDisconnect();
        com.kingodogo.buildscape.adapter.v26x.client.HazeBushParticleHandler.clear();
        com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
    }
    private static void unload(net.minecraftforge.event.level.LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            com.kingodogo.buildscape.client.ClientEvents.onClientWorldUnload();
            com.kingodogo.buildscape.adapter.v26x.client.HazeBushParticleHandler.clear();
            com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
        }
    }
    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
            screens();
        });
    }
    private static void tick(ClientTickEvent.Post event) {
        com.kingodogo.buildscape.client.ClientEvents.onClientTick();
        com.kingodogo.buildscape.adapter.v26x.client.HazeBushParticleHandler.clientTick();
    }
    private static void screens() {
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            public void registerWorkbench() { net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.BUILDERS_WORKBENCH_MENU, GuiProvider::createWorkbenchScreen); }
            public void registerPouch() { net.minecraft.client.gui.screens.MenuScreens.register(ModMenuTypes.BUILDERS_POUCH_MENU, GuiProvider::createPouchScreen); }
        });
    }
    private static void particles(RegisterParticleProvidersEvent event) {
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory) {
                // Replace the age-based confetti provider with the reference's random shapes.
                Function<SpriteSet, ParticleProvider<T>> selected = type.equals(com.kingodogo.buildscape.particle.ModParticles.CONFETTI.get())
                        ? com.kingodogo.buildscape.adapter.v26x.client.ConfettiParticle::provider : factory;
                event.registerSpriteSet(type, selected::apply);
            }
            public <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
                event.registerSpecial(type, provider);
            }
        });
    }
    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        RenderFactory.registerRenderers(event::registerEntityRenderer);
        RenderFactory.registerBlockEntityRenderers(event::registerBlockEntityRenderer);
    }
    private static void guiLayers(net.minecraftforge.client.event.AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(net.minecraftforge.client.gui.overlay.ForgeLayeredDraw.POST_SLEEP_STACK,net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", "overlay"),
                (graphics, delta) -> com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderClientOverlay(
                        graphics, graphics.guiWidth(), graphics.guiHeight()));
    }
}

