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
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class VariantPackResources implements PackResources {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> cachedResources = new java.util.concurrent.ConcurrentHashMap<>();

    private synchronized void generateContent() {
        if (!cachedResources.isEmpty()) return;

        JsonObject langObj = new JsonObject();

        for (Block baseBlock : BlockBiMaps.BASE_BLOCKS) {
            ResourceLocation parentId = baseBlock.getRegistryName();
            if (parentId == null || parentId.equals(new ResourceLocation("minecraft", "air"))) continue;

            for (BlockShape shape : BlockShape.values()) {
                Block variant = BlockBiMaps.getBlockOf(shape, baseBlock);
                if (variant == null) continue;

                ResourceLocation verticalId = variant.getRegistryName();
                if (verticalId == null) continue;

                // Recipes
                addShapelessRecipe(verticalId, parentId);
                addStonecuttingRecipe(verticalId, parentId);

                // Loot Table
                addLootTable(verticalId);

                // Tags
                addBlockTags(verticalId, baseBlock, shape);

                // Blockstates and Models (Dummies for model baking)
                if (shape == BlockShape.VERTICAL_SLAB) {
                    addVerticalSlabResources(verticalId);
                } else if (shape == BlockShape.VERTICAL_STAIRS) {
                    addVerticalStairResources(verticalId);
                } else if (shape == BlockShape.QUARTER_PIECE) {
                    addQuarterPieceResources(verticalId);
                } else if (shape == BlockShape.VERTICAL_QUARTER_PIECE) {
                    addVerticalQuarterPieceResources(verticalId);
                }

                // Add to language file
                String langName = VariantNamingUtil.generateLangName(parentId, shape);
                langObj.addProperty("block." + BuildScape.MODID + "." + verticalId.getPath(), langName);
                langObj.addProperty("item." + BuildScape.MODID + "." + verticalId.getPath(), langName);
            }
        }

        cachedResources.put("assets/" + BuildScape.MODID + "/lang/en_us.json", GSON.toJson(langObj));
    }

    private void addVerticalSlabResources(ResourceLocation verticalId) {
        String path = verticalId.getPath();
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;
        String doubleModelPath = BuildScape.MODID + ":block/" + path + "_double";

        for (String facing : new String[]{"north", "south", "east", "west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                int rotation = getRotation(facing);
                variants.add("facing=" + facing + ",type=bottom,waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
                variants.add("facing=" + facing + ",type=top,waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
                variants.add("facing=" + facing + ",type=double,waterlogged=" + waterlogged, createVariant(doubleModelPath, 0, rotation));
            }
        }
        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        // Models (Dummies)
        JsonObject dummy = new JsonObject();
        dummy.add("textures", new JsonObject());
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(dummy));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + "_double.json", GSON.toJson(dummy));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(dummy));
    }

    private void addVerticalStairResources(ResourceLocation verticalId) {
        String path = verticalId.getPath();
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;

        for (String facing : new String[]{"north", "south", "east", "west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                int rotation = getRotation(facing);
                variants.add("facing=" + facing + ",waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
            }
        }
        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        JsonObject dummy = new JsonObject();
        dummy.add("textures", new JsonObject());
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(dummy));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(dummy));
    }

    private void addQuarterPieceResources(ResourceLocation verticalId) {
        String path = verticalId.getPath();
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;

        for (String facing : new String[]{"north", "south", "east", "west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                for (String half : new String[]{"bottom", "top"}) {
                    int rotation = getRotation(facing);
                    variants.add("facing=" + facing + ",half=" + half + ",waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
                }
            }
        }
        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        JsonObject dummy = new JsonObject();
        dummy.add("textures", new JsonObject());
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(dummy));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(dummy));
    }

    private void addVerticalQuarterPieceResources(ResourceLocation verticalId) {
        String path = verticalId.getPath();
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String modelPath = BuildScape.MODID + ":block/" + path;

        for (String facing : new String[]{"north_west", "north_east", "south_east", "south_west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                int rotation = getCornerRotation(facing);
                variants.add("facing=" + facing + ",waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
            }
        }
        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));

        JsonObject dummy = new JsonObject();
        dummy.add("textures", new JsonObject());
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(dummy));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(dummy));
    }

    private int getRotation(String facing) {
        switch (facing) {
            case "south":
                return 180;
            case "east":
                return 90;
            case "west":
                return 270;
            default:
                return 0;
        }
    }

    private int getCornerRotation(String facing) {
        switch (facing) {
            case "north_east":
                return 90;
            case "south_east":
                return 180;
            case "south_west":
                return 270;
            default:
                return 0;
        }
    }

    private JsonObject createVariant(String model, int x, int y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", model);
        if (x != 0) obj.addProperty("x", x);
        if (y != 0) obj.addProperty("y", y);
        return obj;
    }

    private void addShapelessRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        // Parent -> Vertical
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
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + ".json", GSON.toJson(toVertical));

        // Vertical -> Parent
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
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + "_revert.json", GSON.toJson(toParent));
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
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/" + path + "_from_" + parentId.getPath() + "_stonecutting.json", GSON.toJson(recipe));
    }

    private void addLootTable(ResourceLocation verticalId) {
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", verticalId.toString());
        entries.add(entry);
        pool.add("entries", entries);
        pools.add(pool);
        lootTable.add("pools", pools);
        cachedResources.put("data/" + BuildScape.MODID + "/loot_tables/blocks/" + verticalId.getPath() + ".json", GSON.toJson(lootTable));
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
        tagJson.getAsJsonArray("values").add(id);
        cachedResources.put(tagPath, GSON.toJson(tagJson));
    }

    @Override
    public InputStream getRootResource(String fileName) throws FileNotFoundException {
        if ("pack.mcmeta".equals(fileName)) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 9); // 1.18.2
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
        return found;
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        generateContent();
        String path = (type == PackType.SERVER_DATA ? "data" : "assets") + "/" + location.getNamespace() + "/" + location.getPath();
        return cachedResources.containsKey(path);
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return Collections.singleton(BuildScape.MODID);
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
