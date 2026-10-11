package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v118x.GuiProvider;
import com.kingodogo.buildscape.adapter.v118x.ParticleFactory;
import com.kingodogo.buildscape.adapter.v118x.RenderFactory;
import com.kingodogo.buildscape.adapter.v118x.client.BuildscapeRenderLayers;
import com.kingodogo.buildscape.adapter.v118x.client.DryFoliageColor;
import com.kingodogo.buildscape.adapter.v118x.client.FoliageColors;
import com.kingodogo.buildscape.adapter.v118x.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.adapter.v118x.client.ShulkerTooltipComponent;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.CopperChestClientEvents;
import com.kingodogo.buildscape.client.GeyserParticleHandler;
import com.kingodogo.buildscape.client.ModKeyBinds;
import com.kingodogo.buildscape.client.PillarOverlayHandler;
import com.kingodogo.buildscape.client.TreeChopHandler;
import com.kingodogo.buildscape.fluid.FabricExperienceFluid;
import com.kingodogo.buildscape.network.PacketDirection;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.util.function.Function;

public final class BuildscapeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Vanilla has not constructed its colour tables yet. Forward adapter registrations
        // into Fabric's deferred registries, and resolve vanilla item colours when rendering.
        FoliageColors.register(new BlockColors() {
            @Override public void register(BlockColor provider, Block... blocks) {
                ColorProviderRegistry.BLOCK.register(provider, blocks);
            }
        }, new ItemColors() {
            @Override public void register(ItemColor provider, ItemLike... items) {
                ColorProviderRegistry.ITEM.register(provider, items);
            }
            @Override public int getColor(ItemStack stack, int tint) {
                ItemColor provider = ColorProviderRegistry.ITEM.get(stack.getItem());
                return provider == null ? -1 : provider.getColor(stack, tint);
            }
        });
        ModKeyBinds.register(KeyBindingHelper::registerKeyBinding);
        GuiProvider.registerMenuScreens(com.kingodogo.buildscape.platform.FabricPlatformAdapter.menuScreenRegistrar());
        ClientEvents.initializeConfigCallback();
        for (Block block : Registry.BLOCK) {
            ResourceLocation id = Registry.BLOCK.getKey(block);
            if (!BuildscapeCommon.MOD_ID.equals(id.getNamespace())) continue;
            RenderType layer = BuildscapeRenderLayers.getRenderType(id.getPath());
            if (layer != null) BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
        }
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(),
                FabricExperienceFluid.STILL, FabricExperienceFluid.FLOWING);
        FluidRenderHandlerRegistry.INSTANCE.register(FabricExperienceFluid.STILL, FabricExperienceFluid.FLOWING,
                new SimpleFluidRenderHandler(new ResourceLocation("minecraft", "block/water_still"),
                        new ResourceLocation("minecraft", "block/water_flow"), 0x4CFF19));
        EntityModelLayerRegistry.registerModelLayer(RenderFactory.HOMEMAKER_LAYER, RenderFactory::createBodyLayer);
        Services.PLATFORM.wrapRegistryAction(() -> RenderFactory.registerRenderers(new RenderFactory.Registrar() {
            public <T extends BlockEntity> void registerBlockEntity(BlockEntityType<T> type,
                    BlockEntityRendererProvider<T> provider) {
                BlockEntityRendererRegistry.register(type, provider);
            }
            public <T extends Entity> void registerEntity(EntityType<T> type, EntityRendererProvider<T> provider) {
                EntityRendererRegistry.register(type, provider);
            }
        }));
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type,
                    Function<SpriteSet, ParticleProvider<T>> factory) {
                ParticleFactoryRegistry.getInstance().register(type, sprites -> factory.apply(sprites));
            }
            public <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
                ParticleFactoryRegistry.getInstance().register(type, provider);
            }
        });
        // Resolve these singletons when rendering, after Minecraft creates its dispatchers.
        for (var item : Registry.ITEM) {
            if (item instanceof com.kingodogo.buildscape.item.CopperChestItem) {
                BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, pose, buffers, light, overlay) ->
                        com.kingodogo.buildscape.client.renderer.CopperChestItemRenderer.INSTANCE
                                .renderByItem(stack, mode, pose, buffers, light, overlay));
            } else if (item instanceof com.kingodogo.buildscape.item.GlassJarItem) {
                BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, pose, buffers, light, overlay) ->
                        com.kingodogo.buildscape.client.renderer.GlassJarItemRenderer.INSTANCE
                                .renderByItem(stack, mode, pose, buffers, light, overlay));
            }
        }
        TooltipComponentCallback.EVENT.register(data -> data instanceof
                com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData shulker
                ? new ShulkerTooltipComponent(shulker) : null);
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, models) ->
                models.accept(new ResourceLocation(BuildscapeCommon.MOD_ID, "block/stringlight_frame")));
        ClientSpriteRegistryCallback.event(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
                .register((atlas, registry) -> CopperChestClientEvents.onTextureStitch(atlas.location(), registry::register));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(BuildscapeCommon.MOD_ID, "client_colours");
                    }
                    public void onResourceManagerReload(ResourceManager manager) {
                        com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog.invalidate();
                        DryFoliageColor.init();
                    }
                });
        registerEvents();
        registerClientNetworking();
    }

    private static void registerEvents() {
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
        HudRenderCallback.EVENT.register((pose, partialTick) -> {
            Minecraft mc = Minecraft.getInstance();
            ClientEvents.renderOverlay(pose, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            PillarOverlayHandler.renderOverlay(pose);
            String text = TreeChopHandler.getOverlayText(mc.player, mc.hitResult);
            if (text != null && !text.isEmpty()) {
                net.minecraft.client.gui.GuiComponent.drawCenteredString(pose, mc.font, text,
                        mc.getWindow().getGuiScaledWidth() / 2, mc.getWindow().getGuiScaledHeight() / 2 + 30, 0xFFFFFF);
            }
        });
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, target) ->
                !ClientEvents.renderBlockHighlight(context.matrixStack(), context.camera(), context.consumers(),
                        Minecraft.getInstance().hitResult));
        WorldRenderEvents.LAST.register(context -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null) return;
            var buffers = mc.renderBuffers().bufferSource();
            ClientEvents.renderLevel(context.matrixStack(), context.camera(), buffers);
            buffers.endBatch();
        });
    }

    private static void registerClientNetworking() {
        for (var descriptor : PacketFactory.getRegisteredDescriptors()) {
            if (descriptor.direction() != PacketDirection.SERVER_TO_CLIENT) continue;
            ResourceLocation id = new ResourceLocation(descriptor.id().getNamespace(), descriptor.id().getPath());
            // Decode before the network-owned buffer is released. The adapter dispatches
            // the decoded packet to the client thread without retaining this buffer.
            ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buffer, sender) ->
                    PacketFactory.handleClientbound(descriptor.id(), buffer, client.player));
        }
    }
}
