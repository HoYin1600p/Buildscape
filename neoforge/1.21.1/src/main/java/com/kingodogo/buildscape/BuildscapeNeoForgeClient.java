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
    private static final java.util.Map<net.minecraft.client.gui.screens.Screen,
            java.util.List<net.minecraft.client.gui.components.AbstractWidget>> pendingWidgets = new java.util.IdentityHashMap<>();
    private BuildscapeNeoForgeClient() {}

    public static void register(IEventBus bus) {
        com.kingodogo.buildscape.adapter.v121x.client.ClientMixins.installWidgetRegistrar(
                (screen, widget) -> pendingWidgets.computeIfAbsent(screen, key -> new java.util.ArrayList<>()).add(widget));
        com.kingodogo.buildscape.adapter.v121x.client.DryFoliageColor.installHumidityAccess(
                biome -> biome.getModifiedClimateSettings().downfall());
        bus.addListener((net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) ->
                com.kingodogo.buildscape.client.ModKeyBinds.register(event::register));
        bus.addListener((net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent event) ->
                event.register(com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.class,
                        com.kingodogo.buildscape.adapter.v121x.client.ShulkerTooltipComponent::new));
        bus.addListener((net.neoforged.neoforge.client.event.ModelEvent.ModifyBakingResult event) ->
                event.getModels().replaceAll((id, model) -> {
                    String path = id.id().getPath();
                    if (BuildscapeCommon.MOD_ID.equals(id.id().getNamespace()) && path.contains("_leaf_hedge"))
                        return new com.kingodogo.buildscape.client.TintedLeafHedgeModel(model);
                    return model;
                }));
        bus.addListener((net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) ->
                com.kingodogo.buildscape.adapter.v121x.client.FoliageColors.registerBlocks(event.getBlockColors()));
        bus.addListener((net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) ->
                com.kingodogo.buildscape.adapter.v121x.client.FoliageColors.registerItems(event.getItemColors()));
        bus.addListener((net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) ->
                Services.PLATFORM.wrapRegistryAction(() ->
                    com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.withRegistrar(
                        new com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.Registrar() {
                            public <T extends net.minecraft.world.level.block.entity.BlockEntity> void registerBlockEntity(
                                    net.minecraft.world.level.block.entity.BlockEntityType<T> type,
                                    net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<T> provider) {
                                event.registerBlockEntityRenderer(type, provider);
                            }
                            public <T extends net.minecraft.world.entity.Entity> void registerEntity(
                                    net.minecraft.world.entity.EntityType<T> type,
                                    net.minecraft.client.renderer.entity.EntityRendererProvider<T> provider) {
                                event.registerEntityRenderer(type, provider);
                            }
                        }, Services.PLATFORM::registerRenderers)));
        bus.addListener(BuildscapeNeoForgeClient::registerPoplarBoatRenderer);
        bus.addListener((net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) -> {
            var chestRenderer = new com.kingodogo.buildscape.adapter.v121x.client.BlockEntityItemRenderer(true);
            var jarRenderer = new com.kingodogo.buildscape.adapter.v121x.client.BlockEntityItemRenderer(false);
            for (var item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
                // On 1.21.1 the copper chests are plain block items, so they are recognised by registry id.
                var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
                boolean copperChest = "buildscape".equals(key.getNamespace()) && key.getPath().endsWith("copper_chest");
                if (copperChest || item instanceof com.kingodogo.buildscape.item.GlassJarItem) {
                    var renderer = copperChest ? chestRenderer : jarRenderer;
                    event.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions() {
                        @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            return renderer;
                        }
                    }, item);
                }
            }
        });
        bus.addListener((net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional event) ->
                event.register(net.minecraft.client.resources.model.ModelResourceLocation.standalone(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "block/stringlight_frame"))));
        bus.addListener((net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) ->
                event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager -> {
                    com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog.invalidate();
                    com.kingodogo.buildscape.adapter.v121x.client.DryFoliageColor.init();
                }));
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
                event.enqueueWork(() -> {
                    com.kingodogo.buildscape.client.ClientEvents.initializeConfigCallback();
                    net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                            com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.still(), net.minecraft.client.renderer.RenderType.translucent());
                    net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                            com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.flowing(), net.minecraft.client.renderer.RenderType.translucent());
                    for (var block : net.minecraft.core.registries.BuiltInRegistries.BLOCK) {
                        var id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
                        if (!BuildscapeCommon.MOD_ID.equals(id.getNamespace())) continue;
                        var layer = com.kingodogo.buildscape.client.BuildscapeRenderLayers.getRenderLayer(id.getPath());
                        var renderType = switch (layer) {
                            case SOLID -> net.minecraft.client.renderer.RenderType.solid();
                            case CUTOUT -> net.minecraft.client.renderer.RenderType.cutout();
                            case CUTOUT_MIPPED -> net.minecraft.client.renderer.RenderType.cutoutMipped();
                            case TRANSLUCENT -> net.minecraft.client.renderer.RenderType.translucent();
                        };
                        net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(block, renderType);
                    }
                }));
        var gameBus = net.neoforged.neoforge.common.NeoForge.EVENT_BUS;
        // Mixin init hooks queue widgets before NeoForge publishes this event.
        // addListener attaches renderables, narration entries and input listeners together.
        gameBus.addListener((net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) -> {
            var widgets = pendingWidgets.remove(event.getScreen());
            if (widgets != null) widgets.forEach(event::addListener);
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Pre event) -> {
            if (event.getScreen() instanceof net.minecraft.client.gui.screens.advancements.AdvancementsScreen screen) {
                com.kingodogo.buildscape.client.ClientAdvancementEvents.pan(screen.selectedTab,
                        event.getScrollDeltaX(), event.getScrollDeltaY());
                event.setCanceled(true);
            }
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor event) -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null && com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.still().isSame(
                    mc.level.getFluidState(event.getCamera().getBlockPosition()).getType())) {
                event.setRed(0.3F);
                event.setGreen(0.9F);
                event.setBlue(0.1F);
            }
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ViewportEvent.RenderFog event) -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null && com.kingodogo.buildscape.adapter.v121x.ExperienceFluids.still().isSame(
                    mc.level.getFluidState(event.getCamera().getBlockPosition()).getType())) {
                event.setNearPlaneDistance(0.5F);
                event.setFarPlaneDistance(12.0F);
                event.setCanceled(true);
            }
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.RenderHighlightEvent.Block event) -> {
            if (com.kingodogo.buildscape.client.ClientEvents.renderBlockHighlight(event.getPoseStack(),
                    event.getCamera(), event.getMultiBufferSource(), event.getTarget())) event.setCanceled(true);
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.RenderLevelStageEvent event) -> {
            if (event.getStage() != net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            var buffers = mc.renderBuffers().bufferSource();
            com.kingodogo.buildscape.client.ClientEvents.renderLevel(event.getPoseStack(), event.getCamera(), buffers);
            buffers.endBatch();
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.RenderGuiEvent.Post event) -> {
            var graphics = event.getGuiGraphics();
            com.kingodogo.buildscape.adapter.v121x.client.ClientAccess.withGuiGraphics(graphics, () -> {
                com.kingodogo.buildscape.client.ClientEvents.renderOverlay(graphics.pose(), graphics.guiWidth(), graphics.guiHeight());
                com.kingodogo.buildscape.client.PillarOverlayHandler.renderOverlay(graphics.pose());
                var mc = net.minecraft.client.Minecraft.getInstance();
                String text = com.kingodogo.buildscape.client.TreeChopHandler.getOverlayText(mc.player, mc.hitResult);
                if (text != null && !text.isEmpty()) graphics.drawCenteredString(mc.font, text,
                        graphics.guiWidth() / 2, graphics.guiHeight() / 2 + 30, 0xFFFFFF);
            });
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ClientTickEvent.Post event) -> {
            com.kingodogo.buildscape.client.ClientEvents.onClientTick();
            com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.clientTick();
            com.kingodogo.buildscape.adapter.v121x.client.GeyserParticleHandler.onClientTick();
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.InputEvent.Key event) ->
                com.kingodogo.buildscape.client.ClientEvents.onKeyInput(event.getKey(), event.getAction()));
        gameBus.addListener((net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post event) -> {
            if (event.getButton() == 1 && event.getAction() == 1) com.kingodogo.buildscape.client.ClientEvents.onRightClick();
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent event) -> {
            if (net.minecraft.client.Minecraft.getInstance().screen == null && com.kingodogo.buildscape.client.ZoomHandler.isZooming()) {
                com.kingodogo.buildscape.client.ZoomHandler.handleScroll(event.getScrollDeltaY());
                event.setCanceled(true);
            }
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered event) -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (event.isAttack()) com.kingodogo.buildscape.client.BiomeBrushClientHandler.onAttack(mc.player, mc.hitResult);
            if (event.isAttack() && mc.screen == null && com.kingodogo.buildscape.client.TreeChopHandler.shouldCancelLeftClick(mc.player, mc.hitResult)) {
                event.setCanceled(true);
                event.setSwingHand(true);
            }
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ViewportEvent.ComputeFov event) ->
                event.setFOV(event.getFOV() * com.kingodogo.buildscape.client.ZoomHandler.getZoomLevel()));
        gameBus.addListener((net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles event) -> {
            float[] angles = com.kingodogo.buildscape.client.ZoomHandler.getSmoothedRotation(event.getYaw(), event.getPitch());
            event.setYaw(angles[0]);
            event.setPitch(angles[1]);
        });
        gameBus.addListener((net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) -> {
            com.kingodogo.buildscape.client.ClientEvents.onClientDisconnect();
            com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.clear();
        });
        gameBus.addListener((net.neoforged.neoforge.event.level.LevelEvent.Unload event) -> {
            if (event.getLevel().isClientSide()) {
                com.kingodogo.buildscape.client.ClientEvents.onClientWorldUnload();
                com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.clear();
            }
        });
        gameBus.addListener((net.neoforged.neoforge.event.level.ChunkEvent.Load event) -> {
            if (event.getLevel().isClientSide() && event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk chunk)
                com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.onChunkLoad(chunk);
        });
        gameBus.addListener((net.neoforged.neoforge.event.level.ChunkEvent.Unload event) -> {
            if (event.getLevel().isClientSide() && event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk chunk)
                com.kingodogo.buildscape.adapter.v121x.client.HazeBushParticleHandler.onChunkUnload(chunk);
        });
    }

    /** NeoForge adds the model/texture-pair hook; common compiles against vanilla. */
    @SuppressWarnings("unchecked")
    private static void registerPoplarBoatRenderer(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        Services.PLATFORM.wrapRegistryAction(() -> event.registerEntityRenderer(
                (net.minecraft.world.entity.EntityType<net.minecraft.world.entity.vehicle.Boat>) Services.PLATFORM.getPoplarBoatEntityType(),
                context -> new net.minecraft.client.renderer.entity.BoatRenderer(context, false) {
                    @Override
                    public com.mojang.datafixers.util.Pair<net.minecraft.resources.ResourceLocation, net.minecraft.client.model.ListModel<net.minecraft.world.entity.vehicle.Boat>> getModelWithLocation(
                            net.minecraft.world.entity.vehicle.Boat boat) {
                        return com.mojang.datafixers.util.Pair.of(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                                BuildscapeCommon.MOD_ID, "textures/entity/boat/poplar.png"), super.getModelWithLocation(boat).getSecond());
                    }
                }));
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
