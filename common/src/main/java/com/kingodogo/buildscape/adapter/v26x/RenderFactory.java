package com.kingodogo.buildscape.adapter.v26x;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.kingodogo.buildscape.block.ModBlockEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

public final class RenderFactory {

    private RenderFactory() {}

    public interface EntityRegistrar {
        <T extends Entity> void register(EntityType<? extends T> type, EntityRendererProvider<T> provider);
    }
    public interface BlockEntityRegistrar {
        <T extends BlockEntity, S extends BlockEntityRenderState> void register(
                BlockEntityType<? extends T> type, BlockEntityRendererProvider<T, S> provider);
    }

    public static void registerBlockEntityRenderers(BlockEntityRegistrar target) {
        target.register(ModBlockEntities.MANGROVE_SIGN_BLOCK_ENTITY_TYPE, StandingSignRenderer::new);
        target.register(ModBlockEntities.BAMBOO_SIGN_BLOCK_ENTITY_TYPE, StandingSignRenderer::new);
        target.register(ModBlockEntities.COPPER_CHEST_TYPE, com.kingodogo.buildscape.adapter.v26x.client.CopperChestRenderer::new);
        target.register(ModBlockEntities.PILLAR_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.PillarRenderer());
        target.register(ModBlockEntities.DECORATED_POT_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.DecoratedPotRenderer<>());
        target.register(ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.DecoratedPotRenderer<>());
        target.register(ModBlockEntities.ICICLE_CAULDRON_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.IcicleCauldronRenderer());
        target.register(ModBlockEntities.FESTIVE_STOCKING_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.FestiveStockingBlockRenderer());
        target.register(ModBlockEntities.GLASS_JAR_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.GlassJarRenderer());
        target.register(ModBlockEntities.SHELF_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.ShelfBlockRenderer());
        target.register(ModBlockEntities.HOLLOW_LOG_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.HollowLogRenderer());
    }

    private static EntityRegistrar registrar;
    private static boolean registered;

    public static void registerRenderers(EntityRegistrar target) {
        registrar = java.util.Objects.requireNonNull(target);
        registerRenderers();
    }

    @SuppressWarnings("unchecked")
    public static void registerRenderers() {
        if (registered) return;
        java.util.Objects.requireNonNull(registrar, "Entity renderers must be registered through the loader hook");
        registrar.register((EntityType<net.minecraft.world.entity.vehicle.boat.AbstractBoat>) Services.PLATFORM.getMangroveBoatEntityType(),
                context -> new com.kingodogo.buildscape.adapter.v26x.client.BuildscapeBoatRenderer(context, "mangrove"));
        registrar.register((EntityType<net.minecraft.world.entity.vehicle.boat.AbstractBoat>) Services.PLATFORM.getPoplarBoatEntityType(),
                context -> new com.kingodogo.buildscape.adapter.v26x.client.BuildscapeBoatRenderer(context, "poplar"));
        registrar.register((EntityType<PlatformAdapterBase.SeatEntityImpl>) Services.PLATFORM.getSeatEntityType(),
                net.minecraft.client.renderer.entity.NoopRenderer::new);
        registrar.register((EntityType<PlatformAdapterBase.FallingIcicleEntityImpl>) Services.PLATFORM.getFallingIcicleEntityType(),
                com.kingodogo.buildscape.adapter.v26x.client.FallingIcicleEntityRenderer::new);
        registrar.register((EntityType<PlatformAdapterBase.FestiveStockingEntityImpl>) Services.PLATFORM.getFestiveStockingEntityType(),
                com.kingodogo.buildscape.adapter.v26x.client.FestiveStockingEntityRenderer::new);
        registrar.register((EntityType<PlatformAdapterBase.ColoredItemFrameEntityImpl>) Services.PLATFORM.getColoredItemFrameEntityType(),
                com.kingodogo.buildscape.adapter.v26x.client.ColoredItemFrameEntityRenderer::new);
        registrar.register((EntityType<WanderingTrader>) Services.PLATFORM.getWanderingHomemakerEntityType(),
                context -> new com.kingodogo.buildscape.adapter.v26x.client.WanderingHomemakerRenderer(context, false));
        registrar.register((EntityType<WanderingTrader>) Services.PLATFORM.getFestiveWanderingHomemakerEntityType(),
                context -> new com.kingodogo.buildscape.adapter.v26x.client.WanderingHomemakerRenderer(context, true));
        registered = true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void renderEntity(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, Object bufferSource, int packedLight) {
        if (entity == null) return;
        try {
            if (bufferSource instanceof com.kingodogo.buildscape.adapter.v26x.client.PillarRenderer.State state) {
                state.extractEntity(entity, partialTicks, poseStack, packedLight);
                return;
            }
            var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer renderer = dispatcher.getRenderer(entity);
            if (renderer != null && bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
                var state = renderer.createRenderState(entity, (float) partialTicks);
                state.lightCoords = packedLight;
                renderer.submit(state, poseStack, collector, null);
            }
        } catch (Throwable exception) { BuildscapeCommon.LOGGER.warn("Failed to render Buildscape entity", exception); }
    }

    public static VertexConsumer getTranslucentBuffer(Object bufferSource) {
        return bufferSource instanceof VertexConsumer consumer ? consumer : null;
    }
}
