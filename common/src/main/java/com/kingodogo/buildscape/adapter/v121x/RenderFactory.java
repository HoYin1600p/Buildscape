package com.kingodogo.buildscape.adapter.v121x;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.client.renderer.ArmorPillarRenderer;
import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.GlassJarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.MobPillarRenderer;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.platform.Services;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;

public final class RenderFactory {

    public static final ModelLayerLocation HOMEMAKER_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "wandering_homemaker"), "main");
    private static final ResourceLocation HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BuildscapeCommon.MOD_ID, "textures/entity/wandering_homemaker.png");
    private static final ResourceLocation FESTIVE_HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            BuildscapeCommon.MOD_ID, "textures/entity/festive_wandering_homemaker.png");

    private RenderFactory() {}

    @SuppressWarnings("unchecked")
    public static void registerRenderers() {
        try {
            java.lang.reflect.Field beField = net.minecraft.client.renderer.blockentity.BlockEntityRenderers.class.getDeclaredField("PROVIDERS");
            beField.setAccessible(true);
            java.util.Map<net.minecraft.world.level.block.entity.BlockEntityType<?>, Object> beProviders =
                    (java.util.Map<net.minecraft.world.level.block.entity.BlockEntityType<?>, Object>) beField.get(null);

            PillarBlockEntityRenderer pillarRenderer = new PillarBlockEntityRenderer();
            beProviders.put(ModBlockEntities.PILLAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> pillarRenderer.render((com.kingodogo.buildscape.block.PillarBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.HOLLOW_LOG_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> HollowLogBlockEntityRenderer.render((com.kingodogo.buildscape.block.HollowLogBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.SHELF_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> ShelfRenderer.render((com.kingodogo.buildscape.block.ShelfBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.FESTIVE_STOCKING_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> FestiveStockingBlockEntityRenderer.render((com.kingodogo.buildscape.block.FestiveStockingBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.GLASS_JAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> GlassJarBlockEntityRenderer.render((com.kingodogo.buildscape.block.GlassJarBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.ICICLE_CAULDRON_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> IcicleCauldronBlockEntityRenderer.render((com.kingodogo.buildscape.block.IcicleCauldronBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> DecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.DecoratedPotBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> TrappedDecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.TrappedDecoratedPotBlockEntity) be, pt, ps, bs, cl, co));
            beProviders.put(ModBlockEntities.COPPER_CHEST_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                    (be, pt, ps, bs, cl, co) -> CopperChestRenderer.render((com.kingodogo.buildscape.block.CopperChestBlockEntity) be, pt, ps, bs, cl, co));

            java.lang.reflect.Field eField = net.minecraft.client.renderer.entity.EntityRenderers.class.getDeclaredField("PROVIDERS");
            eField.setAccessible(true);
            java.util.Map<net.minecraft.world.entity.EntityType<?>, Object> eProviders =
                    (java.util.Map<net.minecraft.world.entity.EntityType<?>, Object>) eField.get(null);

            var homemakerType = (EntityType<WanderingTrader>) Services.PLATFORM.getWanderingHomemakerEntityType();
            if (homemakerType != null) {
                eProviders.put(homemakerType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                        new MobRenderer<WanderingTrader, WanderingHomemakerModel<WanderingTrader>>(
                                context, new WanderingHomemakerModel<>(context.bakeLayer(HOMEMAKER_LAYER)), 0.5F) {
                            @Override
                            public ResourceLocation getTextureLocation(WanderingTrader entity) {
                                return HOMEMAKER_TEXTURE;
                            }
                        }
                );
            }

            var festiveType = (EntityType<WanderingTrader>) Services.PLATFORM.getFestiveWanderingHomemakerEntityType();
            if (festiveType != null) {
                eProviders.put(festiveType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                        new MobRenderer<WanderingTrader, WanderingHomemakerModel<WanderingTrader>>(
                                context, new WanderingHomemakerModel<>(context.bakeLayer(HOMEMAKER_LAYER)), 0.5F) {
                            @Override
                            public ResourceLocation getTextureLocation(WanderingTrader entity) {
                                return FESTIVE_HOMEMAKER_TEXTURE;
                            }
                        }
                );
            }
        } catch (Throwable ignored) {}
    }

    public static void renderEntity(Entity entity, double x, double y, double z, float yaw, float partialTicks, PoseStack poseStack, Object bufferSource, int packedLight) {
        if (entity == null) return;
        try {
            Minecraft.getInstance().getEntityRenderDispatcher().render(
                    entity, x, y, z, yaw, partialTicks, poseStack, (MultiBufferSource) bufferSource, packedLight
            );
        } catch (Throwable ignored) {}
    }

    public static VertexConsumer getTranslucentBuffer(Object bufferSource) {
        if (bufferSource instanceof MultiBufferSource mbs) {
            return mbs.getBuffer(RenderType.translucent());
        }
        return null;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bodyDef = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(32, 45).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                .texOffs(0, 11).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F))
                .texOffs(28, 11).addBox(-6.0F, 4.5F, 3.25F, 12.0F, 10.0F, 6.0F)
                .texOffs(0, 0).addBox(-7.0F, 0.5F, 3.25F, 14.0F, 4.0F, 7.0F)
                .texOffs(66, 0).addBox(-8.0F, 4.5F, 4.75F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.5F))
                .texOffs(22, 13).addBox(-8.0F, 12.0F, 4.75F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.6F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition headDef = bodyDef.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(28, 27).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F)
                .texOffs(0, 45).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.55F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        headDef.addOrReplaceChild("nose", CubeListBuilder.create()
                .texOffs(60, 55).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -5.0F, -1.3090F, 0.0F, 0.0F));

        headDef.addOrReplaceChild("brim", CubeListBuilder.create()
                .texOffs(0, 35).addBox(-4.0F, -2.0F, -4.25F, 8.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.1534F, -2.4870F, 0.6981F, 0.0F, 0.0F));

        bodyDef.addOrReplaceChild("arms", CubeListBuilder.create()
                .texOffs(42, 0).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F)
                .texOffs(60, 43).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                .texOffs(60, 43).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create()
                .texOffs(60, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create()
                .texOffs(60, 27).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static class WanderingHomemakerModel<T extends WanderingTrader> extends HierarchicalModel<T> {
        private final ModelPart root;
        private final ModelPart head;
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;

        public WanderingHomemakerModel(ModelPart root) {
            this.root = root;
            ModelPart body = root.getChild("body");
            this.head = body.getChild("head");
            this.rightLeg = root.getChild("leg0");
            this.leftLeg = root.getChild("leg1");
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
            this.head.xRot = headPitch * ((float) Math.PI / 180F);
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.leftLeg.yRot = 0.0F;
        }

        @Override
        public ModelPart root() {
            return this.root;
        }
    }
}
