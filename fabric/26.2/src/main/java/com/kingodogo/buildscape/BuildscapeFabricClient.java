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
        com.kingodogo.buildscape.adapter.v26x.client.FoliageColors.register(
                net.minecraft.client.Minecraft.getInstance().getBlockColors());
        com.kingodogo.buildscape.client.ModKeyBinds.register(
                net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper::registerKeyMapping);
        // Mod Menu is not on this module's classpath. Its optional config-screen
        // integration must be added together with its API dependency and entrypoint.
        // Raw input and camera hooks live in the client-only entries of
        // buildscape-fabric.mixins.json. Hammer input runs once per mouse press.
        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player, level, hand, pos, face) -> {
            if (level.isClientSide() && com.kingodogo.buildscape.client.TreeChopHandler.shouldCancelLeftClick(
                    player, new net.minecraft.world.phys.BlockHitResult(
                            net.minecraft.world.phys.Vec3.atCenterOf(pos), face, pos, false))) {
                player.swing(hand);
                return net.minecraft.world.InteractionResult.FAIL;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            com.kingodogo.buildscape.client.ClientEvents.onClientDisconnect();
            com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
        });
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> {
            com.kingodogo.buildscape.client.ClientEvents.onClientWorldUnload();
            com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.clearInputRenderState();
        });
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents.AFTER_BLOCK_OUTLINE_EXTRACTION.register(
                (context, target) -> {
                    if (com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.extractBlockHighlight(
                            context.camera(), target, context.levelState())) {
                        context.levelState().blockOutlineRenderState = null;
                    }
                });
        net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents.COLLECT_SUBMITS.register(context ->
                com.kingodogo.buildscape.adapter.v26x.client.ClientPlatformHooks.collectBlockHighlight(
                        context.poseStack(), context.submitNodeCollector(), context.levelState()));
        net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry.register(
                com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.still(),
                com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceFluids.flowing(),
                com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidModel.create());
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
