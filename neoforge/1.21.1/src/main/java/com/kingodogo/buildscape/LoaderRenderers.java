package com.kingodogo.buildscape;

import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.GlassJarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;

public final class LoaderRenderers {

    private static final ResourceLocation HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        BuildscapeCommon.MOD_ID, "textures/entity/wandering_homemaker.png");
    private static final ResourceLocation FESTIVE_HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        BuildscapeCommon.MOD_ID, "textures/entity/festive_wandering_homemaker.png");

    private LoaderRenderers() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerRenderers() {
        PillarBlockEntityRenderer pillarRenderer = new PillarBlockEntityRenderer();
        BlockEntityRenderers.register(ModBlockEntities.PILLAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> pillarRenderer.render((com.kingodogo.buildscape.block.PillarBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.HOLLOW_LOG_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> HollowLogBlockEntityRenderer.render((com.kingodogo.buildscape.block.HollowLogBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.SHELF_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> ShelfRenderer.render((com.kingodogo.buildscape.block.ShelfBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.FESTIVE_STOCKING_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> FestiveStockingBlockEntityRenderer.render((com.kingodogo.buildscape.block.FestiveStockingBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.GLASS_JAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> GlassJarBlockEntityRenderer.render((com.kingodogo.buildscape.block.GlassJarBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.ICICLE_CAULDRON_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> IcicleCauldronBlockEntityRenderer.render((com.kingodogo.buildscape.block.IcicleCauldronBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> DecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.DecoratedPotBlockEntity) be, pt, ps, bs, cl, co));
        BlockEntityRenderers.register(ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> TrappedDecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.TrappedDecoratedPotBlockEntity) be, pt, ps, bs, cl, co));

        var homemakerType = (EntityType<WanderingTrader>) Services.PLATFORM.getWanderingHomemakerEntityType();
        if (homemakerType != null) {
            EntityRenderers.register(homemakerType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                    new MobRenderer<WanderingTrader, com.kingodogo.buildscape.adapter.v121x.RenderFactory.WanderingHomemakerModel<WanderingTrader>>(
                            context, new com.kingodogo.buildscape.adapter.v121x.RenderFactory.WanderingHomemakerModel<>(context.bakeLayer(com.kingodogo.buildscape.adapter.v121x.RenderFactory.HOMEMAKER_LAYER)), 0.5F) {
                        @Override
                        public ResourceLocation getTextureLocation(WanderingTrader entity) {
                            return HOMEMAKER_TEXTURE;
                        }
                    }
            );
        }

        var festiveType = (EntityType<WanderingTrader>) Services.PLATFORM.getFestiveWanderingHomemakerEntityType();
        if (festiveType != null) {
            EntityRenderers.register(festiveType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                    new MobRenderer<WanderingTrader, com.kingodogo.buildscape.adapter.v121x.RenderFactory.WanderingHomemakerModel<WanderingTrader>>(
                            context, new com.kingodogo.buildscape.adapter.v121x.RenderFactory.WanderingHomemakerModel<>(context.bakeLayer(com.kingodogo.buildscape.adapter.v121x.RenderFactory.HOMEMAKER_LAYER)), 0.5F) {
                        @Override
                        public ResourceLocation getTextureLocation(WanderingTrader entity) {
                            return FESTIVE_HOMEMAKER_TEXTURE;
                        }
                    }
            );
        }

    }

}
