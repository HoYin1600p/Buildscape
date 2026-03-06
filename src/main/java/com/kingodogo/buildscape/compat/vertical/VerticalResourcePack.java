package com.kingodogo.buildscape.compat.vertical;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class VerticalResourcePack implements PackResources {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, String> cachedResources = new HashMap<>();

    private void generateContent() {
        if (!cachedResources.isEmpty()) return;

        // Recipes for Slabs
        VerticalRegistry.VERTICAL_SLABS.forEach((parent, vertical) -> {
            ResourceLocation verticalId = vertical.getRegistryName();
            ResourceLocation parentId = parent.getRegistryName();
            
            // Always generate Shapeless Crafting (1:1 reversible) and Stonecutting recipes dynamically
            addShapelessRecipe(verticalId, parentId);
            addStonecuttingRecipe(verticalId, parentId);

            // Loot Table (still needed as fallback)
            addLootTable(verticalId);
            // Blockstate
            addVerticalSlabBlockstate(verticalId);
            // Models
            addVerticalSlabModels(verticalId, parentId);
            // Tags
            addBlockTags(verticalId, parent, true);
        });

        // Recipes for Stairs
        VerticalRegistry.VERTICAL_STAIRS.forEach((parent, vertical) -> {
            ResourceLocation verticalId = vertical.getRegistryName();
            ResourceLocation parentId = parent.getRegistryName();
            
            // Always generate Shapeless Crafting (1:1 reversible) and Stonecutting recipes dynamically
            addShapelessRecipe(verticalId, parentId);
            addStonecuttingRecipe(verticalId, parentId);

            // Loot Table
            addLootTable(verticalId);
            // Blockstate
            addVerticalStairsBlockstate(verticalId);
            // Models
            addVerticalStairsModels(verticalId, parentId);
            // Tags
            addBlockTags(verticalId, parent, false);
        });
    }

    private void addBlockTags(ResourceLocation verticalId, Block parent, boolean isSlab) {
        String pickaxeTag = "data/minecraft/tags/blocks/mineable/pickaxe.json";
        String axeTag = "data/minecraft/tags/blocks/mineable/axe.json";
        String multiTag = isSlab ? "data/minecraft/tags/blocks/slabs.json" : "data/minecraft/tags/blocks/stairs.json";
        
        boolean isWood = parent.defaultBlockState().getMaterial() == net.minecraft.world.level.material.Material.WOOD;
        String mineableTag = isWood ? axeTag : pickaxeTag;
        
        addToTag(mineableTag, verticalId.toString());
        addToTag(multiTag, verticalId.toString());
    }

    private void addToTag(String tagPath, String id) {
        JsonObject tagJson;
        if (cachedResources.containsKey(tagPath)) {
            tagJson = GSON.fromJson(cachedResources.get(tagPath), JsonObject.class);
        } else {
            tagJson = new JsonObject();
            tagJson.addProperty("replace", false);
            tagJson.add("values", new JsonArray());
        }
        tagJson.getAsJsonArray("values").add(id);
        cachedResources.put(tagPath, GSON.toJson(tagJson));
    }

    private void addShapelessRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        // Parent -> Vertical
        JsonObject recipe1 = new JsonObject();
        recipe1.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients1 = new JsonArray();
        JsonObject itemObj1 = new JsonObject();
        itemObj1.addProperty("item", parentId.toString());
        ingredients1.add(itemObj1);
        recipe1.add("ingredients", ingredients1);
        JsonObject result1 = new JsonObject();
        result1.addProperty("item", verticalId.toString());
        result1.addProperty("count", 1);
        recipe1.add("result", result1);
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + ".json", GSON.toJson(recipe1));

        // Vertical -> Parent
        JsonObject recipe2 = new JsonObject();
        recipe2.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients2 = new JsonArray();
        JsonObject itemObj2 = new JsonObject();
        itemObj2.addProperty("item", verticalId.toString());
        ingredients2.add(itemObj2);
        recipe2.add("ingredients", ingredients2);
        JsonObject result2 = new JsonObject();
        result2.addProperty("item", parentId.toString());
        result2.addProperty("count", 1);
        recipe2.add("result", result2);
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/" + verticalId.getPath() + "_revert.json", GSON.toJson(recipe2));
    }

    private void addStonecuttingRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        // 1. Horizontal Block -> Vertical Block
        JsonObject recipe1 = new JsonObject();
        recipe1.addProperty("type", "minecraft:stonecutting");
        JsonObject ingredient1 = new JsonObject();
        ingredient1.addProperty("item", parentId.toString());
        recipe1.add("ingredient", ingredient1);
        recipe1.addProperty("result", verticalId.toString());
        recipe1.addProperty("count", 1);
        cachedResources.put("data/" + BuildScape.MODID + "/recipes/stonecutting_" + verticalId.getPath() + ".json", GSON.toJson(recipe1));

        // 2. Base Block -> Vertical Block
        String basePath = parentId.getPath()
                .replace("_slab", "").replace("_stairs", "").replace("_stair", "");
        String basePathAlt = basePath;
        if (basePath.equals("oak") || basePath.equals("spruce") || basePath.equals("birch") ||
            basePath.equals("jungle") || basePath.equals("acacia") || basePath.equals("dark_oak") ||
            basePath.equals("crimson") || basePath.equals("warped") || basePath.equals("mangrove") ||
            basePath.equals("cherry") || basePath.equals("bamboo")) {
            basePathAlt = basePath + "_planks";
        }
        if (parentId.getPath().contains("brick") && !basePath.endsWith("s")) basePathAlt = basePath + "s";

        if (!basePathAlt.equals(parentId.getPath())) {
            ResourceLocation baseId = new ResourceLocation(parentId.getNamespace(), basePathAlt);
            
            JsonObject recipe2 = new JsonObject();
            recipe2.addProperty("type", "minecraft:stonecutting");
            JsonObject ingredient2 = new JsonObject();
            ingredient2.addProperty("item", baseId.toString());
            recipe2.add("ingredient", ingredient2);
            recipe2.addProperty("result", verticalId.toString());
            // Vanilla gives 1 stair for 1 base block, but 2 slabs for 1 base block.
            int count = verticalId.getPath().contains("slab") ? 2 : 1;
            recipe2.addProperty("count", count);
            cachedResources.put("data/" + BuildScape.MODID + "/recipes/stonecutting_from_base_" + verticalId.getPath() + ".json", GSON.toJson(recipe2));
        }
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

    private void addVerticalSlabBlockstate(ResourceLocation verticalId) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String path = verticalId.getPath();
        String modelPath = BuildScape.MODID + ":block/" + path;
        String doubleModelPath = BuildScape.MODID + ":block/" + path + "_double";

        // Add all combinations of facing, type, and waterlogged
        for (String facing : new String[]{"north", "south", "east", "west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                int rotation = facing.equals("north") ? 0 : facing.equals("south") ? 180 : facing.equals("east") ? 90 : 270;
                variants.add("facing=" + facing + ",type=bottom,waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
                // For vertical slabs, "top" is the same as "bottom" - just a different state
                variants.add("facing=" + facing + ",type=top,waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
                // Double slab variants with facing
                variants.add("facing=" + facing + ",type=double,waterlogged=" + waterlogged, createVariant(doubleModelPath, 0, rotation));
            }
        }

        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));
    }

    private void addVerticalSlabModels(ResourceLocation verticalId, ResourceLocation parentId) {
        String path = verticalId.getPath();
        JsonObject model = new JsonObject();
        // Empty dummy
        model.add("textures", new JsonObject());
        
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(model));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + "_double.json", GSON.toJson(model));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(model));
    }

    private void addVerticalStairsBlockstate(ResourceLocation verticalId) {
        JsonObject blockstate = new JsonObject();
        JsonObject variants = new JsonObject();
        String path = verticalId.getPath();
        String modelPath = BuildScape.MODID + ":block/" + path;

        // Add all combinations of facing and waterlogged
        for (String facing : new String[]{"north", "south", "east", "west"}) {
            for (String waterlogged : new String[]{"false", "true"}) {
                int rotation = facing.equals("north") ? 0 : facing.equals("south") ? 180 : facing.equals("east") ? 90 : 270;
                variants.add("facing=" + facing + ",waterlogged=" + waterlogged, createVariant(modelPath, 0, rotation));
            }
        }

        blockstate.add("variants", variants);
        cachedResources.put("assets/" + BuildScape.MODID + "/blockstates/" + path + ".json", GSON.toJson(blockstate));
    }

    private void addVerticalStairsModels(ResourceLocation verticalId, ResourceLocation parentId) {
        String path = verticalId.getPath();
        JsonObject model = new JsonObject();
        // Empty dummy
        model.add("textures", new JsonObject());
        
        cachedResources.put("assets/" + BuildScape.MODID + "/models/block/" + path + ".json", GSON.toJson(model));
        cachedResources.put("assets/" + BuildScape.MODID + "/models/item/" + path + ".json", GSON.toJson(model));
    }

    private JsonObject createVariant(String model, int x, int y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", model);
        if (x != 0) obj.addProperty("x", x);
        if (y != 0) obj.addProperty("y", y);
        return obj;
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        if ("pack.mcmeta".equals(fileName)) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 9);
            pack.addProperty("description", "BuildScape Vertical Variants");
            meta.add("pack", pack);
            return new ByteArrayInputStream(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException(fileName);
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        generateContent();
        String path = (type == PackType.SERVER_DATA ? "data" : "assets") + "/" + location.getNamespace() + "/" + location.getPath();
        if (cachedResources.containsKey(path)) {
            return new ByteArrayInputStream(cachedResources.get(path).getBytes(StandardCharsets.UTF_8));
        }
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
                if (filter.test(relativePath)) {
                    found.add(new ResourceLocation(namespace, relativePath));
                }
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
        return Set.of(BuildScape.MODID, "minecraft");
    }

    @Override
    public void close() {}

    @Override
    public String getName() {
        return "BuildScape Vertical Variants Resources";
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        return null;
    }
}
