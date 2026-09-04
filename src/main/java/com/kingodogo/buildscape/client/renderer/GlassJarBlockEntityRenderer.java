package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.GlassJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.EmptyModelData;

public class GlassJarBlockEntityRenderer implements BlockEntityRenderer<GlassJarBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;
    private static final float WOBBLE_DURATION = 10.0F;

    public GlassJarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            GlassJarBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay) {
        if (blockEntity == null) {
            return;
        }

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();
        int light = (level != null) ? LevelRenderer.getLightColor(level, pos) : combinedLight;

        long currentTick = (level != null) ? level.getGameTime() : 0;
        long wobbleStartTick = blockEntity.getWobbleStartedAtTick();

        float wobbleProgress = 0.0F;
        if (wobbleStartTick > 0) {
            float ticksSinceWobble = (float) (currentTick - wobbleStartTick) + partialTicks;
            if (ticksSinceWobble < WOBBLE_DURATION) {
                wobbleProgress = ticksSinceWobble / WOBBLE_DURATION;
            }
        }

        poseStack.pushPose();

        if (wobbleProgress > 0.0F && wobbleProgress < 1.0F) {
            float dampening = 1.0F - wobbleProgress;
            float oscillation = (float) Math.sin(wobbleProgress * Math.PI * 6);
            float rotationAngle = 8.0F * dampening * oscillation;

            poseStack.translate(0.5D, 0.0D, 0.5D);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotationAngle));
            poseStack.translate(-0.5D, 0.0D, -0.5D);
        }

        BakedModel jarModel = blockRenderer.getBlockModel(blockState);
        RenderType jarRenderType = ItemBlockRenderTypes.getRenderType(blockState, true);
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(jarRenderType),
                blockState,
                jarModel,
                1.0F, 1.0F, 1.0F,
                light,
                combinedOverlay,
                EmptyModelData.INSTANCE);

        if (blockEntity.hasLiquid()) {
            renderLiquid(blockEntity, poseStack, bufferSource, light, combinedOverlay);
        }
        else if (!blockEntity.isEmpty()) {
            ItemStack storedItem = blockEntity.getStoredItem();
            if (storedItem != null && !storedItem.isEmpty()) {
                int count = storedItem.getCount();
                int renderCount = Math.min(32, count);

                float startY = 0.07F;
                float yStep = 0.019F;
                BakedModel itemModel = this.itemRenderer.getModel(storedItem, level, null, 0);

                for (int i = 0; i < renderCount; i++) {
                    poseStack.pushPose();

                    float offsetX = getJitter(pos, i, 1) * 0.025F;
                    float offsetZ = getJitter(pos, i, 2) * 0.025F;
                    float currentY = startY + (i * yStep);

                    poseStack.translate(0.5D + offsetX, currentY, 0.5D + offsetZ);

                    float randomAngle = getDeterministicRandomAngle(pos, i);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(randomAngle));

                    poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));

                    poseStack.scale(0.42F, 0.42F, 0.42F);

                    this.itemRenderer.render(
                            storedItem,
                            ItemTransforms.TransformType.FIXED,
                            false,
                            poseStack,
                            bufferSource,
                            light,
                            combinedOverlay,
                            itemModel);

                    poseStack.popPose();
                }
            }
        }

        poseStack.popPose();
    }

    private void renderLiquid(
            GlassJarBlockEntity blockEntity,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light,
            int overlay) {
        ItemStack liquidItem = blockEntity.getStoredLiquidItem();
        int level = blockEntity.getLiquidLevel();
        if (liquidItem == null || liquidItem.isEmpty() || level <= 0)
            return;

        ResourceLocation textureLoc = new ResourceLocation("minecraft", "block/water_still");
        int color = 0xFF3F76E4;

        if (GlassJarBlockEntity.isXpLiquid(liquidItem)) {
            color = 0xFFFFFFFF;
            textureLoc = new ResourceLocation("buildscape", "fluid/experience_flow");
        } else if (liquidItem.getItem() instanceof PotionItem) {
            color = PotionUtils.getColor(liquidItem);
            if ((color & 0xFF000000) == 0) {
                color = 0xFF000000 | color;
            }
        } else if (liquidItem.getItem() instanceof HoneyBottleItem) {
            color = 0xFFFF9600;
            textureLoc = new ResourceLocation("minecraft", "block/honey_block_top");
        } else if (liquidItem.is(Items.MILK_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = new ResourceLocation("minecraft", "block/white_concrete");
        } else if (liquidItem.is(Items.LAVA_BUCKET)) {
            color = 0xFFFFFFFF;
            textureLoc = new ResourceLocation("minecraft", "block/lava_still");
        } else if (liquidItem.getItem() instanceof BucketItem bucket) {
            Fluid fluid = bucket.getFluid();
            if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                color = 0xFFFFFFFF;
                textureLoc = new ResourceLocation("minecraft", "block/lava_still");
            } else {
                color = 0xFF3F76E4;
                textureLoc = new ResourceLocation("minecraft", "block/water_still");
            }
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(textureLoc);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        int maxLevel = GlassJarBlockEntity.isXpLiquid(liquidItem)
                ? GlassJarBlockEntity.XP_BOTTLE_MAX
                : 16;
        float fillRatio = Math.min(maxLevel, level) / (float) maxLevel;
        float y1 = 0.07F;
        float y2 = y1 + (fillRatio * 0.65F);

        float x1 = 0.27F;
        float x2 = 0.73F;
        float z1 = 0.27F;
        float z2 = 0.73F;

        renderFluidBox(poseStack, buffer, x1, y1, z1, x2, y2, z2, sprite, color, light, overlay);
    }

    private static void renderFluidBox(
            PoseStack poseStack,
            VertexConsumer buffer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            TextureAtlasSprite sprite,
            int color,
            int light,
            int overlay) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F)
            a = 0.88F;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        Matrix4f matrix = poseStack.last().pose();

        addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 1, 0);

        addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 0, -1);
        addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 0, -1);
        addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v1, light, overlay, 0, 0, -1);
        addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v1, light, overlay, 0, 0, -1);

        addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, 0, 0, 1);
        addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, 0, 1);
        addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v0, light, overlay, 0, 0, 1);
        addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v0, light, overlay, 0, 0, 1);

        addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v1, light, overlay, -1, 0, 0);
        addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u1, v1, light, overlay, -1, 0, 0);
        addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u1, v0, light, overlay, -1, 0, 0);
        addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, -1, 0, 0);

        addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u0, v0, light, overlay, 1, 0, 0);
        addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v0, light, overlay, 1, 0, 0);
        addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 1, 0, 0);
        addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u0, v1, light, overlay, 1, 0, 0);
    }

    private static void addVertex(
            Matrix4f matrix, VertexConsumer buffer,
            float x, float y, float z,
            float r, float g, float b, float a,
            float u, float v,
            int light, int overlay,
            float nx, float ny, float nz) {
        buffer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(nx, ny, nz)
                .endVertex();
    }

    private float getDeterministicRandomAngle(BlockPos pos, int index) {
        long seed = (pos.getX() * 3129871L) ^ (pos.getZ() * 116129781L) ^ (pos.getY() * 9999991L) + index * 10007L;
        long hash = (seed ^ (seed >>> 16)) * 0x45d9f3bL;
        hash = (hash ^ (hash >>> 16)) * 0x45d9f3bL;
        hash = hash ^ (hash >>> 16);
        return (float) (Math.abs(hash % 360));
    }

    private float getJitter(BlockPos pos, int index, int salt) {
        long seed = (pos.getX() * 3129871L) ^ (pos.getZ() * 116129781L)
                ^ (pos.getY() * 9999991L) + index * 10007L + salt * 17L;
        long hash = (seed ^ (seed >>> 16)) * 0x45d9f3bL;
        hash = (hash ^ (hash >>> 16)) * 0x45d9f3bL;
        hash = hash ^ (hash >>> 16);
        return ((hash % 100) - 50) / 50.0F;
    }
}
