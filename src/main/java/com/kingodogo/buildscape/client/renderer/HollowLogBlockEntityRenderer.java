package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HollowLogBlockEntityRenderer implements BlockEntityRenderer<HollowLogBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public HollowLogBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public int getViewDistance() {
        return 48; // Limit interior detail rendering distance to 48 blocks
    }

    @Override
    public void render(
            HollowLogBlockEntity blockEntity,
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

        BlockState decoration = blockEntity.getDecorationState();
        BlockState glassNeg = blockEntity.getGlassCoverNeg();
        BlockState glassPos = blockEntity.getGlassCoverPos();

        boolean hasDecoration = decoration != null && !decoration.isAir();
        boolean hasGlassNeg = glassNeg != null && !glassNeg.isAir();
        boolean hasGlassPos = glassPos != null && !glassPos.isAir();
        String fluidType = blockEntity.getFluidType();
        boolean isLavaLogged = blockState.hasProperty(HollowLogBlock.LAVA_LOGGED) && blockState.getValue(HollowLogBlock.LAVA_LOGGED);
        boolean isWaterLogged = blockState.hasProperty(HollowLogBlock.WATERLOGGED) && blockState.getValue(HollowLogBlock.WATERLOGGED);
        boolean hasFluid = isLavaLogged || isWaterLogged || (!"none".equals(fluidType) && !fluidType.isEmpty());

        // Fast early exit: skip empty Hollow Logs completely!
        if (!hasDecoration && !hasGlassNeg && !hasGlassPos && !hasFluid) {
            return;
        }

        // 1. Render Fluid Interior (Vanilla Water/Lava, Buildscape Experience, or any Modded Fluid)
        Fluid fluid = null;
        if (isLavaLogged || "lava".equals(fluidType)) {
            fluid = net.minecraft.world.level.material.Fluids.LAVA;
        } else if (isWaterLogged || "water".equals(fluidType)) {
            fluid = net.minecraft.world.level.material.Fluids.WATER;
        } else if ("experience".equals(fluidType) || "buildscape:experience_still".equals(fluidType) || "buildscape:experience".equals(fluidType)) {
            fluid = com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get();
        } else if (!"none".equals(fluidType) && !fluidType.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(fluidType);
            if (rl != null && net.minecraftforge.registries.ForgeRegistries.FLUIDS.containsKey(rl)) {
                fluid = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(rl);
            }
        }

        if (fluid != null && fluid != net.minecraft.world.level.material.Fluids.EMPTY) {
            ResourceLocation stillTex = fluid.getAttributes().getStillTexture();
            if (stillTex == null) {
                stillTex = (fluid == net.minecraft.world.level.material.Fluids.LAVA)
                        ? new ResourceLocation("minecraft", "block/lava_still")
                        : new ResourceLocation("minecraft", "block/water_still");
            }
            int color = fluid.getAttributes().getColor(level, pos);
            if (color == 0xFFFFFFFF && fluid == net.minecraft.world.level.material.Fluids.WATER) {
                color = (level != null && pos != null) ? BiomeColors.getAverageWaterColor(level, pos) : 0x3F76E4;
            }
            renderFluidInterior(blockState, poseStack, bufferSource, stillTex, color, light, combinedOverlay);
        }

        // 2. Render Interior Decoration
        if (hasDecoration) {
            poseStack.pushPose();
            if (decoration.getBlock() instanceof FlowerPotBlock) {
                // Center flower pot inside horizontal or vertical log floor, uniform scale ~0.85
                poseStack.translate(0.5D, 0.125D, 0.5D);
                poseStack.scale(0.85F, 0.85F, 0.85F);
                poseStack.translate(-0.5D, 0.0D, -0.5D);
                renderBlockState(decoration, pos, level, poseStack, bufferSource, light, combinedOverlay);
            } else {
                // Recess 1x1x1 full block 1 pixel (0.0625) inside interior
                poseStack.translate(0.0625D, 0.0625D, 0.0625D);
                poseStack.scale(0.875F, 0.875F, 0.875F);
                renderBlockState(decoration, pos, level, poseStack, bufferSource, light, combinedOverlay);
            }
            poseStack.popPose();
        }

        Direction.Axis axis = blockState.hasProperty(HollowLogBlock.AXIS) ? blockState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;

        // 3. Render Glass Cover Negative Face
        if (hasGlassNeg) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, false);
            renderBlockState(glassNeg, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }

        // 4. Render Glass Cover Positive Face
        if (hasGlassPos) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, true);
            renderBlockState(glassPos, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }
    }

    private void renderBlockState(BlockState state, BlockPos pos, Level level, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        BakedModel model = blockRenderer.getBlockModel(state);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(state, true);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;

        int color = (level != null && pos != null)
                ? Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0)
                : Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        if (color != -1) {
            r = ((color >> 16) & 0xFF) / 255.0F;
            g = ((color >> 8) & 0xFF) / 255.0F;
            b = (color & 0xFF) / 255.0F;
        }

        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                buffer,
                state,
                model,
                r, g, b,
                light,
                overlay,
                EmptyModelData.INSTANCE);
    }

    private void positionGlassCover(PoseStack poseStack, Direction.Axis axis, boolean isPositive) {
        float glassThickness = 0.0625F;
        double negOffset = 0.015625D; // 0.25 pixel inward
        double posOffset = 1.0D - 0.015625D - glassThickness; // 0.921875D

        switch (axis) {
            case X:
                if (isPositive) {
                    poseStack.translate(posOffset, 0.125D, 0.125D);
                } else {
                    poseStack.translate(negOffset, 0.125D, 0.125D);
                }
                poseStack.scale(glassThickness, 0.75F, 0.75F);
                break;
            case Z:
                if (isPositive) {
                    poseStack.translate(0.125D, 0.125D, posOffset);
                } else {
                    poseStack.translate(0.125D, 0.125D, negOffset);
                }
                poseStack.scale(0.75F, 0.75F, glassThickness);
                break;
            default: // Y
                if (isPositive) {
                    poseStack.translate(0.125D, posOffset, 0.125D);
                } else {
                    poseStack.translate(0.125D, negOffset, 0.125D);
                }
                poseStack.scale(0.75F, glassThickness, 0.75F);
                break;
        }
    }

    private void renderFluidInterior(BlockState blockState, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation textureLoc, int color, int light, int overlay) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLoc);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        if (blockState.getBlock() instanceof HollowPipeBlock) {
            boolean down  = blockState.hasProperty(HollowPipeBlock.DOWN) && blockState.getValue(HollowPipeBlock.DOWN);
            boolean up    = blockState.hasProperty(HollowPipeBlock.UP) && blockState.getValue(HollowPipeBlock.UP);
            boolean north = blockState.hasProperty(HollowPipeBlock.NORTH) && blockState.getValue(HollowPipeBlock.NORTH);
            boolean south = blockState.hasProperty(HollowPipeBlock.SOUTH) && blockState.getValue(HollowPipeBlock.SOUTH);
            boolean west  = blockState.hasProperty(HollowPipeBlock.WEST) && blockState.getValue(HollowPipeBlock.WEST);
            boolean east  = blockState.hasProperty(HollowPipeBlock.EAST) && blockState.getValue(HollowPipeBlock.EAST);

            int count = (down ? 1 : 0) + (up ? 1 : 0) + (north ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0) + (east ? 1 : 0);

            if (count == 0) {
                Direction.Axis axis = blockState.hasProperty(HollowPipeBlock.AXIS) ? blockState.getValue(HollowPipeBlock.AXIS) : Direction.Axis.Y;
                renderStraightFluid(axis, poseStack, buffer, sprite, color, light, overlay);
            } else if (count == 1 || (count == 2 && ((down && up) || (north && south) || (west && east)))) {
                Direction.Axis axis = (down || up) ? Direction.Axis.Y : ((north || south) ? Direction.Axis.Z : Direction.Axis.X);
                renderStraightFluid(axis, poseStack, buffer, sprite, color, light, overlay);
            } else {
                // Multi-directional junction
                float x1 = west  ? 0.0F : 0.125F;
                float x2 = east  ? 1.0F : 0.875F;
                float y1 = down  ? 0.0F : 0.125F;
                float y2 = up    ? 1.0F : 0.8125F;
                float z1 = north ? 0.0F : 0.125F;
                float z2 = south ? 1.0F : 0.875F;

                renderFluidJunction(poseStack, buffer, x1, y1, z1, x2, y2, z2, down, up, north, south, west, east, sprite, color, light, overlay);
            }
        } else {
            Direction.Axis axis = blockState.hasProperty(HollowLogBlock.AXIS) ? blockState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
            renderStraightFluid(axis, poseStack, buffer, sprite, color, light, overlay);
        }
    }

    private static void renderStraightFluid(Direction.Axis axis, PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int color, int light, int overlay) {
        float x1 = 0.125F, x2 = 0.875F;
        float y1 = 0.125F, y2 = 0.8125F;
        float z1 = 0.125F, z2 = 0.875F;

        switch (axis) {
            case X:
                x1 = 0.0F; x2 = 1.0F;
                break;
            case Z:
                z1 = 0.0F; z2 = 1.0F;
                break;
            default: // Y
                y1 = 0.0F; y2 = 0.8125F;
                break;
        }

        renderFluidBox(poseStack, buffer, axis, x1, y1, z1, x2, y2, z2, sprite, color, light, overlay);
    }

    private static void renderFluidJunction(
            PoseStack poseStack, VertexConsumer buffer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            boolean down, boolean up, boolean north, boolean south, boolean west, boolean east,
            TextureAtlasSprite sprite, int color, int light, int overlay) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F) a = 0.88F;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        Matrix4f matrix = poseStack.last().pose();

        // Top face (ALWAYS rendered for fluid surface)
        addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 1, 0);

        // North face
        if (north) {
            addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v1, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v1, light, overlay, 0, 0, -1);
        }

        // South face
        if (south) {
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v0, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v0, light, overlay, 0, 0, 1);
        }

        // West face
        if (west) {
            addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u1, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u1, v1, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, -1, 0, 0);
        }

        // East face
        if (east) {
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u0, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u0, v0, light, overlay, 1, 0, 0);
        }

        // Bottom face
        if (down) {
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
        }
    }

    private static void renderFluidBox(
            PoseStack poseStack, VertexConsumer buffer, Direction.Axis axis,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            TextureAtlasSprite sprite, int color, int light, int overlay) {
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        if (a == 0.0F) a = 0.88F;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        Matrix4f matrix = poseStack.last().pose();

        // Top face (ALWAYS rendered for fluid surface)
        addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v1, light, overlay, 0, 1, 0);
        addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 1, 0);

        // North face (Open direction for Axis.Z)
        if (axis == Direction.Axis.Z) {
            addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u0, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v1, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v1, light, overlay, 0, 0, -1);
        }

        // South face (Open direction for Axis.Z)
        if (axis == Direction.Axis.Z) {
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u1, v0, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v0, light, overlay, 0, 0, 1);
        }

        // West face (Open direction for Axis.X)
        if (axis == Direction.Axis.X) {
            addVertex(matrix, buffer, x1, y2, z2, r, g, b, a, u0, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y2, z1, r, g, b, a, u1, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u1, v1, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, -1, 0, 0);
        }

        // East face (Open direction for Axis.X)
        if (axis == Direction.Axis.X) {
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u0, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y2, z1, r, g, b, a, u1, v0, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y2, z2, r, g, b, a, u0, v0, light, overlay, 1, 0, 0);
        }

        // Bottom face (Only rendered for Axis.Y if y1 == 0.0F)
        if (axis == Direction.Axis.Y && y1 == 0.0F) {
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
        }
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
}

