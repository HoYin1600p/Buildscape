package com.kingodogo.buildscape.platform.client;

import com.kingodogo.buildscape.adapter.v121x.client.GuiProvider;
import com.kingodogo.buildscape.adapter.v121x.client.ParticleFactory;
import com.kingodogo.buildscape.adapter.v121x.client.RenderFactory;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/** Loaded only from client entrypoints or guarded platform hooks. */
public final class FabricClientAccess {
    private FabricClientAccess() {}
    public static com.mojang.blaze3d.platform.NativeImage spriteImage(net.minecraft.client.renderer.texture.SpriteContents contents) {
        return contents.originalImage;
    }
    public static void registerMenuScreens() {
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            public <M extends net.minecraft.world.inventory.AbstractContainerMenu> void register(
                    net.minecraft.world.inventory.MenuType<M> type, GuiProvider.ScreenFactory<M> factory) {
                net.minecraft.client.gui.screens.MenuScreens.register(type, factory::create);
            }
        });
    }
    public static void registerParticleProviders() {
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type,
                    java.util.function.Function<SpriteSet, ParticleProvider<T>> factory) {
                ParticleFactoryRegistry.getInstance().register(type, sprites -> factory.apply(sprites));
            }
            public <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
                ParticleFactoryRegistry.getInstance().register(type, provider);
            }
        });
    }
    public static void registerRenderers() {
        RenderFactory.registerRenderers(new RenderFactory.Registrar() {
            public <T extends net.minecraft.world.level.block.entity.BlockEntity> void registerBlockEntity(
                    net.minecraft.world.level.block.entity.BlockEntityType<T> type,
                    net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<T> provider) {
                net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(type, provider);
            }
            public <T extends net.minecraft.world.entity.Entity> void registerEntity(net.minecraft.world.entity.EntityType<T> type,
                    net.minecraft.client.renderer.entity.EntityRendererProvider<T> provider) {
                net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(type, provider);
            }
        });
    }
}
