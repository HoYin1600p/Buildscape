package com.kingodogo.buildscape.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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

        ModVerticalSlabs.VERTICAL_SLABS.forEach((slab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            if (verticalId == null) return;

            ResourceLocation parentId = slab.getRegistryName();
            if (parentId == null) return;

            // FOOLPROOF: Try exact model locations that Minecraft/Mods use for slabs
            BakedModel bottomModel = null;
            String[] singleVariants = {"type=bottom", "half=bottom", "normal", "inventory"};
            for (String v : singleVariants) {
                bottomModel = registry.get(new ModelResourceLocation(parentId, v));
                if (bottomModel != null) break;
            }

            BakedModel doubleModel = null;
            String[] doubleVariants = {"type=double", "half=upper", "double"};
            for (String v : doubleVariants) {
                doubleModel = registry.get(new ModelResourceLocation(parentId, v));
                if (doubleModel != null) break;
            }

            if (bottomModel == null) return;

            // 1. Single Slab Model (NORTH BASE)
            // The blockstate JSON converts this to other facings using 'y' rotation.
            BakedModel verticalSingle = new RotatedVerticalSlabModel(bottomModel, getBaseRotation());
            registry.put(new ModelResourceLocation(verticalId, "facing=north,type=single,waterlogged=false"), verticalSingle);
            registry.put(new ModelResourceLocation(verticalId, "facing=north,type=single,waterlogged=true"), verticalSingle);

            // 2. Double Slab Model (DUMMY/PLACEHOLDER OVERRIDE)
            // We register for all facings to ensure no state is left unmapped.
            BakedModel verticalDouble = doubleModel != null ? new RotatedVerticalSlabModel(doubleModel, Transformation.identity()) : new DoubleVerticalSlabModel(bottomModel);

            String[] facings = {"north", "east", "south", "west"};
            for (String f : facings) {
                registry.put(new ModelResourceLocation(verticalId, "facing=" + f + ",type=double,waterlogged=false"), verticalDouble);
                registry.put(new ModelResourceLocation(verticalId, "facing=" + f + ",type=double,waterlogged=true"), verticalDouble);
            }

            // 3. Item Model
            registry.put(new ModelResourceLocation(verticalId, "inventory"), verticalSingle);
        });
    }

    private static Transformation getBaseRotation() {
        // Rotates a bottom slab to be a North-facing vertical slab
        Quaternion rotX = new Quaternion(Vector3f.XP, -90, true);
        Vector3f trans = new Vector3f(0f, 0f, 0.5f);
        return new Transformation(trans, rotX, null, null);
    }

    private static class RotatedVerticalSlabModel implements BakedModel {
        protected final BakedModel original;
        protected final Transformation transform;
        private final Map<Direction, List<BakedQuad>> quadCache = new ConcurrentHashMap<>();
        private final Object unculledLock = new Object();
        private volatile List<BakedQuad> unculledCache = null;

        public RotatedVerticalSlabModel(BakedModel original, Transformation transform) {
            this.original = original;
            this.transform = transform;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable net.minecraft.world.level.block.state.BlockState state, @Nullable Direction side, Random rand) {
            if (side == null) {
                if (unculledCache == null) {
                    synchronized (unculledLock) {
                        if (unculledCache == null) {
                            List<BakedQuad> quads = new ArrayList<>();
                            collectAndTransform(null, quads, rand);
                            unculledCache = quads;
                        }
                    }
                }
                return unculledCache;
            }
            return quadCache.computeIfAbsent(side, s -> {
                List<BakedQuad> list = new ArrayList<>();
                collectAndTransform(s, list, rand);
                return list;
            });
        }

        protected void collectAndTransform(@Nullable Direction targetSide, List<BakedQuad> result, Random rand) {
            for (Direction origD : Direction.values()) {
                for (BakedQuad q : original.getQuads(null, origD, rand)) {
                    BakedQuad tr = transformQuad(q, transform);
                    if (tr.getDirection() == targetSide) result.add(tr);
                }
            }
            for (BakedQuad q : original.getQuads(null, null, rand)) {
                BakedQuad tr = transformQuad(q, transform);
                if (tr.getDirection() == targetSide) result.add(tr);
            }
        }

        protected BakedQuad transformQuad(BakedQuad quad, Transformation transform) {
            int[] vertexData = quad.getVertices();
            int[] newData = new int[vertexData.length];
            System.arraycopy(vertexData, 0, newData, 0, vertexData.length);
            for (int i = 0; i < 4; i++) {
                int offset = i * 8;
                Vector4f vec = new Vector4f(Float.intBitsToFloat(newData[offset]), Float.intBitsToFloat(newData[offset + 1]), Float.intBitsToFloat(newData[offset + 2]), 1.0F);
                vec.transform(transform.getMatrix());
                newData[offset] = Float.floatToRawIntBits(vec.x());
                newData[offset + 1] = Float.floatToRawIntBits(vec.y());
                newData[offset + 2] = Float.floatToRawIntBits(vec.z());

                int normal = newData[offset + 7];
                if (normal != 0) {
                    Vector4f nvec = new Vector4f((float) ((byte) (normal & 0xFF)) / 127f, (float) ((byte) ((normal >> 8) & 0xFF)) / 127f, (float) ((byte) ((normal >> 16) & 0xFF)) / 127f, 0.0F);
                    nvec.transform(transform.getMatrix());
                    newData[offset + 7] = ((int) (nvec.x() * 127f) & 0xFF) | (((int) (nvec.y() * 127f) & 0xFF) << 8) | (((int) (nvec.z() * 127f) & 0xFF) << 16) | (normal & 0xFF000000);
                }
            }
            Direction d = quad.getDirection();
            if (d != null) {
                Vector4f v = new Vector4f(d.getStepX(), d.getStepY(), d.getStepZ(), 0);
                v.transform(transform.getMatrix());
                d = Direction.getNearest(v.x(), v.y(), v.z());
            }
            return new BakedQuad(newData, quad.getTintIndex(), d, quad.getSprite(), quad.isShade());
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
            return original.getOverrides();
        }

        @Override
        public ItemTransforms getTransforms() {
            return original.getTransforms();
        }
    }

    private static class DoubleVerticalSlabModel extends RotatedVerticalSlabModel {
        private final BakedModel part2; // South half
        private final Object doubleUnculledLock = new Object();
        private volatile List<BakedQuad> doubleUnculledCache = null;

        public DoubleVerticalSlabModel(BakedModel bottomSlab) {
            super(bottomSlab, getBaseRotation()); // Part 1 (North)
            Transformation southTransform = new Transformation(new Vector3f(1, 0, 1), new Quaternion(Vector3f.YP, 180, true), null, null)
                    .compose(getBaseRotation());
            this.part2 = new RotatedVerticalSlabModel(bottomSlab, southTransform);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable net.minecraft.world.level.block.state.BlockState state, @Nullable Direction side, Random rand) {
            if (side == null) {
                if (doubleUnculledCache == null) {
                    synchronized (doubleUnculledLock) {
                        if (doubleUnculledCache == null) {
                            List<BakedQuad> quads = new ArrayList<>();
                            quads.addAll(super.getQuads(state, null, rand));
                            quads.addAll(part2.getQuads(state, null, rand));
                            doubleUnculledCache = quads;
                        }
                    }
                }
                return doubleUnculledCache;
            }
            return doubleCache.computeIfAbsent(side, s -> {
                List<BakedQuad> quads = new ArrayList<>();
                quads.addAll(super.getQuads(state, s, rand));
                quads.addAll(part2.getQuads(state, s, rand));
                return quads;
            });
        }
    }
}
