package com.kingodogo.buildscape.variantengine.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.BlockRegistryScanner;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VariantModelBakingManager {

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        BuildScape.LOGGER.info("VariantEngine: Dynamically baking models for variants...");
        Map<ResourceLocation, BakedModel> models = event.getModelRegistry();

        for (BlockFamily family : BlockRegistryScanner.getDetectedFamilies()) {
            Block base = family.getBaseBlock();
            ResourceLocation baseId = base.getRegistryName();
            if (baseId == null) continue;

            // Robust Parent Model lookup
            BakedModel parentModel = null;
            
            // Try 1: Item inventory model (most reliable for simple cubes)
            parentModel = models.get(new ModelResourceLocation(baseId, "inventory"));
            
            // Try 2: Simple block name (some mods use this)
            if (parentModel == null) parentModel = models.get(baseId);
            
            // Try 3: Block with 'normal' variant
            if (parentModel == null) parentModel = models.get(new ModelResourceLocation(baseId, "normal"));

            // Try 4: Explicit block model path
            if (parentModel == null) {
                 parentModel = models.get(new ResourceLocation(baseId.getNamespace() + ":block/" + baseId.getPath()));
            }
            
            // Try 5: Exhaustive search (ignoring variant/suffix)
            if (parentModel == null) {
                for (Map.Entry<ResourceLocation, BakedModel> entry : models.entrySet()) {
                    ResourceLocation loc = entry.getKey();
                    if (loc.getNamespace().equals(BuildScape.MODID)) continue;
                    if (loc.getNamespace().equals(baseId.getNamespace()) && loc.getPath().equals(baseId.getPath())) {
                        parentModel = entry.getValue();
                        break;
                    }
                }
            }

            if (parentModel == null) {
                // Final fallback: look specifically for the block model in the original namespace
                for (Map.Entry<ResourceLocation, BakedModel> entry : models.entrySet()) {
                    ResourceLocation loc = entry.getKey();
                    if (loc.getNamespace().equals(BuildScape.MODID)) continue;
                    if (loc.getNamespace().equals(baseId.getNamespace()) && loc.getPath().contains("block/" + baseId.getPath())) {
                        parentModel = entry.getValue();
                        break;
                    }
                }
            }

            if (parentModel == null) {
                continue;
            }

            final BakedModel finalParent = parentModel;
            // Generate models for our registered variants
            family.getVariants().forEach((shape, variant) -> {
                ResourceLocation variantId = variant.getRegistryName();
                if (variantId != null && variantId.getNamespace().equals(BuildScape.MODID)) {
                    BakedModel variantModel = new VariantBakedModel(shape, finalParent);
                    
                    // Put in the correct model locations for blockstates
                    models.put(new ResourceLocation(variantId.getNamespace(), "block/" + variantId.getPath()), variantModel);
                    
                    // Put for item model
                    models.put(new ModelResourceLocation(variantId, "inventory"), variantModel);

                    // And for double slabs
                    if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB) {
                        models.put(new ResourceLocation(variantId.getNamespace(), "block/" + variantId.getPath() + "_double"), variantModel);
                    }
                }
            });
        }
    }

    private static class VariantBakedModel implements BakedModel {
        private final com.kingodogo.buildscape.variantengine.builder.BlockShape shape;
        private final BakedModel parent;

        public VariantBakedModel(com.kingodogo.buildscape.variantengine.builder.BlockShape shape, BakedModel parent) {
            this.shape = shape;
            this.parent = parent;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            List<BakedQuad> quads = new ArrayList<>();
            boolean isItem = state == null;
            
            if (!isItem) {
                if (state.getBlock() instanceof com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock) {
                    addVerticalSlabQuads(quads, parent, state.getValue(com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock.TYPE), side, rand, false);
                } else if (state.getBlock() instanceof com.kingodogo.buildscape.variantengine.block.VerticalStairsBlock) {
                    addVerticalStairQuads(quads, parent, state.getValue(com.kingodogo.buildscape.variantengine.block.VerticalStairsBlock.FACING), side, rand, false);
                } else if (state.getBlock() instanceof com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock) {
                    addQuarterPieceQuads(quads, parent,
                        state.getValue(com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock.NORTH_WEST),
                        state.getValue(com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock.NORTH_EAST),
                        state.getValue(com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock.SOUTH_WEST),
                        state.getValue(com.kingodogo.buildscape.variantengine.block.QuarterPieceBlock.SOUTH_EAST),
                        side, rand, false);
                } else if (state.getBlock() instanceof com.kingodogo.buildscape.variantengine.block.VerticalQuarterPieceBlock) {
                    addVerticalQuarterPieceQuads(quads, parent, state.getValue(com.kingodogo.buildscape.variantengine.block.VerticalQuarterPieceBlock.FACING), side, rand, false);
                } else {
                    addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 1, side, rand, false);
                }
            } else {
                // Item Preview
                if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_SLAB) {
                    addVerticalSlabQuads(quads, parent, com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock.VerticalSlabType.NORTH, side, rand, true);
                } else if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_STAIRS) {
                    addVerticalStairQuads(quads, parent, com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection.SOUTH_WEST, side, rand, true);
                } else if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.QUARTER_PIECE) {
                    addQuarterPieceQuads(quads, parent, true, true, false, false, side, rand, true);
                } else if (shape == com.kingodogo.buildscape.variantengine.builder.BlockShape.VERTICAL_QUARTER_PIECE) {
                    addVerticalQuarterPieceQuads(quads, parent, com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection.SOUTH_WEST, side, rand, true);
                } else {
                    addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 1, side, rand, true);
                }
            }
            
            return quads;
        }

        private void addVerticalSlabQuads(List<BakedQuad> quads, BakedModel parent, com.kingodogo.buildscape.variantengine.block.VerticalSlabBlock.VerticalSlabType type, @Nullable Direction side, Random rand, boolean isItem) {
            switch (type) {
                case NORTH -> addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 0.5f, side, rand, isItem);
                case SOUTH -> addTransformedBox(quads, parent, 0, 0, 0.5f, 1, 1, 1, side, rand, isItem);
                case EAST -> addTransformedBox(quads, parent, 0.5f, 0, 0, 1, 1, 1, side, rand, isItem);
                case WEST -> addTransformedBox(quads, parent, 0, 0, 0, 0.5f, 1, 1, side, rand, isItem);
                case DOUBLE -> addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 1, side, rand, isItem);
            }
        }

        private void addVerticalStairQuads(List<BakedQuad> quads, BakedModel parent, com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection facing, @Nullable Direction side, Random rand, boolean isItem) {
            switch (facing) {
                case NORTH_WEST -> { 
                    addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 0.5f, side, rand, isItem);
                    addTransformedBox(quads, parent, 0, 0, 0.5f, 0.5f, 1, 1, side, rand, isItem);
                }
                case NORTH_EAST -> { 
                    addTransformedBox(quads, parent, 0, 0, 0, 1, 1, 0.5f, side, rand, isItem);
                    addTransformedBox(quads, parent, 0.5f, 0, 0.5f, 1, 1, 1, side, rand, isItem);
                }
                case SOUTH_WEST -> { 
                    addTransformedBox(quads, parent, 0, 0, 0.5f, 1, 1, 1, side, rand, isItem);
                    addTransformedBox(quads, parent, 0, 0, 0, 0.5f, 1, 0.5f, side, rand, isItem);
                }
                case SOUTH_EAST -> { 
                    addTransformedBox(quads, parent, 0, 0, 0.5f, 1, 1, 1, side, rand, isItem);
                    addTransformedBox(quads, parent, 0.5f, 0, 0, 1, 1, 0.5f, side, rand, isItem);
                }
            }
        }

        private void addVerticalQuarterPieceQuads(List<BakedQuad> quads, BakedModel parent, com.kingodogo.buildscape.variantengine.util.HorizontalCornerDirection facing, @Nullable Direction side, Random rand, boolean isItem) {
            switch (facing) {
                case NORTH_WEST -> addTransformedBox(quads, parent, 0, 0, 0, 0.5f, 1, 0.5f, side, rand, isItem);
                case NORTH_EAST -> addTransformedBox(quads, parent, 0.5f, 0, 0, 1, 1, 0.5f, side, rand, isItem);
                case SOUTH_WEST -> addTransformedBox(quads, parent, 0, 0, 0.5f, 0.5f, 1, 1, side, rand, isItem);
                case SOUTH_EAST -> addTransformedBox(quads, parent, 0.5f, 0, 0.5f, 1, 1, 1, side, rand, isItem);
            }
        }

        private void addQuarterPieceQuads(List<BakedQuad> quads, BakedModel parent, boolean nw, boolean ne, boolean sw, boolean se, @Nullable Direction side, Random rand, boolean isItem) {
            if (nw) addTransformedBox(quads, parent, 0, 0, 0, 0.5f, 1, 0.5f, side, rand, isItem);
            if (ne) addTransformedBox(quads, parent, 0.5f, 0, 0, 1, 1, 0.5f, side, rand, isItem);
            if (sw) addTransformedBox(quads, parent, 0, 0, 0.5f, 0.5f, 1, 1, side, rand, isItem);
            if (se) addTransformedBox(quads, parent, 0.5f, 0, 0.5f, 1, 1, 1, side, rand, isItem);
        }

        private void addTransformedBox(List<BakedQuad> quads, BakedModel parent, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, @Nullable Direction side, Random rand, boolean isItem) {
            for (Direction dir : Direction.values()) {
                for (BakedQuad quad : parent.getQuads(null, dir, rand)) {
                    processQuad(quads, quad, minX, minY, minZ, maxX, maxY, maxZ, side, true, isItem);
                }
            }
            for (BakedQuad quad : parent.getQuads(null, null, rand)) {
                processQuad(quads, quad, minX, minY, minZ, maxX, maxY, maxZ, side, false, isItem);
            }
        }

        private void processQuad(List<BakedQuad> quads, BakedQuad quad, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, @Nullable Direction side, boolean hasAssignedSide, boolean isItem) {
             BakedQuad transformed = transformQuad(quad, minX, minY, minZ, maxX, maxY, maxZ);
             
             if (isItem) {
                 // For items, we only add quads during the null pass to avoid multiple additions
                 if (side == null) quads.add(transformed);
                 return;
             }

             Direction quadDir = transformed.getDirection();
             boolean isEdge = false;
             if (quadDir == Direction.NORTH && Math.abs(getMinZ(transformed) - minZ) < 0.001f) isEdge = true;
             else if (quadDir == Direction.SOUTH && Math.abs(getMaxZ(transformed) - maxZ) < 0.001f) isEdge = true;
             else if (quadDir == Direction.WEST && Math.abs(getMinX(transformed) - minX) < 0.001f) isEdge = true;
             else if (quadDir == Direction.EAST && Math.abs(getMaxX(transformed) - maxX) < 0.001f) isEdge = true;
             else if (quadDir == Direction.UP && Math.abs(getMaxY(transformed) - maxY) < 0.001f) isEdge = true;
             else if (quadDir == Direction.DOWN && Math.abs(getMinY(transformed) - minY) < 0.001f) isEdge = true;

             if (side == null) {
                 if (!isEdge) quads.add(transformed);
             } else if (side == quadDir && isEdge) {
                 quads.add(transformed);
             }
        }

        private BakedQuad transformQuad(BakedQuad quad, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            int[] data = quad.getVertices().clone();
            int stride = data.length / 4;
            for (int i = 0; i < 4; i++) {
                int offset = i * stride;
                float x = Float.intBitsToFloat(data[offset]);
                float y = Float.intBitsToFloat(data[offset + 1]);
                float z = Float.intBitsToFloat(data[offset + 2]);

                data[offset] = Float.floatToRawIntBits(minX + x * (maxX - minX));
                data[offset + 1] = Float.floatToRawIntBits(minY + y * (maxY - minY));
                data[offset + 2] = Float.floatToRawIntBits(minZ + z * (maxZ - minZ));
            }
            return new BakedQuad(data, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
        }

        private float getMinX(BakedQuad quad) {
            float min = Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) min = Math.min(min, getVertexCoord(quad, i, 0));
            return min;
        }
        private float getMaxX(BakedQuad quad) {
            float max = -Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) max = Math.max(max, getVertexCoord(quad, i, 0));
            return max;
        }
        private float getMinY(BakedQuad quad) {
            float min = Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) min = Math.min(min, getVertexCoord(quad, i, 1));
            return min;
        }
        private float getMaxY(BakedQuad quad) {
            float max = -Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) max = Math.max(max, getVertexCoord(quad, i, 1));
            return max;
        }
        private float getMinZ(BakedQuad quad) {
            float min = Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) min = Math.min(min, getVertexCoord(quad, i, 2));
            return min;
        }
        private float getMaxZ(BakedQuad quad) {
            float max = -Float.MAX_VALUE;
            for (int i = 0; i < 4; i++) max = Math.max(max, getVertexCoord(quad, i, 2));
            return max;
        }

        private float getVertexCoord(BakedQuad quad, int vertexIndex, int coordIndex) {
            int[] data = quad.getVertices();
            int stride = data.length / 4;
            return Float.intBitsToFloat(data[vertexIndex * stride + coordIndex]);
        }

        @Override public boolean useAmbientOcclusion() { return parent.useAmbientOcclusion(); }
        @Override public boolean isGui3d() { return parent.isGui3d(); }
        @Override public boolean usesBlockLight() { return parent.usesBlockLight(); }
        @Override public boolean isCustomRenderer() { return parent.isCustomRenderer(); }
        @Override public TextureAtlasSprite getParticleIcon() { return parent.getParticleIcon(); }
        @Override public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() { return parent.getOverrides(); }
        @Override public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() { return parent.getTransforms(); }
    }
}
