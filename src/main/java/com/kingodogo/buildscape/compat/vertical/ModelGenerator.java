package com.kingodogo.buildscape.compat.vertical;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.resources.model.UnbakedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelGenerator {

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();
        ModelBakery bakery = event.getModelLoader();

        BuildScape.LOGGER.info("ModelGenerator: Starting model baking for vertical variants");
        BuildScape.LOGGER.info("Found " + VerticalRegistry.VERTICAL_SLABS.size() + " slabs and " + VerticalRegistry.VERTICAL_STAIRS.size() + " stairs to bake");

        // Replace models for vertical slabs
        VerticalRegistry.VERTICAL_SLABS.forEach((parent, vertical) -> {
            ResourceLocation verticalId = vertical.getRegistryName();
            if (verticalId != null) {
                BuildScape.LOGGER.info("Baking models for vertical slab: " + verticalId);
                // Get particle texture from parent block's existing baked model
                TextureAtlasSprite parentParticle = getParentParticleTexture(parent, registry);
                bakeVerticalSlabModels(verticalId, parent, parentParticle, registry, bakery);
            }
        });

        // Replace models for vertical stairs
        VerticalRegistry.VERTICAL_STAIRS.forEach((parent, vertical) -> {
            ResourceLocation verticalId = vertical.getRegistryName();
            if (verticalId != null) {
                BuildScape.LOGGER.info("Baking models for vertical stairs: " + verticalId);
                // Get particle texture from parent block's existing baked model
                TextureAtlasSprite parentParticle = getParentParticleTexture(parent, registry);
                bakeVerticalStairsModels(verticalId, parent, parentParticle, registry, bakery);
            }
        });

        BuildScape.LOGGER.info("ModelGenerator: Finished model baking");
    }

    private static void bakeVerticalSlabModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        try {
            // Get the parent slab's model to extract textures
            ResourceLocation parentId = parent.getRegistryName();
            if (parentId == null) return;

            // Bake models for all blockstate variants with all combinations
            String[] facings = {"north", "south", "east", "west"};
            String[] waterloggedStates = {"false", "true"};
            BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y180,
                                               BlockModelRotation.X0_Y90, BlockModelRotation.X0_Y270};

            // Bake bottom and top type variants (both use same model for vertical slabs)
            for (int i = 0; i < facings.length; i++) {
                for (String waterlogged : waterloggedStates) {
                    String variantBottom = "facing=" + facings[i] + ",type=bottom,waterlogged=" + waterlogged;
                    bakeVariantModel(new ModelResourceLocation(verticalId, variantBottom),
                        parent, parentParticle, registry, bakery, SLAB_ELEMENTS, rotations[i]);

                    String variantTop = "facing=" + facings[i] + ",type=top,waterlogged=" + waterlogged;
                    bakeVariantModel(new ModelResourceLocation(verticalId, variantTop),
                        parent, parentParticle, registry, bakery, SLAB_ELEMENTS, rotations[i]);
                }
            }

            // Bake double slab variants with facing
            for (int i = 0; i < facings.length; i++) {
                for (String waterlogged : waterloggedStates) {
                    String variant = "facing=" + facings[i] + ",type=double,waterlogged=" + waterlogged;
                    bakeVariantModel(new ModelResourceLocation(verticalId, variant),
                        parent, parentParticle, registry, bakery, DOUBLE_SLAB_ELEMENTS, rotations[i]);
                }
            }

            // Also bake the _double model file that the blockstate references
            ResourceLocation doubleModelLocation = new ResourceLocation(verticalId.getNamespace(), "block/" + verticalId.getPath() + "_double");
            bakeStandaloneModel(doubleModelLocation, parent, parentParticle, registry, bakery, DOUBLE_SLAB_ELEMENTS);

            // Item model (uses inventory variant) - use special item elements that face forward
            bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"),
                parent, parentParticle, registry, bakery, SLAB_ITEM_ELEMENTS, BlockModelRotation.X0_Y0);

            BuildScape.LOGGER.debug("Successfully baked vertical slab models for: " + verticalId);
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to bake vertical slab models for " + verticalId, e);
        }
    }

    private static void bakeVerticalStairsModels(ResourceLocation verticalId, Block parent, TextureAtlasSprite parentParticle, Map<ResourceLocation, BakedModel> registry, ModelBakery bakery) {
        try {
            // Get the parent stairs's model to extract textures
            ResourceLocation parentId = parent.getRegistryName();
            if (parentId == null) return;

            // Bake models for all blockstate variants with all combinations
            String[] facings = {"north", "south", "east", "west"};
            String[] waterloggedStates = {"false", "true"};
            BlockModelRotation[] rotations = {BlockModelRotation.X0_Y0, BlockModelRotation.X0_Y180,
                                               BlockModelRotation.X0_Y90, BlockModelRotation.X0_Y270};

            // Bake all variants
            for (int i = 0; i < facings.length; i++) {
                for (String waterlogged : waterloggedStates) {
                    String variant = "facing=" + facings[i] + ",waterlogged=" + waterlogged;
                    bakeVariantModel(new ModelResourceLocation(verticalId, variant),
                        parent, parentParticle, registry, bakery, STAIRS_ELEMENTS, rotations[i]);
                }
            }

            // Item model (uses inventory variant) - use special item elements that face forward
            bakeVariantModel(new ModelResourceLocation(verticalId, "inventory"),
                parent, parentParticle, registry, bakery, STAIRS_ITEM_ELEMENTS, BlockModelRotation.X0_Y0);

            BuildScape.LOGGER.debug("Successfully baked vertical stairs models for: " + verticalId);
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to bake vertical stairs models for " + verticalId, e);
        }
    }

    private static void bakeStandaloneModel(ResourceLocation location, Block parent, TextureAtlasSprite parentParticle,
                                            Map<ResourceLocation, BakedModel> registry,
                                            ModelBakery bakery, String elementsJson) {
        try {
            // Extract textures from parent block
            Map<String, net.minecraft.client.resources.model.Material> textures = extractTexturesFromParent(parent, bakery, registry);

            // Create model with elements
            JsonObject modelObj = new JsonObject();
            modelObj.add("elements", GSON.fromJson(elementsJson, JsonArray.class));

            BlockModel model = BlockModel.fromString(modelObj.toString());

            // Apply textures including particle
            textures.forEach((key, material) -> {
                model.textureMap.put(key, com.mojang.datafixers.util.Either.left(material));
            });

            // Ensure particle texture is explicitly set if not already
            if (!model.textureMap.containsKey("particle") && textures.containsKey("particle")) {
                model.textureMap.put("particle", com.mojang.datafixers.util.Either.left(textures.get("particle")));
            }

            // Bake the model
            BakedModel baked = model.bake(bakery, model, bakery.getSpriteMap()::getSprite, BlockModelRotation.X0_Y0, location, false);
            if (baked != null) {
                // Use passed parent particle if we have it, otherwise extract from material
                TextureAtlasSprite particleSprite = parentParticle;
                if (particleSprite == null && textures.containsKey("particle")) {
                    net.minecraft.client.resources.model.Material particleMaterial = textures.get("particle");
                    particleSprite = bakery.getSpriteMap().getSprite(particleMaterial);
                }

                // Wrap the model to ensure particle texture is properly delegated
                if (particleSprite != null) {
                    baked = new ParticleDelegatingBakedModel(baked, particleSprite);
                    BuildScape.LOGGER.info("  Wrapped standalone model with particle texture: " + particleSprite.getName());
                }

                registry.put(location, baked);
                BuildScape.LOGGER.debug("Baked standalone model: " + location);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to bake standalone model: " + location, e);
        }
    }

    private static void bakeVariantModel(ModelResourceLocation location, Block parent, TextureAtlasSprite parentParticle,
                                         Map<ResourceLocation, BakedModel> registry,
                                         ModelBakery bakery, String elementsJson,
                                         BlockModelRotation rotation) {
        try {
            // Extract textures from parent block
            Map<String, net.minecraft.client.resources.model.Material> textures = extractTexturesFromParent(parent, bakery, registry);

            // Create model with elements
            JsonObject modelObj = new JsonObject();
            modelObj.add("elements", GSON.fromJson(elementsJson, JsonArray.class));

            // For item models, add proper display transforms
            if (location.getVariant().equals("inventory")) {
                JsonObject display = new JsonObject();

                JsonObject gui = new JsonObject();
                JsonArray guiRotation = new JsonArray();
                guiRotation.add(30);
                guiRotation.add(225);
                guiRotation.add(0);
                gui.add("rotation", guiRotation);
                JsonArray guiTranslation = new JsonArray();
                guiTranslation.add(0);
                guiTranslation.add(0);
                guiTranslation.add(0);
                gui.add("translation", guiTranslation);
                JsonArray guiScale = new JsonArray();
                guiScale.add(0.625);
                guiScale.add(0.625);
                guiScale.add(0.625);
                gui.add("scale", guiScale);
                display.add("gui", gui);

                JsonObject ground = new JsonObject();
                JsonArray groundTranslation = new JsonArray();
                groundTranslation.add(0);
                groundTranslation.add(3);
                groundTranslation.add(0);
                ground.add("translation", groundTranslation);
                JsonArray groundScale = new JsonArray();
                groundScale.add(0.25);
                groundScale.add(0.25);
                groundScale.add(0.25);
                ground.add("scale", groundScale);
                display.add("ground", ground);

                JsonObject fixed = new JsonObject();
                JsonArray fixedScale = new JsonArray();
                fixedScale.add(0.5);
                fixedScale.add(0.5);
                fixedScale.add(0.5);
                fixed.add("scale", fixedScale);
                display.add("fixed", fixed);

                JsonObject thirdperson_righthand = new JsonObject();
                JsonArray tprRotation = new JsonArray();
                tprRotation.add(75);
                tprRotation.add(45);
                tprRotation.add(0);
                thirdperson_righthand.add("rotation", tprRotation);
                JsonArray tprTranslation = new JsonArray();
                tprTranslation.add(0);
                tprTranslation.add(2.5);
                tprTranslation.add(0);
                thirdperson_righthand.add("translation", tprTranslation);
                JsonArray tprScale = new JsonArray();
                tprScale.add(0.375);
                tprScale.add(0.375);
                tprScale.add(0.375);
                thirdperson_righthand.add("scale", tprScale);
                display.add("thirdperson_righthand", thirdperson_righthand);

                JsonObject firstperson_righthand = new JsonObject();
                JsonArray fprRotation = new JsonArray();
                fprRotation.add(0);
                fprRotation.add(45);
                fprRotation.add(0);
                firstperson_righthand.add("rotation", fprRotation);
                JsonArray fprScale = new JsonArray();
                fprScale.add(0.4);
                fprScale.add(0.4);
                fprScale.add(0.4);
                firstperson_righthand.add("scale", fprScale);
                display.add("firstperson_righthand", firstperson_righthand);

                modelObj.add("display", display);
            }

            BlockModel model = BlockModel.fromString(modelObj.toString());

            // Apply textures including particle
            textures.forEach((key, material) -> {
                model.textureMap.put(key, com.mojang.datafixers.util.Either.left(material));
            });

            // Ensure particle texture is explicitly set if not already
            if (!model.textureMap.containsKey("particle") && textures.containsKey("particle")) {
                model.textureMap.put("particle", com.mojang.datafixers.util.Either.left(textures.get("particle")));
            }

            // Debug logging for particle texture
            if (BuildScape.LOGGER.isDebugEnabled()) {
                BuildScape.LOGGER.debug("Baking model for " + location + " with particle texture: " +
                    (textures.containsKey("particle") ? textures.get("particle").texture() : "NONE"));
            }

            // Bake the model
            BakedModel baked = model.bake(bakery, model, bakery.getSpriteMap()::getSprite, rotation, location, false);
            if (baked != null) {
                // Use passed parent particle if we have it, otherwise extract from material
                TextureAtlasSprite particleSprite = parentParticle;
                if (particleSprite == null && textures.containsKey("particle")) {
                    net.minecraft.client.resources.model.Material particleMaterial = textures.get("particle");
                    particleSprite = bakery.getSpriteMap().getSprite(particleMaterial);
                }

                // Wrap the model to ensure particle texture is properly delegated
                if (particleSprite != null) {
                    baked = new ParticleDelegatingBakedModel(baked, particleSprite);
                    BuildScape.LOGGER.info("  Wrapped variant model with particle texture: " + particleSprite.getName());
                }

                registry.put(location, baked);
                BuildScape.LOGGER.info("Successfully registered model at location: " + location);

                // Verify particle texture is set on baked model
                if (baked.getParticleIcon() != null) {
                    BuildScape.LOGGER.info("  Particle texture: " + baked.getParticleIcon().getName());
                } else {
                    BuildScape.LOGGER.warn("  WARNING: No particle texture for " + location);
                }
            } else {
                BuildScape.LOGGER.error("  FAILED to bake model for " + location);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.error("Failed to bake variant model: " + location, e);
        }
    }

    private static Map<String, net.minecraft.client.resources.model.Material> extractTexturesFromParent(
            Block parent, ModelBakery bakery, Map<ResourceLocation, BakedModel> registry) {
        Map<String, net.minecraft.client.resources.model.Material> textures = new HashMap<>();
        ResourceLocation parentId = parent.getRegistryName();

        if (parentId == null) {
            BuildScape.LOGGER.error("Parent block has null registry name!");
            return getDefaultTextures();
        }

        BuildScape.LOGGER.info("Extracting textures for parent: " + parentId);

        // ===================================================================
        // Strategy 1: Parse the unbaked model hierarchy.
        // Works reliably for all Buildscape blocks and well-formed modded blocks.
        // ===================================================================
        BlockModel parentModel = getBlockModelFromParent(parent, bakery);
        if (parentModel != null) {
            resolveFullHierarchy(parentModel, bakery);

            net.minecraft.client.resources.model.Material sideTexture     = findMaterialWithFallback(parentModel, "side", "all", "texture", "particle");
            net.minecraft.client.resources.model.Material topTexture      = findMaterialWithFallback(parentModel, "top",  "all", "texture", "particle");
            net.minecraft.client.resources.model.Material bottomTexture   = findMaterialWithFallback(parentModel, "bottom", "all", "texture", "particle");
            net.minecraft.client.resources.model.Material particleTexture = findMaterialWithFallback(parentModel, "particle", "all", "texture", "side");

            // If the model resolution hit the missing model, it will return missingno textures.
            // In that case, we should abort Strategy 1 and fallback to Strategy 2 (Baked Quads).
            if (!sideTexture.texture().getPath().equals("missingno")) {
                textures.put("side",     sideTexture);
                textures.put("top",      topTexture);
                textures.put("bottom",   bottomTexture);
                textures.put("particle", particleTexture);

                BuildScape.LOGGER.info("  [Unbaked model] side=" + sideTexture.texture() + " top=" + topTexture.texture());
                return textures;
            } else {
                BuildScape.LOGGER.warn("  [Unbaked model] resulted in missingno texture, falling back to Strategy 2.");
            }
        }

        // ===================================================================
        // Strategy 2: Read quads from the BASE CUBE BLOCK (strip _slab/_stairs).
        // Cube blocks have clean face geometry: UP=top, NORTH=side, DOWN=bottom.
        // This fixes vanilla blocks like waxed_cut_copper_slab where the unbaked
        // model lookup fails but minecraft:waxed_cut_copper cube is in the registry.
        // ===================================================================
        String basePath = parentId.getPath()
                .replace("_slab", "").replace("_stairs", "").replace("_stair", "");
        String basePathAlt = basePath;
        if (isWoodType(basePath)) basePathAlt = basePath + "_planks";
        if (parentId.getPath().contains("brick") && !basePath.endsWith("s")) basePathAlt = basePath + "s";

        BakedModel baseModel = findBakedModelForBlock(new ResourceLocation(parentId.getNamespace(), basePath), registry);
        if (baseModel == null && !basePathAlt.equals(basePath)) {
            baseModel = findBakedModelForBlock(new ResourceLocation(parentId.getNamespace(), basePathAlt), registry);
        }
        if (baseModel != null) {
            java.util.Random random = new java.util.Random(42);
            TextureAtlasSprite sideSprite   = getSpriteForDirection(baseModel, net.minecraft.core.Direction.NORTH, random);
            if (sideSprite == null) sideSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.EAST, random);
            TextureAtlasSprite topSprite    = getSpriteForDirection(baseModel, net.minecraft.core.Direction.UP,    random);
            TextureAtlasSprite bottomSprite = getSpriteForDirection(baseModel, net.minecraft.core.Direction.DOWN,  random);
            TextureAtlasSprite particle     = baseModel.getParticleIcon();

            TextureAtlasSprite resolvedSide   = sideSprite   != null ? sideSprite   : particle;
            TextureAtlasSprite resolvedTop    = topSprite    != null ? topSprite    : resolvedSide;
            TextureAtlasSprite resolvedBottom = bottomSprite != null ? bottomSprite : resolvedSide;

            if (resolvedSide != null) {
                textures.put("side",     spriteToMaterial(resolvedSide));
                textures.put("top",      spriteToMaterial(resolvedTop));
                textures.put("bottom",   spriteToMaterial(resolvedBottom));
                textures.put("particle", spriteToMaterial(particle != null ? particle : resolvedSide));
                BuildScape.LOGGER.info("  [Base cube quads] side=" + resolvedSide.getName() + " top=" + resolvedTop.getName());
                return textures;
            }
        }

        // ===================================================================
        // Strategy 3: Infer texture path from block ID (last resort / warning)
        // ===================================================================
        BuildScape.LOGGER.warn("Could not find model for parent " + parentId + ", falling back to texture path inference");
        net.minecraft.client.resources.model.Material baseMaterial = createMaterial(parentId.getNamespace(), "block/" + basePathAlt);
        textures.put("side",     baseMaterial);
        textures.put("top",      baseMaterial);
        textures.put("bottom",   baseMaterial);
        textures.put("particle", baseMaterial);
        BuildScape.LOGGER.info("  [Inferred] texture: " + baseMaterial.texture());
        return textures;
    }


    /** Find any non-inventory baked model variant for the given block. */
    private static BakedModel findBakedModelForBlock(ResourceLocation blockId, Map<ResourceLocation, BakedModel> registry) {
        for (Map.Entry<ResourceLocation, BakedModel> entry : registry.entrySet()) {
            if (entry.getKey() instanceof ModelResourceLocation mrl) {
                if (mrl.getNamespace().equals(blockId.getNamespace())
                        && mrl.getPath().equals(blockId.getPath())
                        && !mrl.getVariant().equals("inventory")) {
                    BuildScape.LOGGER.info("  Found baked model for " + blockId + " via variant '" + mrl.getVariant() + "'");
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /** Extract a sprite from the first quad on a given face, or null if no quads. */
    private static TextureAtlasSprite getSpriteForDirection(
            BakedModel model, net.minecraft.core.Direction face, java.util.Random random) {
        try {
            List<net.minecraft.client.renderer.block.model.BakedQuad> quads = model.getQuads(null, face, random);
            if (quads != null && !quads.isEmpty()) {
                return quads.get(0).getSprite();
            }
        } catch (Exception e) {
            BuildScape.LOGGER.debug("getSpriteForDirection " + face + ": " + e.getMessage());
        }
        return null;
    }

    /** Convert a baked TextureAtlasSprite back into a Material the model baker can use. */
    private static net.minecraft.client.resources.model.Material spriteToMaterial(TextureAtlasSprite sprite) {
        if (sprite == null) return createMaterial("minecraft", "block/oak_planks");
        return new net.minecraft.client.resources.model.Material(
                net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS,
                sprite.getName()
        );
    }

    private static boolean isWoodType(String name) {
        return name.equals("oak") || name.equals("spruce") || name.equals("birch") ||
               name.equals("jungle") || name.equals("acacia") || name.equals("dark_oak") ||
               name.equals("crimson") || name.equals("warped") || name.equals("mangrove") ||
               name.equals("cherry") || name.equals("bamboo");
    }

    private static Map<String, net.minecraft.client.resources.model.Material> getDefaultTextures() {
        Map<String, net.minecraft.client.resources.model.Material> textures = new HashMap<>();
        net.minecraft.client.resources.model.Material fallback = createMaterial("minecraft", "block/oak_planks");
        textures.put("side", fallback);
        textures.put("top", fallback);
        textures.put("bottom", fallback);
        textures.put("particle", fallback);
        return textures;
    }

    private static net.minecraft.client.resources.model.Material findMaterialWithFallback(BlockModel model, String... keys) {
        if (model == null) {
            BuildScape.LOGGER.warn("Model is null, using oak_planks fallback");
            return createMaterial("minecraft", "block/oak_planks");
        }

        for (String key : keys) {
            net.minecraft.client.resources.model.Material mat = findMaterial(model, key);
            if (mat != null) {
                BuildScape.LOGGER.debug("Found material for key '" + key + "': " + mat.texture());
                return mat;
            }
        }
        BuildScape.LOGGER.warn("No material found for keys " + String.join(", ", keys) + ", using oak_planks fallback");
        return createMaterial("minecraft", "block/oak_planks");
    }

    private static net.minecraft.client.resources.model.Material createMaterial(String namespace, String path) {
        return new net.minecraft.client.resources.model.Material(
            net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS,
            new ResourceLocation(namespace, path)
        );
    }

    private static BlockModel getBlockModelFromParent(Block block, ModelBakery bakery) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;

        try {
            // Try to get the block model using various strategies
            UnbakedModel unbaked;

            // 1. Try block path
            ResourceLocation blockPath = new ResourceLocation(id.getNamespace(), "block/" + id.getPath());
            unbaked = bakery.getModel(blockPath);
            if (unbaked instanceof BlockModel bm && !isDummy(bm)) {
                return bm;
            }

            // 2. Try ModelResourceLocation variants for slabs (with and without waterlogged)
            if (block instanceof SlabBlock) {
                for (String variant : new String[]{"type=bottom,waterlogged=false", "type=bottom"}) {
                    unbaked = bakery.getModel(new ModelResourceLocation(id, variant));
                    if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;
                }
            }

            // 3. Try ModelResourceLocation variants for stairs (with and without waterlogged)
            if (block instanceof StairBlock) {
                for (String variant : new String[]{
                        "facing=north,half=bottom,shape=straight,waterlogged=false",
                        "facing=north,half=bottom,shape=straight"}) {
                    unbaked = bakery.getModel(new ModelResourceLocation(id, variant));
                    if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;
                }
            }

            // 4. Try inventory variant
            unbaked = bakery.getModel(new ModelResourceLocation(id, "inventory"));
            if (unbaked instanceof BlockModel bm && !isDummy(bm)) return bm;

        } catch (Exception e) {
            BuildScape.LOGGER.debug("Failed to get block model for " + id + ": " + e.getMessage());
        }

        return null;
    }

    private static void resolveFullHierarchy(BlockModel model, ModelBakery bakery) {
        try {
            java.lang.reflect.Method resolve = BlockModel.class.getDeclaredMethod("resolveParents", java.util.function.Function.class);
            resolve.setAccessible(true);
            resolve.invoke(model, (java.util.function.Function<ResourceLocation, UnbakedModel>) bakery::getModel);
        } catch (Exception e) {
            BuildScape.LOGGER.warn("Failed reflection resolve for " + model);
        }
    }

    private static net.minecraft.client.resources.model.Material findMaterial(BlockModel model, String name) {
        if (name == null || model == null) return null;
        BlockModel current = model;
        while (current != null) {
            com.mojang.datafixers.util.Either<net.minecraft.client.resources.model.Material, String> either = current.textureMap.get(name);
            if (either != null) {
                return either.map(mat -> mat, ref -> {
                    // It's a reference! 
                    if (ref.startsWith("#")) {
                        String target = ref.substring(1);
                        if (target.equals(name)) return null; // Circular
                        return findMaterial(model, target); // Search from the TOP of the map
                    } else {
                        // It's a path!
                        ResourceLocation loc = ref.contains(":") ? new ResourceLocation(ref) : new ResourceLocation("minecraft", ref);
                        return new net.minecraft.client.resources.model.Material(
                            net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS,
                            loc
                        );
                    }
                });
            }
            current = current.parent;
        }
        return null;
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

    private static boolean hasTexture(BlockModel model, String name) {
        BlockModel current = model;
        while (current != null) {
            if (current.textureMap.containsKey(name)) return true;
            current = current.parent;
        }
        return false;
    }

    // For placed blocks facing north (Z=0 to Z=8)
    private static final String SLAB_ELEMENTS = "[" +
        "{\"from\":[0,0,0],\"to\":[16,16,8],\"faces\":{" +
        "\"north\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
        "\"south\":{\"uv\":[0,0,16,16],\"texture\":\"#side\"}," +
        "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
        "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"east\"}," +
        "\"up\":{\"uv\":[0,0,16,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
        "\"down\":{\"uv\":[0,8,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}} " +
        "]";

    // For item rendering perfectly centered in the GUI (spans X: 4 to 12, Z: 0 to 16)
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

    // For placed blocks (L-shape facing north)
    private static final String STAIRS_ELEMENTS = "[" +
        "{\"from\":[0,0,0],\"to\":[16,16,8],\"faces\":{" +
        "\"north\":{\"uv\":[0,0,16,16],\"texture\":\"#side\",\"cullface\":\"north\"}," +
        "\"south\":{\"uv\":[0,0,16,16],\"texture\":\"#side\"}," +
        "\"west\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
        "\"east\":{\"uv\":[8,0,16,16],\"texture\":\"#side\"}," +
        "\"up\":{\"uv\":[0,0,16,8],\"texture\":\"#top\",\"cullface\":\"up\"}," +
        "\"down\":{\"uv\":[0,8,16,16],\"texture\":\"#bottom\",\"cullface\":\"down\"}}}," +
        "{\"from\":[0,0,8],\"to\":[8,16,16],\"faces\":{" +
        "\"north\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
        "\"south\":{\"uv\":[0,0,8,16],\"texture\":\"#side\",\"cullface\":\"south\"}," +
        "\"west\":{\"uv\":[8,0,16,16],\"texture\":\"#side\",\"cullface\":\"west\"}," +
        "\"east\":{\"uv\":[0,0,8,16],\"texture\":\"#side\"}," +
        "\"up\":{\"uv\":[0,8,8,16],\"texture\":\"#top\",\"cullface\":\"up\"}," +
        "\"down\":{\"uv\":[0,0,8,8],\"texture\":\"#bottom\",\"cullface\":\"down\"}}}" +
        "]";

    // For internal item rendering (cut-out is at X:8-16, Z:0-8, facing North-East to leave the right corner of the diamond empty in GUI)
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

    private static final Gson GSON = new Gson();

    /**
     * Get the particle texture from the parent block's existing baked model
     */
    private static TextureAtlasSprite getParentParticleTexture(Block parent, Map<ResourceLocation, BakedModel> registry) {
        ResourceLocation parentId = parent.getRegistryName();
        if (parentId == null) return null;

        // Try to get the parent's baked model from registry
        for (Map.Entry<ResourceLocation, BakedModel> entry : registry.entrySet()) {
            if (entry.getKey() instanceof ModelResourceLocation mrl) {
                if (mrl.getNamespace().equals(parentId.getNamespace()) && mrl.getPath().equals(parentId.getPath())) {
                    BakedModel parentModel = entry.getValue();
                    if (parentModel != null && parentModel.getParticleIcon() != null) {
                        BuildScape.LOGGER.info("Found parent particle texture for " + parentId + ": " + parentModel.getParticleIcon().getName());
                        return parentModel.getParticleIcon();
                    }
                }
            }
        }

        BuildScape.LOGGER.warn("Could not find particle texture for parent: " + parentId);
        return null;
    }
}
