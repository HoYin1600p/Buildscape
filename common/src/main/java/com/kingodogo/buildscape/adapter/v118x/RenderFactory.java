package com.kingodogo.buildscape.adapter.v118x;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.block.ModBlockEntities;
import com.kingodogo.buildscape.client.renderer.ArmorPillarRenderer;
import com.kingodogo.buildscape.client.renderer.CopperChestRenderer;
import com.kingodogo.buildscape.client.renderer.DecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.FestiveStockingBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.GlassJarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.HollowLogBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.IcicleCauldronBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.MobPillarRenderer;
import com.kingodogo.buildscape.client.renderer.PipeWaterSurface;
import com.kingodogo.buildscape.client.renderer.PillarBlockEntityRenderer;
import com.kingodogo.buildscape.client.renderer.ShelfRenderer;
import com.kingodogo.buildscape.client.renderer.TrappedDecoratedPotBlockEntityRenderer;
import com.kingodogo.buildscape.pipe.transport.BubbleColumnState;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.kingodogo.buildscape.pipe.transport.PipeFluidTransport;
import com.kingodogo.buildscape.pipe.transport.WorldPipeTopologyAccess;
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
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public final class RenderFactory {

    public static final ModelLayerLocation HOMEMAKER_LAYER = new ModelLayerLocation(
            new ResourceLocation(BuildscapeCommon.MOD_ID, "wandering_homemaker"), "main");
    private static final ResourceLocation HOMEMAKER_TEXTURE = new ResourceLocation(
            BuildscapeCommon.MOD_ID, "textures/entity/wandering_homemaker.png");
    private static final ResourceLocation FESTIVE_HOMEMAKER_TEXTURE = new ResourceLocation(
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

    public static class TransparentMultiBufferSource implements MultiBufferSource {
        private final MultiBufferSource parent;
        private final float alpha;

        public TransparentMultiBufferSource(MultiBufferSource parent, float alpha) {
            this.parent = parent;
            this.alpha = alpha;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            return new AlphaVertexConsumer(parent.getBuffer(translucentVersion(renderType)), alpha);
        }

        private static RenderType translucentVersion(RenderType renderType) {
            if (renderType == Sheets.solidBlockSheet() || renderType == Sheets.cutoutBlockSheet()
                    || renderType == Sheets.translucentCullBlockSheet()) {
                return Sheets.translucentItemSheet();
            }
            if (renderType == Sheets.chestSheet()) return RenderType.entityTranslucentCull(Sheets.CHEST_SHEET);
            if (renderType == Sheets.shulkerBoxSheet()) return RenderType.entityTranslucentCull(Sheets.SHULKER_SHEET);
            if (renderType == Sheets.signSheet()) return RenderType.entityTranslucentCull(Sheets.SIGN_SHEET);
            if (renderType == Sheets.bannerSheet()) return RenderType.entityTranslucentCull(Sheets.BANNER_SHEET);
            if (renderType == Sheets.shieldSheet()) return RenderType.entityTranslucentCull(Sheets.SHIELD_SHEET);
            if (renderType == Sheets.bedSheet()) return RenderType.entityTranslucentCull(Sheets.BED_SHEET);
            return renderType;
        }

        private static final class AlphaVertexConsumer implements VertexConsumer {
            private final VertexConsumer parent;
            private final float alpha;

            private AlphaVertexConsumer(VertexConsumer parent, float alpha) {
                this.parent = parent;
                this.alpha = alpha;
            }

            @Override public VertexConsumer vertex(double x, double y, double z) { parent.vertex(x, y, z); return this; }
            @Override public VertexConsumer color(int red, int green, int blue, int alpha) { parent.color(red, green, blue, Math.round(alpha * this.alpha)); return this; }
            @Override public VertexConsumer uv(float u, float v) { parent.uv(u, v); return this; }
            @Override public VertexConsumer overlayCoords(int u, int v) { parent.overlayCoords(u, v); return this; }
            @Override public VertexConsumer uv2(int u, int v) { parent.uv2(u, v); return this; }
            @Override public VertexConsumer normal(float x, float y, float z) { parent.normal(x, y, z); return this; }
            @Override public void endVertex() { parent.endVertex(); }
            @Override public void defaultColor(int red, int green, int blue, int alpha) { parent.defaultColor(red, green, blue, Math.round(alpha * this.alpha)); }
            @Override public void unsetDefaultColor() { parent.unsetDefaultColor(); }
        }
    }

    public static final class PipeSpillVertexConsumer implements VertexConsumer {
        private static final double OPEN_MIN = 0.127;
        private static final double OPEN_MAX = 1.0 - OPEN_MIN;

        public record Outlet(Direction direction, double height) {}

        private final VertexConsumer delegate;
        private final List<Outlet> outlets;
        private final boolean downwardOutlet;
        private final int baseX, baseY, baseZ;
        private final Vertex[] quad = new Vertex[4];
        private int count;
        private double x, y, z;
        private int red = 255, green = 255, blue = 255, alpha = 255, overlayU, overlayV, lightU, lightV;
        private float u, v, nx, ny, nz;

        public PipeSpillVertexConsumer(VertexConsumer delegate, BlockPos pos, List<Outlet> outlets) {
            this.delegate = delegate;
            this.outlets = List.copyOf(outlets);
            downwardOutlet = outlets.stream().anyMatch(outlet -> outlet.direction == Direction.UP);
            baseX = pos.getX() & 15;
            baseY = pos.getY() & 15;
            baseZ = pos.getZ() & 15;
        }

        public static VertexConsumer wrap(VertexConsumer delegate, BlockAndTintGetter level, BlockPos pos,
                                          BlockState state, FluidState fluid) {
            List<Outlet> outlets = findOutlets(level, pos, state, fluid);
            return outlets.isEmpty() ? delegate : new PipeSpillVertexConsumer(delegate, pos, outlets);
        }

        public static List<Outlet> findOutlets(BlockAndTintGetter level, BlockPos pos, BlockState state, FluidState fluid) {
            if (!(state.getBlock() instanceof LiquidBlock) || !WorldPipeTopologyAccess.isTransportFluid(fluid.getType())
                    || fluid.isSource()) {
                return List.of();
            }
            BlockPos above = pos.above();
            BlockState pipeAbove = level.getBlockState(above);
            if (fluid.getValue(net.minecraft.world.level.material.FlowingFluid.FALLING)
                    && PipeFluidTransport.isHollowPipe(pipeAbove)
                    && level.getBlockEntity(above) instanceof HollowLogBlockEntity entity
                    && entity.getPipeFlowState().hasFluid()
                    && WorldPipeTopologyAccess.fluidById(entity.getPipeFlowState().getFluidId()).isSame(fluid.getType())
                    && PipeFluidTransport.isOpenEndpoint(pipeAbove, Direction.DOWN)
                    && entity.getPipeFlowState().hasFlowDirection(Direction.DOWN)) {
                return List.of(new Outlet(Direction.UP, 1.0));
            }
            if (fluid.getValue(net.minecraft.world.level.material.FlowingFluid.FALLING)
                    || level.getFluidState(pos.above()).getType().isSame(fluid.getType())) {
                return List.of();
            }
            List<Outlet> outlets = null;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos pipePos = pos.relative(direction);
                BlockState pipe = level.getBlockState(pipePos);
                Direction exit = direction.getOpposite();

                if (pipe.getBlock() instanceof HollowPipeBlock) {
                    if (!HollowPipeBlock.isOpenEndpoint(pipe, exit)
                            || !(level.getBlockEntity(pipePos) instanceof HollowLogBlockEntity entity)) {
                        continue;
                    }
                    PipeFlowState flow = entity.getPipeFlowState();
                    if (flow == null || !flow.hasFluid()
                            || !WorldPipeTopologyAccess.fluidById(flow.getFluidId()).isSame(fluid.getType())
                            || !flow.hasFlowDirection(exit) || flow.getDistance() >= 7
                            || flow.getInflowDirection() == Direction.UP
                            || (flow.hasFlowDirection(Direction.UP) && flow.getBubbleColumn() == BubbleColumnState.UP)) {
                        continue;
                    }
                    PipeWaterSurface.Heights heights = PipeWaterSurface.flowing(pipe, flow);
                    Direction.Axis axis = pipe.getValue(HollowPipeBlock.AXIS);
                    boolean straightX = (axis == Direction.Axis.X || pipe.getValue(HollowPipeBlock.WEST)
                            || pipe.getValue(HollowPipeBlock.EAST))
                            && !pipe.getValue(HollowPipeBlock.NORTH) && !pipe.getValue(HollowPipeBlock.SOUTH);
                    boolean straightZ = (axis == Direction.Axis.Z || pipe.getValue(HollowPipeBlock.NORTH)
                            || pipe.getValue(HollowPipeBlock.SOUTH))
                            && !pipe.getValue(HollowPipeBlock.WEST) && !pipe.getValue(HollowPipeBlock.EAST);
                    double height = straightX || straightZ ? heights.outlet() : heights.center();
                    if (straightX && exit == Direction.WEST && flow.getInflowDirection() == Direction.WEST) height = heights.inlet();
                    if (straightX && exit == Direction.EAST && flow.getInflowDirection() == Direction.EAST) height = heights.inlet();
                    if (straightZ && exit == Direction.NORTH && flow.getInflowDirection() == Direction.NORTH) height = heights.inlet();
                    if (straightZ && exit == Direction.SOUTH && flow.getInflowDirection() == Direction.SOUTH) height = heights.inlet();
                    if (outlets == null) outlets = new ArrayList<>(2);
                    outlets.add(new Outlet(direction, height));
                }
            }
            return outlets == null ? List.of() : outlets;
        }

        @Override public VertexConsumer vertex(double x, double y, double z) { this.x = x; this.y = y; this.z = z; return this; }
        @Override public VertexConsumer color(int r, int g, int b, int a) { red = r; green = g; blue = b; alpha = a; return this; }
        @Override public VertexConsumer uv(float u, float v) { this.u = u; this.v = v; return this; }
        @Override public VertexConsumer overlayCoords(int u, int v) { overlayU = u; overlayV = v; return this; }
        @Override public VertexConsumer uv2(int u, int v) { lightU = u; lightV = v; return this; }
        @Override public VertexConsumer normal(float x, float y, float z) { nx = x; ny = y; nz = z; return this; }
        @Override public void defaultColor(int r, int g, int b, int a) { color(r, g, b, a); }
        @Override public void unsetDefaultColor() {}

        @Override public void endVertex() {
            quad[count++] = new Vertex(x, y, z, u, v, red, green, blue, alpha, overlayU, overlayV, lightU, lightV, nx, ny, nz);
            if (count == 4) { renderQuad(); count = 0; }
        }

        private void renderQuad() {
            Vertex[] corners = new Vertex[4];
            for (Vertex vertex : quad) {
                double px = vertex.x - baseX, pz = vertex.z - baseZ;
                if (vertex.y <= baseY + 0.01 || vertex.y > baseY + 1.0 || (px != 0 && px != 1) || (pz != 0 && pz != 1)) {
                    emitOriginal(); return;
                }
                int index = px == 0 ? (pz == 0 ? 0 : 1) : (pz == 0 ? 3 : 2);
                if (corners[index] != null) { emitOriginal(); return; }
                corners[index] = vertex;
            }
            for (Vertex corner : corners) { if (corner == null) { emitOriginal(); return; } }
            if (downwardOutlet) { renderDownwardOutlet(corners); return; }
            List<Spill> spills = new ArrayList<>(outlets.size());
            TreeSet<Double> cuts = new TreeSet<>(List.of(0.0, OPEN_MIN, 0.25, 0.5, 0.75, OPEN_MAX, 1.0));
            for (Outlet outlet : outlets) {
                double edgeX = outlet.direction == Direction.WEST ? 0 : outlet.direction == Direction.EAST ? 1 : 0.5;
                double edgeZ = outlet.direction == Direction.NORTH ? 0 : outlet.direction == Direction.SOUTH ? 1 : 0.5;
                double gap = outlet.height - (sample(corners, edgeX, edgeZ).y - baseY);
                for (double across : new double[]{OPEN_MIN, OPEN_MAX}) {
                    double sideX = outlet.direction.getAxis() == Direction.Axis.X ? edgeX : across;
                    double sideZ = outlet.direction.getAxis() == Direction.Axis.Z ? edgeZ : across;
                    gap = Math.max(gap, outlet.height - (sample(corners, sideX, sideZ).y - baseY));
                }
                if (gap <= 0.001) continue;
                double length = Math.min(0.875, Math.max(0.25, gap * 2.0));
                spills.add(new Spill(outlet, length));
                cuts.add(length);
                cuts.add(1.0 - length);
            }
            if (spills.isEmpty()) { emitOriginal(); return; }
            Double[] grid = cuts.toArray(Double[]::new);
            boolean forward = quad[1].z > quad[0].z;
            for (int ix = 0; ix < grid.length - 1; ix++) {
                for (int iz = 0; iz < grid.length - 1; iz++) {
                    Vertex nw = raised(corners, spills, grid[ix], grid[iz]);
                    Vertex sw = raised(corners, spills, grid[ix], grid[iz + 1]);
                    Vertex se = raised(corners, spills, grid[ix + 1], grid[iz + 1]);
                    Vertex ne = raised(corners, spills, grid[ix + 1], grid[iz]);
                    emit(nw); emit(forward ? sw : ne); emit(se); emit(forward ? ne : sw);
                }
            }
        }

        private void renderDownwardOutlet(Vertex[] corners) {
            Vertex[] neck = new Vertex[4];
            for (int i = 0; i < 4; i++) {
                double px = i < 2 ? OPEN_MIN : OPEN_MAX;
                double pz = i == 0 || i == 3 ? OPEN_MIN : OPEN_MAX;
                Vertex sampled = sample(corners, px, pz);
                neck[i] = sampled.at(sampled.x, baseY + 1.0, sampled.z, sampled.u, sampled.v);
            }
            boolean forward = quad[1].z > quad[0].z;
            for (int i = 0; i < 4; i++) {
                int next = (i + 1) % 4;
                emit(neck[i]); emit(forward ? corners[i] : neck[next]);
                emit(corners[next]); emit(forward ? neck[next] : corners[i]);
            }
        }

        private Vertex raised(Vertex[] corners, List<Spill> spills, double x, double z) {
            Vertex original = sample(corners, x, z);
            double height = original.y;
            for (Spill spill : spills) {
                Direction direction = spill.outlet.direction;
                double distance = switch (direction) {
                    case WEST -> x; case EAST -> 1 - x; case NORTH -> z; case SOUTH -> 1 - z; default -> 1;
                };
                double across = direction.getAxis() == Direction.Axis.X ? z : x;
                double widthWeight = Math.min(1, Math.min(across / OPEN_MIN, (1 - across) / OPEN_MIN));
                double alongWeight = Math.max(0, 1 - distance / spill.length);
                double edgeX = direction == Direction.WEST ? 0 : direction == Direction.EAST ? 1 : x;
                double edgeZ = direction == Direction.NORTH ? 0 : direction == Direction.SOUTH ? 1 : z;
                double edgeHeight = sample(corners, edgeX, edgeZ).y;
                double lift = Math.max(0, baseY + spill.outlet.height - edgeHeight);
                height = Math.max(height, original.y + lift * widthWeight * alongWeight);
            }
            return original.at(original.x, height, original.z, original.u, original.v);
        }

        private Vertex sample(Vertex[] c, double x, double z) {
            Vertex a = c[0], b = x <= z ? c[1] : c[3], d = c[2];
            double wb = Math.abs(z - x), wd = Math.min(x, z), wa = 1 - wb - wd;
            return a.at(baseX + x, a.y * wa + b.y * wb + d.y * wd, baseZ + z,
                    (float)(a.u * wa + b.u * wb + d.u * wd), (float)(a.v * wa + b.v * wb + d.v * wd));
        }

        private void emitOriginal() { for (Vertex vertex : quad) emit(vertex); }
        private void emit(Vertex v) {
            delegate.vertex(v.x, v.y, v.z).color(v.red, v.green, v.blue, v.alpha).uv(v.u, v.v)
                    .overlayCoords(v.overlayU, v.overlayV).uv2(v.lightU, v.lightV).normal(v.nx, v.ny, v.nz).endVertex();
        }

        private record Spill(Outlet outlet, double length) {}
        private record Vertex(double x, double y, double z, float u, float v,
                              int red, int green, int blue, int alpha, int overlayU, int overlayV,
                              int lightU, int lightV, float nx, float ny, float nz) {
            Vertex at(double x, double y, double z, float u, float v) {
                return new Vertex(x, y, z, u, v, red, green, blue, alpha, overlayU, overlayV, lightU, lightV, nx, ny, nz);
            }
        }
    }
}
