package com.kingodogo.buildscape.variantengine.resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import com.kingodogo.buildscape.variantengine.util.BlockBiMaps;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class VariantPackResources implements PackResources {
    private static final String ROOT_DIR = "buildscape/generated";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> cachedResources = new ConcurrentHashMap<>();

    private synchronized void generateContent() {
        if (!cachedResources.isEmpty()) return;

        try {
            File generatedDir = new File(ROOT_DIR);
            if (generatedDir.exists()) {
                deleteDirectory(generatedDir);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.warn("VariantEngine: Failed to clear generated resources: {}", e.getMessage());
        }

        BuildScape.LOGGER.info("VariantEngine: Generating and exporting resources to {}/", ROOT_DIR);

        JsonObject langObj = new JsonObject();

        for (Block baseBlock : BlockBiMaps.BASE_BLOCKS) {
            ResourceLocation parentId = baseBlock.getRegistryName();
            if (parentId == null || parentId.equals(new ResourceLocation("minecraft", "air"))) continue;

            for (BlockShape shape : BlockShape.values()) {
                if (shape == BlockShape.BASE) continue;
                Block variant = BlockBiMaps.getBlockOf(shape, baseBlock);
                if (variant == null) continue;

                ResourceLocation verticalId = variant.getRegistryName();
                if (verticalId == null) continue;

                addShapelessRecipe(verticalId, parentId);
                addStonecuttingRecipe(verticalId, parentId);
                addLootTable(verticalId, shape);
                addBlockTags(verticalId, baseBlock, shape);

                if (shape == BlockShape.VERTICAL_SLAB) {
                    addVerticalSlabResources(verticalId, parentId);
                } else if (shape == BlockShape.VERTICAL_STAIRS) {
                    addVerticalStairResources(verticalId, parentId);
                }

                String langName = VariantNamingUtil.generateLangName(parentId, shape);
                String translationKey = verticalId.getPath();
                langObj.addProperty("block." + BuildScape.MODID + "." + translationKey, langName);
                langObj.addProperty("item." + BuildScape.MODID + "." + translationKey, langName);
            }
        }

        putResource("assets/" + BuildScape.MODID + "/lang/en_us.json", GSON.toJson(langObj));
        cachedResources.forEach(this::saveToDisk);
    }

    private void addVerticalSlabResources(ResourceLocation verticalId, ResourceLocation parentId) {
        String path = verticalId.getPath();
        String tex = parentId.getNamespace() + ":block/" + parentId.getPath().replace("_slab", "").replace("_stairs", "").replace("_stair", "");

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
        faces.add("south", createFace(null, "side", 0, 0, 16, 16)); 
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

        // Item Model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", BuildScape.MODID + ":block/" + path);
        // Perfectly Centered Slab: Move -2 on X and -1 on Y to perfectly center and align profile @ 315 rotation
        itemModel.add("display", getPremiumDisplay(-2, -1, 4, 30, 315, 0));
        putResource("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(itemModel));
    }

    private void addVerticalStairResources(ResourceLocation verticalId, ResourceLocation parentId) {
        String path = verticalId.getPath();
        String tex = parentId.getNamespace() + ":block/" + parentId.getPath().replace("_slab", "").replace("_stairs", "").replace("_stair", "");

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
        JsonObject e1 = new JsonObject();
        e1.add("from", createJsonArray(0, 0, 0));
        e1.add("to", createJsonArray(16, 16, 8));
        JsonObject f1 = new JsonObject();
        f1.add("north", createFace("north", "side", 0, 0, 16, 16));
        f1.add("south", createFace(null, "side", 0, 0, 16, 16)); 
        f1.add("west", createFace("west", "side", 0, 0, 8, 16));
        f1.add("east", createFace("east", "side", 8, 0, 16, 16));
        f1.add("up", createFace("up", "top", 0, 0, 16, 8));
        f1.add("down", createFace("down", "bottom", 0, 8, 16, 16));
        e1.add("faces", f1);
        elements.add(e1);

        JsonObject e2 = new JsonObject();
        e2.add("from", createJsonArray(0, 0, 8));
        e2.add("to", createJsonArray(8, 16, 16));
        JsonObject f2 = new JsonObject();
        f2.add("north", createFace(null, "side", 8, 0, 16, 16)); 
        f2.add("south", createFace("south", "side", 0, 0, 16, 16));
        f2.add("west", createFace("west", "side", 8, 0, 16, 16));
        f2.add("east", createFace(null, "side", 0, 0, 8, 16)); 
        f2.add("up", createFace("up", "top", 0, 8, 8, 16));
        f2.add("down", createFace("down", "bottom", 0, 0, 8, 8));
        e2.add("faces", f2);
        elements.add(e2);

        model.add("elements", elements);
        putResource("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(model));

        // Item Model
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", BuildScape.MODID + ":block/" + path);
        // Front-Facing Stair: Exactly 315 degrees as requested, standard centering (0,0,0)
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
        String path = verticalId.getPath();
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:stonecutting");
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", parentId.toString());
        recipe.add("ingredient", ingredient);
        recipe.addProperty("result", verticalId.toString());
        recipe.addProperty("count", 1);
        putResource("data/" + BuildScape.MODID + "/recipes/" + path + "_from_" + parentId.getPath() + "_stonecutting.json", GSON.toJson(recipe));
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
        cachedResources.put(path, content);
        saveToDisk(path, content);
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
        if (Files.exists(diskPath)) {
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
        return cachedResources.containsKey(path) || Files.exists(Paths.get(ROOT_DIR, path));
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return new HashSet<>(Arrays.asList(BuildScape.MODID, "minecraft"));
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
