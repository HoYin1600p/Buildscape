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
        bus.addListener(BuildscapeNeoForgeClient::setup);
        bus.addListener(BuildscapeNeoForgeClient::screens);
        bus.addListener(BuildscapeNeoForgeClient::particles);
        bus.addListener(BuildscapeNeoForgeClient::renderers);
        bus.addListener(BuildscapeNeoForgeClient::specialModels);
        bus.addListener(BuildscapeNeoForgeClient::guiLayers);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::tick);
        NeoForge.EVENT_BUS.addListener(BuildscapeNeoForgeClient::worldOverlays);
    }
    private static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(com.kingodogo.buildscape.client.ClientEvents::initializeConfigCallback);
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
    }
}
