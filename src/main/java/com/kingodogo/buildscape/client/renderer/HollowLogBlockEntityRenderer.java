package com.kingodogo.buildscape.client.renderer;

import com.kingodogo.buildscape.block.HollowLogBlock;
import com.kingodogo.buildscape.block.HollowLogBlockEntity;
import com.kingodogo.buildscape.block.HollowPipeBlock;
import com.kingodogo.buildscape.pipe.transport.PipeFlowState;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
        PipeFlowState flowState = blockEntity.getPipeFlowState();
        boolean hasTransportWater = blockState.getBlock() instanceof HollowPipeBlock
                && flowState != null && flowState.hasWater();
        boolean hasFluid = isLavaLogged || isWaterLogged || hasTransportWater || (!"none".equals(fluidType) && !fluidType.isEmpty());

        // Fast early exit: skip empty Hollow Logs completely!
        if (!hasDecoration && !hasGlassNeg && !hasGlassPos && !hasFluid) {
            return;
        }

        // 1. Render Fluid Interior (Vanilla Water/Lava, Buildscape Experience, or any Modded Fluid)
        Fluid fluid = null;
        if (isLavaLogged || "lava".equals(fluidType)) {
            fluid = net.minecraft.world.level.material.Fluids.LAVA;
        } else if (isWaterLogged || hasTransportWater || "water".equals(fluidType)) {
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
            renderFluidInterior(level, pos, blockState, flowState, poseStack, bufferSource, stillTex, color, light, combinedOverlay);
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

    /**
     * Renders the fluid interior of a pipe or log.
     *
     * For pipes: reads connected directions from blockState to CULL internal end-cap faces
     * between connected pipes — so a continuous chain of pipes shows a seamless stream
     * without dividing walls between each pipe block.
     *
     * Top-face slope is EXPONENTIAL based on distance/maxDistance stored in flowState:
     *   - Source pipe (distance=0)       → flat top at FULL_HEIGHT (0.8125)
     *   - Intermediate pipes              → progressively lower top using t^1.5 curve
     *   - Last pipe (isOpenEndpoint=true) → downstream end drops to vanilla end-slope height (~0.1875)
     *
     * IMPORTANT: This method is READ-ONLY. It never modifies blockState, flowState,
     * fluidType, or any server-side state.
     */
    private void renderFluidInterior(Level level, BlockPos pos, BlockState blockState, PipeFlowState flowState, PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation textureLoc, int color, int light, int overlay) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(textureLoc);
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.translucent());

        if (blockState.getBlock() instanceof HollowPipeBlock) {
            boolean connDown  = blockState.hasProperty(HollowPipeBlock.DOWN)  && blockState.getValue(HollowPipeBlock.DOWN);
            boolean connUp    = blockState.hasProperty(HollowPipeBlock.UP)    && blockState.getValue(HollowPipeBlock.UP);
            boolean connNorth = blockState.hasProperty(HollowPipeBlock.NORTH) && blockState.getValue(HollowPipeBlock.NORTH);
            boolean connSouth = blockState.hasProperty(HollowPipeBlock.SOUTH) && blockState.getValue(HollowPipeBlock.SOUTH);
            boolean connWest  = blockState.hasProperty(HollowPipeBlock.WEST)  && blockState.getValue(HollowPipeBlock.WEST);
            boolean connEast  = blockState.hasProperty(HollowPipeBlock.EAST)  && blockState.getValue(HollowPipeBlock.EAST);

            // A topology connection is not necessarily a renderable fluid continuation:
            // newly placed neighbors receive transport state after a short server delay.
            // Keep an end-cap until that neighbor is actually wet, otherwise the old pipe
            // exposes a dark hole/floating bottom while the replacement settles.
            connDown  &= hasRenderableFluid(level, pos, Direction.DOWN);
            connUp    &= hasRenderableFluid(level, pos, Direction.UP);
            connNorth &= hasRenderableFluid(level, pos, Direction.NORTH);
            connSouth &= hasRenderableFluid(level, pos, Direction.SOUTH);
            connWest  &= hasRenderableFluid(level, pos, Direction.WEST);
            connEast  &= hasRenderableFluid(level, pos, Direction.EAST);

            int count = (connDown ? 1 : 0) + (connUp ? 1 : 0) + (connNorth ? 1 : 0)
                      + (connSouth ? 1 : 0) + (connWest ? 1 : 0) + (connEast ? 1 : 0);

            // Determine primary flow direction for slope direction.
            // READ-ONLY: flowState is never modified here.
            Direction primaryFlow = getPrimaryFlowDirection(flowState, blockState, connDown, connUp, connNorth, connSouth, connWest, connEast);

            // --- Exponential slope calculation ---
            // Source block (dist == 0 / isSource): stays 100% full height (0.8125F) throughout.
            // Downstream pipes (dist > 0): slope smoothly from y(dist - 1) to y(dist),
            // where y(d) = FULL_HEIGHT - (d / maxDist)^1.5 * MAX_DROP.
            // At the final terminal end (dist == maxDist / isOpenEndpoint), the exit lip reaches VANILLA_END (0.1875F).
            final float FULL_HEIGHT  = 0.8125F;  // Pipe interior ceiling (13/16)
            final float VANILLA_END  = 0.1875F;  // Vanilla level-7 water height (3/16)
            final float MAX_DROP     = FULL_HEIGHT - VANILLA_END;  // 0.625F total fall
            final float EXPONENT     = 1.5F;

            float yUpstream   = FULL_HEIGHT;   // top-face height at the upstream (entry) corner
            float yDownstream = FULL_HEIGHT;   // top-face height at the downstream (exit) corner

            if (flowState != null && flowState.hasWater() && primaryFlow != null
                    && primaryFlow.getAxis().isHorizontal() && !connUp) {
                int dist    = flowState.getDistance();
                int maxDist = Math.max(1, flowState.getMaxDistance());
                boolean isSource = flowState.isSource() || dist == 0;

                if (isSource) {
                    // Source block is always full height
                    yUpstream   = FULL_HEIGHT;
                    yDownstream = FULL_HEIGHT;
                } else {
                    // Entry of this pipe matches the exit of the previous pipe (dist - 1)
                    float tIn = (float) (dist - 1) / maxDist;
                    yUpstream = FULL_HEIGHT - (float) Math.pow(tIn, EXPONENT) * MAX_DROP;

                    // Exit of this pipe reaches the level for this distance
                    float tOut = Math.min(1.0F, (float) dist / maxDist);
                    yDownstream = FULL_HEIGHT - (float) Math.pow(tOut, EXPONENT) * MAX_DROP;

                    if (flowState.isOpenEndpoint() || dist >= maxDist) {
                        yDownstream = VANILLA_END;
                    }
                }

                // Clamp to valid range
                yUpstream   = Math.max(VANILLA_END, Math.min(FULL_HEIGHT, yUpstream));
                yDownstream = Math.max(VANILLA_END, Math.min(FULL_HEIGHT, yDownstream));
            }

            if (count == 0) {
                // Isolated pipe — axis-based straight segment, no connections to cull
                Direction.Axis axis = blockState.hasProperty(HollowPipeBlock.AXIS) ? blockState.getValue(HollowPipeBlock.AXIS) : Direction.Axis.Y;
                renderStraightFluid(axis, false, false, false, false, false, false, primaryFlow, yUpstream, yDownstream, poseStack, buffer, sprite, color, light, overlay);
            } else if (count == 1 || (count == 2 && ((connDown && connUp) || (connNorth && connSouth) || (connWest && connEast)))) {
                // Straight segment — cull end-caps in connected directions
                Direction.Axis axis = (connDown || connUp) ? Direction.Axis.Y : ((connNorth || connSouth) ? Direction.Axis.Z : Direction.Axis.X);
                renderStraightFluid(axis, connDown, connUp, connNorth, connSouth, connWest, connEast, primaryFlow, yUpstream, yDownstream, poseStack, buffer, sprite, color, light, overlay);
            } else {
                // Multi-directional junction — expand fluid box into all connected directions
                float x1 = connWest  ? 0.0F : 0.125F;
                float x2 = connEast  ? 1.0F : 0.875F;
                float y1 = connDown  ? 0.0F : 0.125F;
                float y2 = connUp    ? 1.0F : FULL_HEIGHT;
                float z1 = connNorth ? 0.0F : 0.125F;
                float z2 = connSouth ? 1.0F : 0.875F;

                renderFluidJunction(poseStack, buffer, x1, y1, z1, x2, y2, z2,
                        connDown, connUp, connNorth, connSouth, connWest, connEast,
                        primaryFlow, yUpstream, yDownstream, sprite, color, light, overlay);
            }

        } else {
            Direction.Axis axis = blockState.hasProperty(HollowLogBlock.AXIS) ? blockState.getValue(HollowLogBlock.AXIS) : Direction.Axis.Y;
            renderStraightFluid(axis, false, false, false, false, false, false, null, 0.8125F, 0.8125F, poseStack, buffer, sprite, color, light, overlay);
        }
    }

    private static boolean hasRenderableFluid(Level level, BlockPos pos, Direction direction) {
        if (level == null || pos == null) return false;
        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof HollowPipeBlock)) return false;
        if (neighborState.hasProperty(HollowPipeBlock.WATERLOGGED)
                && neighborState.getValue(HollowPipeBlock.WATERLOGGED)) return true;
        if (neighborState.hasProperty(HollowPipeBlock.LAVA_LOGGED)
                && neighborState.getValue(HollowPipeBlock.LAVA_LOGGED)) return true;
        if (level.getBlockEntity(neighborPos) instanceof HollowLogBlockEntity neighborEntity) {
            PipeFlowState neighborFlow = neighborEntity.getPipeFlowState();
            return neighborFlow != null && neighborFlow.hasWater();
        }
        return false;
    }

    /**
     * Returns the primary flow direction for slope calculations.
     * READ-ONLY: reads flowState.getFlowDirections() without modifying it.
     * Falls back to blockstate axis when no flow is active.
     */
    private static Direction getPrimaryFlowDirection(PipeFlowState flowState, BlockState blockState,
            boolean connDown, boolean connUp, boolean connNorth, boolean connSouth, boolean connWest, boolean connEast) {
        if (flowState != null && !flowState.getFlowDirections().isEmpty()) {
            // The physical open end is the downstream end. Prefer it over the
            // EnumSet iteration order, which is not a valid flow ordering at a
            // branch/junction and caused slopes to point sideways.
            for (Direction dir : flowState.getFlowDirections()) {
                if (dir.getAxis().isHorizontal() && HollowPipeBlock.isOpenEndpoint(blockState, dir)) {
                    return dir;
                }
            }
            // Priority: first horizontal flow direction, then down, then up
            for (Direction dir : flowState.getFlowDirections()) {
                if (dir.getAxis().isHorizontal()) return dir;
            }
            for (Direction dir : flowState.getFlowDirections()) {
                if (dir == Direction.DOWN) return dir;
            }
            return flowState.getFlowDirections().iterator().next();
        }
        // Fall back to connection topology
        if (connNorth) return Direction.NORTH;
        if (connSouth) return Direction.SOUTH;
        if (connEast)  return Direction.EAST;
        if (connWest)  return Direction.WEST;
        if (connDown)  return Direction.DOWN;
        return null;
    }

    /**
     * Renders a straight fluid segment along an axis.
     *
     * The connXxx parameters indicate which ends are topologically CONNECTED to another pipe.
     * Connected ends have their end-cap face CULLED — the neighbour pipe's fluid will continue
     * flush without a dividing wall between them. This creates the continuous stream appearance.
     *
     * @param connDown/Up/North/South/West/East — true if this end is connected to another pipe
     * @param primaryFlow   — optional flow direction for the exponential top-face slope
     * @param yUpstream     — water surface height at the upstream corner of this pipe
     * @param yDownstream   — water surface height at the downstream corner of this pipe
     */
    private static void renderStraightFluid(
            Direction.Axis axis,
            boolean connDown, boolean connUp, boolean connNorth, boolean connSouth, boolean connWest, boolean connEast,
            Direction primaryFlow,
            float yUpstream, float yDownstream,
            PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int color, int light, int overlay) {

        float x1 = 0.125F, x2 = 0.875F;
        float y1 = 0.125F, y2 = 0.8125F;
        float z1 = 0.125F, z2 = 0.875F;

        // Extend fluid bounds to block face in the connected directions
        if (connDown)  y1 = 0.0F;
        if (connUp)    y2 = 1.0F;
        if (connNorth) z1 = 0.0F;
        if (connSouth) z2 = 1.0F;
        if (connWest)  x1 = 0.0F;
        if (connEast)  x2 = 1.0F;

        // Also extend in the natural open-ends of the axis
        switch (axis) {
            case X: x1 = 0.0F; x2 = 1.0F; break;
            case Z: z1 = 0.0F; z2 = 1.0F; break;
            default: // Y
                y1 = connDown ? 0.0F : 0.125F;
                y2 = connUp ? 1.0F : 0.8125F;
                break;
        }

        renderFluidBox(poseStack, buffer,
                axis, x1, y1, z1, x2, y2, z2,
                connDown, connUp, connNorth, connSouth, connWest, connEast,
                primaryFlow, yUpstream, yDownstream, sprite, color, light, overlay);
    }

    private static void renderFluidJunction(
            PoseStack poseStack, VertexConsumer buffer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            boolean connDown, boolean connUp, boolean connNorth, boolean connSouth, boolean connWest, boolean connEast,
            Direction primaryFlow,
            float yUpstream, float yDownstream,
            TextureAtlasSprite sprite, int color, int light, int overlay) {

        renderFluidBox(poseStack, buffer,
                null, x1, y1, z1, x2, y2, z2,
                connDown, connUp, connNorth, connSouth, connWest, connEast,
                primaryFlow, yUpstream, yDownstream, sprite, color, light, overlay);
    }

    /**
     * Renders a fluid-filled AABB box with precise face culling and exponential slope.
     *
     * Face culling:
     *   - connXxx == true means the fluid continues into the adjacent pipe: cull that face.
     *   - connXxx == false: render the face (closed wall or open end).
     *
     * Top-face slope:
     *   The top face uses a per-corner height computed from yUpstream/yDownstream.
     *   "Upstream" corners = the two corners on the side that water enters from.
     *   "Downstream" corners = the two corners on the side that water exits toward.
     *   This creates a planar tilt across the pipe interior in the flow direction.
     *
     *   For the last pipe in the chain (isOpenEndpoint), yDownstream is forced to
     *   vanilla's level-7 height (0.1875), giving a steep end-slope identical to
     *   how vanilla water looks at the last block before a waterfall.
     */
    private static void renderFluidBox(
            PoseStack poseStack, VertexConsumer buffer,
            Direction.Axis axis,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            boolean connDown, boolean connUp, boolean connNorth, boolean connSouth, boolean connWest, boolean connEast,
            Direction primaryFlow,
            float yUpstream, float yDownstream,
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

        // --- Exponential top-face slope ---
        // The top-face 4 corners are assigned heights from yUpstream/yDownstream
        // based on which side of the pipe they are on relative to the flow direction.
        //
        // Corner naming (looking down at the block from above, North = -Z, East = +X):
        //   topNW = north-west corner (x1, z1)
        //   topNE = north-east corner (x2, z1)
        //   topSW = south-west corner (x1, z2)
        //   topSE = south-east corner (x2, z2)
        //
        // For EAST flow: upstream = west face (NW, SW), downstream = east face (NE, SE)
        // etc.
        float topNW = y2, topNE = y2, topSW = y2, topSE = y2;
        if (primaryFlow != null && !connUp) {
            switch (primaryFlow) {
                case EAST:
                    // Upstream = west side (NW, SW), downstream = east side (NE, SE)
                    topNW = yUpstream;   topSW = yUpstream;
                    topNE = yDownstream; topSE = yDownstream;
                    break;
                case WEST:
                    // Upstream = east side (NE, SE), downstream = west side (NW, SW)
                    topNE = yUpstream;   topSE = yUpstream;
                    topNW = yDownstream; topSW = yDownstream;
                    break;
                case SOUTH:
                    // Upstream = north side (NW, NE), downstream = south side (SW, SE)
                    topNW = yUpstream;   topNE = yUpstream;
                    topSW = yDownstream; topSE = yDownstream;
                    break;
                case NORTH:
                    // Upstream = south side (SW, SE), downstream = north side (NW, NE)
                    topSW = yUpstream;   topSE = yUpstream;
                    topNW = yDownstream; topNE = yDownstream;
                    break;
                default:
                    // DOWN/UP: no horizontal slope
                    break;
            }
        }

        // 1. Top face (+Y) — the primary water surface, rendered double-sided
        if (!connUp) {
            // Outward top (+Y)
            addVertex(matrix, buffer, x1, topNW, z1, r, g, b, a, u0, v0, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x2, topNE, z1, r, g, b, a, u1, v0, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x2, topSE, z2, r, g, b, a, u1, v1, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x1, topSW, z2, r, g, b, a, u0, v1, light, overlay, 0, 1, 0);

            // Inward underside (-Y)
            addVertex(matrix, buffer, x1, topSW, z2, r, g, b, a, u0, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, topSE, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, topNE, z1, r, g, b, a, u1, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x1, topNW, z1, r, g, b, a, u0, v0, light, overlay, 0, -1, 0);
        }

        // 2. Bottom face (-Y) — double-sided when vertical pipe is open downwards
        if ((axis == Direction.Axis.Y || axis == null) && !connDown) {
            // Outward bottom (-Y)
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v0, light, overlay, 0, -1, 0);
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v0, light, overlay, 0, -1, 0);

            // Inward floor (+Y)
            addVertex(matrix, buffer, x1, y1, z1, r, g, b, a, u0, v0, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x2, y1, z1, r, g, b, a, u1, v0, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x2, y1, z2, r, g, b, a, u1, v1, light, overlay, 0, 1, 0);
            addVertex(matrix, buffer, x1, y1, z2, r, g, b, a, u0, v1, light, overlay, 0, 1, 0);
        }

        // 3. North face (-Z) — open end-cap for Z-axis pipes or junctions, rendered double-sided
        if ((axis == Direction.Axis.Z || axis == null) && !connNorth) {
            float topW = topNW, topE = topNE;
            // Outward North (-Z)
            addVertex(matrix, buffer, x2, topE, z1, r, g, b, a, u1, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x1, topW, z1, r, g, b, a, u0, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x1, y1,   z1, r, g, b, a, u0, v1, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y1,   z1, r, g, b, a, u1, v1, light, overlay, 0, 0, -1);

            // Inward North (+Z)
            addVertex(matrix, buffer, x2, y1,   z1, r, g, b, a, u1, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x1, y1,   z1, r, g, b, a, u0, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x1, topW, z1, r, g, b, a, u0, v0, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, topE, z1, r, g, b, a, u1, v0, light, overlay, 0, 0, 1);
        }

        // 4. South face (+Z) — open end-cap for Z-axis pipes or junctions, rendered double-sided
        if ((axis == Direction.Axis.Z || axis == null) && !connSouth) {
            float topW = topSW, topE = topSE;
            // Outward South (+Z)
            addVertex(matrix, buffer, x1, topW, z2, r, g, b, a, u0, v0, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, topE, z2, r, g, b, a, u1, v0, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x2, y1,   z2, r, g, b, a, u1, v1, light, overlay, 0, 0, 1);
            addVertex(matrix, buffer, x1, y1,   z2, r, g, b, a, u0, v1, light, overlay, 0, 0, 1);

            // Inward South (-Z)
            addVertex(matrix, buffer, x1, y1,   z2, r, g, b, a, u0, v1, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, y1,   z2, r, g, b, a, u1, v1, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x2, topE, z2, r, g, b, a, u1, v0, light, overlay, 0, 0, -1);
            addVertex(matrix, buffer, x1, topW, z2, r, g, b, a, u0, v0, light, overlay, 0, 0, -1);
        }

        // 5. West face (-X) — open end-cap for X-axis pipes or junctions, rendered double-sided
        if ((axis == Direction.Axis.X || axis == null) && !connWest) {
            float topN = topNW, topS = topSW;
            // Outward West (-X)
            addVertex(matrix, buffer, x1, topN, z1, r, g, b, a, u0, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, topS, z2, r, g, b, a, u1, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1,   z2, r, g, b, a, u1, v1, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x1, y1,   z1, r, g, b, a, u0, v1, light, overlay, -1, 0, 0);

            // Inward West (+X)
            addVertex(matrix, buffer, x1, y1,   z1, r, g, b, a, u0, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x1, y1,   z2, r, g, b, a, u1, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x1, topS, z2, r, g, b, a, u1, v0, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x1, topN, z1, r, g, b, a, u0, v0, light, overlay, 1, 0, 0);
        }

        // 6. East face (+X) — open end-cap for X-axis pipes or junctions, rendered double-sided
        if ((axis == Direction.Axis.X || axis == null) && !connEast) {
            float topN = topNE, topS = topSE;
            // Outward East (+X)
            addVertex(matrix, buffer, x2, topS, z2, r, g, b, a, u0, v0, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, topN, z1, r, g, b, a, u1, v0, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y1,   z1, r, g, b, a, u1, v1, light, overlay, 1, 0, 0);
            addVertex(matrix, buffer, x2, y1,   z2, r, g, b, a, u0, v1, light, overlay, 1, 0, 0);

            // Inward East (-X)
            addVertex(matrix, buffer, x2, y1,   z2, r, g, b, a, u0, v1, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x2, y1,   z1, r, g, b, a, u1, v1, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x2, topN, z1, r, g, b, a, u1, v0, light, overlay, -1, 0, 0);
            addVertex(matrix, buffer, x2, topS, z2, r, g, b, a, u0, v0, light, overlay, -1, 0, 0);
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
