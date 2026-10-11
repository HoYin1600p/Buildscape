package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v121x.client.GuiProvider;
import com.kingodogo.buildscape.adapter.v121x.client.ParticleFactory;
import com.kingodogo.buildscape.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** Client-only event bindings; never loaded by the dedicated server. */
public final class BuildscapeNeoForgeClient {
    private static RegisterMenuScreensEvent menuEvent;
    private static RegisterParticleProvidersEvent particleEvent;
    private BuildscapeNeoForgeClient() {}

    public static void register(IEventBus bus) {
        bus.addListener((RegisterMenuScreensEvent event) -> {
            menuEvent = event;
            try { Services.PLATFORM.registerMenuScreens(); }
            finally { menuEvent = null; }
        });
        bus.addListener((RegisterParticleProvidersEvent event) -> {
            particleEvent = event;
            try { Services.PLATFORM.registerParticleProviders(); }
            finally { particleEvent = null; }
        });
        bus.addListener((net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) ->
                Services.PLATFORM.registerLayerDefinitions((layer, supplier) -> event.registerLayerDefinition(
                        (net.minecraft.client.model.geom.ModelLayerLocation) layer,
                        () -> (net.minecraft.client.model.geom.builders.LayerDefinition) supplier.get())));
        bus.addListener((net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) ->
                event.enqueueWork(com.kingodogo.buildscape.client.ClientEvents::initializeConfigCallback));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.client.event.ClientTickEvent.Post event) ->
                com.kingodogo.buildscape.client.ClientEvents.onClientTick());
    }

    public static void registerMenuScreens() {
        if (menuEvent == null) throw new IllegalStateException("Menu screens require their registration event");
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            public <M extends net.minecraft.world.inventory.AbstractContainerMenu> void register(
                    net.minecraft.world.inventory.MenuType<M> type, GuiProvider.ScreenFactory<M> factory) {
                menuEvent.register(type, factory::create);
            }
        });
    }

    public static void registerParticleProviders() {
        if (particleEvent == null) throw new IllegalStateException("Particle providers require their registration event");
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends net.minecraft.core.particles.ParticleOptions> void register(
                    net.minecraft.core.particles.ParticleType<T> type,
                    java.util.function.Function<net.minecraft.client.particle.SpriteSet, net.minecraft.client.particle.ParticleProvider<T>> factory) {
                particleEvent.registerSpriteSet(type, factory::apply);
            }
            public <T extends net.minecraft.core.particles.ParticleOptions> void registerDirect(
                    net.minecraft.core.particles.ParticleType<T> type, net.minecraft.client.particle.ParticleProvider<T> provider) {
                particleEvent.registerSpecial(type, provider);
            }
        });
    }
}
