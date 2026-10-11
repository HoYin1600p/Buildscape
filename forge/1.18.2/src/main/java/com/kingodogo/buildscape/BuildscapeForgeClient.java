package com.kingodogo.buildscape;

import com.kingodogo.buildscape.adapter.v118x.GuiProvider;
import com.kingodogo.buildscape.adapter.v118x.ParticleFactory;
import com.kingodogo.buildscape.adapter.v118x.RenderFactory;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

/** Forge discovers this subscriber only on the physical client. */
@Mod.EventBusSubscriber(modid = BuildscapeCommon.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BuildscapeForgeClient {
    private BuildscapeForgeClient() {}

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, parent) -> GuiProvider.createConfigScreen(parent)));
        event.enqueueWork(() -> {
            com.kingodogo.buildscape.client.ModKeyBinds.register(ClientRegistry::registerKeyBinding);
            ClientEvents.initializeConfigCallback();
            net.minecraftforge.client.MinecraftForgeClient.registerTooltipComponentFactory(
                    com.kingodogo.buildscape.client.tooltip.ShulkerBoxTooltipData.class,
                    com.kingodogo.buildscape.adapter.v118x.client.ShulkerTooltipComponent::new);
            GuiProvider.registerMenuScreens(com.kingodogo.buildscape.platform.ForgePlatformAdapter.menuScreenRegistrar());
            com.kingodogo.buildscape.adapter.v118x.client.FoliageColors.register(
                    Minecraft.getInstance().getBlockColors(), Minecraft.getInstance().getItemColors());
            ItemBlockRenderTypes.setRenderLayer(com.kingodogo.buildscape.fluid.ForgeExperienceFluids.EXPERIENCE_STILL.get(), net.minecraft.client.renderer.RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(com.kingodogo.buildscape.fluid.ForgeExperienceFluids.EXPERIENCE_FLOWING.get(), net.minecraft.client.renderer.RenderType.translucent());
            for (var block : ForgeRegistries.BLOCKS.getValues()) {
                var id = ForgeRegistries.BLOCKS.getKey(block);
                if (id != null && BuildscapeCommon.MOD_ID.equals(id.getNamespace())) {
                    var layer = com.kingodogo.buildscape.adapter.v118x.client.BuildscapeRenderLayers.getRenderType(id.getPath());
                    if (layer != null) ItemBlockRenderTypes.setRenderLayer(block, layer);
                }
            }
        });
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        Services.PLATFORM.wrapRegistryAction(() -> RenderFactory.registerRenderers(new RenderFactory.Registrar() {
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
        }));
    }

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RenderFactory.HOMEMAKER_LAYER, RenderFactory::createBodyLayer);
    }

    @SubscribeEvent
    public static void particles(ParticleFactoryRegisterEvent event) {
        ParticleFactory.registerProviders(new ParticleFactory.Registrar() {
            public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> factory) {
                Minecraft.getInstance().particleEngine.register(type, sprites -> factory.apply(sprites));
            }
            public <T extends ParticleOptions> void registerDirect(ParticleType<T> type, ParticleProvider<T> provider) {
                Minecraft.getInstance().particleEngine.register(type, provider);
            }
        });
    }

    @SubscribeEvent
    public static void textures(TextureStitchEvent.Pre event) {
        com.kingodogo.buildscape.client.CopperChestClientEvents.onTextureStitch(event.getAtlas().location(), event::addSprite);
    }

    @SubscribeEvent
    public static void reloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener)
                manager -> {
                    com.kingodogo.buildscape.client.workbench.ClientBlockColorCatalog.invalidate();
                    com.kingodogo.buildscape.adapter.v118x.client.DryFoliageColor.init();
                });
    }

    @SubscribeEvent
    public static void specialModels(net.minecraftforge.client.event.ModelRegistryEvent event) {
        net.minecraftforge.client.model.ForgeModelBakery.addSpecialModel(
                new net.minecraft.resources.ResourceLocation(BuildscapeCommon.MOD_ID, "block/stringlight_frame"));
    }

    @SubscribeEvent
    public static void bakedModels(net.minecraftforge.client.event.ModelBakeEvent event) {
        for (String wood : java.util.List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "azalea", "flowering_azalea")) {
            for (String suffix : java.util.List.of("post", "side", "side_tall", "inventory")) {
                tintHedge(event.getModelRegistry(), new net.minecraft.resources.ResourceLocation(BuildscapeCommon.MOD_ID,
                        "block/" + wood + "_leaf_hedge_" + suffix));
            }
            tintHedge(event.getModelRegistry(), new net.minecraft.resources.ResourceLocation(BuildscapeCommon.MOD_ID,
                    "item/" + wood + "_leaf_hedge"));
        }
    }

    private static void tintHedge(java.util.Map<net.minecraft.resources.ResourceLocation, net.minecraft.client.resources.model.BakedModel> models,
            net.minecraft.resources.ResourceLocation location) {
        models.computeIfPresent(location, (key, model) -> new com.kingodogo.buildscape.client.model.TintedLeafHedgeModel(model));
    }
}
