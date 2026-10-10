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
import net.minecraft.resources.Identifier;
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
        registerHelper(target, ModBlockEntities.PILLAR_TYPE, new com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer()::render, 64);
        registerHelper(target, ModBlockEntities.DECORATED_POT_TYPE, com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer::render, 64);
        registerHelper(target, ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer::render, 64);
        registerHelper(target, ModBlockEntities.ICICLE_CAULDRON_TYPE, com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer::render, 64);
        registerHelper(target, ModBlockEntities.FESTIVE_STOCKING_TYPE, com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer::render, 64);
        target.register(ModBlockEntities.GLASS_JAR_TYPE, context -> new com.kingodogo.buildscape.adapter.v26x.client.GlassJarRenderer());
        registerHelper(target, ModBlockEntities.SHELF_TYPE, com.kingodogo.buildscape.client.renderer.ShelfRenderer::render, 64);
        registerHelper(target, ModBlockEntities.HOLLOW_LOG_TYPE, com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer::render,
                com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer.getViewDistance());
    }

    @FunctionalInterface
    private interface BlockDraw<T extends BlockEntity> {
        void render(T entity, float partialTick, PoseStack pose, Object buffer, int light, int overlay);
    }

    private static final class HelperBlockState extends BlockEntityRenderState {
        RenderCapture capture;
    }

    private static <T extends BlockEntity> void registerHelper(BlockEntityRegistrar target,
            BlockEntityType<T> type, BlockDraw<T> draw, int viewDistance) {
        target.register(type, context -> new net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T, HelperBlockState>() {
            public HelperBlockState createRenderState() { return new HelperBlockState(); }
            public int getViewDistance() { return viewDistance; }
            public void extractRenderState(T entity, HelperBlockState state, float partialTick,
                    net.minecraft.world.phys.Vec3 camera, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
                BlockEntityRenderState.extractBase(entity, state, overlay);
                state.capture = new RenderCapture();
                draw.render(entity, partialTick, new PoseStack(), state.capture, state.lightCoords,
                        net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
            }
            public void submit(HelperBlockState state, PoseStack pose, net.minecraft.client.renderer.SubmitNodeCollector collector,
                    net.minecraft.client.renderer.state.level.CameraRenderState camera) {
                state.capture.submit(pose, collector, camera);
            }
        });
    }

    @FunctionalInterface
    private interface EntityDraw<T extends Entity> {
        void render(T entity, float yaw, float partialTick, PoseStack pose, Object buffer, int light);
    }

    private static final class HelperEntityState extends net.minecraft.client.renderer.entity.state.EntityRenderState {
        RenderCapture capture;
        net.minecraft.world.phys.Vec3 offset = net.minecraft.world.phys.Vec3.ZERO;
    }

    private static <T extends Entity> EntityRendererProvider<T> helperProvider(EntityDraw<T> draw,
            java.util.function.BiFunction<T, Float, net.minecraft.world.phys.Vec3> offset) {
        return context -> new EntityRenderer<T, HelperEntityState>(context) {
            public HelperEntityState createRenderState() { return new HelperEntityState(); }
            public net.minecraft.world.phys.Vec3 getRenderOffset(HelperEntityState state) { return state.offset; }
            public void extractRenderState(T entity, HelperEntityState state, float partialTick) {
                super.extractRenderState(entity, state, partialTick);
                state.offset = offset.apply(entity, partialTick);
                state.capture = new RenderCapture();
                draw.render(entity, entity.getYRot(), partialTick, new PoseStack(), state.capture, state.lightCoords);
            }
            public void submit(HelperEntityState state, PoseStack pose, net.minecraft.client.renderer.SubmitNodeCollector collector,
                    net.minecraft.client.renderer.state.level.CameraRenderState camera) {
                state.capture.submit(pose, collector, camera);
                super.submit(state, pose, collector, camera);
            }
        };
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
                helperProvider(com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer::render, (entity, tick) -> net.minecraft.world.phys.Vec3.ZERO));
        registrar.register((EntityType<PlatformAdapterBase.FestiveStockingEntityImpl>) Services.PLATFORM.getFestiveStockingEntityType(),
                helperProvider(com.kingodogo.buildscape.client.renderer.FestiveStockingRenderer::render, (entity, tick) -> net.minecraft.world.phys.Vec3.ZERO));
        registrar.register((EntityType<PlatformAdapterBase.ColoredItemFrameEntityImpl>) Services.PLATFORM.getColoredItemFrameEntityType(),
                helperProvider(com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer::render,
                        com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer::getRenderOffset));
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
            var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer renderer = dispatcher.getRenderer(entity);
            if (renderer != null && bufferSource instanceof RenderCapture capture) {
                var state = renderer.createRenderState(entity, partialTicks);
                state.lightCoords = packedLight;
                capture.record(poseStack, (pose, collector, camera) -> renderer.submit(state, pose, collector, camera));
                return;
            }
            if (renderer != null && bufferSource instanceof net.minecraft.client.renderer.SubmitNodeCollector collector) {
                var state = renderer.createRenderState(entity, (float) partialTicks);
                state.lightCoords = packedLight;
                renderer.submit(state, poseStack, collector, null);
            }
        } catch (Throwable exception) { BuildscapeCommon.LOGGER.warn("Failed to render Buildscape entity", exception); }
    }

    public static VertexConsumer getTranslucentBuffer(Object bufferSource) {
        if (bufferSource instanceof RenderCapture capture) return capture.translucent();
        return bufferSource instanceof VertexConsumer consumer ? consumer : null;
    }
}
