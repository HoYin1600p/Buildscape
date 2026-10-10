package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v26x.GuiProvider;
import com.kingodogo.buildscape.adapter.v26x.ParticleFactory;
import com.kingodogo.buildscape.adapter.v26x.RenderFactory;
import com.kingodogo.buildscape.adapter.v26x.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.menu.ModMenuTypes;
import com.kingodogo.buildscape.network.PacketDirection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import java.util.function.Function;

public class BuildscapeFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        com.kingodogo.buildscape.adapter.v26x.client.SpecialItemRenderers.registerFabric();
        GuiProvider.registerMenuScreens(new GuiProvider.Registrar() {
            public void registerWorkbench() {
                MenuScreens.register(ModMenuTypes.BUILDERS_WORKBENCH_MENU, GuiProvider::createWorkbenchScreen);
            }
            public void registerPouch() {
                MenuScreens.register(ModMenuTypes.BUILDERS_POUCH_MENU, GuiProvider::createPouchScreen);
            }
        });
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory) {
                ParticleProviderRegistry.getInstance().register(type,
                        (ParticleProviderRegistry.PendingParticleProvider<T>) factory::apply);
            }
            public <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
                ParticleProviderRegistry.getInstance().register(type, provider);
            }
        });
        RenderFactory.registerRenderers(EntityRendererRegistry::register);
        RenderFactory.registerBlockEntityRenderers(net.minecraft.client.renderer.blockentity.BlockEntityRenderers::register);
        net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath("buildscape", "overlay"), (graphics, delta) ->
                        com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.renderClientOverlay(
                                graphics, graphics.guiWidth(), graphics.guiHeight()));
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context ->
                com.kingodogo.buildscape.adapter.v26x.client.ClientWorldHooks.collectWorldOverlays(
                        context.poseStack(), context.submitNodeCollector(), context.levelState()));
        com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
        ClientTickEvents.END_CLIENT_TICK.register(client -> com.kingodogo.buildscape.client.ClientEvents.onClientTick());
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            if (descriptor.direction() != PacketDirection.SERVER_TO_CLIENT) continue;
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(Identifier.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                    com.kingodogo.buildscape.network.PacketFactory.handleClientbound(
                            descriptor.id(), payload.data(), context.player()));
        }
    }
}
