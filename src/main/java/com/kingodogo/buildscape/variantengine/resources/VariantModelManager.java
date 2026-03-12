package com.kingodogo.buildscape.variantengine.resources;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// @Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VariantModelManager {

    private static final Gson GSON = new Gson();
    private static final Random RANDOM = new Random(42);
    private static final String SLAB_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[16,16,8],\"faces\":{" +
            "\"north\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[0,0,16,16],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[0,0,16,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,8,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
            "]";
    private static final String SLAB_ITEM_ELEMENTS = "[" +
            "{\"from\":[4,0,0],\"to\":[12,16,16],\"faces\":{" +
            "\"north\":{\"uv\":[4,0,12,16],\"texture\":\"#side\"}," +
            "\"south\":{\"uv\":[4,0,12,16],\"texture\":\"#side\",\"cullface\":\"south\"}," +
            "\"west\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[4,0,12,16],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[4,0,12,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
            "]";
    private static final String DOUBLE_SLAB_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[16,16,16],\"faces\":{" +
            "\"north\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"south\"}," +
            "\"west\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[0,0,16,16],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,0,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
            "]";
    private static final String STAIRS_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[8,16,8],\"faces\":{" +
            "\"north\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"up\":{\"uv\":[0,0,8,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,8,8,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}}," +
            "{\"from\":[8,0,0],\"to\":[16,16,8],\"faces\":{" +
            "\"north\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
            "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[8,0,16,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[8,8,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}}," +
            "{\"from\":[0,0,8],\"to\":[8,16,16],\"faces\":{" +
            "\"south\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"south\"}," +
            "\"west\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
            "\"up\":{\"uv\":[0,8,8,16],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,0,8,8],\"texture\":\"#bottom\",\"cullface\":\"down\"}}}" +
            "]";
    private static final String STAIRS_ITEM_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[8,16,16],\"faces\":{" +
            "\"north\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
            "\"south\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,0,16,16],\"texture\":\"#side\"}," +
            "\"east\":{\"uv\":[0,0,16,16],\"texture\":\"#side\"}," +
            "\"up\":{\"uv\":[0,0,8,16],\"texture\":\"#top\"}," +
            "\"down\":{\"uv\":[0,0,8,16],\"texture\":\"#bottom\"}}}," +
            "{\"from\":[8,0,8],\"to\":[16,16,16],\"faces\":{" +
            "\"north\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
            "\"south\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
            "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
            "\"up\":{\"uv\":[8,8,16,16],\"texture\":\"#top\"}," +
            "\"down\":{\"uv\":[8,8,16,16],\"texture\":\"#bottom\"}}}" +
            "]";
    private static final String QUARTER_PIECE_BOTTOM_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[16,8,8],\"faces\":{" +
            "\"north\":{\"uv\":[0,8,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[0,8,16,16],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,8,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[8,8,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[0,0,16,8],\"texture\":\"#top\"}," +
            "\"down\":{\"uv\":[0,8,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
            "]";
    private static final String QUARTER_PIECE_TOP_ELEMENTS = "[" +
            "{\"from\":[0,8,0],\"to\":[16,16,8],\"faces\":{" +
            "\"north\":{\"uv\":[0,0,16,8],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[0,0,16,8],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,0,8,8],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[8,0,16,8],\"texture\":\"#side\",\"cullface\":\"east\"}," +
            "\"up\":{\"uv\":[0,0,16,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,8,16,16],\"texture\":\"#bottom\"}}} " +
            "]";
    private static final String VERTICAL_QUARTER_PIECE_ELEMENTS = "[" +
            "{\"from\":[0,0,0],\"to\":[8,16,8],\"faces\":{" +
            "\"north\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
            "\"south\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
            "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
            "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
            "\"up\":{\"uv\":[0,0,8,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
            "\"down\":{\"uv\":[0,8,8,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
            "]";

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();
        ModelBakery bakery = event.getModelLoader();

        for (Block baseBlock : BlockBiMaps.BASE_BLOCKS) {
            for (BlockShape shape : BlockShape.values()) {
                Block variant = BlockBiMaps.getBlockOf(shape, baseBlock);
                if (variant != null) {
                    bakeVariant(baseBlock, variant, shape, registry, bakery);
                }
            }
        }
    }

    private static void bakeVariant(Block parent, Block variant, BlockShape shape, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        try {
            ResourceLocation verticalId = variant.getRegistryName();
            if (verticalId == null || parent.getRegistryName() == null) return;

            TextureAtlasSprite parentParticle = getParentParticleTexture(parent, registry);

            if (shape == BlockShape.VERTICAL_SLAB) {
                bakeVerticalSlabModels(verticalId, parent, parentParticle, registry, bakery);
            } else if (shape == BlockShape.VERTICAL_STAIRS) {
                bakeVerticalStairsModels(verticalId, parent, parentParticle, registry, bakery);
            } else if (shape == BlockShape.QUARTER_PIECE) {
                bakeQuarterPieceModels(verticalId, parent, parentParticle, registry, bakery);
            } else if (shape == BlockShape.VERTICAL_QUARTER_PIECE) {
                bakeVerticalQuarterPieceModels(verticalId, parent, parentParticle, registry, bakery);
            }
        } catch (Exception e) {
        }
    }

    private static void bakeVerticalSlabModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        String[] facings = {"north", "south", "east", "west"};
        String[] waterloggedStates = {"false", "true"};
        BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y180, BlockModelRotation.X0_Y90, BlockModelRotation.X0_Y270};

        for (int i = 0; i < facings.length; i++) {
            for (String waterlogged : waterloggedStates) {
                bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",type=bottom,waterlogged=" + waterlogged),
                        parent, parentParticle, registry, bakery, SLAB_ELEMENTS, rotations[i], false);
                bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",type=top,waterlogged=" + waterlogged),
                        parent, parentParticle, registry, bakery, SLAB_ELEMENTS, rotations[i], false);
                bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",type=double,waterlogged=" + waterlogged),
                        parent, parentParticle, registry, bakery, DOUBLE_SLAB_ELEMENTS, rotations[i], false);
            }
        }
        bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"), parent, parentParticle, registry, bakery, SLAB_ITEM_ELEMENTS, BlockModelRotation.X0_Y0, true);
    }

    private static void bakeVerticalStairsModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        String[] facings = {"north", "south", "east", "west"};
        String[] waterloggedStates = {"false", "true"};
        BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y180, BlockModelRotation.X0_Y270, BlockModelRotation.X0_Y90};

        for (int i = 0; i < facings.length; i++) {
            for (String waterlogged : waterloggedStates) {
                bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",waterlogged=" + waterlogged),
                        parent, parentParticle, registry, bakery, STAIRS_ELEMENTS, rotations[i], false);
            }
        }
        bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"), parent, parentParticle, registry, bakery, STAIRS_ITEM_ELEMENTS, BlockModelRotation.X0_Y0, true);
    }

    private static void bakeQuarterPieceModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        String[] facings = {"north", "south", "east", "west"};
        String[] waterloggedStates = {"false", "true"};
        String[] halves = {"bottom", "top"};
        BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y180, BlockModelRotation.X0_Y270, BlockModelRotation.X0_Y90};

        for (int i = 0; i < facings.length; i++) {
            for (String waterlogged : waterloggedStates) {
                for (String half : halves) {
                    bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",half=" + half + ",waterlogged=" + waterlogged),
                            parent, parentParticle, registry, bakery, half.equals("bottom") ? QUARTER_PIECE_BOTTOM_ELEMENTS : QUARTER_PIECE_TOP_ELEMENTS, rotations[i], false);
                }
            }
        }
        bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"), parent, parentParticle, registry, bakery, QUARTER_PIECE_BOTTOM_ELEMENTS, BlockModelRotation.X0_Y0, true);
    }

    private static void bakeVerticalQuarterPieceModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        String[] facings = {"north_west", "north_east", "south_east", "south_west"};
        String[] waterloggedStates = {"false", "true"};
        BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y90, BlockModelRotation.X0_Y180, BlockModelRotation.X0_Y270};

        for (int i = 0; i < facings.length; i++) {
            for (String waterlogged : waterloggedStates) {
                bakeVariantModel(new ModelResourceLocation(verticalId, "facing=" + facings[i] + ",waterlogged=" + waterlogged),
                        parent, parentParticle, registry, bakery, VERTICAL_QUARTER_PIECE_ELEMENTS, rotations[i], false);
            }
        }
        bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"), parent, parentParticle, registry, bakery, VERTICAL_QUARTER_PIECE_ELEMENTS, BlockModelRotation.X0_Y0, true);
    }

    private static void bakeVariantModel(ModelResourceLocation location, Block parent, TextureAtlasSprite parentParticle,
                                         Map<ResourceLocation, BakedModel> registry, ModelBakery bakery,
                                         String elementsJson, BlockModelRotation rotation, boolean isItem) {
        try {
            Map<String, Material> textures = extractTexturesFromParent(parent, bakery, registry);

            JsonObject modelObj = new JsonObject();
            modelObj.add("elements", GSON.fromJson(elementsJson, JsonArray.class));

            if (isItem) {
                addDisplayTransforms(modelObj);
            }

            BlockModel model = BlockModel.fromString(modelObj.toString());

            // Apply textures
            textures.forEach((key, material) -> model.textureMap.put(key, com.mojang.datafixers.util.Either.left(material)));

            // Ensure particle texture
            if (!model.textureMap.containsKey("particle") && textures.containsKey("particle")) {
                model.textureMap.put("particle", com.mojang.datafixers.util.Either.left(textures.get("particle")));
            }

            BakedModel baked = model.bake(bakery, model, bakery.getSpriteMap()::getSprite, rotation, location, false);
            if (baked != null) {
                TextureAtlasSprite particleSprite = parentParticle;
                if (particleSprite == null && textures.containsKey("particle")) {
                    particleSprite = bakery.getSpriteMap().getSprite(textures.get("particle"));
                }

                if (particleSprite != null) {
                    baked = new ParticleDelegatingBakedModel(baked, particleSprite);
                }

                registry.put(location, baked);
            }
        } catch (Exception e) {
        }
    }

    private static Map<String, Material> extractTexturesFromParent(Block parent, ModelBakery bakery, Map<ResourceLocation, BakedModel> registry) {
        Map<String, Material> textures = new HashMap<>();
        ResourceLocation parentId = parent.getRegistryName();

        if (parentId == null) return getDefaultTextures();

        // Strategy 1: Unbaked model hierarchy
        BlockModel parentModel = getBlockModelFromParent(parent, bakery);
        if (parentModel != null) {
            resolveFullHierarchy(parentModel, bakery);

            Material side = findMaterialWithFallback(parentModel, "side", "all", "texture", "particle");
            Material top = findMaterialWithFallback(parentModel, "top", "all", "texture", "particle");
            Material bottom = findMaterialWithFallback(parentModel, "bottom", "all", "texture", "particle");
            Material particle = findMaterialWithFallback(parentModel, "particle", "all", "texture", "side");

            if (!side.texture().getPath().equals("missingno")) {
                textures.put("side", side);
                textures.put("top", top);
                textures.put("bottom", bottom);
                textures.put("particle", particle);
                return textures;
            }
        }

        // Strategy 2: Base block quads
        String basePath = parentId.getPath().replace("_slab", "").replace("_stairs", "").replace("_stair", "");

        BakedModel baseModel = findBakedModelForBlock(new ResourceLocation(parentId.getNamespace(), basePath), registry);

        if (baseModel != null) {
            TextureAtlasSprite sideSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.NORTH);
            if (sideSprite == null) sideSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.EAST);
            TextureAtlasSprite topSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.UP);
            TextureAtlasSprite bottomSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.DOWN);
            TextureAtlasSprite particle = baseModel.getParticleIcon();

            TextureAtlasSprite resolvedSide = sideSprite != null ? sideSprite : particle;
            TextureAtlasSprite resolvedTop = topSprite != null ? topSprite : resolvedSide;
            TextureAtlasSprite resolvedBottom = bottomSprite != null ? bottomSprite : resolvedSide;

            if (resolvedSide != null) {
                textures.put("side", spriteToMaterial(resolvedSide));
                textures.put("top", spriteToMaterial(resolvedTop));
                textures.put("bottom", spriteToMaterial(resolvedBottom));
                textures.put("particle", spriteToMaterial(particle != null ? particle : resolvedSide));
                return textures;
            }
        }

        // Strategy 3: Inference fallback
        Material fallback = createMaterial(parentId.getNamespace(), "block/" + basePath);
        textures.put("side", fallback);
        textures.put("top", fallback);
        textures.put("bottom", fallback);
        textures.put("particle", fallback);
        return textures;
    }

    private static BlockModel getBlockModelFromParent(Block block, ModelBakery bakery) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;

        try {
            ResourceLocation blockPath = new ResourceLocation(id.getNamespace(), "block/" + id.getPath());
            UnbakedModel unbaked = bakery.getModel(blockPath);
            if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;

            if (block instanceof SlabBlock) {
                unbaked = bakery.getModel(new ModelResourceLocation(id, "type=bottom"));
                if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;
            }

            if (block instanceof StairBlock) {
                unbaked = bakery.getModel(new ModelResourceLocation(id, "facing=north,half=bottom,shape=straight"));
                if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;
            }

            unbaked = bakery.getModel(new ModelResourceLocation(id, "inventory"));
            if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;
        } catch (Exception ignored) {
        }

        return null;
    }

    private static void resolveFullHierarchy(BlockModel model, ModelBakery bakery) {
        try {
            java.lang.reflect.Method resolve = BlockModel.class.getDeclaredMethod("resolveParents", java.util.function.Function.class);
            resolve.setAccessible(true);
            resolve.invoke(model, (java.util.function.Function<ResourceLocation, UnbakedModel>) bakery::getModel);
        } catch (Exception e) {
        }
    }

    private static Material findMaterialWithFallback(BlockModel model, String... keys) {
        for (String key : keys) {
            Material mat = findMaterial(model, key);
            if (mat != null) return mat;
        }
        return createMaterial("minecraft", "block/oak_planks");
    }

    private static Material findMaterial(BlockModel model, String name) {
        BlockModel current = model;
        while (current != null) {
            com.mojang.datafixers.util.Either<Material, String> either = current.textureMap.get(name);
            if (either != null) {
                return either.map(mat -> mat, ref -> {
                    if (ref.startsWith("#")) {
                        String target = ref.substring(1);
                        if (target.equals(name)) return null;
                        return findMaterial(model, target);
                    } else {
                        ResourceLocation loc = ref.contains(":") ? new ResourceLocation(ref) : new ResourceLocation("minecraft", ref);
                        return createMaterial(loc.getNamespace(), loc.getPath());
                    }
                });
            }
            current = current.parent;
        }
        return null;
    }

    private static BakedModel findBakedModelForBlock(ResourceLocation blockId, Map<ResourceLocation, BakedModel> registry) {
        for (Map.Entry<ResourceLocation, BakedModel> entry : registry.entrySet()) {
            if (entry.getKey() instanceof ModelResourceLocation mrl) {
                if (mrl.getNamespace().equals(blockId.getNamespace()) && mrl.getPath().equals(blockId.getPath()) && !mrl.getVariant().equals("inventory")) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private static TextureAtlasSprite getSpriteForDirection(BakedModel model, net.minecraft.core.Direction face) {
        try {
            List<net.minecraft.client.renderer.block.model.BakedQuad> quads = model.getQuads(null, face, RANDOM);
            if (quads != null && !quads.isEmpty()) return quads.get(0).getSprite();
        } catch (Exception ignored) {
        }
        return null;
    }

    // Elements JSON constants

    private static Material spriteToMaterial(TextureAtlasSprite sprite) {
        if (sprite == null) return createMaterial("minecraft", "block/oak_planks");
        return new Material(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, sprite.getName());
    }

    private static Material createMaterial(String namespace, String path) {
        return new Material(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS, new ResourceLocation(namespace, path));
    }

    private static Map<String, Material> getDefaultTextures() {
        Map<String, Material> textures = new HashMap<>();
        Material fallback = createMaterial("minecraft", "block/oak_planks");
        textures.put("side", fallback);
        textures.put("top", fallback);
        textures.put("bottom", fallback);
        textures.put("particle", fallback);
        return textures;
    }

    private static boolean isDummy(BlockModel model) {
        if (!model.getElements().isEmpty() || !model.textureMap.isEmpty()) return false;
        try {
            java.lang.reflect.Field f = BlockModel.class.getDeclaredField("parentLocation");
            f.setAccessible(true);
            return f.get(model) == null;
        } catch (Exception e) {
            return true;
        }
    }

    private static TextureAtlasSprite getParentParticleTexture(Block parent, Map<ResourceLocation, BakedModel> registry) {
        ResourceLocation parentId = parent.getRegistryName();
        if (parentId == null) return null;

        for (Map.Entry<ResourceLocation, BakedModel> entry : registry.entrySet()) {
            if (entry.getKey() instanceof ModelResourceLocation mrl) {
                if (mrl.getNamespace().equals(parentId.getNamespace()) && mrl.getPath().equals(parentId.getPath())) {
                    BakedModel parentModel = entry.getValue();
                    if (parentModel != null && parentModel.getParticleIcon() != null)
                        return parentModel.getParticleIcon();
                }
            }
        }
        return null;
    }

    private static void addDisplayTransforms(JsonObject modelObj) {
        JsonObject display = new JsonObject();
        display.add("gui", createTransform(30, 225, 0, 0, 0, 0, 0.625, 0.625, 0.625));
        display.add("ground", createTransform(0, 0, 0, 0, 3, 0, 0.25, 0.25, 0.25));
        display.add("fixed", createTransform(0, 0, 0, 0, 0, 0, 0.5, 0.5, 0.5));
        display.add("thirdperson_righthand", createTransform(75, 45, 0, 0, 2.5, 0, 0.375, 0.375, 0.375));
        display.add("firstperson_righthand", createTransform(0, 45, 0, 0, 0, 0, 0.4, 0.4, 0.4));
        modelObj.add("display", display);
    }

    private static JsonObject createTransform(int rx, int ry, int rz, double tx, double ty, double tz, double sx, double sy, double sz) {
        JsonObject obj = new JsonObject();
        JsonArray r = new JsonArray();
        r.add(rx);
        r.add(ry);
        r.add(rz);
        obj.add("rotation", r);
        JsonArray t = new JsonArray();
        t.add(tx);
        t.add(ty);
        t.add(tz);
        obj.add("translation", t);
        JsonArray s = new JsonArray();
        s.add(sx);
        s.add(sy);
        s.add(sz);
        obj.add("scale", s);
        return obj;
    }

    private static class ParticleDelegatingBakedModel extends net.minecraftforge.client.model.BakedModelWrapper<BakedModel> {
        private final TextureAtlasSprite particleSprite;

        public ParticleDelegatingBakedModel(BakedModel originalModel, TextureAtlasSprite particleSprite) {
            super(originalModel);
            this.particleSprite = particleSprite;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.particleSprite;
        }
    }
}
