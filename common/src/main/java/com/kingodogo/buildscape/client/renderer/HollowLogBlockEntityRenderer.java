package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.BubbleColumnState;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.Set;

public class HollowLogBlockEntityRenderer {

    public static int getViewDistance() {
        return 48;
    }

    public static void render(
            HollowLogBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            Object bufferSource,
            int combinedLight,
            int combinedOverlay) {
        if (blockEntity == null) {
            return;
        }

        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState blockState = blockEntity.getBlockState();
        int light = (level != null) ? Services.PLATFORM.getLightColor(level, pos) : combinedLight;

        BlockState decoration = blockEntity.getDecorationState();
        BlockState glassNeg = blockEntity.getGlassCoverNeg();
        BlockState glassPos = blockEntity.getGlassCoverPos();

        boolean hasDecoration = decoration != null && !decoration.isAir();
        boolean hasGlassNeg = (glassNeg != null && !glassNeg.isAir())
                || (blockState.hasProperty(HollowLogBlock.HAS_GLASS_NEG) && blockState.getValue(HollowLogBlock.HAS_GLASS_NEG));
        boolean hasGlassPos = (glassPos != null && !glassPos.isAir())
                || (blockState.hasProperty(HollowLogBlock.HAS_GLASS_POS) && blockState.getValue(HollowLogBlock.HAS_GLASS_POS));

        if (blockState.getBlock() instanceof HollowPipeBlock) {
            renderPipeFluid(level, pos, blockState, blockEntity, poseStack, bufferSource, light, combinedOverlay);
        } else if (blockState.getBlock() instanceof HollowLogBlock) {
            renderLogFluid(level, pos, blockState, blockEntity, poseStack, bufferSource, light, combinedOverlay, hasGlassNeg, hasGlassPos);
        }

        if (!hasDecoration && !hasGlassNeg && !hasGlassPos) {
            return;
        }

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

        if (hasGlassNeg && glassNeg != null && !glassNeg.isAir()) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, false);
            renderBlockState(glassNeg, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }

        if (hasGlassPos && glassPos != null && !glassPos.isAir()) {
            poseStack.pushPose();
            positionGlassCover(poseStack, axis, true);
            renderBlockState(glassPos, pos, level, poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }
    }

    /** Immutable fluid inputs; geometry is computed at submission rather than captured as vertices. */
    public record FluidDisplay(BlockState state, boolean pipe, TextureAtlasSprite sprite, int color,
            Direction.Axis axis, Direction inlet, Set<Direction> outlets, PipeWaterSurface.Heights heights,
            boolean fromAbove, boolean toAbove, int neighborMask, boolean glassNeg, boolean glassPos) {
        public FluidDisplay {
            outlets = Set.copyOf(outlets);
        }

        public boolean hasNeighborFluid(Direction direction) {
            return (neighborMask & (1 << direction.ordinal())) != 0;
        }
    }

    public static FluidDisplay extractFluid(HollowLogBlockEntity entity, boolean glassNeg, boolean glassPos) {
        BlockState state = entity.getBlockState();
        boolean pipe = state.getBlock() instanceof HollowPipeBlock;
        if (!pipe && !(state.getBlock() instanceof HollowLogBlock)) return null;
        Level level = entity.getLevel();
        BlockPos pos = entity.getBlockPos();
        PipeFlowState flow = entity.getPipeFlowState();
        Fluid fluid = HollowPipeBlock.getContainedFluid(state, entity);
        if (fluid == null || fluid == Fluids.EMPTY) {
            if (pipe && state.getValue(HollowPipeBlock.LAVA_LOGGED)) fluid = Fluids.LAVA;
            else return null;
        }
        Direction.Axis axis = state.hasProperty(HollowLogBlock.AXIS) ? state.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
        Direction inlet = flow == null ? null : flow.getInflowDirection();
        Set<Direction> outlets = flow == null ? Set.of() : Set.copyOf(flow.getFlowDirections());
        boolean fromAbove = inlet == Direction.UP;
        boolean toAbove = outlets.contains(Direction.UP) && flow != null && flow.getBubbleColumn() == BubbleColumnState.UP;
        boolean network = flow != null && flow.hasFluid();
        boolean directSource = HollowPipeBlock.getSourceFluid(state, entity) != Fluids.EMPTY;
        PipeWaterSurface.Heights heights = pipe && (fromAbove || toAbove)
                ? new PipeWaterSurface.Heights(1, 1)
                : network ? PipeWaterSurface.flowing(directSource,
                        pipe ? state.getValue(HollowPipeBlock.DOWN) : axis == Direction.Axis.Y, flow)
                : new PipeWaterSurface.Heights(HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT, HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT);
        boolean flowing = network && !outlets.isEmpty();
        if (pipe && fluid == Fluids.WATER) {
            boolean stationarySource = flow != null && flow.isSource() && inlet == null && outlets.size() != 1;
            flowing = flow != null && flow.hasWater() && !outlets.isEmpty() && !stationarySource;
        }
        Fluid xpStill = Services.PLATFORM.getFluid(new CommonId("buildscape", "experience_still"));
        Fluid xpFlow = Services.PLATFORM.getFluid(new CommonId("buildscape", "experience_flow"));
        boolean xp = fluid == xpStill || fluid == xpFlow;
        CommonId texture = fluid == Fluids.LAVA
                ? new CommonId("minecraft", flowing ? "block/lava_flow" : "block/lava_still")
                : xp ? new CommonId("buildscape", flowing ? "fluid/experience_flow" : "fluid/experience_still")
                : new CommonId("minecraft", fluid == Fluids.WATER && flowing ? "block/water_flow" : "block/water_still");
        TextureAtlasSprite sprite = Services.PLATFORM.getBlockAtlasSprite(texture);
        if (sprite == null) {
            com.kingodogo.buildscape.BuildscapeCommon.LOGGER.warn("Missing hollow-log fluid sprite {}", texture);
            return null;
        }
        int color = fluid == Fluids.WATER ? 0xFF000000 | (level == null ? 0x3F76E4 : Services.PLATFORM.getWaterColor(level, pos)) : -1;
        int neighbors = 0;
        for (Direction direction : Direction.values()) {
            if (isNeighborFluid(level, pos, direction, fluid)) neighbors |= 1 << direction.ordinal();
        }
        return new FluidDisplay(state, pipe, sprite, color, axis, inlet, outlets, heights,
                fromAbove, toAbove, neighbors, glassNeg, glassPos);
    }

    public static void submitFluid(FluidDisplay data, PoseStack pose, VertexConsumer buffer, int light, int overlay) {
        if (data.pipe) submitPipeFluid(data, pose, buffer, light, overlay);
        else submitLogFluid(data, pose, buffer, light, overlay);
    }

    private static void renderPipeFluid(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity entity,
            PoseStack pose, Object bufferSource, int light, int overlay) {
        FluidDisplay data = extractFluid(entity, false, false);
        VertexConsumer buffer = Services.PLATFORM.getTranslucentBuffer(bufferSource);
        if (data != null && buffer != null) submitPipeFluid(data, pose, buffer, light, overlay);
    }

    private static void submitPipeFluid(FluidDisplay data, PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
        BlockState state = data.state;
        TextureAtlasSprite sprite = data.sprite;
        float r = ((data.color >> 16) & 255) / 255.0F;
        float g = ((data.color >> 8) & 255) / 255.0F;
        float b = (data.color & 255) / 255.0F;
        float a = 1;
        boolean connDown = state.getValue(HollowPipeBlock.DOWN);
        float yFloor = connDown ? 0 : 0.125F;
        Direction inDir = data.inlet;
        Set<Direction> outDirs = data.outlets;
        boolean flowFromAbove = data.fromAbove, flowToAbove = data.toAbove;
        boolean flowIsVertical = flowFromAbove || flowToAbove;
        float yIn = data.heights.inlet(), yOut = data.heights.outlet(), yCenter = data.heights.center();
        final float ZB = 0.002F;
        boolean connNorth = state.getValue(HollowPipeBlock.NORTH), connSouth = state.getValue(HollowPipeBlock.SOUTH);
        boolean connWest = state.getValue(HollowPipeBlock.WEST), connEast = state.getValue(HollowPipeBlock.EAST);
        Direction.Axis pipeAxis = data.axis;
        boolean isStraightX = (pipeAxis == Direction.Axis.X || connWest || connEast) && !connNorth && !connSouth && !flowIsVertical;
        boolean isStraightZ = (pipeAxis == Direction.Axis.Z || connNorth || connSouth) && !connWest && !connEast && !flowIsVertical;

        if (isStraightX) {
            boolean openWest = HollowPipeBlock.isOpenEndpoint(state, Direction.WEST);
            boolean openEast = HollowPipeBlock.isOpenEndpoint(state, Direction.EAST);
            float x1 = (connWest || openWest) ? 0.0F : (0.125F + ZB);
            float x2 = (connEast || openEast) ? 1.0F : (0.875F - ZB);
            float z1 = 0.125F + ZB;
            float z2 = 0.875F - ZB;

            boolean flowWestToEast = (inDir == Direction.WEST || (inDir == null && outDirs.contains(Direction.EAST) && !outDirs.contains(Direction.WEST)));
            boolean flowEastToWest = (inDir == Direction.EAST || (inDir == null && outDirs.contains(Direction.WEST) && !outDirs.contains(Direction.EAST)));

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
                yE = yIn;
            }

            float uZ1 = getSpriteU(sprite, z1);
            float uZ2 = getSpriteU(sprite, z2);
            float vX1 = getSpriteV(sprite, flowEastToWest ? (1.0F - x1) : x1);
            float vX2 = getSpriteV(sprite, flowEastToWest ? (1.0F - x2) : x2);

            renderQuad(poseStack, buffer,
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

            if (!connWest && openWest && !data.hasNeighborFluid(Direction.WEST)) {
                float uW0 = getSpriteU(sprite, z1);
                float uW1 = getSpriteU(sprite, z2);
                float vW0 = getSpriteV(sprite, yFloor);
                float vW1 = getSpriteV(sprite, yW);
                renderQuad(poseStack, buffer, x1, yW, z1, x1, yFloor, z1, x1, yFloor, z2, x1, yW, z2, r, g, b, a, uW0, vW1, uW0, vW0, uW1, vW0, uW1, vW1, light, overlay, -1, 0, 0);
                renderQuad(poseStack, buffer, x1, yW, z2, x1, yFloor, z2, x1, yFloor, z1, x1, yW, z1, r, g, b, a, uW1, vW1, uW1, vW0, uW0, vW0, uW0, vW1, light, overlay, 1, 0, 0);
            }

            if (!connEast && openEast && !data.hasNeighborFluid(Direction.EAST)) {
                float uE0 = getSpriteU(sprite, z1);
                float uE1 = getSpriteU(sprite, z2);
                float vE0 = getSpriteV(sprite, yFloor);
                float vE1 = getSpriteV(sprite, yE);
                renderQuad(poseStack, buffer, x2, yE, z2, x2, yFloor, z2, x2, yFloor, z1, x2, yE, z1, r, g, b, a, uE1, vE1, uE1, vE0, uE0, vE0, uE0, vE1, light, overlay, 1, 0, 0);
                renderQuad(poseStack, buffer, x2, yE, z1, x2, yFloor, z1, x2, yFloor, z2, x2, yE, z2, r, g, b, a, uE0, vE1, uE0, vE0, uE1, vE0, uE1, vE1, light, overlay, -1, 0, 0);
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

            boolean flowNorthToSouth = (inDir == Direction.NORTH || (inDir == null && outDirs.contains(Direction.SOUTH) && !outDirs.contains(Direction.NORTH)));
            boolean flowSouthToNorth = (inDir == Direction.SOUTH || (inDir == null && outDirs.contains(Direction.NORTH) && !outDirs.contains(Direction.SOUTH)));

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
                yS = yIn;
            }

            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, flowSouthToNorth ? (1.0F - z1) : z1);
            float vZ2 = getSpriteV(sprite, flowSouthToNorth ? (1.0F - z2) : z2);

            renderQuad(poseStack, buffer,
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

            if (!connNorth && openNorth && !data.hasNeighborFluid(Direction.NORTH)) {
                float uN0 = getSpriteU(sprite, x1);
                float uN1 = getSpriteU(sprite, x2);
                float vN0 = getSpriteV(sprite, yFloor);
                float vN1 = getSpriteV(sprite, yN);
                renderQuad(poseStack, buffer, x2, yN, z1, x2, yFloor, z1, x1, yFloor, z1, x1, yN, z1, r, g, b, a, uN1, vN1, uN1, vN0, uN0, vN0, uN0, vN1, light, overlay, 0, 0, -1);
                renderQuad(poseStack, buffer, x1, yN, z1, x1, yFloor, z1, x2, yFloor, z1, x2, yN, z1, r, g, b, a, uN0, vN1, uN0, vN0, uN1, vN0, uN1, vN1, light, overlay, 0, 0, 1);
            }

            if (!connSouth && openSouth && !data.hasNeighborFluid(Direction.SOUTH)) {
                float uS0 = getSpriteU(sprite, x1);
                float uS1 = getSpriteU(sprite, x2);
                float vS0 = getSpriteV(sprite, yFloor);
                float vS1 = getSpriteV(sprite, yS);
                renderQuad(poseStack, buffer, x1, yS, z2, x1, yFloor, z2, x2, yFloor, z2, x2, yS, z2, r, g, b, a, uS0, vS1, uS0, vS0, uS1, vS0, uS1, vS1, light, overlay, 0, 0, 1);
                renderQuad(poseStack, buffer, x2, yS, z2, x2, yFloor, z2, x1, yFloor, z2, x1, yS, z2, r, g, b, a, uS1, vS1, uS1, vS0, uS0, vS0, uS0, vS1, light, overlay, 0, 0, -1);
            }
            return;
        }

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
            renderQuad(poseStack, buffer,
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

        if (!connNorth && openNorth && !data.hasNeighborFluid(Direction.NORTH)) {
            float uN0 = getSpriteU(sprite, x1);
            float uN1 = getSpriteU(sprite, x2);
            float vN0 = getSpriteV(sprite, yFloor);
            float vN1 = getSpriteV(sprite, yTop);
            renderQuad(poseStack, buffer, x2, yTop, z1, x2, yFloor, z1, x1, yFloor, z1, x1, yTop, z1, r, g, b, a, uN1, vN1, uN1, vN0, uN0, vN0, uN0, vN1, light, overlay, 0, 0, -1);
            renderQuad(poseStack, buffer, x1, yTop, z1, x1, yFloor, z1, x2, yFloor, z1, x2, yTop, z1, r, g, b, a, uN0, vN1, uN0, vN0, uN1, vN0, uN1, vN1, light, overlay, 0, 0, 1);
        }

        if (!connSouth && openSouth && !data.hasNeighborFluid(Direction.SOUTH)) {
            float uS0 = getSpriteU(sprite, x1);
            float uS1 = getSpriteU(sprite, x2);
            float vS0 = getSpriteV(sprite, yFloor);
            float vS1 = getSpriteV(sprite, yTop);
            renderQuad(poseStack, buffer, x1, yTop, z2, x1, yFloor, z2, x2, yFloor, z2, x2, yTop, z2, r, g, b, a, uS0, vS1, uS0, vS0, uS1, vS0, uS1, vS1, light, overlay, 0, 0, 1);
            renderQuad(poseStack, buffer, x2, yTop, z2, x2, yFloor, z2, x1, yFloor, z2, x1, yTop, z2, r, g, b, a, uS1, vS1, uS1, vS0, uS0, vS0, uS0, vS1, light, overlay, 0, 0, -1);
        }

        if (!connWest && openWest && !data.hasNeighborFluid(Direction.WEST)) {
            float uW0 = getSpriteU(sprite, z1);
            float uW1 = getSpriteU(sprite, z2);
            float vW0 = getSpriteV(sprite, yFloor);
            float vW1 = getSpriteV(sprite, yTop);
            renderQuad(poseStack, buffer, x1, yTop, z1, x1, yFloor, z1, x1, yFloor, z2, x1, yTop, z2, r, g, b, a, uW0, vW1, uW0, vW0, uW1, vW0, uW1, vW1, light, overlay, -1, 0, 0);
            renderQuad(poseStack, buffer, x1, yTop, z2, x1, yFloor, z2, x1, yFloor, z1, x1, yTop, z1, r, g, b, a, uW1, vW1, uW1, vW0, uW0, vW0, uW0, vW1, light, overlay, 1, 0, 0);
        }

        if (!connEast && openEast && !data.hasNeighborFluid(Direction.EAST)) {
            float uE0 = getSpriteU(sprite, z1);
            float uE1 = getSpriteU(sprite, z2);
            float vE0 = getSpriteV(sprite, yFloor);
            float vE1 = getSpriteV(sprite, yTop);
            renderQuad(poseStack, buffer, x2, yTop, z2, x2, yFloor, z2, x2, yFloor, z1, x2, yTop, z1, r, g, b, a, uE1, vE1, uE1, vE0, uE0, vE0, uE0, vE1, light, overlay, 1, 0, 0);
            renderQuad(poseStack, buffer, x2, yTop, z1, x2, yFloor, z1, x2, yFloor, z2, x2, yTop, z2, r, g, b, a, uE0, vE1, uE0, vE0, uE1, vE0, uE1, vE1, light, overlay, -1, 0, 0);
        }
    }

    public static float getOwnFluidHeight(BlockGetter level, BlockState state, BlockPos pos, Fluid fluid) {
        if (state.getBlock() instanceof HollowPipeBlock) {
            Fluid contained = HollowPipeBlock.getContainedFluid(state, level.getBlockEntity(pos));
            if (!contained.isSame(fluid)) return -1.0F;
            if (HollowPipeBlock.getSourceFluid(state, level.getBlockEntity(pos)) != Fluids.EMPTY) {
                return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
            }
            if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity entity
                    && entity.getPipeFlowState().hasFluid()) {
                return PipeWaterSurface.flowing(false, state.getValue(HollowPipeBlock.DOWN),
                        entity.getPipeFlowState()).center();
            }
        } else if (state.getBlock() instanceof HollowLogBlock) {
            Fluid contained = HollowPipeBlock.getContainedFluid(state, level.getBlockEntity(pos));
            if (!contained.isSame(fluid)) return -1.0F;
            if (level.getBlockEntity(pos) instanceof HollowLogBlockEntity entity
                    && entity.getPipeFlowState().hasFluid()) {
                boolean source = HollowPipeBlock.getSourceFluid(state, entity) != Fluids.EMPTY;
                return PipeWaterSurface.flowing(source, state.getValue(HollowLogBlock.AXIS) == Direction.Axis.Y,
                        entity.getPipeFlowState()).center();
            }
            return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
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

        if (neighborState.getBlock() instanceof HollowPipeBlock) {
            Fluid contained = HollowPipeBlock.getContainedFluid(neighborState, level.getBlockEntity(neighborPos));
            if (!contained.isSame(fluid)) return -1.0F;
            if (HollowPipeBlock.getSourceFluid(neighborState, level.getBlockEntity(neighborPos)) != Fluids.EMPTY) {
                return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
            }
            if (level.getBlockEntity(neighborPos) instanceof HollowLogBlockEntity entity
                    && entity.getPipeFlowState().hasFluid()) {
                return PipeWaterSurface.flowing(false, neighborState.getValue(HollowPipeBlock.DOWN),
                        entity.getPipeFlowState()).center();
            }
            return -1.0F;
        }

        if (neighborState.getBlock() instanceof HollowLogBlock) {
            Direction.Axis nAxis = neighborState.hasProperty(HollowLogBlock.AXIS) ? neighborState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
            if (nAxis == dir.getAxis()) {
                Fluid nFluid = HollowPipeBlock.getContainedFluid(neighborState, level.getBlockEntity(neighborPos));
                if (nFluid != Fluids.EMPTY && (fluid == null || nFluid.isSame(fluid))) {
                    if (level.getBlockEntity(neighborPos) instanceof HollowLogBlockEntity entity
                            && entity.getPipeFlowState().hasFluid()) {
                        boolean source = HollowPipeBlock.getSourceFluid(neighborState, entity) != Fluids.EMPTY;
                        return PipeWaterSurface.flowing(source, nAxis == Direction.Axis.Y,
                                entity.getPipeFlowState()).center();
                    }
                    return HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
                }
            }
            return -1.0F;
        }

        FluidState nFluidState = neighborState.getFluidState();
        if (nFluidState.getType().isSame(fluid)) {
            BlockState aboveState = level.getBlockState(neighborPos.above());
            if (aboveState.getFluidState().getType().isSame(fluid)) {
                return 1.0F;
            }
            return nFluidState.getOwnHeight();
        }

        BlockState selfState = level.getBlockState(pos);
        if (neighborState.isAir() && HollowPipeBlock.isOpenEndpoint(selfState, dir)) {
            return 0.125F;
        }

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
            if (HollowPipeBlock.isOpenEndpoint(neighborState, dir.getOpposite())
                    || (neighborState.hasProperty(HollowPipeBlock.getPropertyForDirection(dir.getOpposite()))
                        && neighborState.getValue(HollowPipeBlock.getPropertyForDirection(dir.getOpposite())))) {
                Fluid neighborFluid = HollowPipeBlock.getContainedFluid(neighborState, level.getBlockEntity(neighborPos));
                if (neighborFluid != Fluids.EMPTY && (fluid == null || neighborFluid.isSame(fluid))) {
                    return true;
                }
            }
            return false;
        }
        if (neighborState.getBlock() instanceof HollowLogBlock) {
            Direction.Axis neighborAxis = neighborState.hasProperty(HollowLogBlock.AXIS) ? neighborState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
            if (neighborAxis == dir.getAxis()) {
                boolean isNeg = (dir.getOpposite() == Direction.WEST || dir.getOpposite() == Direction.NORTH || dir.getOpposite() == Direction.DOWN);
                boolean hasNeighborGlass = isNeg
                        ? (neighborState.hasProperty(HollowLogBlock.HAS_GLASS_NEG) && neighborState.getValue(HollowLogBlock.HAS_GLASS_NEG))
                        : (neighborState.hasProperty(HollowLogBlock.HAS_GLASS_POS) && neighborState.getValue(HollowLogBlock.HAS_GLASS_POS));
                if (hasNeighborGlass) {
                    return false;
                }
                Fluid neighborFluid = HollowPipeBlock.getContainedFluid(neighborState, level.getBlockEntity(neighborPos));
                if (neighborFluid != Fluids.EMPTY && (fluid == null || neighborFluid.isSame(fluid))) {
                    return true;
                }
            }
            return false;
        }
        FluidState fs = level.getFluidState(neighborPos);
        return fs != null && !fs.isEmpty() && (fluid == null || fs.getType().isSame(fluid));
    }

    private static void renderQuad(
            PoseStack poseStack, VertexConsumer buffer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float r, float g, float b, float a,
            float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        Services.PLATFORM.renderColoredQuad(poseStack, buffer,
                x0, y0, z0, u0, v0,
                x1, y1, z1, u1, v1,
                x2, y2, z2, u2, v2,
                x3, y3, z3, u3, v3,
                r, g, b, a,
                light, overlay, nx, ny, nz);
    }

    private static void renderQuad(
            PoseStack poseStack, VertexConsumer buffer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float r, float g, float b, float a,
            float u0, float v0, float u1, float v1,
            int light, int overlay,
            float nx, float ny, float nz
    ) {
        renderQuad(poseStack, buffer,
                x0, y0, z0,
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                r, g, b, a,
                u0, v0, u0, v1, u1, v1, u1, v0,
                light, overlay, nx, ny, nz);
    }

    private static void renderBlockState(BlockState state, BlockPos pos, Level level, PoseStack poseStack, Object bufferSource, int light, int overlay) {
        Services.PLATFORM.renderBlockModelWithTint(state, pos, level, poseStack, bufferSource, light, overlay);
    }

    public static void positionGlassCover(PoseStack poseStack, Direction.Axis axis, boolean isPositive) {
        float glassThickness = 0.0625F;
        double negOffset = 0.015625D;
        double posOffset = 1.0D - 0.015625D - glassThickness;

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
            default:
                if (isPositive) {
                    poseStack.translate(0.125D, posOffset, 0.125D);
                } else {
                    poseStack.translate(0.125D, negOffset, 0.125D);
                }
                poseStack.scale(0.75F, glassThickness, 0.75F);
                break;
        }
    }

    private static void renderLogFluid(Level level, BlockPos pos, BlockState state, HollowLogBlockEntity entity,
            PoseStack pose, Object bufferSource, int light, int overlay, boolean hasGlassNeg, boolean hasGlassPos) {
        FluidDisplay data = extractFluid(entity, hasGlassNeg, hasGlassPos);
        VertexConsumer buffer = Services.PLATFORM.getTranslucentBuffer(bufferSource);
        if (data != null && buffer != null) submitLogFluid(data, pose, buffer, light, overlay);
    }

    private static void submitLogFluid(FluidDisplay data, PoseStack poseStack, VertexConsumer buffer, int light, int overlay) {
        TextureAtlasSprite sprite = data.sprite;
        float r = ((data.color >> 16) & 255) / 255.0F;
        float g = ((data.color >> 8) & 255) / 255.0F;
        float b = (data.color & 255) / 255.0F;
        float a = 1;
        Direction.Axis axis = data.axis;
        boolean hasGlassNeg = data.glassNeg, hasGlassPos = data.glassPos;
        final float ZB = 0.002F;
        float x1 = 0.125F + ZB, x2 = 0.875F - ZB;
        float y1 = 0.125F, y2 = HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT;
        float z1 = 0.125F + ZB, z2 = 0.875F - ZB;
        PipeWaterSurface.Heights surface = data.heights;
        Direction inDir = data.inlet;
        Set<Direction> outDirs = data.outlets;

        if (axis == Direction.Axis.Z) {
            z1 = hasGlassNeg ? 0.08F : 0.0F;
            z2 = hasGlassPos ? 0.92F : 1.0F;
            boolean southbound = inDir == Direction.NORTH
                    || (inDir == null && outDirs.contains(Direction.SOUTH) && !outDirs.contains(Direction.NORTH));
            boolean northbound = inDir == Direction.SOUTH
                    || (inDir == null && outDirs.contains(Direction.NORTH) && !outDirs.contains(Direction.SOUTH));
            float yNorth = southbound ? surface.inlet() : northbound ? surface.outlet() : surface.center();
            float ySouth = southbound ? surface.outlet() : northbound ? surface.inlet() : surface.center();

            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, z1);
            float vZ2 = getSpriteV(sprite, z2);

            renderQuad(poseStack, buffer,
                    x1, yNorth, z1,
                    x1, ySouth, z2,
                    x2, ySouth, z2,
                    x2, yNorth, z1,
                    r, g, b, a,
                    uX1, vZ1,
                    uX1, vZ2,
                    uX2, vZ2,
                    uX2, vZ1,
                    light, overlay, 0, 1, 0);

            if (hasGlassNeg || !data.hasNeighborFluid(Direction.NORTH)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, yNorth);
                renderQuad(poseStack, buffer, x2, yNorth, z1, x2, y1, z1, x1, y1, z1, x1, yNorth, z1, r, g, b, a, uX2, vY2, uX2, vY1, uX1, vY1, uX1, vY2, light, overlay, 0, 0, -1);
                renderQuad(poseStack, buffer, x1, yNorth, z1, x1, y1, z1, x2, y1, z1, x2, yNorth, z1, r, g, b, a, uX1, vY2, uX1, vY1, uX2, vY1, uX2, vY2, light, overlay, 0, 0, 1);
            }

            if (hasGlassPos || !data.hasNeighborFluid(Direction.SOUTH)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, ySouth);
                renderQuad(poseStack, buffer, x1, ySouth, z2, x1, y1, z2, x2, y1, z2, x2, ySouth, z2, r, g, b, a, uX1, vY2, uX1, vY1, uX2, vY1, uX2, vY2, light, overlay, 0, 0, 1);
                renderQuad(poseStack, buffer, x2, ySouth, z2, x2, y1, z2, x1, y1, z2, x1, ySouth, z2, r, g, b, a, uX2, vY2, uX2, vY1, uX1, vY1, uX1, vY2, light, overlay, 0, 0, -1);
            }
        } else if (axis == Direction.Axis.X) {
            x1 = hasGlassNeg ? 0.08F : 0.0F;
            x2 = hasGlassPos ? 0.92F : 1.0F;
            boolean eastbound = inDir == Direction.WEST
                    || (inDir == null && outDirs.contains(Direction.EAST) && !outDirs.contains(Direction.WEST));
            boolean westbound = inDir == Direction.EAST
                    || (inDir == null && outDirs.contains(Direction.WEST) && !outDirs.contains(Direction.EAST));
            float yWest = eastbound ? surface.inlet() : westbound ? surface.outlet() : surface.center();
            float yEast = eastbound ? surface.outlet() : westbound ? surface.inlet() : surface.center();

            float uZ1 = getSpriteU(sprite, z1);
            float uZ2 = getSpriteU(sprite, z2);
            float vX1 = getSpriteV(sprite, x1);
            float vX2 = getSpriteV(sprite, x2);

            renderQuad(poseStack, buffer,
                    x1, yWest, z1,
                    x1, yWest, z2,
                    x2, yEast, z2,
                    x2, yEast, z1,
                    r, g, b, a,
                    uZ1, vX1,
                    uZ2, vX1,
                    uZ2, vX2,
                    uZ1, vX2,
                    light, overlay, 0, 1, 0);

            if (hasGlassNeg || !data.hasNeighborFluid(Direction.WEST)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, yWest);
                renderQuad(poseStack, buffer, x1, yWest, z1, x1, y1, z1, x1, y1, z2, x1, yWest, z2, r, g, b, a, uZ1, vY2, uZ1, vY1, uZ2, vY1, uZ2, vY2, light, overlay, -1, 0, 0);
                renderQuad(poseStack, buffer, x1, yWest, z2, x1, y1, z2, x1, y1, z1, x1, yWest, z1, r, g, b, a, uZ2, vY2, uZ2, vY1, uZ1, vY1, uZ1, vY2, light, overlay, 1, 0, 0);
            }

            if (hasGlassPos || !data.hasNeighborFluid(Direction.EAST)) {
                float vY1 = getSpriteV(sprite, y1);
                float vY2 = getSpriteV(sprite, yEast);
                renderQuad(poseStack, buffer, x2, yEast, z2, x2, y1, z2, x2, y1, z1, x2, yEast, z1, r, g, b, a, uZ2, vY2, uZ2, vY1, uZ1, vY1, uZ1, vY2, light, overlay, 1, 0, 0);
                renderQuad(poseStack, buffer, x2, yEast, z1, x2, y1, z1, x2, y1, z2, x2, yEast, z2, r, g, b, a, uZ1, vY2, uZ1, vY1, uZ2, vY1, uZ2, vY2, light, overlay, -1, 0, 0);
            }
        } else {
            y1 = hasGlassNeg ? 0.08F : 0.0F;
            y2 = hasGlassPos ? 0.92F : (data.hasNeighborFluid(Direction.UP) ? 1.0F : HollowPipeBlock.WATER_SOURCE_VISUAL_HEIGHT);

            float uX1 = getSpriteU(sprite, x1);
            float uX2 = getSpriteU(sprite, x2);
            float vZ1 = getSpriteV(sprite, z1);
            float vZ2 = getSpriteV(sprite, z2);

            if (hasGlassPos || !data.hasNeighborFluid(Direction.UP)) {
                renderQuad(poseStack, buffer,
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

            if (hasGlassNeg || !data.hasNeighborFluid(Direction.DOWN)) {
                renderQuad(poseStack, buffer,
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
