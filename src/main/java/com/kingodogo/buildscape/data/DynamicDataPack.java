package com.kingodogo.buildscape.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
import com.kingodogo.buildscape.block.ModVerticalStairs;
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

public class DynamicDataPack implements PackResources {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final String description;
    private final PackType activeType;
    
    // Cache generated content
    private final Map<String, String> cachedResources = new HashMap<>();

    public DynamicDataPack(String description, PackType type) {
        this.description = description;
        this.activeType = type;
    }

    private synchronized void generateContent() {
        if (!cachedResources.isEmpty()) return;

        try {
            Map<Block, Block> slabMap = ModVerticalSlabs.VERTICAL_SLABS;
            Map<Block, Block> stairMap = ModVerticalStairs.VERTICAL_STAIRS;
            
            if ((slabMap == null || slabMap.isEmpty()) && (stairMap == null || stairMap.isEmpty())) {
                return;
            }

            if (activeType == PackType.SERVER_DATA) {
                generateServerData(slabMap, stairMap);
            } else {
                generateClientResources(slabMap, stairMap);
            }

        } catch (Exception e) {
            BuildScape.LOGGER.error("Error in Dynamic Content Generation", e);
        }
    }

    private void generateServerData(Map<Block, Block> slabMap, Map<Block, Block> stairMap) {
        // Generate Slabs
        slabMap.forEach((parentSlab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            ResourceLocation parentId = parentSlab.getRegistryName();
            if (verticalId == null || parentId == null) return;

            // Skip if original block doesn't exist
            if (!ForgeRegistries.BLOCKS.containsKey(parentId)) return;
            Block parentBlockObj = ForgeRegistries.BLOCKS.getValue(parentId);
            if (parentBlockObj == null || parentBlockObj == net.minecraft.world.level.block.Blocks.AIR) return;

            // Loot Table
            cachedResources.put(PacketPath("data", verticalId.getNamespace(), "loot_tables/blocks", verticalId.getPath() + ".json"), 
                createSimpleLootTable(verticalId));

            // Recipes
            addSlabRecipes(verticalId, parentId);
            addStonecuttingRecipe(verticalId, parentId);
        });

        // Generate Stairs
        stairMap.forEach((parentStair, verticalStair) -> {
            ResourceLocation verticalId = verticalStair.getRegistryName();
            ResourceLocation parentId = parentStair.getRegistryName();
            if (verticalId == null || parentId == null) return;

            // Skip if original block doesn't exist
            if (!ForgeRegistries.BLOCKS.containsKey(parentId)) return;
            Block parentBlockObj = ForgeRegistries.BLOCKS.getValue(parentId);
            if (parentBlockObj == null || parentBlockObj == net.minecraft.world.level.block.Blocks.AIR) return;

            // Loot Table
            cachedResources.put(PacketPath("data", verticalId.getNamespace(), "loot_tables/blocks", verticalId.getPath() + ".json"), 
                createSimpleLootTable(verticalId));

            // Recipes
            addStairRecipes(verticalId, parentId);
            addStonecuttingRecipe(verticalId, parentId);
        });

        // Tags
        addTags(slabMap, stairMap);
    }

    private void generateClientResources(Map<Block, Block> slabMap, Map<Block, Block> stairMap) {
        // Generate Slabs
        slabMap.forEach((parentSlab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            ResourceLocation parentId = parentSlab.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();
            String parentNamespace = parentId.getNamespace();
            String parentPath = parentId.getPath();

            // Skip if original block doesn't exist or is air
            if (!ForgeRegistries.BLOCKS.containsKey(parentId)) return;
            Block parentBlockObj = ForgeRegistries.BLOCKS.getValue(parentId);
            if (parentBlockObj == null || parentBlockObj == net.minecraft.world.level.block.Blocks.AIR) return;

            // Blockstate
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();
            String modelPath = BuildScape.MODID + ":block/" + path;
            
            variants.add("facing=north,type=bottom", createVariant(modelPath, 0, 0));
            variants.add("facing=south,type=bottom", createVariant(modelPath, 0, 180));
            variants.add("facing=east,type=bottom", createVariant(modelPath, 0, 90));
            variants.add("facing=west,type=bottom", createVariant(modelPath, 0, 270));
            
            // Double uses full block model
            String cleanPath = parentPath.replace("_slab", "").replace("slab_", "");
            String fullBlock = parentNamespace + ":block/" + cleanPath;
            variants.add("type=double", createVariant(fullBlock, 0, 0));

            // Determine the best parent model and texturing strategy
            String modelToUse = parentNamespace + ":block/" + parentPath;
            boolean useAllTexture = false;

            // Known mods that use a single #all texture for slabs/stairs
            boolean isSingleTextureMod = parentNamespace.equals("auxiliaryblocks") || 
                                       parentNamespace.equals("the_vault") ||
                                       parentNamespace.equals("biomesoplenty");

            if (parentNamespace.equals("minecraft")) {
                // Vanilla stable track: parent to horizontal slab
                if (parentPath.startsWith("waxed_")) {
                    modelToUse = parentNamespace + ":block/" + parentPath.replace("waxed_", "");
                }
            } else if (isSingleTextureMod) {
                // Modded fix track: parent to the FULL BLOCK to get textures reliably
                modelToUse = parentNamespace + ":block/" + cleanPath;
                useAllTexture = true;
            }

            blockstate.add("variants", variants);
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "blockstates", path + ".json"), GSON.toJson(blockstate));
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "models/block", path + ".json"), 
                GSON.toJson(createVerticalSlabModel(modelToUse, useAllTexture)));

            // Item Model
            JsonObject itemModel = createVerticalSlabModel(modelToUse, useAllTexture);
            addVerticalDisplay(itemModel, -2.0, 4.0); // Nudge left (-2) and forward (+4)
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "models/item", path + ".json"), GSON.toJson(itemModel));
        });

        // Generate Stairs
        stairMap.forEach((parentStair, verticalStair) -> {
            ResourceLocation verticalId = verticalStair.getRegistryName();
            ResourceLocation parentId = parentStair.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();
            String parentNamespace = parentId.getNamespace();
            String parentPath = parentId.getPath();

            // Skip if original block doesn't exist
            if (!ForgeRegistries.BLOCKS.containsKey(parentId)) return;
            Block parentBlockObj = ForgeRegistries.BLOCKS.getValue(parentId);
            if (parentBlockObj == null || parentBlockObj == net.minecraft.world.level.block.Blocks.AIR) return;

            // Blockstate
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();
            String modelPath = BuildScape.MODID + ":block/" + path;

            variants.add("facing=north", createVariant(modelPath, 0, 0));
            variants.add("facing=south", createVariant(modelPath, 0, 180));
            variants.add("facing=east", createVariant(modelPath, 0, 90));
            variants.add("facing=west", createVariant(modelPath, 0, 270));

            blockstate.add("variants", variants);
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "blockstates", path + ".json"), GSON.toJson(blockstate));

            // Block Model
            String cleanPath = parentPath.replace("_stairs", "").replace("stairs_", "").replace("_stair", "").replace("stair_", "");
            boolean isSingleTextureMod = parentNamespace.equals("auxiliaryblocks") || 
                                       parentNamespace.equals("the_vault") ||
                                       parentNamespace.equals("biomesoplenty");
            
            String modelToUse = parentNamespace + ":block/" + parentPath;
            boolean useAllTexture = false;

            if (parentNamespace.equals("minecraft")) {
                // Vanilla stable track
                if (parentPath.startsWith("waxed_")) {
                    modelToUse = parentNamespace + ":block/" + parentPath.replace("waxed_", "");
                }
            } else if (isSingleTextureMod) {
                modelToUse = parentNamespace + ":block/" + cleanPath;
                useAllTexture = true;
            }
            
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "models/block", path + ".json"), 
                GSON.toJson(createVerticalStairModel(modelToUse, useAllTexture)));

            // Item Model
            JsonObject itemModel = createVerticalStairModel(modelToUse, useAllTexture);
            addVerticalDisplay(itemModel, 0.0, 2.0); // Reverted stairs to original (0 X, 2 Z)
            cachedResources.put(PacketPath("assets", verticalId.getNamespace(), "models/item", path + ".json"), GSON.toJson(itemModel));
        });
    }

    private JsonObject createVerticalSlabModel(String parentModel, boolean useAll) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", parentModel);

        if (useAll) {
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "#all");
            model.add("textures", textures);
        }

        JsonArray elements = new JsonArray();
        JsonObject slab = new JsonObject();
        slab.add("from", createJsonArray(0, 0, 0));
        slab.add("to", createJsonArray(16, 16, 8));
        JsonObject faces = new JsonObject();
        String mainTex = useAll ? "#all" : "#side";
        String topTex = useAll ? "#all" : "#top";
        String botTex = useAll ? "#all" : "#bottom";

        faces.add("north", createFace(mainTex, "north", 0, 0, 16, 16));
        faces.add("south", createFace(mainTex, "south", 0, 0, 16, 16));
        faces.add("west", createFace(mainTex, "west", 0, 0, 8, 16));
        faces.add("east", createFace(mainTex, "east", 8, 0, 16, 16));
        faces.add("up", createFace(topTex, "up", 0, 0, 16, 8));
        faces.add("down", createFace(botTex, "down", 0, 8, 16, 16));
        slab.add("faces", faces);
        elements.add(slab);
        model.add("elements", elements);
        return model;
    }

    private JsonObject createVerticalStairModel(String parentModel, boolean useAll) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", parentModel);

        if (useAll) {
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", "#all");
            model.add("textures", textures);
        }

        JsonArray elements = new JsonArray();
        String mainTex = useAll ? "#all" : "#side";
        String topTex = useAll ? "#all" : "#top";
        String botTex = useAll ? "#all" : "#bottom";

        // Element 1: Back Slab
        JsonObject slab = new JsonObject();
        slab.add("from", createJsonArray(0, 0, 0));
        slab.add("to", createJsonArray(16, 16, 8));
        JsonObject slabFaces = new JsonObject();
        slabFaces.add("north", createFace(mainTex, "north", 0, 0, 16, 16));
        slabFaces.add("south", createFace(mainTex, null, 0, 0, 16, 16));
        slabFaces.add("west", createFace(mainTex, "west", 0, 0, 8, 16));
        slabFaces.add("east", createFace(mainTex, "east", 8, 0, 16, 16));
        slabFaces.add("up", createFace(topTex, "up", 0, 0, 16, 8));
        slabFaces.add("down", createFace(botTex, "down", 0, 8, 16, 16));
        slab.add("faces", slabFaces);
        elements.add(slab);

        // Element 2: Front Pillar
        JsonObject pillar = new JsonObject();
        pillar.add("from", createJsonArray(0, 0, 8));
        pillar.add("to", createJsonArray(8, 16, 16));
        JsonObject pFaces = new JsonObject();
        pFaces.add("north", createFace(mainTex, null, 0, 0, 8, 16));
        pFaces.add("south", createFace(mainTex, "south", 0, 0, 8, 16));
        pFaces.add("west", createFace(mainTex, "west", 8, 0, 16, 16));
        pFaces.add("east", createFace(mainTex, null, 0, 0, 8, 16));
        pFaces.add("up", createFace(topTex, "up", 0, 8, 8, 16));
        pFaces.add("down", createFace(botTex, "down", 0, 0, 8, 8));
        pillar.add("faces", pFaces);
        elements.add(pillar);

        model.add("elements", elements);
        return model;
    }

    private void addVerticalDisplay(JsonObject model, double translateX, double translateZ) {
        JsonObject display = new JsonObject();
        
        // GUI: 315 degree rotation to face correctly, with centering translation
        JsonObject gui = new JsonObject();
        gui.add("rotation", createJsonArray(30, 315, 0));
        gui.add("translation", createJsonArray(translateX, 0, translateZ)); 
        gui.add("scale", createJsonArray(0.625, 0.625, 0.625));
        display.add("gui", gui);

        // Ground: Item floating/spinning on the floor
        JsonObject ground = new JsonObject();
        ground.add("translation", createJsonArray(0, 3, 0));
        ground.add("scale", createJsonArray(0.25, 0.25, 0.25));
        display.add("ground", ground);

        // Third Person
        JsonObject thirdPersonRight = new JsonObject();
        thirdPersonRight.add("rotation", createJsonArray(75, 45, 0));
        thirdPersonRight.add("translation", createJsonArray(0, 2.5, 0));
        thirdPersonRight.add("scale", createJsonArray(0.375, 0.375, 0.375));
        display.add("thirdperson_righthand", thirdPersonRight);

        // First Person: Handheld view
        JsonObject firstPersonRight = new JsonObject();
        firstPersonRight.add("rotation", createJsonArray(0, 315, 0));
        firstPersonRight.add("translation", createJsonArray(0, 0, translateZ / 2.0)); // Slight centering in hand
        firstPersonRight.add("scale", createJsonArray(0.4, 0.4, 0.4));
        display.add("firstperson_righthand", firstPersonRight);

        JsonObject firstPersonLeft = new JsonObject();
        firstPersonLeft.add("rotation", createJsonArray(0, 135, 0));
        firstPersonLeft.add("translation", createJsonArray(0, 0, translateZ / 2.0));
        firstPersonLeft.add("scale", createJsonArray(0.4, 0.4, 0.4));
        display.add("firstperson_lefthand", firstPersonLeft);

        model.add("display", display);
    }

    private JsonObject createFace(String texture, String cullface, int u1, int v1, int u2, int v2) {
        JsonObject face = new JsonObject();
        face.addProperty("texture", texture);
        if (cullface != null) face.addProperty("cullface", cullface);
        face.add("uv", createJsonArray(u1, v1, u2, v2));
        return face;
    }

    private String createSimpleLootTable(ResourceLocation blockId) {
        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:block");
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", blockId.toString());
        entries.add(entry);
        pool.add("entries", entries);
        pools.add(pool);
        lootTable.add("pools", pools);
        return GSON.toJson(lootTable);
    }

    private void addSlabRecipes(ResourceLocation verticalId, ResourceLocation parentId) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients = new JsonArray();
        JsonObject itemObj = new JsonObject();
        itemObj.addProperty("item", parentId.toString());
        ingredients.add(itemObj);
        recipe.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        result.addProperty("item", verticalId.toString());
        result.addProperty("count", 1);
        recipe.add("result", result);
        cachedResources.put(PacketPath("data", BuildScape.MODID, "recipes", verticalId.getPath() + ".json"), GSON.toJson(recipe));
    }

    private void addStairRecipes(ResourceLocation verticalId, ResourceLocation parentId) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray ingredients = new JsonArray();
        JsonObject itemObj = new JsonObject();
        itemObj.addProperty("item", parentId.toString());
        ingredients.add(itemObj);
        recipe.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        result.addProperty("item", verticalId.toString());
        result.addProperty("count", 1);
        recipe.add("result", result);
        cachedResources.put(PacketPath("data", BuildScape.MODID, "recipes", verticalId.getPath() + ".json"), GSON.toJson(recipe));
    }

    private void addStonecuttingRecipe(ResourceLocation verticalId, ResourceLocation parentId) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:stonecutting");
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", parentId.toString());
        recipe.add("ingredient", ingredient);
        recipe.addProperty("result", verticalId.toString());
        recipe.addProperty("count", 1);
        cachedResources.put(PacketPath("data", BuildScape.MODID, "recipes", "stonecutting_" + verticalId.getPath() + ".json"), GSON.toJson(recipe));
    }

    private void addTags(Map<Block, Block> slabMap, Map<Block, Block> stairMap) {
        // Blocks Tags
        addTag(PacketPath("data", "minecraft", "tags/blocks", "slabs.json"), slabMap.values());
        addTag(PacketPath("data", "minecraft", "tags/blocks", "stairs.json"), stairMap.values());
        
        // Item Tags
        addTag(PacketPath("data", "minecraft", "tags/items", "slabs.json"), slabMap.values());
        addTag(PacketPath("data", "minecraft", "tags/items", "stairs.json"), stairMap.values());
    }

    private void addTag(String path, Collection<Block> blocks) {
        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (Block block : blocks) {
            ResourceLocation id = block.getRegistryName();
            if (id != null) values.add(id.toString());
        }
        tag.add("values", values);
        cachedResources.put(path, GSON.toJson(tag));
    }

    private JsonObject createVariant(String model, int x, int y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", model);
        if (x != 0) obj.addProperty("x", x);
        if (y != 0) obj.addProperty("y", y);
        return obj;
    }

    private JsonArray createJsonArray(Number... values) {
        JsonArray array = new JsonArray();
        for (Number value : values) array.add(GSON.toJsonTree(value));
        return array;
    }

    private String PacketPath(String root, String namespace, String category, String path) {
        return root + "/" + namespace + "/" + category + "/" + path;
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        if ("pack.mcmeta".equals(fileName)) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 9);
            pack.addProperty("description", description);
            meta.add("pack", pack);
            return new ByteArrayInputStream(GSON.toJson(meta).getBytes(StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException(fileName);
    }

    @Override
    public InputStream getResource(PackType type, ResourceLocation location) throws IOException {
        generateContent();
        String root = type == PackType.SERVER_DATA ? "data" : "assets";
        String path = root + "/" + location.getNamespace() + "/" + location.getPath();
        if (cachedResources.containsKey(path)) {
            return new ByteArrayInputStream(cachedResources.get(path).getBytes(StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException(location.toString());
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        generateContent();
        String root = type == PackType.SERVER_DATA ? "data" : "assets";
        List<ResourceLocation> found = new ArrayList<>();
        String searchPrefix = root + "/" + namespace + "/" + path;
        
        for (String key : cachedResources.keySet()) {
            if (key.startsWith(searchPrefix)) {
                String relativeData = key.substring((root + "/" + namespace + "/").length());
                if (filter.test(relativeData)) {
                     found.add(new ResourceLocation(namespace, relativeData));
                }
            }
        }
        return found;
    }

    @Override
    public boolean hasResource(PackType type, ResourceLocation location) {
        generateContent();
        String root = type == PackType.SERVER_DATA ? "data" : "assets";
        String path = root + "/" + location.getNamespace() + "/" + location.getPath();
        return cachedResources.containsKey(path);
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        // Return BuildScape namespace only when we actually have content to serve
        // to avoid interfering with the main mod's file lookups early on.
        return Set.of(BuildScape.MODID, "minecraft");
    }

    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return "BuildScape Dynamic Resources";
    }

    @Override
    public void close() {
    }
}
