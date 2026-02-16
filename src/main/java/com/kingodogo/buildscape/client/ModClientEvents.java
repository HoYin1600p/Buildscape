package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        ModVerticalSlabs.VERTICAL_SLABS.forEach((originalSlab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            if (verticalId != null) {
                try {
                    // Try to get the FULL BLOCK model (Double Slab)
                    BlockState parentDoubleState = originalSlab.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
                    ResourceLocation parentDoubleMrl = BlockModelShaper.stateToModelLocation(parentDoubleState);
                    BakedModel parentDoubleModel = registry.get(parentDoubleMrl);

                    if (parentDoubleModel == null || parentDoubleModel == Minecraft.getInstance().getModelManager().getMissingModel()) {
                        // Fallback to default state if double is missing (unlikely for standard slabs)
                        parentDoubleModel = registry.get(BlockModelShaper.stateToModelLocation(originalSlab.defaultBlockState()));
                    }

                    if (parentDoubleModel != null) {
                        for (BlockState state : verticalSlab.getStateDefinition().getPossibleStates()) {
                            Direction facing = state.getValue(VerticalSlabBlock.FACING);
                            SlabType type = state.getValue(VerticalSlabBlock.TYPE);

                            String variant = "facing=" + facing.getName() + ",type=" + type.getSerializedName() + ",waterlogged=" + state.getValue(VerticalSlabBlock.WATERLOGGED);
                            ModelResourceLocation mrl = new ModelResourceLocation(verticalId, variant);

                            // Register correct model based on type
                            // For Double slabs (full block), use the original parent double model directly.
                            // For Vertical slabs (half block), use our custom model wrapper.
                            if (type == SlabType.DOUBLE) {
                                registry.put(mrl, parentDoubleModel);
                            } else {
                                registry.put(mrl, new VerticalSlabBakedModel(parentDoubleModel, facing, false));
                            }
                        }

                        // Item Model
                        ModelResourceLocation itemMrl = new ModelResourceLocation(verticalId, "inventory");
                        registry.put(itemMrl, new VerticalSlabBakedModel(parentDoubleModel, Direction.NORTH, true));
                    }
                } catch (Exception e) {
                    BuildScape.LOGGER.error("Failed to replace model for vertical slab: " + verticalId, e);
                }
            }
        });
    }

    private static class VerticalSlabBakedModel implements BakedModel {
        private final BakedModel original;
        private final Direction facing;
        private final boolean isInventory;
        private final Map<Direction, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();
        private List<BakedQuad> unculledCache = null;

        public VerticalSlabBakedModel(BakedModel original, Direction facing, boolean isInventory) {
            this.original = original;
            this.facing = facing;
            this.isInventory = isInventory;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            // Unculled quads (side == null) handling
            if (side == null) {
                if (unculledCache == null) {
                    unculledCache = new ArrayList<>();
                    generateQuads(null, unculledCache, rand);
                }
                return unculledCache;
            }

            // Culled quads
            return quadCache.computeIfAbsent(side, s -> {
                List<BakedQuad> list = new ArrayList<>();
                generateQuads(s, list, rand);
                return list;
            });
        }

        private void generateQuads(@Nullable Direction targetSide, List<BakedQuad> result, Random rand) {
            // We iterate over all quads of the original DOUBLE slab model
            // And selectively keep/modify them based on our facing.
            // A Vertical Slab occupies half the block space.

            for (Direction d : Direction.values()) {
                processQuads(original.getQuads(null, d, rand), targetSide, result);
            }
            processQuads(original.getQuads(null, null, rand), targetSide, result);
        }

        private void processQuads(List<BakedQuad> sourceQuads, @Nullable Direction targetSide, List<BakedQuad> result) {
            for (BakedQuad q : sourceQuads) {
                BakedQuad transformed = transformQuad(q);
                if (transformed != null) {
                    // Only add if it matches the target side (culling check)
                    // If targetSide is null, we add everything that isn't culled.
                    // If targetSide is not null, we only add quads that face that way.
                    if (targetSide == null || transformed.getDirection() == targetSide) {
                        result.add(transformed);
                    }
                }
            }
        }

        private BakedQuad transformQuad(BakedQuad quad) {
            int[] vertexData = quad.getVertices();
            int[] newData = new int[vertexData.length];
            System.arraycopy(vertexData, 0, newData, 0, vertexData.length);

            Direction quadDir = quad.getDirection();
            TextureAtlasSprite sprite = quad.getSprite();

            // Logic:
            // We want to "Mask" or "Crop" the full block model to the half we want.
            // Facing = NORTH -> Keep Z in [0, 0.5] (approx 0..8 pixels)
            // Facing = SOUTH -> Keep Z in [0.5, 1.0]
            // Facing = EAST  -> Keep X in [0.5, 1.0]
            // Facing = WEST  -> Keep X in [0, 0.5]

            float minX = 0, maxX = 1;
            float minZ = 0, maxZ = 1;

            if (facing == Direction.NORTH) maxZ = 0.5f;
            else if (facing == Direction.SOUTH) minZ = 0.5f;
            else if (facing == Direction.WEST) maxX = 0.5f;
            else if (facing == Direction.EAST) minX = 0.5f;

            boolean needsRemapping = false;

            // Step 1: Clamp Vertices to logical slab bounds
            for (int i = 0; i < 4; i++) {
                int offset = i * 8;
                float x = Float.intBitsToFloat(newData[offset]);
                float z = Float.intBitsToFloat(newData[offset + 2]);

                // Clamp Position
                float newX = Math.max(minX, Math.min(maxX, x));
                float newZ = Math.max(minZ, Math.min(maxZ, z));

                if (newX != x || newZ != z) {
                    needsRemapping = true;
                    newData[offset] = Float.floatToRawIntBits(newX);
                    newData[offset + 2] = Float.floatToRawIntBits(newZ);
                }
            }

            // Step 2: Recalculate UVs based on logical bounds (before centering)
            // This ensures we use the correct slice of the texture (e.g. 0-8 pixels for a North slab)
            if (needsRemapping) {
                recalculateUVs(newData, sprite);
            }

            // Step 3: Apply Inventory Centering
            // Move the geometry to the center of the block space for GUI display, but Keep UVs from Step 2.
            if (isInventory) {
                for (int i = 0; i < 4; i++) {
                    int offset = i * 8;
                    float x = Float.intBitsToFloat(newData[offset]);
                    float z = Float.intBitsToFloat(newData[offset + 2]);

                    if (facing == Direction.NORTH) z += 0.25f;
                    else if (facing == Direction.SOUTH) z -= 0.25f;
                    else if (facing == Direction.WEST) x += 0.25f;
                    else if (facing == Direction.EAST) x -= 0.25f;

                    newData[offset] = Float.floatToRawIntBits(x);
                    newData[offset + 2] = Float.floatToRawIntBits(z);
                }
            }

            // Check if quad is valid area
            if (isDegenerate(newData)) return null;

            return new BakedQuad(newData, quad.getTintIndex(), quadDir, sprite, quad.isShade());
        }

        private void recalculateUVs(int[] vertexData, TextureAtlasSprite sprite) {
            // Logic to deduce UV from XYZ for standard block mapping
            // Find normal
            // (We can assume the quad Direction is preserved or irrelevant for UV calc on axis-aligned faces)
            // But we need to handle the "New" faces created by clamping?
            // Actually, when we clamp a Face (e.g. South Face Z=1 to Z=0.5), it becomes the Back face of the slab.
            // It should use the texture of the original face, effectively cropped.
            // If we kept original UVs, we would see the Z=1 texture at Z=0.5.
            // We want to see the texture AT Z=0.5.
            // So we must interpolate UVs.

            // Get bounds of UVs in the quad to determine orientation?
            // Too complex.
            // Standard MC Block mapping:
            // U = x * 16, V = z * 16 (Up/Down)
            // U = x * 16, V = 16 - y * 16 (North/South)
            // ...

            // Re-projecting UVs based on position and sprite is the safest bet for "Cropping".
            // We assume 0..1 range for positions.

            // Determine major axis (Normal).
            // Since we modified vertices, we might have flattened a quad. The Normal is derived from vertices usually?
            // Or we use the original Direction.

            // If we clamped the South Face (Z=1) to Z=0.5. It is still a South Face?
            // No, it is now at Z=0.5. It is effectively inside.
            // Wait. If we have a North Vertical Slab (Z 0..0.5).
            // The North Face (Z=0) is untouched.
            // The South Face (Z=1) is clamped to Z=0.5. It becomes the Back of the slab.
            // We want this Back face to show the texture that WAS at Z=0.5?
            // Or do we want to show the specific "Side" texture?
            // If we use the original South Face texture (Side texture), and clamp it.
            // We are essentially saying "The back of this slab uses the Side texture". Correct.
            // But we need to crop it.

            // For each vertex:
            for (int i = 0; i < 4; i++) {
                int offset = i * 8;
                float x = Float.intBitsToFloat(vertexData[offset]);
                float y = Float.intBitsToFloat(vertexData[offset + 1]);
                float z = Float.intBitsToFloat(vertexData[offset + 2]);

                float u = 0, v = 0;

                // Heuristic mapping
                // This works for standard blocks but might fail for rotated logs if not careful.
                // However, Vertical Slabs are dynamically generated.

                // Facing logic for UVs:
                // We don't have the "Face" direction easily for arbitrary quads, but we can guess from Normal in vertex data?
                // Or pass it in.
                // Let's rely on simple axis alignment.

                // Standard Block Models:
                // Up/Down: U=X, V=Z.
                // North/South: U=X, V=1-Y.
                // East/West: U=Z, V=1-Y.

                // However, sprite coordinates are minU..maxU.
                // We need to interp.

                // U_interpolated = sprite.getU(u_01 * 16.0)

                // Identify Face Direction from Quad Data (Normal is stored in int at offset + 7)
                // But simply:
                // if normal.y != 0 -> Up/Down.
                // if normal.z != 0 -> North/South.
                // if normal.x != 0 -> East/West.

                int normal = vertexData[offset + 7];
                float nx = (float) ((byte) (normal & 0xFF)) / 127.0f;
                float ny = (float) ((byte) ((normal >> 8) & 0xFF)) / 127.0f;
                float nz = (float) ((byte) ((normal >> 16) & 0xFF)) / 127.0f;

                if (Math.abs(ny) > 0.5) { // Up/Down
                    u = x;
                    v = z;
                } else if (Math.abs(nz) > 0.5) { // North/South
                    u = x;
                    v = 1.0f - y;
                } else { // East/West
                    u = z;
                    v = 1.0f - y;
                }

                // Clamp 0..1
                u = Math.max(0, Math.min(1, u));
                v = Math.max(0, Math.min(1, v));

                vertexData[offset + 4] = Float.floatToRawIntBits(sprite.getU(u * 16.0f));
                vertexData[offset + 5] = Float.floatToRawIntBits(sprite.getV(v * 16.0f));
            }
        }

        private boolean isDegenerate(int[] vertexData) {
            // Check if area is zero.
            // Simple check: if all xs are same AND all ys are same...
            float x0 = Float.intBitsToFloat(vertexData[0]);
            float y0 = Float.intBitsToFloat(vertexData[1]);
            float z0 = Float.intBitsToFloat(vertexData[2]);

            boolean allX = true, allY = true, allZ = true;
            for (int i = 1; i < 4; i++) {
                int o = i * 8;
                if (Math.abs(Float.intBitsToFloat(vertexData[o]) - x0) > 1e-4) allX = false;
                if (Math.abs(Float.intBitsToFloat(vertexData[o + 1]) - y0) > 1e-4) allY = false;
                if (Math.abs(Float.intBitsToFloat(vertexData[o + 2]) - z0) > 1e-4) allZ = false;
            }
            // If any two dimensions are flattened, it's degenerate (line or point).
            // Ideally a Face must vary in 2 dimensions.
            return (allX && allY) || (allX && allZ) || (allY && allZ);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return original.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return original.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return original.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return original.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return original.getParticleIcon();
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }

        @Override
        public ItemTransforms getTransforms() {
            return original.getTransforms();
        }
    }
}
