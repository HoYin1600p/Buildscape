package com.kingodogo.buildscape.adapter.v121x.client;
import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.client.renderer.ArmorPillarRenderer;
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

    private static final ResourceLocation HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        BuildscapeCommon.MOD_ID, "textures/entity/wandering_homemaker.png");
    private static final ResourceLocation FESTIVE_HOMEMAKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        BuildscapeCommon.MOD_ID, "textures/entity/festive_wandering_homemaker.png");

    public static final net.minecraft.client.model.geom.ModelLayerLocation HOMEMAKER_LAYER = new net.minecraft.client.model.geom.ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(BuildscapeCommon.MOD_ID, "wandering_homemaker"), "main");
    public interface Registrar {
        <T extends net.minecraft.world.level.block.entity.BlockEntity> void registerBlockEntity(
                net.minecraft.world.level.block.entity.BlockEntityType<T> type,
                net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider<T> provider);
        <T extends net.minecraft.world.entity.Entity> void registerEntity(EntityType<T> type,
                net.minecraft.client.renderer.entity.EntityRendererProvider<T> provider);
    }
    public static void registerLayerDefinitions(java.util.function.BiConsumer<Object, java.util.function.Supplier<Object>> registrar) {
        registrar.accept(HOMEMAKER_LAYER, RenderFactory::createBodyLayer);
    }
    private RenderFactory() {}

    private static Registrar activeRegistrar;

    public static void withRegistrar(Registrar registrar, Runnable action) {
        Registrar previous = activeRegistrar;
        activeRegistrar = registrar;
        try { action.run(); }
        finally { activeRegistrar = previous; }
    }

    public static void registerRenderers() {
        if (activeRegistrar == null) throw new IllegalStateException("Renderers require their loader registration event");
        registerRenderers(activeRegistrar);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerRenderers(Registrar registrar) {
        PillarBlockEntityRenderer pillarRenderer = new PillarBlockEntityRenderer();
        registrar.registerBlockEntity(ModBlockEntities.PILLAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> pillarRenderer.render((com.kingodogo.buildscape.block.PillarBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.HOLLOW_LOG_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> HollowLogBlockEntityRenderer.render((com.kingodogo.buildscape.block.HollowLogBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.SHELF_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> ShelfRenderer.render((com.kingodogo.buildscape.block.ShelfBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.FESTIVE_STOCKING_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> FestiveStockingBlockEntityRenderer.render((com.kingodogo.buildscape.block.FestiveStockingBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.GLASS_JAR_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> GlassJarBlockEntityRenderer.render((com.kingodogo.buildscape.block.GlassJarBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.ICICLE_CAULDRON_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> IcicleCauldronBlockEntityRenderer.render((com.kingodogo.buildscape.block.IcicleCauldronBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> DecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.DecoratedPotBlockEntity) be, pt, ps, bs, cl, co));
        registrar.registerBlockEntity(ModBlockEntities.TRAPPED_DECORATED_POT_TYPE, (net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider) context ->
                (be, pt, ps, bs, cl, co) -> TrappedDecoratedPotBlockEntityRenderer.render((com.kingodogo.buildscape.block.TrappedDecoratedPotBlockEntity) be, pt, ps, bs, cl, co));

        registrar.registerBlockEntity(com.kingodogo.buildscape.adapter.v121x.RestoredContentTypes.<com.kingodogo.buildscape.block.CopperChestBlockEntity>blockEntity("copper_chest"), CopperChestRenderer::new);
        registrar.registerEntity((EntityType<Entity>) Services.PLATFORM.getSeatEntityType(),
                net.minecraft.client.renderer.entity.NoopRenderer::new);
        registrar.registerEntity((EntityType<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FallingIcicleEntityImpl>) Services.PLATFORM.getFallingIcicleEntityType(),
                context -> new EntityRenderer<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FallingIcicleEntityImpl>(context) {
                    @Override public ResourceLocation getTextureLocation(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FallingIcicleEntityImpl entity) {
                        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
                    }
                    @Override public void render(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FallingIcicleEntityImpl entity,
                            float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
                        com.kingodogo.buildscape.client.renderer.FallingIcicleRenderer.render(entity, yaw, partialTick, pose, buffers, light);
                        super.render(entity, yaw, partialTick, pose, buffers, light);
                    }
                });
        registrar.registerEntity((EntityType<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.ColoredItemFrameEntityImpl>) Services.PLATFORM.getColoredItemFrameEntityType(),
                context -> new EntityRenderer<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.ColoredItemFrameEntityImpl>(context) {
                    @Override public ResourceLocation getTextureLocation(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.ColoredItemFrameEntityImpl entity) {
                        return ResourceLocation.parse(com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer.getTextureForColor(entity.getColorVariant()).toString());
                    }
                    @Override public net.minecraft.world.phys.Vec3 getRenderOffset(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.ColoredItemFrameEntityImpl entity, float partialTick) {
                        return com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer.getRenderOffset(entity, partialTick);
                    }
                    @Override public void render(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.ColoredItemFrameEntityImpl entity,
                            float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
                        com.kingodogo.buildscape.client.renderer.ColoredItemFrameRenderer.render(entity, yaw, partialTick, pose, buffers, light);
                        super.render(entity, yaw, partialTick, pose, buffers, light);
                    }
                });
        registrar.registerEntity((EntityType<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FestiveStockingEntityImpl>) Services.PLATFORM.getFestiveStockingEntityType(),
                context -> new EntityRenderer<com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FestiveStockingEntityImpl>(context) {
                    @Override public ResourceLocation getTextureLocation(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FestiveStockingEntityImpl entity) {
                        return ResourceLocation.parse(com.kingodogo.buildscape.client.renderer.FestiveStockingRenderer.getTextureForColor(entity.getColorVariant()).toString());
                    }
                    @Override public void render(com.kingodogo.buildscape.adapter.v121x.PlatformAdapterBase.FestiveStockingEntityImpl entity,
                            float yaw, float partialTick, PoseStack pose, MultiBufferSource buffers, int light) {
                        com.kingodogo.buildscape.client.renderer.FestiveStockingRenderer.render(entity, yaw, partialTick, pose, buffers, light);
                        super.render(entity, yaw, partialTick, pose, buffers, light);
                    }
                });
        var homemakerType = (EntityType<WanderingTrader>) Services.PLATFORM.getWanderingHomemakerEntityType();
        if (homemakerType != null) {
            registrar.registerEntity(homemakerType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                    new MobRenderer<WanderingTrader, com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.WanderingHomemakerModel<WanderingTrader>>(
                            context, new com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.WanderingHomemakerModel<>(context.bakeLayer(com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.HOMEMAKER_LAYER)), 0.5F) {
                        @Override
                        public ResourceLocation getTextureLocation(WanderingTrader entity) {
                            return HOMEMAKER_TEXTURE;
                        }
                    }
            );
        }

        var festiveType = (EntityType<WanderingTrader>) Services.PLATFORM.getFestiveWanderingHomemakerEntityType();
        if (festiveType != null) {
            registrar.registerEntity(festiveType, (net.minecraft.client.renderer.entity.EntityRendererProvider) context ->
                    new MobRenderer<WanderingTrader, com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.WanderingHomemakerModel<WanderingTrader>>(
                            context, new com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.WanderingHomemakerModel<>(context.bakeLayer(com.kingodogo.buildscape.adapter.v121x.client.RenderFactory.HOMEMAKER_LAYER)), 0.5F) {
                        @Override
                        public ResourceLocation getTextureLocation(WanderingTrader entity) {
                            return FESTIVE_HOMEMAKER_TEXTURE;
                        }
                    }
            );
        }

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

