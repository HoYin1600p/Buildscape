package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v121x.PacketFactory.BuildscapeCustomPayload;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.adapter.v121x.client.*;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.platform.Services;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Client bindings stay behind Fabric's client entrypoint. */
public final class BuildscapeFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        ClientMixins.installWidgetRegistrar((screen, widget) ->
                ((com.kingodogo.buildscape.mixin.fabric.client.ScreenWidgetAccess) screen).buildscape$addWidget(widget));
        DryFoliageColor.installHumidityAccess(biome ->
                ((com.kingodogo.buildscape.mixin.fabric.client.BiomeClimateAccess) (Object) biome)
                        .buildscape$climateSettings().downfall());
        FoliageColors.registerBlocks(new net.minecraft.client.color.block.BlockColors() {
            @Override public void register(net.minecraft.client.color.block.BlockColor provider,
                    net.minecraft.world.level.block.Block... blocks) {
                ColorProviderRegistry.BLOCK.register(provider, blocks);
            }
        });
        FoliageColors.registerItems(new net.minecraft.client.color.item.ItemColors() {
            @Override public void register(net.minecraft.client.color.item.ItemColor provider,
                    net.minecraft.world.level.ItemLike... items) {
                ColorProviderRegistry.ITEM.register(provider, items);
            }
            @Override public int getColor(net.minecraft.world.item.ItemStack stack, int tint) {
                var provider = ColorProviderRegistry.ITEM.get(stack.getItem());
                return provider == null ? -1 : provider.getColor(stack, tint);
            }
        });
        com.kingodogo.buildscape.client.ModKeyBinds.register(KeyBindingHelper::registerKeyBinding);
        for (var block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (!BuildscapeCommon.MOD_ID.equals(id.getNamespace())) continue;
            var layer = BuildscapeRenderLayers.getRenderType(id.getPath());
            if (layer != null) BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
        }
        var still = com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.still();
        var flowing = com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.flowing();
        BlockRenderLayerMap.INSTANCE.putFluids(net.minecraft.client.renderer.RenderType.translucent(), still, flowing);
        FluidRenderHandlerRegistry.INSTANCE.register(still, flowing, new SimpleFluidRenderHandler(
                ResourceLocation.withDefaultNamespace("block/water_still"),
                ResourceLocation.withDefaultNamespace("block/water_flow"), 0x4CFF19));
        EntityModelLayerRegistry.registerModelLayer(RenderFactory.HOMEMAKER_LAYER, RenderFactory::createBodyLayer);
        Services.PLATFORM.wrapRegistryAction(Services.PLATFORM::registerRenderers);
        Services.PLATFORM.wrapRegistryAction(com.kingodogo.buildscape.platform.client.FabricClientAccess::registerPoplarBoatRenderer);
        for (var item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            boolean chest = BuildscapeCommon.MOD_ID.equals(id.getNamespace()) && id.getPath().endsWith("copper_chest");
            if (chest || item instanceof com.kingodogo.buildscape.item.GlassJarItem) {
                // Resolve dispatchers only after Minecraft has finished constructing them.
                BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, pose, buffers, light, overlay) ->
                        new BlockEntityItemRenderer(chest).renderByItem(stack, mode, pose, buffers, light, overlay));
            }
        }
        TooltipComponentCallback.EVENT.register(data -> data instanceof
                com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData shulker
                ? new ShulkerTooltipComponent(shulker) : null);
        ModelLoadingPlugin.register(context -> {
            context.addModels(ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "block/stringlight_frame"));
            context.modifyModelAfterBake().register((model, bakeContext) -> {
                var id = bakeContext.resourceId();
                if (id == null && bakeContext.topLevelId() != null) id = bakeContext.topLevelId().id();
                return id != null && BuildscapeCommon.MOD_ID.equals(id.getNamespace())
                        && id.getPath().contains("_leaf_hedge")
                        ? new com.kingodogo.buildscape.client.TintedLeafHedgeModel(model) : model;
            });
        });
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "client_colours");
            }
            public void onResourceManagerReload(ResourceManager manager) {
                com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog.invalidate();
                DryFoliageColor.init();
            }
        });
        com.kingodogo.buildscape.platform.Services.PLATFORM.registerMenuScreens();
        com.kingodogo.buildscape.platform.Services.PLATFORM.registerParticleProviders();
        com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientEvents.onClientTick();
            HazeBushParticleHandler.clientTick();
            GeyserParticleHandler.onClientTick();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ClientEvents.onClientDisconnect();
            HazeBushParticleHandler.clear();
        });
        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> HazeBushParticleHandler.onChunkLoad(chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> HazeBushParticleHandler.onChunkUnload(chunk));
        HudRenderCallback.EVENT.register((graphics, delta) -> ClientAccess.withGuiGraphics(graphics, () -> {
            ClientEvents.renderOverlay(graphics.pose(), graphics.guiWidth(), graphics.guiHeight());
            com.kingodogo.buildscape.client.PillarOverlayHandler.renderOverlay(graphics.pose());
            var mc = Minecraft.getInstance();
            String text = com.kingodogo.buildscape.client.TreeChopHandler.getOverlayText(mc.player, mc.hitResult);
            if (text != null && !text.isEmpty()) graphics.drawCenteredString(mc.font, text,
                    graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 30, 0xFFFFFF);
        }));
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, target) ->
                !ClientEvents.renderBlockHighlight(context.matrixStack(), context.camera(), context.consumers(),
                        Minecraft.getInstance().hitResult));
        WorldRenderEvents.LAST.register(context -> {
            var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            var buffers = mc.renderBuffers().bufferSource();
            ClientEvents.renderLevel(context.matrixStack(), context.camera(), buffers);
            buffers.endBatch();
        });
        for (var descriptor : com.kingodogo.buildscape.network.PacketFactory.getRegisteredDescriptors()) {
            if (descriptor.direction() != PacketDirection.SERVER_TO_CLIENT) continue;
            var type = new CustomPacketPayload.Type<BuildscapeCustomPayload>(ResourceLocation.fromNamespaceAndPath(
                    descriptor.id().getNamespace(), descriptor.id().getPath()));
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                    com.kingodogo.buildscape.network.PacketFactory.handleClientbound(
                            descriptor.id(), payload.data(), context.player()));
        }
    }
}
