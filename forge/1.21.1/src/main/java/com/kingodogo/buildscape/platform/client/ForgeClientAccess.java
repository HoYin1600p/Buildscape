package com.kingodogo.buildscape.platform.client;

import com.kingodogo.buildscape.adapter.v121x.client.GuiProvider;
import com.kingodogo.buildscape.adapter.v121x.client.ParticleFactory;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Only the required platform hooks live here; Forge client features are a separate port. */
@Mod.EventBusSubscriber(modid = "buildscape", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ForgeClientAccess {
    private static RegisterParticleProvidersEvent particleEvent;
    private ForgeClientAccess() {}

    public static NativeImage spriteImage(SpriteContents contents) {
        return contents.originalImage;
    }

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        particleEvent = event;
        try { Services.PLATFORM.registerParticleProviders(); }
        finally { particleEvent = null; }
    }

    public static void registerParticleProviders() {
        if (particleEvent == null) throw new IllegalStateException("Particle registration outside Forge event");
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            @Override public <T extends ParticleOptions> void register(ParticleType<T> type,
                    java.util.function.Function<SpriteSet, ParticleProvider<T>> factory) {
                particleEvent.registerSpriteSet(type, factory::apply);
            }
            @Override public <T extends ParticleOptions> void registerDirect(ParticleType<T> type,
                    ParticleProvider<T> provider) {
                particleEvent.registerSpecial(type, provider);
            }
        });
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(Services.PLATFORM::registerMenuScreens);
    }

    public static void registerMenuScreens() {
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            @Override public <M extends AbstractContainerMenu> void register(MenuType<M> type,
                    GuiProvider.ScreenFactory<M> factory) {
                MenuScreens.register(type, factory::create);
            }
        });
    }
}