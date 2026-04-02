package com.kingodogo.buildscape.variantengine.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
import com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector;
import com.kingodogo.buildscape.variantengine.util.VariantNamingUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class VariantPackResources implements PackResources {
    private static final String ROOT_DIR = "buildscape/generated";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> cachedResources = new ConcurrentHashMap<>();
    private final Set<String> diskFiles = ConcurrentHashMap.newKeySet();

    private static final Object GEN_LOCK = new Object();
    private static boolean diskMatchesFingerprint = false;
    private boolean instanceInitialized = false;

    private void generateContent() {
        if (instanceInitialized) return;

        // Ensure we don't generate until ALL blocks from ALL mods are registered
        if (!com.kingodogo.buildscape.variantengine.registry.VariantRegistrar.isScanningComplete()) {
            return;
        }

        synchronized (GEN_LOCK) {
            if (diskMatchesFingerprint && !cachedResources.isEmpty()) {
                instanceInitialized = true;
                return;
            }

            String currentFingerprint = calculateFingerprint();
            File generatedDir = new File(ROOT_DIR);
            File fingerprintFile = new File(generatedDir, "fingerprint.txt");

            if (generatedDir.exists() && fingerprintFile.exists()) {
                try {
                    String oldFingerprint = Files.readString(fingerprintFile.toPath(), StandardCharsets.UTF_8).trim();
                    if (currentFingerprint.equals(oldFingerprint)) {
                        BuildScape.LOGGER.info("BuildScape: Vertical variants are up to date ({} blocks). Caching disk index...", BlockBiMaps.BASE_BLOCKS.size());
                        try {
                            java.nio.file.Path genPath = generatedDir.toPath();
                            Files.walk(genPath).forEach(p -> {
                                if (Files.isRegularFile(p) && !p.getFileName().toString().equals("fingerprint.txt")) {
                                    String rel = genPath.relativize(p).toString().replace("\\", "/");
                                    diskFiles.add(rel);
                                }
                            });
                        } catch (Exception e) {}
                        
                        diskMatchesFingerprint = true;
                        instanceInitialized = true;
                        return;
                    }
                } catch (Exception ignored) {
                }
            }

            BuildScape.LOGGER.info("BuildScape: Detected new blocks or mods. Regenerating vertical variants for {} blocks...", BlockBiMaps.BASE_BLOCKS.size());
            long startTime = System.currentTimeMillis();

            try {
                if (generatedDir.exists()) {
                    deleteDirectory(generatedDir);
                }
                generatedDir.mkdirs();
            } catch (Exception e) {
            }

            cachedResources.clear();
            JsonObject langObj = new JsonObject();

            for (Block baseBlock : BlockBiMaps.BASE_BLOCKS) {
                ResourceLocation baseId = baseBlock.getRegistryName();
                if (baseId == null || baseId.equals(new ResourceLocation("minecraft", "air"))) continue;

                // Only generate VERTICAL_SLAB and VERTICAL_STAIRS shapes
                BlockShape[] recipeShapes = {BlockShape.SLAB, BlockShape.VERTICAL_SLAB, BlockShape.STAIRS, BlockShape.VERTICAL_STAIRS};

                for (BlockShape shape : recipeShapes) {
                    try {
                        Block variant = BlockBiMaps.getBlockOf(shape, baseBlock);
                        if (variant == null) continue;

                        ResourceLocation verticalId = variant.getRegistryName();
                        if (verticalId == null || !verticalId.getNamespace().equals(BuildScape.MODID)) continue;

                        ResourceLocation horizontalId = null;
                        if (shape == BlockShape.VERTICAL_SLAB) {
                            Block slab = BlockBiMaps.getBlockOf(BlockShape.SLAB, baseBlock);
                            if (slab != null && slab.getRegistryName() != null) {
                                horizontalId = slab.getRegistryName();
                            }
                        } else if (shape == BlockShape.VERTICAL_STAIRS) {
                            Block stairs = BlockBiMaps.getBlockOf(BlockShape.STAIRS, baseBlock);
                            if (stairs != null && stairs.getRegistryName() != null) {
                                horizontalId = stairs.getRegistryName();
                            }
                        }

                        if (horizontalId != null) {
                            addShapelessRecipe(verticalId, horizontalId);
                            addStonecuttingRecipe(verticalId, horizontalId);

                            if (!baseId.equals(horizontalId) && isValidStonecuttingSource(baseId)) {
                                addStonecuttingRecipe(verticalId, baseId, "_from_base_stonecutting");
                            }
                        } else {
                            if (isValidStonecuttingSource(baseId)) {
                                addStonecuttingRecipe(verticalId, baseId);
                            }
                        }

                        addLootTable(verticalId, shape);
                        addBlockTags(verticalId, baseBlock, shape);

                        boolean isTransparent = false;
                        try {
                            isTransparent = BlockFamilyDetector.isGlass(baseBlock) || !baseBlock.defaultBlockState().canOcclude();
                        } catch (Exception ignored) {}
                        
                        if (shape == BlockShape.VERTICAL_SLAB) {
                            addVerticalSlabResources(verticalId, baseId, isTransparent);
                        } else if (shape == BlockShape.VERTICAL_STAIRS) {
                            addVerticalStairResources(verticalId, baseId, isTransparent);
                        } else if (shape == BlockShape.SLAB) {
                            addSlabResources(verticalId, baseId, BlockFamilyDetector.isGlass(baseBlock));
                        } else if (shape == BlockShape.STAIRS) {
                            addStairResources(verticalId, baseId, BlockFamilyDetector.isGlass(baseBlock));
                        }

                        String langName = VariantNamingUtil.generateLangName(baseId, shape);
                        String translationKey = verticalId.getPath();
                        langObj.addProperty("block." + BuildScape.MODID + "." + translationKey, langName);
                        langObj.addProperty("item." + BuildScape.MODID + "." + translationKey, langName);
                    } catch (Exception ignored) {}
                }
            }

            // Write lang entries to a separate 'zz_buildscape_variants' namespace.
            // This is the "lib mod" approach: by using a different namespace for our generated keys,
            // we avoid file collisions with the main 'buildscape' JAR resources.
            // Minecraft will load both files and merge the keys into the global language map.
            putResource("assets/zz_buildscape_variants/lang/en_us.json", GSON.toJson(langObj));
            
            // Save fingerprint to disk
            try {
                if (!generatedDir.exists()) generatedDir.mkdirs();
                Files.writeString(fingerprintFile.toPath(), currentFingerprint, StandardCharsets.UTF_8);
            } catch (Exception e) {}

            BuildScape.LOGGER.info("BuildScape: Generation completed in {}ms", (System.currentTimeMillis() - startTime));
            diskMatchesFingerprint = true;
        }
        instanceInitialized = true;
    }

    private String calculateFingerprint() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();

            // 1. All base blocks currently being processed (sorted by ID)
            List<String> ids = new ArrayList<>();
            for (Block b : BlockBiMaps.BASE_BLOCKS) {
                ResourceLocation id = b.getRegistryName();
                if (id != null) ids.add(id.toString());
            }
            Collections.sort(ids);
            sb.append("blocks:");
            for (String id : ids) sb.append(id).append("|");

            // 2. Mod list and versions (sorted)
            List<String> mods = net.minecraftforge.fml.ModList.get().getMods().stream()
                    .map(m -> m.getModId() + ":" + m.getVersion().toString())
                    .sorted()
                    .collect(Collectors.toList());
            sb.append("mods:");
            for (String m : mods) sb.append(m).append(";");

            // 3. VerticalConfig state (Crucial Fix: Include UI configuration in the JSON generation fingerprint)
            com.kingodogo.buildscape.config.VerticalConfig cfg = com.kingodogo.buildscape.config.VerticalConfig.get();
            
            List<String> allowedFams = new ArrayList<>(cfg.getAllowedFamilies());
            Collections.sort(allowedFams);
            sb.append("allowedFam:").append(String.join(",", allowedFams)).append("|");
            
            List<String> blockedFams = new ArrayList<>(cfg.getBlocklistedFamilies());
            Collections.sort(blockedFams);
            sb.append("blockedFam:").append(String.join(",", blockedFams)).append("|");
            
            List<String> allowedMods = new ArrayList<>(cfg.getAllowedMods());
            Collections.sort(allowedMods);
            sb.append("allowedMod:").append(String.join(",", allowedMods)).append("|");
            
            List<String> blockedMods = new ArrayList<>(cfg.getBlocklistedMods());
            Collections.sort(blockedMods);
            sb.append("blockedMod:").append(String.join(",", blockedMods)).append("|");

            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "fallback-" + BlockBiMaps.BASE_BLOCKS.size() + "-" + System.currentTimeMillis();
        }
    }

    private void addSlabResources(ResourceLocation slabId, ResourceLocation baseId, boolean isGlass) {
        String path = slabId.getPath();
        String cleanPath = baseId.getPath().replaceAll("_slab$", "").replaceAll("_stairs$", "").replaceAll("_stair$", "");
        String tex = getGuessedTexture(baseId);

        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String bottomModel = BuildScape.MODID + ":block/" + path;
        String topModel = BuildScape.MODID + ":block/" + path + "_top";
        String doubleModel = baseId.getNamespace() + ":block/" + cleanPath;

        variants.add("type=bottom", createVariant(bottomModel, 0, 0));
        variants.add("type=top", createVariant(topModel, 0, 0));
        variants.add("type=double", createVariant(doubleModel, 0, 0));

        blockstate.add("variants", variants);
        putResource("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        // Bottom Model
        JsonObject modelBottom = new JsonObject();
        modelBottom.addProperty("parent", "minecraft:block/slab");
        JsonObject texBottom = new JsonObject();
        texBottom.addProperty("bottom", tex);
        texBottom.addProperty("top", tex);
        texBottom.addProperty("side", tex);
        modelBottom.add("textures", texBottom);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(modelBottom));

        // Top Model
        JsonObject modelTop = new JsonObject();
        modelTop.addProperty("parent", "minecraft:block/slab_top");
        JsonObject texTop = new JsonObject();
        texTop.addProperty("bottom", tex);
        texTop.addProperty("top", tex);
        texTop.addProperty("side", tex);
        modelTop.add("textures", texTop);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + "_top.json", GSON.toJson(modelTop));

        // Item Model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", bottomModel);
        putResource("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(itemModel));
    }

    private void addStairResources(ResourceLocation stairId, ResourceLocation baseId, boolean isGlass) {
        String path = stairId.getPath();
        String tex = getGuessedTexture(baseId);

        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String stairModel = BuildScape.MODID + ":block/" + path;
        String innerModel = BuildScape.MODID + ":block/" + path + "_inner";
        String outerModel = BuildScape.MODID + ":block/" + path + "_outer";


        Map<String, int[]> GLASS_STAIR_ROTATIONS = Map.ofEntries(
            Map.entry("facing=east,half=bottom,shape=inner_left", new int[]{0, 270}),
            Map.entry("facing=east,half=bottom,shape=inner_right", new int[]{0, 0}),
            Map.entry("facing=east,half=bottom,shape=outer_left", new int[]{0, 270}),
            Map.entry("facing=east,half=bottom,shape=outer_right", new int[]{0, 0}),
            Map.entry("facing=east,half=bottom,shape=straight", new int[]{0, 270}),
            Map.entry("facing=east,half=top,shape=inner_left", new int[]{180, 0}),
            Map.entry("facing=east,half=top,shape=inner_right", new int[]{180, 90}),
            Map.entry("facing=east,half=top,shape=outer_left", new int[]{180, 0}),
            Map.entry("facing=east,half=top,shape=outer_right", new int[]{180, 90}),
            Map.entry("facing=east,half=top,shape=straight", new int[]{180, 90}),
            Map.entry("facing=north,half=bottom,shape=inner_left", new int[]{0, 180}),
            Map.entry("facing=north,half=bottom,shape=inner_right", new int[]{0, 270}),
            Map.entry("facing=north,half=bottom,shape=outer_left", new int[]{0, 180}),
            Map.entry("facing=north,half=bottom,shape=outer_right", new int[]{0, 270}),
            Map.entry("facing=north,half=bottom,shape=straight", new int[]{0, 180}),
            Map.entry("facing=north,half=top,shape=inner_left", new int[]{180, 270}),
            Map.entry("facing=north,half=top,shape=inner_right", new int[]{180, 0}),
            Map.entry("facing=north,half=top,shape=outer_left", new int[]{180, 270}),
            Map.entry("facing=north,half=top,shape=outer_right", new int[]{180, 0}),
            Map.entry("facing=north,half=top,shape=straight", new int[]{180, 0}),
            Map.entry("facing=south,half=bottom,shape=inner_left", new int[]{0, 0}),
            Map.entry("facing=south,half=bottom,shape=inner_right", new int[]{0, 90}),
            Map.entry("facing=south,half=bottom,shape=outer_left", new int[]{0, 0}),
            Map.entry("facing=south,half=bottom,shape=outer_right", new int[]{0, 90}),
            Map.entry("facing=south,half=bottom,shape=straight", new int[]{0, 0}),
            Map.entry("facing=south,half=top,shape=inner_left", new int[]{180, 90}),
            Map.entry("facing=south,half=top,shape=inner_right", new int[]{180, 180}),
            Map.entry("facing=south,half=top,shape=outer_left", new int[]{180, 90}),
            Map.entry("facing=south,half=top,shape=outer_right", new int[]{180, 180}),
            Map.entry("facing=south,half=top,shape=straight", new int[]{180, 180}),
            Map.entry("facing=west,half=bottom,shape=inner_left", new int[]{0, 90}),
            Map.entry("facing=west,half=bottom,shape=inner_right", new int[]{0, 180}),
            Map.entry("facing=west,half=bottom,shape=outer_left", new int[]{0, 90}),
            Map.entry("facing=west,half=bottom,shape=outer_right", new int[]{0, 180}),
            Map.entry("facing=west,half=bottom,shape=straight", new int[]{0, 90}),
            Map.entry("facing=west,half=top,shape=inner_left", new int[]{180, 180}),
            Map.entry("facing=west,half=top,shape=inner_right", new int[]{180, 270}),
            Map.entry("facing=west,half=top,shape=outer_left", new int[]{180, 180}),
            Map.entry("facing=west,half=top,shape=outer_right", new int[]{180, 270}),
            Map.entry("facing=west,half=top,shape=straight", new int[]{180, 270})
        );
        Map<String, int[]> VANILLA_STAIR_ROTATIONS = Map.ofEntries(
            Map.entry("facing=east,half=bottom,shape=inner_left", new int[]{0, 270}),
            Map.entry("facing=east,half=bottom,shape=inner_right", new int[]{0, 0}),
            Map.entry("facing=east,half=bottom,shape=outer_left", new int[]{0, 270}),
            Map.entry("facing=east,half=bottom,shape=outer_right", new int[]{0, 0}),
            Map.entry("facing=east,half=bottom,shape=straight", new int[]{0, 0}),
            Map.entry("facing=east,half=top,shape=inner_left", new int[]{180, 0}),
            Map.entry("facing=east,half=top,shape=inner_right", new int[]{180, 90}),
            Map.entry("facing=east,half=top,shape=outer_left", new int[]{180, 0}),
            Map.entry("facing=east,half=top,shape=outer_right", new int[]{180, 90}),
            Map.entry("facing=east,half=top,shape=straight", new int[]{180, 0}),
            Map.entry("facing=north,half=bottom,shape=inner_left", new int[]{0, 180}),
            Map.entry("facing=north,half=bottom,shape=inner_right", new int[]{0, 270}),
            Map.entry("facing=north,half=bottom,shape=outer_left", new int[]{0, 180}),
            Map.entry("facing=north,half=bottom,shape=outer_right", new int[]{0, 270}),
            Map.entry("facing=north,half=bottom,shape=straight", new int[]{0, 270}),
            Map.entry("facing=north,half=top,shape=inner_left", new int[]{180, 270}),
            Map.entry("facing=north,half=top,shape=inner_right", new int[]{180, 0}),
            Map.entry("facing=north,half=top,shape=outer_left", new int[]{180, 270}),
            Map.entry("facing=north,half=top,shape=outer_right", new int[]{180, 0}),
            Map.entry("facing=north,half=top,shape=straight", new int[]{180, 270}),
            Map.entry("facing=south,half=bottom,shape=inner_left", new int[]{0, 0}),
            Map.entry("facing=south,half=bottom,shape=inner_right", new int[]{0, 90}),
            Map.entry("facing=south,half=bottom,shape=outer_left", new int[]{0, 0}),
            Map.entry("facing=south,half=bottom,shape=outer_right", new int[]{0, 90}),
            Map.entry("facing=south,half=bottom,shape=straight", new int[]{0, 90}),
            Map.entry("facing=south,half=top,shape=inner_left", new int[]{180, 90}),
            Map.entry("facing=south,half=top,shape=inner_right", new int[]{180, 180}),
            Map.entry("facing=south,half=top,shape=outer_left", new int[]{180, 90}),
            Map.entry("facing=south,half=top,shape=outer_right", new int[]{180, 180}),
            Map.entry("facing=south,half=top,shape=straight", new int[]{180, 90}),
            Map.entry("facing=west,half=bottom,shape=inner_left", new int[]{0, 90}),
            Map.entry("facing=west,half=bottom,shape=inner_right", new int[]{0, 180}),
            Map.entry("facing=west,half=bottom,shape=outer_left", new int[]{0, 90}),
            Map.entry("facing=west,half=bottom,shape=outer_right", new int[]{0, 180}),
            Map.entry("facing=west,half=bottom,shape=straight", new int[]{0, 180}),
            Map.entry("facing=west,half=top,shape=inner_left", new int[]{180, 180}),
            Map.entry("facing=west,half=top,shape=inner_right", new int[]{180, 270}),
            Map.entry("facing=west,half=top,shape=outer_left", new int[]{180, 180}),
            Map.entry("facing=west,half=top,shape=outer_right", new int[]{180, 270}),
            Map.entry("facing=west,half=top,shape=straight", new int[]{180, 180})
        );
        Map<String, int[]> STAIR_ROTATIONS = isGlass ? GLASS_STAIR_ROTATIONS : VANILLA_STAIR_ROTATIONS;

        for (Map.Entry<String, int[]> entry : STAIR_ROTATIONS.entrySet()) {
             String stateKey = entry.getKey();
             int[] value = entry.getValue();
             String m = stateKey.contains("straight") ? stairModel : stateKey.contains("inner") ? innerModel : outerModel;
             variants.add(stateKey, createVariant(m, value[0], value[1]));
        }

        blockstate.add("variants", variants);
        putResource("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        // Models (Stair, Inner, Outer)
        String[] types = {"", "_inner", "_outer"};
        String[] parents = {"minecraft:block/stairs", "minecraft:block/inner_stairs", "minecraft:block/outer_stairs"};

        if (isGlass) {
            parents = new String[] {
                "buildscape:block/glass_stairs_template",
                "buildscape:block/glass_stairs_inner_template",
                "buildscape:block/glass_stairs_outer_template"
            };
        }

        for (int i=0; i<3; i++) {
            JsonObject m = new JsonObject();
            m.addProperty("parent", parents[i]);
            JsonObject t = new JsonObject();
            t.addProperty("bottom", tex);
            t.addProperty("top", tex);
            t.addProperty("side", tex);
            m.add("textures", t);
            putResource("assets/" + BuildScape.MODID + "/models/block/" + path + types[i] + ".json", GSON.toJson(m));
        }

        // Item Model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", stairModel);
        putResource("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(itemModel));
    }


    private void addVerticalSlabResources(ResourceLocation verticalId, ResourceLocation baseId, boolean isTransparent) {
        String path = verticalId.getPath();
        String tex = getGuessedTexture(baseId);

        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;

        variants.add("type=north,waterlogged=false", createVariant(modelPath, 0, 0));
        variants.add("type=north,waterlogged=true", createVariant(modelPath, 0, 0));
        variants.add("type=south,waterlogged=false", createVariant(modelPath, 0, 180));
        variants.add("type=south,waterlogged=true", createVariant(modelPath, 0, 180));
        variants.add("type=east,waterlogged=false", createVariant(modelPath, 0, 90));
        variants.add("type=east,waterlogged=true", createVariant(modelPath, 0, 90));
        variants.add("type=west,waterlogged=false", createVariant(modelPath, 0, 270));
        variants.add("type=west,waterlogged=true", createVariant(modelPath, 0, 270));
        
        String doubleModelPath = BuildScape.MODID + ":block/" + path + "_double";
        variants.add("type=double,waterlogged=false", createVariant(doubleModelPath, 0, 0));
        variants.add("type=double,waterlogged=true", createVariant(doubleModelPath, 0, 0));

        blockstate.add("variants", variants);
        putResource("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", tex);
        textures.addProperty("top", tex);
        textures.addProperty("bottom", tex);
        textures.addProperty("side", tex);
        model.add("textures", textures);

        JsonArray elements = new JsonArray();
        JsonObject element = new JsonObject();
        element.add("from", createJsonArray(0, 0, 0));
        element.add("to", createJsonArray(16, 16, 8));
        
        JsonObject faces = new JsonObject();
        faces.add("north", createFace("north", "side", 0, 0, 16, 16));
        faces.add("south", createFace(null, "side", 0, 0, 16, 16)); // Internal 
        faces.add("west", createFace("west", "side", 0, 0, 8, 16));
        faces.add("east", createFace("east", "side", 8, 0, 16, 16));
        faces.add("up", createFace("up", "top", 0, 0, 16, 8));
        faces.add("down", createFace("down", "bottom", 0, 8, 16, 16));
        element.add("faces", faces);
        elements.add(element);
        
        model.add("elements", elements);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(model));

        // Double Model
        JsonObject doubleModel = new JsonObject();
        doubleModel.addProperty("parent", "minecraft:block/cube_all");
        JsonObject doubleTextures = new JsonObject();
        doubleTextures.addProperty("all", tex);
        doubleTextures.addProperty("particle", tex);
        doubleModel.add("textures", doubleTextures);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + "_double.json", GSON.toJson(doubleModel));

        // Item Model — standalone geometry, does NOT inherit from block model.
        // This ensures consistent facing in inventory regardless of model baking.
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:block/block");
        
        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("particle", tex);
        itemTextures.addProperty("top", tex);
        itemTextures.addProperty("bottom", tex);
        itemTextures.addProperty("side", tex);
        itemModel.add("textures", itemTextures);

        // Defined slab geometry inline — always north-facing half (from=0,0,0 to=16,16,8)
        JsonArray itemElements = new JsonArray();
        JsonObject itemElement = new JsonObject();
        itemElement.add("from", createJsonArray(0, 0, 0));
        itemElement.add("to", createJsonArray(16, 16, 8));
        JsonObject itemFaces = new JsonObject();
        // CULLFACE MUST BE NULL FOR ITEMS TO PREVENT THEM FROM LOOKING HOLLOW IN INVENTORY
        itemFaces.add("north", createFace(null, "side", 0, 0, 16, 16));
        itemFaces.add("south", createFace(null, "side", 0, 0, 16, 16)); 
        itemFaces.add("west", createFace(null, "side", 0, 0, 8, 16));
        itemFaces.add("east", createFace(null, "side", 8, 0, 16, 16));
        itemFaces.add("up", createFace(null, "top", 0, 0, 16, 8));
        itemFaces.add("down", createFace(null, "bottom", 0, 8, 16, 16));
        itemElement.add("faces", itemFaces);
        itemElements.add(itemElement);
        itemModel.add("elements", itemElements);

        // Perfectly Centered Slab: Shift +4 on Z to center the 8-unit depth slab in a 16-unit depth workspace.
        // Moved -2.0 on X to shift it left in the GUI slot for better visual centering as requested.
        itemModel.add("display", getPremiumDisplay(-2.0f, 0, 4, 30, 315, 0));
        putResource("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(itemModel));
    }

    private void addVerticalStairResources(ResourceLocation verticalId, ResourceLocation baseId, boolean isTransparent) {
        String path = verticalId.getPath();
        String tex = getGuessedTexture(baseId);

        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;

        variants.add("facing=north_west,waterlogged=false", createVariant(modelPath, 0, 0));
        variants.add("facing=north_west,waterlogged=true", createVariant(modelPath, 0, 0));
        variants.add("facing=north_east,waterlogged=false", createVariant(modelPath, 0, 90));
        variants.add("facing=north_east,waterlogged=true", createVariant(modelPath, 0, 90));
        variants.add("facing=south_east,waterlogged=false", createVariant(modelPath, 0, 180));
        variants.add("facing=south_east,waterlogged=true", createVariant(modelPath, 0, 180));
        variants.add("facing=south_west,waterlogged=false", createVariant(modelPath, 0, 270));
        variants.add("facing=south_west,waterlogged=true", createVariant(modelPath, 0, 270));

        blockstate.add("variants", variants);
        putResource("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", tex);
        textures.addProperty("top", tex);
        textures.addProperty("bottom", tex);
        textures.addProperty("side", tex);
        model.add("textures", textures);

        JsonArray elements = new JsonArray();
        
        // Box 1: Front-Left (0,0,0 to 8,16,8)
        JsonObject e1 = new JsonObject();
        e1.add("from", createJsonArray(0, 0, 0));
        e1.add("to", createJsonArray(8, 16, 8));
        JsonObject f1 = new JsonObject();
        f1.add("north", createFace("north", "side", 8, 0, 16, 16));
        f1.add("west", createFace("west", "side", 0, 0, 8, 16));
        f1.add("up", createFace("up", "top", 0, 0, 8, 8));
        f1.add("down", createFace("down", "bottom", 0, 8, 8, 16));
        e1.add("faces", f1);
        elements.add(e1);

        // Box 2: Front-Right (8,0,0 to 16,16,8)
        JsonObject e2 = new JsonObject();
        e2.add("from", createJsonArray(8, 0, 0));
        e2.add("to", createJsonArray(16, 16, 8));
        JsonObject f2 = new JsonObject();
        f2.add("north", createFace("north", "side", 0, 0, 8, 16));
        f2.add("south", createFace(null, "side", 8, 0, 16, 16)); // Exposed inside surface
        f2.add("east", createFace("east", "side", 8, 0, 16, 16));
        f2.add("up", createFace("up", "top", 8, 0, 16, 8));
        f2.add("down", createFace("down", "bottom", 8, 8, 16, 16));
        e2.add("faces", f2);
        elements.add(e2);

        // Box 3: Back-Left (0,0,8 to 8,16,16)
        JsonObject e3 = new JsonObject();
        e3.add("from", createJsonArray(0, 0, 8));
        e3.add("to", createJsonArray(8, 16, 16));
        JsonObject f3 = new JsonObject();
        f3.add("south", createFace("south", "side", 0, 0, 8, 16));
        f3.add("west", createFace("west", "side", 8, 0, 16, 16));
        f3.add("east", createFace(null, "side", 0, 0, 8, 16)); // Exposed inside surface
        f3.add("up", createFace("up", "top", 0, 8, 8, 16));
        f3.add("down", createFace("down", "bottom", 0, 0, 8, 8));
        e3.add("faces", f3);
        elements.add(e3);

        model.add("elements", elements);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(model));

        // Item Model — standalone geometry for consistent inventory display
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:block/block");

        JsonObject itemTextures = new JsonObject();
        itemTextures.addProperty("particle", tex);
        itemTextures.addProperty("top", tex);
        itemTextures.addProperty("bottom", tex);
        itemTextures.addProperty("side", tex);
        itemModel.add("textures", itemTextures);

        // Stair geometry: Three-box mosaic to avoid overlapping internal segments (no middle line)
        // This keeps it 100% solid while being seamless for glass.
        JsonArray itemElements = new JsonArray();
        
        // Box 1: Front-Left (0,0,0 to 8,16,8)
        JsonObject ie1 = new JsonObject();
        ie1.add("from", createJsonArray(0, 0, 0));
        ie1.add("to", createJsonArray(8, 16, 8));
        JsonObject if1 = new JsonObject();
        // CULLFACE MUST BE NULL FOR ITEMS
        if1.add("north", createFace(null, "side", 0, 0, 8, 16));
        if1.add("west", createFace(null, "side", 0, 0, 8, 16));
        if1.add("up", createFace(null, "top", 0, 0, 8, 8));
        if1.add("down", createFace(null, "bottom", 0, 8, 8, 16));
        ie1.add("faces", if1);
        itemElements.add(ie1);

        // Box 2: Front-Right (8,0,0 to 16,16,8)
        JsonObject ie2 = new JsonObject();
        ie2.add("from", createJsonArray(8, 0, 0));
        ie2.add("to", createJsonArray(16, 16, 8));
        JsonObject if2 = new JsonObject();
        if2.add("north", createFace(null, "side", 8, 0, 16, 16));
        if2.add("south", createFace(null, "side", 8, 0, 16, 16));
        if2.add("east", createFace(null, "side", 8, 0, 16, 16));
        if2.add("up", createFace(null, "top", 8, 0, 16, 8));
        if2.add("down", createFace(null, "bottom", 8, 8, 16, 16));
        ie2.add("faces", if2);
        itemElements.add(ie2);

        // Box 3: Back-Left (0,0,8 to 8,16,16)
        JsonObject ie3 = new JsonObject();
        ie3.add("from", createJsonArray(0, 0, 8));
        ie3.add("to", createJsonArray(8, 16, 16));
        JsonObject if3 = new JsonObject();
        if3.add("south", createFace(null, "side", 0, 0, 8, 16));
        if3.add("west", createFace(null, "side", 8, 0, 16, 16));
        if3.add("east", createFace(null, "side", 0, 0, 8, 16));
        if3.add("up", createFace(null, "top", 0, 8, 8, 16));
        if3.add("down", createFace(null, "bottom", 0, 0, 8, 8));
        ie3.add("faces", if3);
        itemElements.add(ie3);

        itemModel.add("elements", itemElements);
        itemModel.add("display", getPremiumDisplay(0, 0, 0, 30, 315, 0));
        putResource("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(itemModel));
    }

    private JsonObject getPremiumDisplay() {
        return getPremiumDisplay(0, 0, 0, 30, 225, 0);
    }

    private JsonObject getPremiumDisplay(float tx, float ty, float tz, int rx, int ry, int rz) {
        JsonObject display = new JsonObject();
        display.add("thirdperson_righthand", createDisplay(75, 45, 0, 0, 2.5f, 0, 0.375f));
        display.add("thirdperson_lefthand", createDisplay(75, 45, 0, 0, 2.5f, 0, 0.375f));
        display.add("firstperson_righthand", createDisplay(0, 45, 0, 1.5f, 1.25f, 0, 0.4f));
        display.add("firstperson_lefthand", createDisplay(0, 45, 0, 1.5f, 1.25f, 0, 0.4f));
        display.add("ground", createDisplay(0, 0, 0, 0, 3, 0, 0.25f));
        display.add("gui", createDisplay(rx, ry, rz, tx, ty, tz, 0.625f));
        display.add("fixed", createDisplay(0, 0, 0, 0, 0, 0, 0.5f));
        return display;
    }

    private JsonObject createDisplay(int rx, int ry, int rz, float tx, float ty, float tz, float scale) {
        JsonObject obj = new JsonObject();
        obj.add("rotation", createJsonArray(rx, ry, rz));
        obj.add("translation", createJsonArray(tx, ty, tz));
        obj.add("scale", createJsonArray(scale, scale, scale));
        return obj;
    }

    private JsonArray createJsonArray(float... values) {
        JsonArray array = new JsonArray();
        for (float v : values) array.add(v);
        return array;
    }

    private JsonObject createFace(@Nullable String cull, String texture, float u1, float v1, float u2, float v2) {
        JsonObject obj = new JsonObject();
        obj.addProperty("texture", "#" + texture);
        if (cull != null) obj.addProperty("cullface", cull);
        JsonArray uv = new JsonArray();
        uv.add(u1); uv.add(v1); uv.add(u2); uv.add(v2);
        obj.add("uv", uv);
        return obj;
    }

    private JsonObject createVariant(String model, int x, int y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", model);
        if (x != 0) obj.addProperty("x", x);
        if (y != 0) obj.addProperty("y", y);
        return obj;
    }

    private String getGuessedTexture(ResourceLocation baseId) {
        String path = baseId.getPath();
        String cleanPath = path.replaceAll("_slab$", "").replaceAll("_stairs$", "").replaceAll("_stair$", "");
        
        // Handle common suffix shifts
        if (baseId.getNamespace().equals(BuildScape.MODID)) {
            if (cleanPath.equals("hay_bale")) cleanPath = "hay_block";
        }
        
        String tex = baseId.getNamespace() + ":block/" + cleanPath;
        
        // Handle vanilla blocks with side textures
        if (baseId.getNamespace().equals("minecraft")) {
            if (cleanPath.equals("hay_block")) return "minecraft:block/hay_block_side";
            if (cleanPath.equals("grass_block")) return "minecraft:block/grass_block_side";
            if (cleanPath.equals("dirt_path")) return "minecraft:block/dirt_path_side";
        }
        
        return tex;
    }

    private void addShapelessRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        JsonObject toVertical = new JsonObject();
        toVertical.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients1 = new JsonArray();
        JsonObject item1 = new JsonObject();
        item1.addProperty("item", parentId.toString());
        ingredients1.add(item1);
        toVertical.add("ingredients", ingredients1);
        JsonObject result1 = new JsonObject();
        result1.addProperty("item", verticalId.toString());
        result1.addProperty("count", 1);
        toVertical.add("result", result1);
        putResource("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + ".json", GSON.toJson(toVertical));

        JsonObject toParent = new JsonObject();
        toParent.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients2 = new JsonArray();
        JsonObject item2 = new JsonObject();
        item2.addProperty("item", verticalId.toString());
        ingredients2.add(item2);
        toParent.add("ingredients", ingredients2);
        JsonObject result2 = new JsonObject();
        result2.addProperty("item", parentId.toString());
        result2.addProperty("count", 1);
        toParent.add("result", result2);
        putResource("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + "_revert.json", GSON.toJson(toParent));
    }

    private void addStonecuttingRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        addStonecuttingRecipe(verticalId, parentId, "_stonecutting");
    }

    private void addStonecuttingRecipe(ResourceLocation verticalId, ResourceLocation parentId, String suffix) {
        String path = verticalId.getPath();
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:stonecutting");
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", parentId.toString());
        recipe.add("ingredient", ingredient);
        recipe.addProperty("result", verticalId.toString());
        recipe.addProperty("count", 1);
        putResource("data/" + BuildScape.MODID + "/recipes/" + path + suffix + ".json", GSON.toJson(recipe));
    }

    /**
     * Check if a block is a valid stonecutting source.
     * Prevents creating recipes like oak_log → v_slab_oak or iron_ore → v_slab_iron.
     * Valid sources: solid building blocks, planks, bricks, stone types, etc.
     * Invalid sources: logs, ores, raw materials, ingots, etc.
     */
    private boolean isValidStonecuttingSource(ResourceLocation id) {
        if (id == null) return false;
        String path = id.getPath().toLowerCase();
        
        // Use ForgeRegistries to safely get the block instance
        Block b = net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValue(id);
        if (b == null || b == net.minecraft.world.level.block.Blocks.AIR) return false;

        // Allowed keywords: logs, wood, stems, etc. (per user request)
        // Invalid source keywords — raw materials, ores, functional blocks
        if (path.contains("_ore") || path.contains("raw_") || path.contains("_ingot") || path.contains("_nugget")) return false;
        if (path.contains("spawner") || path.contains("command") || path.contains("barrier")) return false;
        if (path.contains("_egg") || path.contains("_spawn")) return false;
        if (path.contains("command") || path.contains("jigsaw") || path.contains("structure_block")) return false;
        
        return true;
    }

    private void addLootTable(ResourceLocation verticalId, BlockShape shape) {
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", verticalId.toString());

        if (shape == BlockShape.VERTICAL_SLAB) {
            JsonArray entriesArray = new JsonArray();
            JsonObject doubleEntry = new JsonObject();
            doubleEntry.addProperty("type", "minecraft:item");
            doubleEntry.addProperty("name", verticalId.toString());
            
            JsonArray functions = new JsonArray();
            JsonObject setCount = new JsonObject();
            setCount.addProperty("function", "minecraft:set_count");
            setCount.addProperty("count", 2);
            functions.add(setCount);
            doubleEntry.add("functions", functions);
            
            JsonArray conditions = new JsonArray();
            JsonObject stateCondition = new JsonObject();
            stateCondition.addProperty("condition", "minecraft:block_state_property");
            stateCondition.addProperty("block", verticalId.toString());
            JsonObject props = new JsonObject();
            props.addProperty("type", "double");
            stateCondition.add("properties", props);
            conditions.add(stateCondition);
            doubleEntry.add("conditions", conditions);
            
            entriesArray.add(doubleEntry);
            JsonObject singleEntry = new JsonObject();
            singleEntry.addProperty("type", "minecraft:item");
            singleEntry.addProperty("name", verticalId.toString());
            entriesArray.add(singleEntry);

            JsonObject alternatives = new JsonObject();
            alternatives.addProperty("type", "minecraft:alternatives");
            alternatives.add("children", entriesArray);
            
            pool.add("entries", new JsonArray());
            pool.get("entries").getAsJsonArray().add(alternatives);
        } else {
            entries.add(entry);
            pool.add("entries", entries);
        }
        pools.add(pool);
        lootTable.add("pools", pools);
        putResource("data/" + BuildScape.MODID + "/loot_tables/blocks/" + verticalId.getPath() + ".json", GSON.toJson(lootTable));
    }

    private void addBlockTags(ResourceLocation verticalId, Block parent, BlockShape shape) {
        String mineableTag = (parent.defaultBlockState().getMaterial() == net.minecraft.world.level.material.Material.WOOD)
                ? "data/minecraft/tags/blocks/mineable/axe.json"
                : "data/minecraft/tags/blocks/mineable/pickaxe.json";

        String typeTag = null;
        if (shape == BlockShape.VERTICAL_SLAB) typeTag = "data/minecraft/tags/blocks/slabs.json";
        else if (shape == BlockShape.VERTICAL_STAIRS) typeTag = "data/minecraft/tags/blocks/stairs.json";

        addToTag(mineableTag, verticalId.toString());
        if (typeTag != null) addToTag(typeTag, verticalId.toString());
    }

    private void addToTag(String tagPath, String id) {
        JsonObject tagJson = cachedResources.containsKey(tagPath)
                ? GSON.fromJson(cachedResources.get(tagPath), JsonObject.class)
                : new JsonObject();
        if (!tagJson.has("replace")) tagJson.addProperty("replace", false);
        if (!tagJson.has("values")) tagJson.add("values", new JsonArray());
        
        JsonArray values = tagJson.getAsJsonArray("values");
        boolean exists = false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getAsString().equals(id)) {
                exists = true;
                break;
            }
        }
        if (!exists) values.add(id);
        putResource(tagPath, GSON.toJson(tagJson));
    }
    
    private void putResource(String path, String content) {
        // Do NOT generate a resource if it already exists in the mod's own JAR.
        // This preserves handcrafted files like hay_bale_slab.json over generated ones.
        // EXCEPTION: lang files — Minecraft merges lang additively so its safe to always write them.
        boolean isLang = path.contains("/lang/");
        if (!isLang && path.startsWith("assets/") && isResourceInModJar(path)) {
            return;
        }
        cachedResources.put(path, content);
        saveToDisk(path, content);
    }

    private boolean isResourceInModJar(String path) {
        // path is like "assets/buildscape/models/block/hay_bale_slab.json"
        // The mod classloader can find resources from the JAR/resources folder.
        try {
            String resourcePath = path; // already relative
            return VariantPackResources.class.getClassLoader().getResource(resourcePath) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveToDisk(String path, String content) {
        try {
            Path filePath = Paths.get(ROOT_DIR, path);
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
        }
    }

    private void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }

    @Override
    public InputStream getRootResource(String fileName) throws FileNotFoundException {
        Path rootPath = Paths.get(ROOT_DIR, fileName);
        if (Files.exists(rootPath)) {
            try { return Files.newInputStream(rootPath); } catch (IOException ignored) {}
        }

        if ("pack.mcmeta".equals(fileName)) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 9);
            pack.addProperty("description", "BuildScape Variant Engine Resources");
            meta.add("pack", pack);
            return new ByteArrayInputStream(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException(fileName);
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws FileNotFoundException {
        generateContent();
        String path = (type == PackType.SERVER_DATA ? "data" : "assets") + "/" + location.getNamespace() + "/" + location.getPath();

        Path diskPath = Paths.get(ROOT_DIR, path);
        if (diskFiles.contains(path)) {
            try { return Files.newInputStream(diskPath); } catch (IOException e) {}
        }
        
        if (cachedResources.containsKey(path))
            return new ByteArrayInputStream(cachedResources.get(path).getBytes(StandardCharsets.UTF_8));
        throw new FileNotFoundException(location.toString());
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        generateContent();
        String searchPrefix = (type == PackType.SERVER_DATA ? "data" : "assets") + "/" + namespace + "/" + path;
        List<ResourceLocation> found = new ArrayList<>();

        cachedResources.keySet().forEach(key -> {
            if (key.startsWith(searchPrefix)) {
                String prefix = (key.startsWith("data") ? "data/" : "assets/") + namespace + "/";
                String relativePath = key.substring(prefix.length());
                if (filter.test(relativePath)) found.add(new ResourceLocation(namespace, relativePath));
            }
        });

        Path baseDir = Paths.get(ROOT_DIR, (type == PackType.SERVER_DATA ? "data" : "assets"), namespace, path);
        if (Files.exists(baseDir)) {
             try {
                 Files.walk(baseDir, maxDepth).forEach(p -> {
                     if (!Files.isRegularFile(p)) return;
                     String rel = baseDir.getParent().relativize(p).toString().replace("\\", "/");
                     if (filter.test(rel)) found.add(new ResourceLocation(namespace, rel));
                 });
             } catch (IOException ignored) {}
        }
        return found;
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        generateContent();
        String path = (type == PackType.SERVER_DATA ? "data" : "assets") + "/" + location.getNamespace() + "/" + location.getPath();
        return cachedResources.containsKey(path) || diskFiles.contains(path);
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return new HashSet<>(Arrays.asList(BuildScape.MODID, "minecraft", "zz_buildscape_variants"));
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        if (deserializer.getMetadataSectionName().equals("pack")) {
            return (T) new net.minecraft.server.packs.metadata.pack.PackMetadataSection(
                    new net.minecraft.network.chat.TextComponent("BuildScape Variant Engine Resources"),
                    9
            );
        }
        return null;
    }

    @Override
    public String getName() {
        return "BuildScape Variant Engine";
    }

    @Override
    public void close() {
    }
}
