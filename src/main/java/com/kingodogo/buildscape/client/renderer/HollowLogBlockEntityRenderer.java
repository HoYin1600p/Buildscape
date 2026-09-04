package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.BubbleColumnState;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Set;

public class HollowLogBlockEntityRenderer implements BlockEntityRenderer<HollowLogBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public HollowLogBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public int getViewDistance() {
        return 48;
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
        boolean hasGlassNeg = (glassNeg != null && !glassNeg.isAir())
                || (blockState.hasProperty(HollowLogBlock.HAS_GLASS_NEG) && blockState.getValue(HollowLogBlock.HAS_GLASS_NEG));
        boolean hasGlassPos = (glassPos != null && !glassPos.isAir())
                || (blockState.hasProperty(HollowLogBlock.HAS_GLASS_POS) && blockState.getValue(HollowLogBlock.HAS_GLASS_POS));

        // 1. Render Contained Fluid in Hollow Pipe or Hollow Log
        if (blockState.getBlock() instanceof HollowPipeBlock) {
            renderPipeFluid(level, pos, blockState, blockEntity, poseStack, bufferSource, light, combinedOverlay);
        } else if (blockState.getBlock() instanceof HollowLogBlock) {
            renderLogFluid(level, pos, blockState, blockEntity, poseStack, bufferSource, light, combinedOverlay, hasGlassNeg, hasGlassPos);
        }

        if (!hasDecoration && !hasGlassNeg && !hasGlassPos) {
            return;
        }

        // 2. Render Nested / Inset Decoration Block
        if (hasDecoration) {
            poseStack.pushPose();
            if (decoration.getBlock() instanceof FlowerPotBlock) {
                poseStack.translate(0.5D, 0.125D, 0.5D);
                poseStack.scale(0.85F, 0.85F, 0.85F);
                poseStack.translate(-0.5D, 0.0D, -0.5D);
                renderBlockState(decoration, pos, level, poseStack, bufferSource, light, combinedOverlay);
            } else if (decoration.getBlock() instanceof HollowLogBlock) {
                renderBlockState(decoration, pos, level, poseStack, bufferSource, light, combinedOverlay);
            } else {
                poseStack.translate(0.0625D, 0.0625D, 0.0625D);
                poseStack.scale(0.875F, 0.875F, 0.875F);
                renderBlockState(decoration, pos, level, poseStack, bufferSource, light, combinedOverlay);
            }
            poseStack.popPose();
        }

        Direction.Axis axis = blockState.hasProperty(HollowLogBlock.AXIS) ? blockState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;

        // 3. Render Glass Cover Negative Face
        if (hasGlassNeg && glassNeg != null && !glassNeg.isAir()) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, false);
            renderBlockState(glassNeg, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }

        // 4. Render Glass Cover Positive Face
        if (hasGlassPos && glassPos != null && !glassPos.isAir()) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, true);
            renderBlockState(glassPos, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }
    }

    private void renderPipeFluid(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity blockEntity,
                                 PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        PipeFlowState flowState = blockEntity.getPipeFlowState();
        Fluid fluid = HollowPipeBlock.getContainedFluid(state, blockEntity);
        boolean hasWater = (fluid == Fluids.WATER) || (flowState != null && flowState.hasWater());
        boolean hasLava = (fluid == Fluids.LAVA) || (state.hasProperty(HollowPipeBlock.LAVA_LOGGED) && state.getValue(HollowPipeBlock.LAVA_LOGGED));
        boolean hasXp = (fluid == com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get())
                || (fluid == com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_FLOWING.get());

        if (fluid == null || fluid == Fluids.EMPTY) {
            if (hasWater) {
                fluid = Fluids.WATER;
            } else if (hasLava) {
                fluid = Fluids.LAVA;
            } else {
                return;
            }
        }

        boolean connDown = state.getValue(HollowPipeBlock.DOWN);
        float yFloor = connDown ? 0.0F : 0.125F;

        Direction inDir = flowState != null ? flowState.getInflowDirection() : null;
        Set<Direction> outDirs = flowState != null ? flowState.getFlowDirections() : Set.of();

        boolean flowFromAbove = (inDir == Direction.UP);
        boolean flowToAbove = outDirs.contains(Direction.UP) && (flowState != null && flowState.getBubbleColumn() == BubbleColumnState.UP);
        boolean flowIsVertical = flowFromAbove || flowToAbove;

        // Use vanilla's flowing-water levels (7/9 through 1/9) so the internal
        // channel falls in the same sequence as the world water emitted at its
        // downstream endpoint.
        boolean isFlowingWater = hasWater && flowState != null && flowState.hasWater();
        float yIn;
        float yOut;
        if (flowIsVertical) {
            yIn  = 1.0F;
            yOut = 1.0F;
        } else if (isFlowingWater) {
            PipeWaterSurface.Heights heights = PipeWaterSurface.flowing(state, flowState);
            yIn = heights.inlet();
            yOut = heights.outlet();
        } else {
            yIn  = 0.75F;
            yOut = 0.75F;
        }
        float yCenter = (yIn + yOut) * 0.5F;

        // Small inward offset to prevent Z-fighting where water quads would be
        // coplanar with the pipe's inner wall geometry (0.125 / 0.875 faces).
        final float ZB = 0.002F; // ~0.5 pixel inward bias

        boolean connUp    = state.getValue(HollowPipeBlock.UP);
        boolean connNorth = state.getValue(HollowPipeBlock.NORTH);
        boolean connSouth = state.getValue(HollowPipeBlock.SOUTH);
        boolean connWest  = state.getValue(HollowPipeBlock.WEST);
        boolean connEast  = state.getValue(HollowPipeBlock.EAST);

        ResourceLocation texLoc;
        if (fluid == Fluids.LAVA) {
            texLoc = new ResourceLocation("minecraft", "block/lava_still");
        } else if (fluid == Fluids.WATER) {
            // A source pipe with a downstream direction is flowing too. Using
            // water_flow here keeps the animated flow texture continuous from
            // the first channel segment through the final world-water block.
            texLoc = (flowState != null && flowState.hasWater() && !flowState.getFlowDirections().isEmpty())
                    ? new ResourceLocation("minecraft", "block/water_flow")
                    : new ResourceLocation("minecraft", "block/water_still");
        } else if (hasXp) {
            texLoc = (flowState != null && !flowState.isSource())
                    ? new ResourceLocation("buildscape", "fluid/experience_flow")
                    : new ResourceLocation("buildscape", "fluid/experience_still");
        } else {
            texLoc = (flowState != null && !flowState.isSource() && fluid.getAttributes().getFlowingTexture() != null)
                    ? fluid.getAttributes().getFlowingTexture()
                    : fluid.getAttributes().getStillTexture();
            if (texLoc == null) {
                texLoc = new ResourceLocation("minecraft", "block/water_still");
            }
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texLoc);

        int fluidColor = 0xFFFFFFFF;
        if (fluid == Fluids.WATER) {
            fluidColor = (level != null && pos != null) ? BiomeColors.getAverageWaterColor(level, pos) : 0x3F76E4;
            fluidColor |= 0xFF000000;
        } else if (fluid == Fluids.LAVA || hasXp) {
            fluidColor = 0xFFFFFFFF;
        } else {
            fluidColor = (level != null && pos != null) ? fluid.getAttributes().getColor(level, pos) : fluid.getAttributes().getColor();
            if ((fluidColor & 0xFF000000) == 0) {
                fluidColor |= 0xFF000000;
            }
        }

        float r = ((fluidColor >> 16) & 0xFF) / 255.0F;
        float g = ((fluidColor >> 8) & 0xFF) / 255.0F;
        float b = (fluidColor & 0xFF) / 255.0F;
        float a = 1.0F; // Full 1.0F vertex alpha to match vanilla Minecraft water rendering

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        Direction.Axis pipeAxis = state.hasProperty(HollowPipeBlock.AXIS) ? state.getValue(HollowPipeBlock.AXIS) : Direction.Axis.Y;

        boolean isStraightX = (pipeAxis == Direction.Axis.X || connWest || connEast) && !connNorth && !connSouth && !flowIsVertical;
        boolean isStraightZ = (pipeAxis == Direction.Axis.Z || connNorth || connSouth) && !connWest && !connEast && !flowIsVertical;

        if (isStraightX) {
            boolean openWest = HollowPipeBlock.isOpenEndpoint(state, Direction.WEST);
            boolean openEast = HollowPipeBlock.isOpenEndpoint(state, Direction.EAST);
            float x1 = (connWest || openWest) ? 0.0F : (0.125F + ZB);
            float x2 = (connEast || openEast) ? 1.0F : (0.875F - ZB);
            float z1 = 0.125F + ZB;
            float z2 = 0.875F - ZB;

            boolean flowWestToEast = (inDir == Direction.WEST || outDirs.contains(Direction.EAST));
            boolean flowEastToWest = (inDir == Direction.EAST || outDirs.contains(Direction.WEST));

            float yW;
            float yE;
            if (flowWestToEast) {
                yW = yIn;
                yE = yOut;
            } else if (flowEastToWest) {
                yW = yOut;
                yE = yIn;
            } else {
                yW = yIn;
                yE = yOut;
            }

            // Unsquished 1:1 UV mapping along flow direction
            float uZ1 = getSpriteU(sprite, z1);
            float uZ2 = getSpriteU(sprite, z2);
            float vX1 = getSpriteV(sprite, flowEastToWest ? (1.0F - x1) : x1);
            float vX2 = getSpriteV(sprite, flowEastToWest ? (1.0F - x2) : x2);

            // Top surface
            renderQuad(matrix, buffer,
                    x1, yW, z1,
                    x1, yW, z2,
                    x2, yE, z2,
                    x2, yE, z1,
                    r, g, b, a,
                    uZ1, vX1,
                    uZ2, vX1,
                    uZ2, vX2,
                    uZ1, vX2,
                    light, overlay, 0, 1, 0);

            // West face (only if open exit to air and neighbor is not fluid)
            if (!connWest && openWest && !isNeighborFluid(level, pos, Direction.WEST, fluid)) {
                float uW0 = getSpriteU(sprite, z1);
                float uW1 = getSpriteU(sprite, z2);
                float vW0 = getSpriteV(sprite, yFloor);
                float vW1 = getSpriteV(sprite, yW);
                renderQuad(matrix, buffer, x1, yW, z1, x1, yFloor, z1, x1, yFloor, z2, x1, yW, z2, r, g, b, a, uW0, vW1, uW0, vW0, uW1, vW0, uW1, vW1, light, overlay, -1, 0, 0);
                renderQuad(matrix, buffer, x1, yW, z2, x1, yFloor, z2, x1, yFloor, z1, x1, yW, z1, r, g, b, a, uW1, vW1, uW1, vW0, uW0, vW0, uW0, vW1, light, overlay, 1, 0, 0);
            }

            // East face (only if open exit to air and neighbor is not fluid)
            if (!connEast && openEast && !isNeighborFluid(level, pos, Direction.EAST, fluid)) {
                float uE0 = getSpriteU(sprite, z1);
                float uE1 = getSpriteU(sprite, z2);
                float vE0 = getSpriteV(sprite, yFloor);
                float vE1 = getSpriteV(sprite, yE);
                renderQuad(matrix, buffer, x2, yE, z2, x2, yFloor, z2, x2, yFloor, z1, x2, yE, z1, r, g, b, a, uE1, vE1, uE1, vE0, uE0, vE0, uE0, vE1, light, overlay, 1, 0, 0);
                renderQuad(matrix, buffer, x2, yE, z1, x2, yFloor, z1, x2, yFloor, z2, x2, yE, z2, r, g, b, a, uE0, vE1, uE0, vE0, uE1, vE0, uE1, vE1, light, overlay, -1, 0, 0);
            }
            return;
        }

        if (isStraightZ) {
            boolean openNorth = HollowPipeBlock.isOpenEndpoint(state, Direction.NORTH);
            boolean openSouth = HollowPipeBlock.isOpenEndpoint(state, Direction.SOUTH);
            float x1 = 0.125F + ZB;
            float x2 = 0.875F - ZB;
            float z1 = (connNorth || openNorth) ? 0.0F : (0.125F + ZB);
            float z2 = (connSouth || openSouth) ? 1.0F : (0.875F - ZB);

            boolean flowNorthToSouth = (inDir == Direction.NORTH || outDirs.contains(Direction.SOUTH));
            boolean flowSouthToNorth = (inDir == Direction.SOUTH || outDirs.contains(Direction.NORTH));

            float yN;
            float yS;
            if (flowNorthToSouth) {
                yN = yIn;
                yS = yOut;
            } else if (flowSouthToNorth) {
                yN = yOut;
                yS = yIn;
            } else {
                yN = yIn;
                yS = yOut;
            }

            // Unsquished 1:1 UV mapping along flow direction
            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, flowSouthToNorth ? (1.0F - z1) : z1);
            float vZ2 = getSpriteV(sprite, flowSouthToNorth ? (1.0F - z2) : z2);

            // Top surface
            renderQuad(matrix, buffer,
                    x1, yN, z1,
                    x1, yS, z2,
                    x2, yS, z2,
                    x2, yN, z1,
                    r, g, b, a,
                    uX1, vZ1,
                    uX1, vZ2,
                    uX2, vZ2,
                    uX2, vZ1,
                    light, overlay, 0, 1, 0);

            // North face (only if open exit to air and neighbor is not fluid)
            if (!connNorth && openNorth && !isNeighborFluid(level, pos, Direction.NORTH, fluid)) {
                float uN0 = getSpriteU(sprite, x1);
                float uN1 = getSpriteU(sprite, x2);
                float vN0 = getSpriteV(sprite, yFloor);
                float vN1 = getSpriteV(sprite, yN);
                renderQuad(matrix, buffer, x2, yN, z1, x2, yFloor, z1, x1, yFloor, z1, x1, yN, z1, r, g, b, a, uN1, vN1, uN1, vN0, uN0, vN0, uN0, vN1, light, overlay, 0, 0, -1);
                renderQuad(matrix, buffer, x1, yN, z1, x1, yFloor, z1, x2, yFloor, z1, x2, yN, z1, r, g, b, a, uN0, vN1, uN0, vN0, uN1, vN0, uN1, vN1, light, overlay, 0, 0, 1);
            }

            // South face (only if open exit to air and neighbor is not fluid)
            if (!connSouth && openSouth && !isNeighborFluid(level, pos, Direction.SOUTH, fluid)) {
                float uS0 = getSpriteU(sprite, x1);
                float uS1 = getSpriteU(sprite, x2);
                float vS0 = getSpriteV(sprite, yFloor);
                float vS1 = getSpriteV(sprite, yS);
                renderQuad(matrix, buffer, x1, yS, z2, x1, yFloor, z2, x2, yFloor, z2, x2, yS, z2, r, g, b, a, uS0, vS1, uS0, vS0, uS1, vS0, uS1, vS1, light, overlay, 0, 0, 1);
                renderQuad(matrix, buffer, x2, yS, z2, x2, yFloor, z2, x1, yFloor, z2, x1, yS, z2, r, g, b, a, uS1, vS1, uS1, vS0, uS0, vS0, uS0, vS1, light, overlay, 0, 0, -1);
            }
            return;
        }

        // Generic Junction / Vertical Column
        boolean openNorth = HollowPipeBlock.isOpenEndpoint(state, Direction.NORTH);
        boolean openSouth = HollowPipeBlock.isOpenEndpoint(state, Direction.SOUTH);
        boolean openWest  = HollowPipeBlock.isOpenEndpoint(state, Direction.WEST);
        boolean openEast  = HollowPipeBlock.isOpenEndpoint(state, Direction.EAST);
        float x1 = (connWest  || openWest)  ? 0.0F : (0.125F + ZB);
        float x2 = (connEast  || openEast)  ? 1.0F : (0.875F - ZB);
        float z1 = (connNorth || openNorth) ? 0.0F : (0.125F + ZB);
        float z2 = (connSouth || openSouth) ? 1.0F : (0.875F - ZB);
        float yTop = flowIsVertical ? 1.0F : yCenter;

        if (!flowToAbove && !flowFromAbove) {
            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, z1);
            float vZ2 = getSpriteV(sprite, z2);
            renderQuad(matrix, buffer,
                    x1, yTop, z1,
                    x1, yTop, z2,
                    x2, yTop, z2,
                    x2, yTop, z1,
                    r, g, b, a,
                    uX1, vZ1,
                    uX1, vZ2,
                    uX2, vZ2,
                    uX2, vZ1,
                    light, overlay, 0, 1, 0);
        }

        if (!connNorth && openNorth && !isNeighborFluid(level, pos, Direction.NORTH, fluid)) {
            float uN0 = getSpriteU(sprite, x1);
            float uN1 = getSpriteU(sprite, x2);
            float vN0 = getSpriteV(sprite, yFloor);
            float vN1 = getSpriteV(sprite, yTop);
            renderQuad(matrix, buffer, x2, yTop, z1, x2, yFloor, z1, x1, yFloor, z1, x1, yTop, z1, r, g, b, a, uN1, vN1, uN1, vN0, uN0, vN0, uN0, vN1, light, overlay, 0, 0, -1);
            renderQuad(matrix, buffer, x1, yTop, z1, x1, yFloor, z1, x2, yFloor, z1, x2, yTop, z1, r, g, b, a, uN0, vN1, uN0, vN0, uN1, vN0, uN1, vN1, light, overlay, 0, 0, 1);
        }

        if (!connSouth && openSouth && !isNeighborFluid(level, pos, Direction.SOUTH, fluid)) {
            float uS0 = getSpriteU(sprite, x1);
            float uS1 = getSpriteU(sprite, x2);
            float vS0 = getSpriteV(sprite, yFloor);
            float vS1 = getSpriteV(sprite, yTop);
            renderQuad(matrix, buffer, x1, yTop, z2, x1, yFloor, z2, x2, yFloor, z2, x2, yTop, z2, r, g, b, a, uS0, vS1, uS0, vS0, uS1, vS0, uS1, vS1, light, overlay, 0, 0, 1);
            renderQuad(matrix, buffer, x2, yTop, z2, x2, yFloor, z2, x1, yFloor, z2, x1, yTop, z2, r, g, b, a, uS1, vS1, uS1, vS0, uS0, vS0, uS0, vS1, light, overlay, 0, 0, -1);
        }

        if (!connWest && openWest && !isNeighborFluid(level, pos, Direction.WEST, fluid)) {
            float uW0 = getSpriteU(sprite, z1);
            float uW1 = getSpriteU(sprite, z2);
            float vW0 = getSpriteV(sprite, yFloor);
            float vW1 = getSpriteV(sprite, yTop);
            renderQuad(matrix, buffer, x1, yTop, z1, x1, yFloor, z1, x1, yFloor, z2, x1, yTop, z2, r, g, b, a, uW0, vW1, uW0, vW0, uW1, vW0, uW1, vW1, light, overlay, -1, 0, 0);
            renderQuad(matrix, buffer, x1, yTop, z2, x1, yFloor, z2, x1, yFloor, z1, x1, yTop, z1, r, g, b, a, uW1, vW1, uW1, vW0, uW0, vW0, uW0, vW1, light, overlay, 1, 0, 0);
        }

        if (!connEast && openEast && !isNeighborFluid(level, pos, Direction.EAST, fluid)) {
            float uE0 = getSpriteU(sprite, z1);
            float uE1 = getSpriteU(sprite, z2);
            float vE0 = getSpriteV(sprite, yFloor);
            float vE1 = getSpriteV(sprite, yTop);
            renderQuad(matrix, buffer, x2, yTop, z2, x2, yFloor, z2, x2, yFloor, z1, x2, yTop, z1, r, g, b, a, uE1, vE1, uE1, vE0, uE0, vE0, uE0, vE1, light, overlay, 1, 0, 0);
            renderQuad(matrix, buffer, x2, yTop, z1, x2, yFloor, z1, x2, yFloor, z2, x2, yTop, z2, r, g, b, a, uE0, vE1, uE0, vE0, uE1, vE0, uE1, vE1, light, overlay, -1, 0, 0);
        }
    }

    public static float getOwnFluidHeight(BlockGetter level, BlockState state, BlockPos pos, Fluid fluid) {
        if (state.getBlock() instanceof HollowPipeBlock) {
            if (state.getValue(HollowPipeBlock.WATERLOGGED)) {
                return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
            }
            int wl = state.getValue(HollowPipeBlock.WATER_LEVEL);
            if (wl > 0) {
                return wl / 9.0F;
            }
            return 8.0F / 9.0F;
        } else if (state.getBlock() instanceof HollowLogBlock) {
            return 0.75F;
        }
        FluidState fs = state.getFluidState();
        if (fs.getType().isSame(fluid)) {
            return fs.getOwnHeight();
        }
        return 8.0F / 9.0F;
    }

    public static float getNeighborFluidHeight(BlockGetter level, BlockPos pos, Direction dir, Fluid fluid) {
        if (level == null || pos == null) return -1.0F;
        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);

        // 1. Neighbor is a HollowPipeBlock:
        if (neighborState.getBlock() instanceof HollowPipeBlock) {
            if (neighborState.getValue(HollowPipeBlock.WATERLOGGED)) {
                return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
            }
            int nWl = neighborState.getValue(HollowPipeBlock.WATER_LEVEL);
            if (nWl > 0) {
                return nWl / 9.0F;
            }
            return -1.0F;
        }

        // 2. Neighbor is a HollowLogBlock:
        if (neighborState.getBlock() instanceof HollowLogBlock) {
            return 0.75F;
        }

        // 3. Neighbor is a world fluid block:
        FluidState nFluidState = neighborState.getFluidState();
        if (nFluidState.getType().isSame(fluid)) {
            BlockState aboveState = level.getBlockState(neighborPos.above());
            if (aboveState.getFluidState().getType().isSame(fluid)) {
                return 1.0F;
            }
            return nFluidState.getOwnHeight();
        }

        // 4. Neighbor is air at an open pipe endpoint:
        BlockState selfState = level.getBlockState(pos);
        if (neighborState.isAir() && HollowPipeBlock.isOpenEndpoint(selfState, dir)) {
            return 0.125F; // Pipe floor drop
        }

        // Solid block or closed wall:
        return -1.0F;
    }

    public static float getBoundaryHeight(float hSelf, float hNeighbor) {
        if (hNeighbor >= 0.0F) {
            return (hSelf + hNeighbor) * 0.5F;
        }
        return hSelf;
    }

    public static float calculatePipeCorner(float hSelf, float h1, float h2) {
        if (h1 >= 0.0F && h2 >= 0.0F) {
            return (hSelf + h1 + h2) / 3.0F;
        } else if (h1 >= 0.0F) {
            return (hSelf + h1) * 0.5F;
        } else if (h2 >= 0.0F) {
            return (hSelf + h2) * 0.5F;
        } else {
            return hSelf;
        }
    }

    private static float getSpriteU(TextureAtlasSprite sprite, float coord0To1) {
        return sprite.getU0() + (sprite.getU1() - sprite.getU0()) * coord0To1;
    }

    private static float getSpriteV(TextureAtlasSprite sprite, float coord0To1) {
        return sprite.getV0() + (sprite.getV1() - sprite.getV0()) * coord0To1;
    }

    private static boolean isNeighborFluid(Level level, BlockPos pos, Direction dir, Fluid fluid) {
        if (level == null || pos == null || dir == null) return false;
        BlockPos neighborPos = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (neighborState.getBlock() instanceof HollowPipeBlock) {
            if (HollowPipeBlock.isOpenEndpoint(neighborState, dir.getOpposite())) {
                return true;
            }
        }
        if (neighborState.getBlock() instanceof HollowLogBlock) {
            Direction.Axis neighborAxis = neighborState.hasProperty(HollowLogBlock.AXIS) ? neighborState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
            if (neighborAxis == dir.getAxis()) {
                return true;
            }
        }
        FluidState fs = level.getFluidState(neighborPos);
        return fs != null && !fs.isEmpty();
    }

    private static void renderQuad(
            Matrix4f matrix, VertexConsumer buffer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float r, float g, float b, float a,
            float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        buffer.vertex(matrix, x0, y0, z0).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
        buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3).overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
    }

    private static void renderQuad(
            Matrix4f matrix, VertexConsumer buffer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float r, float g, float b, float a,
            float u0, float v0, float u1, float v1,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        renderQuad(matrix, buffer, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, r, g, b, a, u0, v0, u0, v1, u1, v1, u1, v0, light, overlay, nx, ny, nz);
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

    private void renderLogFluid(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity blockEntity,
                                PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay,
                                boolean hasGlassNeg, boolean hasGlassPos) {
        String fluidType = blockEntity.getFluidType();
        boolean isWaterLogged = state.hasProperty(HollowLogBlock.WATERLOGGED) && state.getValue(HollowLogBlock.WATERLOGGED);
        boolean isLavaLogged = state.hasProperty(HollowLogBlock.LAVA_LOGGED) && state.getValue(HollowLogBlock.LAVA_LOGGED);

        Fluid fluid = null;
        if (isLavaLogged || "lava".equals(fluidType)) {
            fluid = Fluids.LAVA;
        } else if (isWaterLogged || "water".equals(fluidType)) {
            fluid = Fluids.WATER;
        } else if ("experience".equals(fluidType) || "buildscape:experience_still".equals(fluidType) || "buildscape:experience".equals(fluidType)) {
            fluid = com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get();
        } else if (!"none".equals(fluidType) && !fluidType.isEmpty()) {
            ResourceLocation rl = ResourceLocation.tryParse(fluidType);
            if (rl != null && net.minecraftforge.registries.ForgeRegistries.FLUIDS.containsKey(rl)) {
                fluid = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(rl);
            }
        }

        if (fluid == null || fluid == Fluids.EMPTY) {
            return;
        }

        ResourceLocation texLoc;
        if (fluid == Fluids.LAVA) {
            texLoc = new ResourceLocation("minecraft", "block/lava_still");
        } else if (fluid == Fluids.WATER) {
            texLoc = new ResourceLocation("minecraft", "block/water_still");
        } else if (fluid == com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get() || fluid == com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_FLOWING.get()) {
            texLoc = new ResourceLocation("buildscape", "fluid/experience_still");
        } else {
            texLoc = fluid.getAttributes().getStillTexture();
            if (texLoc == null) {
                texLoc = new ResourceLocation("minecraft", "block/water_still");
            }
        }

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texLoc);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        int color = 0xFFFFFFFF;
        if (fluid == Fluids.WATER) {
            color = (level != null && pos != null) ? BiomeColors.getAverageWaterColor(level, pos) : 0x3F76E4;
            color |= 0xFF000000;
        } else if (fluid == Fluids.LAVA || fluid == com.kingodogo.buildscape.fluid.ModFluids.EXPERIENCE_STILL.get()) {
            color = 0xFFFFFFFF;
        } else {
            color = (level != null && pos != null) ? fluid.getAttributes().getColor(level, pos) : fluid.getAttributes().getColor();
            if ((color & 0xFF000000) == 0) {
                color |= 0xFF000000;
            }
        }

        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = 1.0F; // Full 1.0F vertex alpha to match vanilla Minecraft water rendering

        Matrix4f matrix = poseStack.last().pose();
        Direction.Axis axis = state.hasProperty(HollowLogBlock.AXIS) ? state.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;

        float x1 = 0.125F, x2 = 0.875F;
        float y1 = 0.125F, y2 = 0.75F;
        float z1 = 0.125F, z2 = 0.875F;

        if (axis == Direction.Axis.Z) {
            z1 = hasGlassNeg ? 0.08F : 0.0F;
            z2 = hasGlassPos ? 0.92F : 1.0F;

            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, z1);
            float vZ2 = getSpriteV(sprite, z2);

            // Top surface (1:1 UV mapped, no squishing)
            renderQuad(matrix, buffer,
                    x1, y2, z1,
                    x1, y2, z2,
                    x2, y2, z2,
                    x2, y2, z1,
                    r, g, b, a,
                    uX1, vZ1,
                    uX1, vZ2,
                    uX2, vZ2,
                    uX2, vZ1,
                    light, overlay, 0, 1, 0);

            // North end cap (only if glass or open to air / not neighbor fluid)
            if (hasGlassNeg || !isNeighborFluid(level, pos, Direction.NORTH, fluid)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, y2);
                renderQuad(matrix, buffer, x2, y2, z1, x2, y1, z1, x1, y1, z1, x1, y2, z1, r, g, b, a, uX2, vY2, uX2, vY1, uX1, vY1, uX1, vY2, light, overlay, 0, 0, -1);
                renderQuad(matrix, buffer, x1, y2, z1, x1, y1, z1, x2, y1, z1, x2, y2, z1, r, g, b, a, uX1, vY2, uX1, vY1, uX2, vY1, uX2, vY2, light, overlay, 0, 0, 1);
            }

            // South end cap (only if glass or open to air / not neighbor fluid)
            if (hasGlassPos || !isNeighborFluid(level, pos, Direction.SOUTH, fluid)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, y2);
                renderQuad(matrix, buffer, x1, y2, z2, x1, y1, z2, x2, y1, z2, x2, y2, z2, r, g, b, a, uX1, vY2, uX1, vY1, uX2, vY1, uX2, vY2, light, overlay, 0, 0, 1);
                renderQuad(matrix, buffer, x2, y2, z2, x2, y1, z2, x1, y1, z2, x1, y2, z2, r, g, b, a, uX2, vY2, uX2, vY1, uX1, vY1, uX1, vY2, light, overlay, 0, 0, -1);
            }
        } else if (axis == Direction.Axis.X) {
            x1 = hasGlassNeg ? 0.08F : 0.0F;
            x2 = hasGlassPos ? 0.92F : 1.0F;

            float uZ1 = getSpriteU(sprite, z1);
            float uZ2 = getSpriteU(sprite, z2);
            float vX1 = getSpriteV(sprite, x1);
            float vX2 = getSpriteV(sprite, x2);

            // Top surface (1:1 UV mapped, no squishing)
            renderQuad(matrix, buffer,
                    x1, y2, z1,
                    x1, y2, z2,
                    x2, y2, z2,
                    x2, y2, z1,
                    r, g, b, a,
                    uZ1, vX1,
                    uZ2, vX1,
                    uZ2, vX2,
                    uZ1, vX2,
                    light, overlay, 0, 1, 0);

            // West end cap (only if glass or open to air / not neighbor fluid)
            if (hasGlassNeg || !isNeighborFluid(level, pos, Direction.WEST, fluid)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, y2);
                renderQuad(matrix, buffer, x1, y2, z1, x1, y1, z1, x1, y1, z2, x1, y2, z2, r, g, b, a, uZ1, vY2, uZ1, vY1, uZ2, vY1, uZ2, vY2, light, overlay, -1, 0, 0);
                renderQuad(matrix, buffer, x1, y2, z2, x1, y1, z2, x1, y1, z1, x1, y2, z1, r, g, b, a, uZ2, vY2, uZ2, vY1, uZ1, vY1, uZ1, vY2, light, overlay, 1, 0, 0);
            }

            // East end cap (only if glass or open to air / not neighbor fluid)
            if (hasGlassPos || !isNeighborFluid(level, pos, Direction.EAST, fluid)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, y2);
                renderQuad(matrix, buffer, x2, y2, z2, x2, y1, z2, x2, y1, z1, x2, y2, z1, r, g, b, a, uZ2, vY2, uZ2, vY1, uZ1, vY1, uZ1, vY2, light, overlay, 1, 0, 0);
                renderQuad(matrix, buffer, x2, y2, z1, x2, y1, z1, x2, y1, z2, x2, y2, z2, r, g, b, a, uZ1, vY2, uZ1, vY1, uZ2, vY1, uZ2, vY2, light, overlay, -1, 0, 0);
            }
        } else { // Y axis
            y1 = hasGlassNeg ? 0.08F : 0.0F;
            y2 = hasGlassPos ? 0.92F : 0.75F;

            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, z1);
            float vZ2 = getSpriteV(sprite, z2);

            // Top surface
            if (hasGlassPos || !isNeighborFluid(level, pos, Direction.UP, fluid)) {
                renderQuad(matrix, buffer,
                        x1, y2, z1,
                        x1, y2, z2,
                        x2, y2, z2,
                        x2, y2, z1,
                        r, g, b, a,
                        uX1, vZ1,
                        uX1, vZ2,
                        uX2, vZ2,
                        uX2, vZ1,
                        light, overlay, 0, 1, 0);
            }

            // Bottom surface
            if (hasGlassNeg || !isNeighborFluid(level, pos, Direction.DOWN, fluid)) {
                renderQuad(matrix, buffer,
                        x1, y1, z2,
                        x1, y1, z1,
                        x2, y1, z1,
                        x2, y1, z2,
                        r, g, b, a,
                        uX1, vZ2,
                        uX1, vZ1,
                        uX2, vZ1,
                        uX2, vZ2,
                        light, overlay, 0, -1, 0);
            }
        }
    }
}
