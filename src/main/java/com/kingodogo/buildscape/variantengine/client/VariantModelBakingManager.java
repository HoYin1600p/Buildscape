package com.kingodogo.buildscape.variantengine.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.VariantRegistrar;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
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
        try {
            BuildScape.LOGGER.info("VariantEngine: Restoring block textures and fixing item icons...");
            Map<ResourceLocation, BakedModel> models = event.getModelRegistry();
            ResourceLocation missing = MissingTextureAtlasSprite.getLocation();

            List<BlockFamily> families;
            try {
                families = VariantRegistrar.getDetectedFamilies();
            } catch (Exception e) {
                BuildScape.LOGGER.error("VariantEngine: Failed to get detected families, skipping model bake: {}", e.getMessage());
                return;
            }

            int processed = 0;
            for (BlockFamily family : families) {
                try {
                    Block base = family.getBaseBlock();
                    ResourceLocation baseId = base.getRegistryName();
                    if (baseId == null) continue;

                    // Find a good donor block model (the base block itself)
                    BakedModel donor = findDonorModel(baseId, models);
                    if (donor == null) continue;

                    family.getVariants().forEach((shape, variant) -> {
                        try {
                            ResourceLocation variantId = variant.getRegistryName();
                            if (variantId == null || !variantId.getNamespace().equals(BuildScape.MODID)) return;

                            for (Map.Entry<ResourceLocation, BakedModel> entry : models.entrySet()) {
                                ResourceLocation loc = entry.getKey();
                                if (loc.getNamespace().equals(variantId.getNamespace()) && 
                                    (loc.getPath().equals(variantId.getPath()) || loc.getPath().equals(variantId.getPath() + "_double"))) {
                                    BakedModel original = entry.getValue();
                                    if (original != null && !(original instanceof VariantBakedModel)) {
                                        boolean isItem = loc instanceof ModelResourceLocation && ((ModelResourceLocation)loc).getVariant().equals("inventory");
                                        models.put(loc, new VariantBakedModel(original, donor, isItem, getCleanState(base), missing));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            BuildScape.LOGGER.debug("VariantEngine: Skipping variant model for {}: {}", variant.getRegistryName(), e.getMessage());
                        }
                    });
                    processed++;
                } catch (Exception e) {
                    BuildScape.LOGGER.debug("VariantEngine: Skipping family during model bake: {}", e.getMessage());
                }
            }
            BuildScape.LOGGER.info("VariantEngine: Model bake complete. Processed {} families.", processed);
        } catch (Exception e) {
            BuildScape.LOGGER.error("VariantEngine: Model bake failed entirely (non-fatal): {}", e.getMessage());
        }
    }

    private static BakedModel findDonorModel(ResourceLocation id, Map<ResourceLocation, BakedModel> registry) {
        // Priority: Real block states often have better directional data than inventory models
        String[] variants = {"axis=y", "facing=north", "type=double", "type=top", "normal", "inventory", ""};
        for (String v : variants) {
            BakedModel m = registry.get(new ModelResourceLocation(id, v));
            if (isValidModel(m)) return m;
        }
        // Last ditch: literal path search (unlikely but safe)
        BakedModel m = registry.get(new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
        if (isValidModel(m)) return m;
        
        return null;
    }

    private static boolean isValidModel(BakedModel model) {
        if (model == null || model instanceof VariantBakedModel) return false;
        try {
            return !model.getQuads(null, null, new Random()).isEmpty() || 
                   Arrays.stream(Direction.values()).anyMatch(d -> !model.getQuads(null, d, new Random()).isEmpty());
        } catch (Exception e) { return false; }
    }

    private static BlockState getCleanState(Block block) {
        BlockState state = block.defaultBlockState();
        try {
            if (state.hasProperty(BlockStateProperties.AXIS)) state = state.setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
            if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) state = state.setValue(BlockStateProperties.SLAB_TYPE, SlabType.DOUBLE);
        } catch (Exception ignored) {}
        return state;
    }

    private static class VariantBakedModel implements BakedModel {
        private final BakedModel geometry;
        private final BakedModel donor;
        private final boolean isItem;
        private final BlockState donorState;
        private final ResourceLocation missing;
        private final Map<Direction, TextureAtlasSprite> resolvedSprites = new EnumMap<>(Direction.class);

        public VariantBakedModel(BakedModel geometry, BakedModel donor, boolean isItem, BlockState donorState, ResourceLocation missing) {
            this.geometry = geometry;
            this.donor = donor;
            this.isItem = isItem;
            this.donorState = donorState;
            this.missing = missing;
            
            // PRE-RESOLVE sprites on the Render Thread!
            Random rand = new Random(42);
            for (Direction d : Direction.values()) {
                resolvedSprites.put(d, findBestSprite(d, rand));
            }
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            // For items, we aggregate ALL quads into the null-side list to avoid culling issues in GUI
            if (isItem) {
                if (side != null) return Collections.emptyList();
                List<BakedQuad> itemQuads = new ArrayList<>();
                itemQuads.addAll(geometry.getQuads(null, null, rand));
                for (Direction d : Direction.values()) itemQuads.addAll(geometry.getQuads(null, d, rand));
                return processQuads(itemQuads, rand);
            }

            return processQuads(geometry.getQuads(state, side, rand), rand);
        }

        private List<BakedQuad> processQuads(List<BakedQuad> jsonQuads, Random rand) {
            List<BakedQuad> result = new ArrayList<>();
            for (BakedQuad quad : jsonQuads) {
                Direction dir = quad.getDirection();
                if (dir == null) dir = inferDirection(quad.getVertices());

                // Use the PRE-RESOLVED sprite
                TextureAtlasSprite sprite = resolvedSprites.get(dir);
                if (sprite == null) sprite = resolvedSprites.get(Direction.NORTH); // fallback

                result.add(remapUVs(quad, sprite));
            }
            return result;
        }

        private TextureAtlasSprite findBestSprite(Direction dir, Random rand) {
            TextureAtlasSprite topS = null;
            TextureAtlasSprite sideS = null;
            TextureAtlasSprite fallbackS = null;

            // Step 1: Geometry-based Search in Donor
            List<BakedQuad> allDonor = new ArrayList<>();
            allDonorQuads(allDonor, donor, donorState, rand);
            
            for (BakedQuad q : allDonor) {
                TextureAtlasSprite s = q.getSprite();
                if (!isValidSprite(s)) continue;
                if (fallbackS == null) fallbackS = s;
                
                Direction qDir = q.getDirection();
                if (qDir == null) qDir = inferDirection(q.getVertices());
                
                if (qDir == Direction.UP) {
                    if (topS == null) topS = s;
                } else if (qDir.getAxis().isHorizontal()) {
                    if (sideS == null) sideS = s;
                }
            }

            // Step 2: Registry Guessing (If geometry failed or for extra precision)
            ResourceLocation id = donorState.getBlock().getRegistryName();
            if (id != null) {
                String ns = id.getNamespace();
                String path = id.getPath();
                // Strip common variant suffixes/prefixes to get the core name
                String cp = path.replace("_slab", "").replace("_stairs", "").replace("_stair", "")
                                .replace("_vertical", "").replace("v_slab_", "").replace("v_stair_", "")
                                .replace("vslab_", "").replace("vstair_", "");
                
                String[] guesses = dir.getAxis().isVertical() ? 
                    new String[]{cp + "_top", cp + "_log_top", cp + "_end", cp + "_cap", cp + "_all", cp + "_up", cp + "_side", cp} :
                    new String[]{cp + "_side", cp + "_bark", cp + "_block", cp + "_all", cp + "_front", cp + "_back", cp};
                
                for (String g : guesses) {
                    try {
                        TextureAtlasSprite s = net.minecraft.client.Minecraft.getInstance()
                            .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                            .apply(new ResourceLocation(ns, "block/" + g));
                        if (isValidSprite(s)) {
                            if (dir.getAxis().isVertical()) {
                                if (topS == null) topS = s;
                            } else {
                                if (sideS == null) sideS = s;
                            }
                            if (topS != null && sideS != null) break;
                        }
                    } catch (Exception ignored) {}
                }
            }

            // Step 3: Final Resolution Logic
            if (dir.getAxis().isVertical()) {
                if (topS != null) return topS;
                if (sideS != null) return sideS;
            } else {
                if (sideS != null) return sideS;
                if (topS != null) return topS;
            }

            if (fallbackS != null) return fallbackS;
            
            // Absolute fallback: try the particle icon
            try {
                TextureAtlasSprite particle = donor.getParticleIcon();
                if (isValidSprite(particle)) return particle;
            } catch (Exception ignored) {}

            // ULTIMATE FALLBACK: Scan EVERY quad in the donor for a valid sprite.
            // This handles blocks with complex models (like Bamboo) that don't use standard directional faces.
            for (BakedQuad q : allDonor) {
                TextureAtlasSprite s = q.getSprite();
                if (isValidSprite(s)) return s;
            }

            // Last resort: If it's an item, we REALLY don't want pink/black.
            if (isItem) {
                for (String common : new String[]{"oak_planks", "stone", "dirt"}) {
                    TextureAtlasSprite s = net.minecraft.client.Minecraft.getInstance()
                        .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                        .apply(new ResourceLocation("minecraft", "block/" + common));
                    if (isValidSprite(s)) return s;
                }
            }
            
            return net.minecraft.client.Minecraft.getInstance()
                .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                .apply(MissingTextureAtlasSprite.getLocation());
        }

        private void allDonorQuads(List<BakedQuad> list, BakedModel m, BlockState s, Random r) {
            list.addAll(m.getQuads(s, null, r));
            list.addAll(m.getQuads(null, null, r));
            for (Direction d : Direction.values()) {
                list.addAll(m.getQuads(s, d, r));
                list.addAll(m.getQuads(null, d, r));
            }
        }

        private @Nullable BakedQuad findBestDonorQuad(Direction dir, Random rand) {
            // Direct Direction Match is always best if valid
            List<BakedQuad> quads = new ArrayList<>(donor.getQuads(donorState, dir, rand));
            quads.addAll(donor.getQuads(null, dir, rand));
            for (BakedQuad q : quads) if (isValidSprite(q.getSprite())) return q;
            return null;
        }

        private boolean isValidSprite(@Nullable TextureAtlasSprite sprite) {
            if (sprite == null) return false;
            ResourceLocation loc = sprite.getName();
            // Critical: comparison against the missing texture location
            if (loc.equals(missing)) return false;
            
            String path = loc.getPath().toLowerCase();
            if (path.contains("missing") || path.contains("missingno") || path.equals("null")) return false;
            if (path.endsWith("/empty") || path.endsWith("/error")) return false;
            
            // A missing texture often has width/height of 16 in the atlas
            if (sprite.getWidth() <= 0 || sprite.getHeight() <= 0) return false;
            
            return true;
        }

        private Direction inferDirection(int[] vertices) {
            if (vertices.length < 28) return Direction.NORTH;
            int stride = vertices.length / 4;
            float minX = 100, maxX = -100, minY = 100, maxY = -100, minZ = 100, maxZ = -100;
            boolean pixelScale = false;
            for (int i = 0; i < 4; i++) {
                float x = Float.intBitsToFloat(vertices[i * stride]);
                float y = Float.intBitsToFloat(vertices[i * stride + 1]);
                float z = Float.intBitsToFloat(vertices[i * stride + 2]);
                if (x > 1.1f || y > 1.1f || z > 1.1f) pixelScale = true;
                minX = Math.min(minX, x); maxX = Math.max(maxX, x);
                minY = Math.min(minY, y); maxY = Math.max(maxY, y);
                minZ = Math.min(minZ, z); maxZ = Math.max(maxZ, z);
            }
            float dx = maxX - minX; float dy = maxY - minY; float dz = maxZ - minZ;
            float mid = pixelScale ? 8f : 0.5f;
            float eps = 0.05f; // Small epsilon for floating point comparison
            
            // Priority: Smallest variance axis (identifies the face plane)
            // Use a threshold to decide if a face is on one side or the other of the block center
            if (dy <= dx && dy <= dz) {
                // Horizontal face: UP or DOWN?
                // For stairs, top step is at y=1.0 (16), bottom step top is at y=0.5 (8).
                // Both should be identified as UP if they are the topmost faces of their segment.
                // Here we just check if it's in the upper half.
                return (maxY >= mid - eps) ? Direction.UP : Direction.DOWN;
            }
            if (dz <= dx && dz <= dy) {
                return (maxZ >= mid - eps) ? Direction.SOUTH : Direction.NORTH;
            }
            if (dx <= dy && dx <= dz) {
                return (maxX >= mid - eps) ? Direction.EAST : Direction.WEST;
            }
            
            return Direction.NORTH;
        }

        private BakedQuad remapUVs(BakedQuad jsonQuad, TextureAtlasSprite sprite) {
            int[] data = jsonQuad.getVertices().clone();
            if (sprite == null || !isValidSprite(sprite)) return jsonQuad;

            int tint = jsonQuad.getTintIndex();
            int stride = data.length / 4;
            float minU = sprite.getU0(); float maxU = sprite.getU1();
            float minV = sprite.getV0(); float maxV = sprite.getV1();

            Direction dir = jsonQuad.getDirection();
            if (dir == null) dir = inferDirection(jsonQuad.getVertices());

            // 1. Determine coordinate scale (0..1 or 0..16)
            float vMinX = 16, vMaxX = 0, vMinY = 16, vMaxY = 0, vMinZ = 16, vMaxZ = 0;
            for (int i = 0; i < 4; i++) {
                float x = Float.intBitsToFloat(data[i * stride]);
                float y = Float.intBitsToFloat(data[i * stride + 1]);
                float z = Float.intBitsToFloat(data[i * stride + 2]);
                vMinX = Math.min(vMinX, x); vMaxX = Math.max(vMaxX, x);
                vMinY = Math.min(vMinY, y); vMaxY = Math.max(vMaxY, y);
                vMinZ = Math.min(vMinZ, z); vMaxZ = Math.max(vMaxZ, z);
            }
            boolean isPixelScale = vMaxX > 1.1f || vMaxY > 1.1f || vMaxZ > 1.1f;
            float scale = isPixelScale ? 16.0f : 1.0f;

            for (int i = 0; i < 4; i++) {
                int offset = i * stride;
                float x = Float.intBitsToFloat(data[offset]) / scale;
                float y = Float.intBitsToFloat(data[offset + 1]) / scale;
                float z = Float.intBitsToFloat(data[offset + 2]) / scale;
                float u = 0, v = 0;
                
                // Keep within 0..1 range to prevent atlas bleed/pink textures
                x = Math.max(0, Math.min(1, x));
                y = Math.max(0, Math.min(1, y));
                z = Math.max(0, Math.min(1, z));

                switch (dir) {
                    case UP -> { u = x; v = z; }
                    case DOWN -> { u = x; v = 1.0f - z; }
                    case NORTH -> { u = 1.0f - x; v = 1.0f - y; }
                    case SOUTH -> { u = x; v = 1.0f - y; }
                    case WEST -> { u = z; v = 1.0f - y; }
                    case EAST -> { u = 1.0f - z; v = 1.0f - y; }
                    default -> { u = x; v = z; }
                }
                data[offset + 4] = Float.floatToRawIntBits(minU + (maxU - minU) * u);
                data[offset + 5] = Float.floatToRawIntBits(minV + (maxV - minV) * v);
            }
            return new BakedQuad(data, tint, dir, sprite, jsonQuad.isShade());
        }

        @Override public boolean useAmbientOcclusion() { return donor.useAmbientOcclusion(); }
        @Override public boolean isGui3d() { return donor.isGui3d(); }
        @Override public boolean usesBlockLight() { return donor.usesBlockLight(); }
        @Override public boolean isCustomRenderer() { return false; }
        @Override public TextureAtlasSprite getParticleIcon() { return donor.getParticleIcon(); }
        @Override public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() { return donor.getOverrides(); }
        @Override public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() { 
            return geometry.getTransforms(); 
        }
    }
}
