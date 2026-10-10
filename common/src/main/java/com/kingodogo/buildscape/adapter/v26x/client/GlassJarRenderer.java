package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;

/** Extracts jar contents before submitting; no live block entity is retained by a draw. */
public final class GlassJarRenderer implements BlockEntityRenderer<GlassJarBlockEntity, GlassJarRenderer.State> {
    public static final class State extends BlockEntityRenderState {
        final BlockModelRenderState body = new BlockModelRenderState();
        final ItemStackRenderState food = new ItemStackRenderState();
        BlockPos seedPos = BlockPos.ZERO;
        int count, liquidColor;
        float liquidHeight, wobbleAngle;
        TextureAtlasSprite liquidSprite;
        boolean renderBody;
    }

    @Override public State createRenderState() { return new State(); }

    @Override public void extractRenderState(GlassJarBlockEntity entity, State state, float partialTick,
            Vec3 camera, ModelFeatureRenderer.CrumblingOverlay breaking) {
        BlockEntityRenderState.extractBase(entity, state, breaking);
        // MODEL jars already have a chunk mesh. Do not submit a second overlapping shell.
        extractContents(entity, state, partialTick, entity.getBlockState().getRenderShape() != RenderShape.MODEL);
    }

    static void extractContents(GlassJarBlockEntity entity, State state, float partialTick, boolean renderBody) {
        Minecraft client = Minecraft.getInstance();
        state.renderBody = renderBody;
        state.body.clear();
        if (renderBody) {
            new BlockModelResolver(client.getModelManager()).update(state.body, entity.getBlockState(),
                    BlockDisplayContext.create());
        }
        state.seedPos = entity.getBlockPos().immutable();
        state.wobbleAngle = 0;
        if (entity.getLevel() != null && entity.getWobbleStartedAtTick() > 0) {
            float elapsed = entity.getLevel().getGameTime() - entity.getWobbleStartedAtTick() + partialTick;
            if (elapsed > 0 && elapsed < 10) {
                float progress = elapsed / 10;
                state.wobbleAngle = 8 * (1 - progress) * (float) Math.sin(progress * Math.PI * 6);
            }
        }
        state.food.clear();
        ItemStack stored = entity.getStoredItem();
        state.count = entity.hasLiquid() ? 0 : Math.min(32, entity.getItemCount());
        if (state.count > 0) {
            client.getItemModelResolver().updateForTopItem(state.food, stored, ItemDisplayContext.FIXED,
                    entity.getLevel(), null, 0);
        }
        state.liquidSprite = null;
        state.liquidHeight = 0;
        if (entity.hasLiquid()) {
            ItemStack liquid = entity.getStoredLiquidItem();
            state.liquidColor = liquidColor(liquid);
            state.liquidHeight = fillHeight(entity.getLiquidLevel(), GlassJarBlockEntity.getLiquidCap(liquid));
            state.liquidSprite = Services.PLATFORM.getBlockAtlasSprite(CommonId.parse(liquidTexture(liquid)));
        }
    }

    public static float fillHeight(int amount, int capacity) {
        return 0.07F + Math.max(0, Math.min(capacity, amount)) / (float) capacity * 0.65F;
    }

    public static int liquidColor(ItemStack liquid) {
        if (GlassJarBlockEntity.isXpLiquid(liquid) || liquid.is(Items.MILK_BUCKET) || liquid.is(Items.LAVA_BUCKET)) return -1;
        if (liquid.is(Items.HONEY_BOTTLE)) return 0xFFFF9600;
        if (liquid.getItem() instanceof PotionItem) {
            return 0xFF000000 | liquid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
        }
        return 0xFF3F76E4;
    }

    public static String liquidTexture(ItemStack liquid) {
        if (GlassJarBlockEntity.isXpLiquid(liquid)) return "buildscape:fluid/experience_flow";
        if (liquid.is(Items.HONEY_BOTTLE)) return "minecraft:block/honey_block_top";
        if (liquid.is(Items.MILK_BUCKET)) return "minecraft:block/white_concrete";
        if (liquid.is(Items.LAVA_BUCKET)) return "minecraft:block/lava_still";
        return "minecraft:block/water_still";
    }

    @Override public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        submitContents(state, pose, collector, state.lightCoords,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 0);
    }

    static void submitContents(State state, PoseStack pose, SubmitNodeCollector collector,
            int light, int overlay, int outlineColor) {
        pose.pushPose();
        try {
            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.ZP.rotationDegrees(state.wobbleAngle));
            pose.translate(-0.5, 0, -0.5);
            if (state.liquidSprite != null) {
                collector.submitCustomGeometry(pose, RenderTypes.entityTranslucent(TextureAtlas.LOCATION_BLOCKS),
                        (transform, consumer) -> submitLiquid(state, transform, consumer, light, overlay));
            }
            for (int i = 0; i < state.count; i++) {
                pose.pushPose();
                try {
                    pose.translate(0.5 + jitter(state.seedPos, i, 1) * 0.025F, 0.07F + i * 0.019F,
                            0.5 + jitter(state.seedPos, i, 2) * 0.025F);
                    pose.mulPose(Axis.YP.rotationDegrees(angle(state.seedPos, i)));
                    pose.mulPose(Axis.XP.rotationDegrees(90));
                    pose.scale(0.42F, 0.42F, 0.42F);
                    state.food.submit(pose, collector, light, overlay, outlineColor);
                } finally { pose.popPose(); }
            }
            if (state.renderBody) state.body.submit(pose, collector, light, overlay, outlineColor);
        } finally { pose.popPose(); }
    }

    private static long hash(long seed) {
        long hash = (seed ^ (seed >>> 16)) * 0x45d9f3bL;
        hash = (hash ^ (hash >>> 16)) * 0x45d9f3bL;
        return hash ^ (hash >>> 16);
    }

    private static long seed(BlockPos pos, int index, int salt) {
        return (pos.getX() * 3129871L) ^ (pos.getZ() * 116129781L)
                ^ (pos.getY() * 9999991L) + index * 10007L + salt * 17L;
    }

    private static float angle(BlockPos pos, int index) { return Math.abs(hash(seed(pos, index, 0)) % 360); }
    private static float jitter(BlockPos pos, int index, int salt) { return ((hash(seed(pos, index, salt)) % 100) - 50) / 50.0F; }

    static void submitLiquid(State state, PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay) {
        float lo = 0.27F, hi = 0.73F, bottom = 0.07F, top = state.liquidHeight;
        quad(state, pose, consumer, light, overlay, 0, 1, 0,
                lo, top, lo, lo, top, hi, hi, top, hi, hi, top, lo);
        quad(state, pose, consumer, light, overlay, 0, 0, -1,
                lo, top, lo, hi, top, lo, hi, bottom, lo, lo, bottom, lo);
        quad(state, pose, consumer, light, overlay, 0, 0, 1,
                lo, bottom, hi, hi, bottom, hi, hi, top, hi, lo, top, hi);
        quad(state, pose, consumer, light, overlay, -1, 0, 0,
                lo, bottom, lo, lo, bottom, hi, lo, top, hi, lo, top, lo);
        quad(state, pose, consumer, light, overlay, 1, 0, 0,
                hi, top, lo, hi, top, hi, hi, bottom, hi, hi, bottom, lo);
    }

    private static void quad(State state, PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay,
            float nx, float ny, float nz, float... coordinates) {
        TextureAtlasSprite sprite = state.liquidSprite;
        for (int i = 0; i < 4; i++) {
            boolean firstU = ny == 1 ? i < 2 : i == 0 || i == 3;
            boolean firstV = ny == 1 ? i == 0 || i == 3 : i < 2;
            if (nz == 1 || nx == -1) firstV = !firstV;
            float u = firstU ? sprite.getU0() : sprite.getU1();
            float v = firstV ? sprite.getV0() : sprite.getV1();
            consumer.addVertex(pose, coordinates[i * 3], coordinates[i * 3 + 1], coordinates[i * 3 + 2])
                    .setColor(state.liquidColor).setUv(u, v).setOverlay(overlay).setLight(light)
                    .setNormal(pose, nx, ny, nz);
        }
    }
}
