package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v26x.GuiProvider;
import com.kingodogo.buildscape.adapter.v26x.ParticleFactory;
import com.kingodogo.buildscape.adapter.v26x.RenderFactory;
import com.kingodogo.buildscape.menu.ModMenuTypes;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import java.util.function.Function;

final class BuildscapeNeoForgeClient {
    private BuildscapeNeoForgeClient() {}
    static void register(IEventBus bus) {
        net.neoforged.fml.ModLoadingContext.get().registerExtensionPoint(
                net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                () -> (container, parent) -> com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.createConfigScreen(parent));
        bus.addListener(BuildscapeNeoForgeClient::keys);
        bus.addListener(BuildscapeNeoForgeClient::fluidModels);
        bus.addListener(BuildscapeNeoForgeClient::fluidExtensions);
        bus.addListener(BuildscapeNeoForgeClient::setup);
        bus.addListener(BuildscapeNeoForgeClient::screens);
        bus.addListener(BuildscapeNeoForgeClient::particles);
        bus.addListener(BuildscapeNeoForgeClient::renderers);
        bus.addListener(BuildscapeNeoForgeClient::specialModels);
        bus.addListener(BuildscapeNeoForgeClient::guiLayers);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::tick);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::worldOverlays);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::keyInput);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::mouseInput);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::scroll);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::interaction);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::highlight);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::fov);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::camera);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::disconnect);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::unload);
    }
    private static void keys(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
        com.kingodogo.buildscape.client.ModKeyBinds.register(event::register);
    }
    private static void keyInput(net.neoforged.neoforge.client.event.InputEvent.Key event) {
        com.kingodogo.buildscape.client.ClientEvents.onKeyInput(event.getKey(), event.getAction());
    }
    private static void mouseInput(net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post event) {
        if (event.getButton() == 1 && event.getAction() == 1) com.kingodogo.buildscape.client.ClientEvents.onRightClick();
    }
    private static void scroll(net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent event) {
        if (!com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isScreenOpen()
                && com.kingodogo.buildscape.client.ZoomHandler.isZooming()) {
            com.kingodogo.buildscape.client.ZoomHandler.handleScroll(event.getScrollDeltaY());
            event.setCanceled(true);
        }
    }
    private static void interaction(net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered event) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (event.isAttack()) com.kingodogo.buildscape.client.BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
        if (event.isAttack() && !com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.isScreenOpen()
                && com.kingodogo.buildscape.client.TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
            event.setCanceled(true);
            event.setSwingHand(true);
        }
    }
    private static void highlight(net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent event) {
        if (com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.extractBlockHighlight(
                event.getCamera(), event.getHitResult(), event.getLevelRenderState())) event.setCanceled(true);
    }
    private static void fov(net.neoforged.neoforge.client.event.ViewportEvent.ComputeFov event) {
        event.setFOV(event.getFOV() * com.kingodogo.buildscape.client.ZoomHandler.getZoomLevel());
    }
    private static void camera(net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles event) {
        float[] rotation = com.kingodogo.buildscape.client.ZoomHandler.getSmoothedRotation(event.getYaw(), event.getPitch());
        event.setYaw(rotation[0]);
        event.setPitch(rotation[1]);
    }
    private static void disconnect(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        com.kingodogo.buildscape.client.ClientEvents.onClientDisconnect();
        com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
    }
    private static void unload(net.neoforged.neoforge.event.level.LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            com.kingodogo.buildscape.client.ClientEvents.onClientWorldUnload();
            com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
        }
    }
    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(com.kingodogo.buildscape.client.ClientEvents::initializeConfigCallback);
    }
    private static void fluidModels(net.neoforged.neoforge.client.event.RegisterFluidModelsEvent event) {
        event.register(com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidModel.create(),
                com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.still(),
                com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.flowing());
    }
    private static void fluidExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            @Override public void modifyFogColor(net.minecraft.client.Camera camera, float partialTick,
                    net.minecraft.client.multiplayer.ClientLevel level, int renderDistance, float darken,
                    org.joml.Vector4f color) {
                color.set(0.3F, 0.9F, 0.1F, 1.0F);
            }
        }, com.kingodogo.buildscape.fluid.NeoForgeExperienceFluids.type());
    }
    private static void tick(ClientTickEvent.Post event) { com.kingodogo.buildscape.client.ClientEvents.onClientTick(); }
    private static void screens(RegisterMenuScreensEvent event) {
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            public void registerWorkbench() { event.register(ModMenuTypes.BUILDERS_WORKBENCH_MENU, GuiProvider::createWorkbenchScreen); }
            public void registerPouch() { event.register(ModMenuTypes.BUILDERS_POUCH_MENU, GuiProvider::createPouchScreen); }
        });
    }
    private static void particles(RegisterParticleProvidersEvent event) {
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory) {
                event.registerSpriteSet(type, factory::apply);
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
    private static void specialModels(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {
        com.kingodogo.buildscape.adapter.v26x.client.SpecialItemRenderers.register(event::register);
    }
    private static void guiLayers(net.neoforged.neoforge.client.event.RegisterGuiLayersEvent event) {
        event.registerAboveAll(net.minecraft.resources.Identifier.fromNamespaceAndPath("buildscape", "overlay"),
                (graphics, delta) -> com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderClientOverlay(
                        graphics, graphics.guiWidth(), graphics.guiHeight()));
    }
    private static void worldOverlays(net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent event) {
        com.kingodogo.buildscape.adapter.v26x.client.ClientWorldHooks.collectWorldOverlays(
                event.getPoseStack(), event.getSubmitNodeCollector(), event.getLevelRenderState());
        com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.collectBlockHighlight(
                event.getPoseStack(), event.getSubmitNodeCollector(), event.getLevelRenderState());
    }
}
