package com.kingodogo.buildscape.data;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.block.ModVerticalSlabs;
import com.kingodogo.buildscape.block.ModVerticalStairs;
import com.kingodogo.buildscape.block.VerticalSlabBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.common.Tags;
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
    
    // Cache generated content
    private final Map<String, String> cachedResources = new HashMap<>();

    public DynamicDataPack(String description) {
        this.description = description;
    }

    private synchronized void generateContent() {
        if (!cachedResources.isEmpty()) return;

        BuildScape.LOGGER.info("Starting Dynamic Content Generation for: " + description);

        try {
            // Use BuildScape's own tracking map instead of iterating ForgeRegistries.BLOCKS.
            // Iterating the Forge registry during early pack loading (AddPackFindersEvent)
            // causes a native access violation crash because the registry isn't fully initialized.
            Map<Block, Block> slabMap = ModVerticalSlabs.VERTICAL_SLABS;
            Map<Block, Block> stairMap = ModVerticalStairs.VERTICAL_STAIRS;
            
            if ((slabMap == null || slabMap.isEmpty()) && (stairMap == null || stairMap.isEmpty())) {
                BuildScape.LOGGER.info("Vertical slab/stair maps not populated yet. Deferring content generation.");
                return;
            }
            
            if (slabMap != null && !slabMap.isEmpty()) {
                BuildScape.LOGGER.info("Found " + slabMap.size() + " Vertical Slabs to generate resources for.");
            }
            if (stairMap != null && !stairMap.isEmpty()) {
                BuildScape.LOGGER.info("Found " + stairMap.size() + " Vertical Stairs to generate resources for.");
            }

        // Generate Vertical Slab Logic
        slabMap.forEach((parentSlab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            ResourceLocation parentId = parentSlab.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();
            
            // 1. Loot Tables
            // Path: data/<modid>/loot_tables/blocks/<name>.json
            JsonObject lootTable = new JsonObject();
            lootTable.addProperty("type", "minecraft:block");
            
            JsonArray pools = new JsonArray();
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            
            JsonArray entries = new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", verticalId.toString());
            
            JsonArray functions = new JsonArray();
            
            // Function: Set Count (Double Slab drops 2)
            JsonObject funcCount = new JsonObject();
            funcCount.addProperty("function", "minecraft:set_count");
            funcCount.addProperty("count", 2);
            
            JsonArray conditions = new JsonArray();
            JsonObject cond = new JsonObject();
            cond.addProperty("condition", "minecraft:block_state_property");
            cond.addProperty("block", verticalId.toString());
            JsonObject props = new JsonObject();
            props.addProperty("type", "double");
            cond.add("properties", props);
            conditions.add(cond);
            funcCount.add("conditions", conditions);
            functions.add(funcCount);
            
            // Function: Explosion Decay
            JsonObject funcExplosion = new JsonObject();
            funcExplosion.addProperty("function", "minecraft:explosion_decay");
            functions.add(funcExplosion);
            
            entry.add("functions", functions);
            entries.add(entry);
            pool.add("entries", entries);
            pools.add(pool);
            lootTable.add("pools", pools);
            
            String lootPath = PacketPath("data", verticalId.getNamespace(), "loot_tables/blocks", verticalId.getPath() + ".json");
            cachedResources.put(lootPath, GSON.toJson(lootTable));
            // BuildScape.LOGGER.debug("Generated loot table: " + lootPath);

            // 2. Recipe: Stonecutting (Slab -> Vertical)
            JsonObject r1 = new JsonObject();
            r1.addProperty("type", "minecraft:stonecutting");
            JsonObject r1Ing = new JsonObject();
            r1Ing.addProperty("item", parentId.toString());
            r1.add("ingredient", r1Ing);
            r1.addProperty("result", verticalId.toString());
            r1.addProperty("count", 1);
            
            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", path + "_from_slab_stonecutting.json"),
                GSON.toJson(r1)
            );

            // 3. Recipe: Stonecutting (Vertical -> Slab)
            JsonObject r2 = new JsonObject();
            r2.addProperty("type", "minecraft:stonecutting");
            JsonObject r2Ing = new JsonObject();
            r2Ing.addProperty("item", verticalId.toString());
            r2.add("ingredient", r2Ing);
            r2.addProperty("result", parentId.toString());
            r2.addProperty("count", 1);
            
            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", parentId.getPath() + "_from_vertical_stonecutting.json"),
                GSON.toJson(r2)
            );
            
            // 4. Recipe: Crafting (Shaped vertical)
            JsonObject rCraft = new JsonObject();
            rCraft.addProperty("type", "minecraft:crafting_shaped");
            JsonArray patternArr = new JsonArray();
            patternArr.add("S");
            patternArr.add("S");
            patternArr.add("S");
            rCraft.add("pattern", patternArr);
            
            JsonObject keyObj = new JsonObject();
            JsonObject keyS = new JsonObject();
            keyS.addProperty("item", parentId.toString());
            keyObj.add("S", keyS);
            rCraft.add("key", keyObj);
            
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", verticalId.toString());
            resultObj.addProperty("count", 3);
            rCraft.add("result", resultObj);
            
            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", path + "_crafting.json"),
                GSON.toJson(rCraft)
            );
        });

        stairMap.forEach((parentStair, verticalStair) -> {
            ResourceLocation verticalId = verticalStair.getRegistryName();
            ResourceLocation parentId = parentStair.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();

            // 1. Loot Tables
            JsonObject lootTable = new JsonObject();
            lootTable.addProperty("type", "minecraft:block");
            JsonArray pools = new JsonArray();
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            JsonArray entries = new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", verticalId.toString());

            JsonArray functions = new JsonArray();
            JsonObject funcExplosion = new JsonObject();
            funcExplosion.addProperty("function", "minecraft:explosion_decay");
            functions.add(funcExplosion);
            entry.add("functions", functions);

            entries.add(entry);
            pool.add("entries", entries);
            pools.add(pool);
            lootTable.add("pools", pools);

            String lootPath = PacketPath("data", verticalId.getNamespace(), "loot_tables/blocks", verticalId.getPath() + ".json");
            cachedResources.put(lootPath, GSON.toJson(lootTable));

            // 2. Recipe: Stonecutting (Stair -> Vertical)
            JsonObject r1 = new JsonObject();
            r1.addProperty("type", "minecraft:stonecutting");
            JsonObject r1Ing = new JsonObject();
            r1Ing.addProperty("item", parentId.toString());
            r1.add("ingredient", r1Ing);
            r1.addProperty("result", verticalId.toString());
            r1.addProperty("count", 1);
            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", path + "_from_stair_stonecutting.json"),
                GSON.toJson(r1)
            );

            // 3. Recipe: Stonecutting (Vertical -> Stair)
            JsonObject r2 = new JsonObject();
            r2.addProperty("type", "minecraft:stonecutting");
            JsonObject r2Ing = new JsonObject();
            r2Ing.addProperty("item", verticalId.toString());
            r2.add("ingredient", r2Ing);
            r2.addProperty("result", parentId.toString());
            r2.addProperty("count", 1);
            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", parentId.getPath() + "_from_vertical_stonecutting.json"),
                GSON.toJson(r2)
            );

            // 4. Recipe: Crafting (Shaped vertical)
            JsonObject rCraft = new JsonObject();
            rCraft.addProperty("type", "minecraft:crafting_shaped");
            JsonArray patternArr = new JsonArray();
            patternArr.add("S");
            patternArr.add("S");
            patternArr.add("S");
            rCraft.add("pattern", patternArr);

            JsonObject keyObj = new JsonObject();
            JsonObject keyS = new JsonObject();
            keyS.addProperty("item", parentId.toString());
            keyObj.add("S", keyS);
            rCraft.add("key", keyObj);

            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", verticalId.toString());
            resultObj.addProperty("count", 3);
            rCraft.add("result", resultObj);

            cachedResources.put(
                PacketPath("data", BuildScape.MODID, "recipes", path + "_crafting.json"),
                GSON.toJson(rCraft)
            );
        });
        
        // Generate Tags Aggregation
        Map<String, Set<String>> blockTags = new HashMap<>(); 
        Map<String, Set<String>> itemTags = new HashMap<>(); 
        
        slabMap.forEach((parent, vertical) -> {
            ResourceLocation id = vertical.getRegistryName();
            if (id == null) return;
            
            String logPath = parent.getRegistryName().getPath().toLowerCase();
            net.minecraft.world.level.material.Material mat = parent.defaultBlockState().getMaterial();

            // 1. Tool Tags (Block) - Mineable
            String toolTag = "minecraft:mineable/pickaxe"; // Default
            if (mat == net.minecraft.world.level.material.Material.WOOD || 
                logPath.contains("wood") || logPath.contains("plank") || logPath.contains("log") || 
                logPath.contains("bamboo") || logPath.contains("mangrove") || logPath.contains("fence") || logPath.contains("gate")) {
                toolTag = "minecraft:mineable/axe";
            } else if (mat == net.minecraft.world.level.material.Material.DIRT || 
                       mat == net.minecraft.world.level.material.Material.SAND ||
                       mat == net.minecraft.world.level.material.Material.CLAY ||
                       mat == net.minecraft.world.level.material.Material.GRASS ||
                       logPath.contains("dirt") || logPath.contains("sand") || logPath.contains("mud") || logPath.contains("grass") || 
                       logPath.contains("podzol") || logPath.contains("mycelium")) {
                toolTag = "minecraft:mineable/shovel";
            } else if (logPath.contains("glass") || logPath.contains("pane") || logPath.contains("glowstone") || logPath.contains("leaves")) {
                toolTag = null; // No specific tool required usually
            }
            
            if (toolTag != null) {
                blockTags.computeIfAbsent(toolTag, k -> new HashSet<>()).add(id.toString());
            }
            
            // 2. Tier Tags (Block)
            // Pickaxe blocks often need tier tags for drops
            if ("minecraft:mineable/pickaxe".equals(toolTag)) {
                if (logPath.contains("copper") || logPath.contains("steel") || logPath.contains("iron") || logPath.contains("metal") || logPath.contains("gold")) {
                    blockTags.computeIfAbsent("minecraft:needs_iron_tool", k -> new HashSet<>()).add(id.toString());
                } else if (logPath.contains("diamond") || logPath.contains("obsidian") || logPath.contains("netherite")) {
                    blockTags.computeIfAbsent("minecraft:needs_diamond_tool", k -> new HashSet<>()).add(id.toString());
                } else if (!logPath.contains("glass")) {
                    // Generic stones/bricks need stone tool
                    blockTags.computeIfAbsent("minecraft:needs_stone_tool", k -> new HashSet<>()).add(id.toString());
                }
            }

            // 3. Category Tags (Block & Item)
            blockTags.computeIfAbsent("minecraft:slabs", k -> new HashSet<>()).add(id.toString());
            blockTags.computeIfAbsent(BuildScape.MODID + ":vertical_slabs", k -> new HashSet<>()).add(id.toString());
            
            itemTags.computeIfAbsent("minecraft:slabs", k -> new HashSet<>()).add(id.toString());
            itemTags.computeIfAbsent(BuildScape.MODID + ":vertical_slabs", k -> new HashSet<>()).add(id.toString());
        });

        stairMap.forEach((parent, vertical) -> {
            ResourceLocation id = vertical.getRegistryName();
            if (id == null) return;

            String logPath = parent.getRegistryName().getPath().toLowerCase();
            net.minecraft.world.level.material.Material mat = parent.defaultBlockState().getMaterial();

            String toolTag = "minecraft:mineable/pickaxe";
            if (mat == net.minecraft.world.level.material.Material.WOOD ||
                logPath.contains("wood") || logPath.contains("plank") || logPath.contains("log") ||
                logPath.contains("bamboo") || logPath.contains("mangrove") || logPath.contains("fence") || logPath.contains("gate")) {
                toolTag = "minecraft:mineable/axe";
            } else if (mat == net.minecraft.world.level.material.Material.DIRT ||
                       mat == net.minecraft.world.level.material.Material.SAND ||
                       mat == net.minecraft.world.level.material.Material.CLAY ||
                       mat == net.minecraft.world.level.material.Material.GRASS ||
                       logPath.contains("dirt") || logPath.contains("sand") || logPath.contains("mud") || logPath.contains("grass") ||
                       logPath.contains("podzol") || logPath.contains("mycelium")) {
                toolTag = "minecraft:mineable/shovel";
            } else if (logPath.contains("glass") || logPath.contains("pane") || logPath.contains("glowstone") || logPath.contains("leaves")) {
                toolTag = null;
            }

            if (toolTag != null) {
                blockTags.computeIfAbsent(toolTag, k -> new HashSet<>()).add(id.toString());
            }

            if ("minecraft:mineable/pickaxe".equals(toolTag)) {
                if (logPath.contains("copper") || logPath.contains("steel") || logPath.contains("iron") || logPath.contains("metal") || logPath.contains("gold")) {
                    blockTags.computeIfAbsent("minecraft:needs_iron_tool", k -> new HashSet<>()).add(id.toString());
                } else if (logPath.contains("diamond") || logPath.contains("obsidian") || logPath.contains("netherite")) {
                    blockTags.computeIfAbsent("minecraft:needs_diamond_tool", k -> new HashSet<>()).add(id.toString());
                } else if (!logPath.contains("glass")) {
                    blockTags.computeIfAbsent("minecraft:needs_stone_tool", k -> new HashSet<>()).add(id.toString());
                }
            }

            blockTags.computeIfAbsent("minecraft:stairs", k -> new HashSet<>()).add(id.toString());
            blockTags.computeIfAbsent(BuildScape.MODID + ":vertical_stairs", k -> new HashSet<>()).add(id.toString());

            itemTags.computeIfAbsent("minecraft:stairs", k -> new HashSet<>()).add(id.toString());
            itemTags.computeIfAbsent(BuildScape.MODID + ":vertical_stairs", k -> new HashSet<>()).add(id.toString());
        });
        
        // Write Block Tags
        blockTags.forEach((tagId, ids) -> {
            JsonObject tagJson = new JsonObject();
            tagJson.addProperty("replace", false);
            JsonArray values = new JsonArray();
            ids.forEach(values::add);
            tagJson.add("values", values);
            
            String[] split = tagId.split(":");
            cachedResources.put(
                PacketPath("data", split[0], "tags/blocks", split[1] + ".json"),
                GSON.toJson(tagJson)
            );
        });

        // Write Item Tags
        itemTags.forEach((tagId, ids) -> {
            JsonObject tagJson = new JsonObject();
            tagJson.addProperty("replace", false);
            JsonArray values = new JsonArray();
            ids.forEach(values::add);
            tagJson.add("values", values);
            
            String[] split = tagId.split(":");
            cachedResources.put(
                PacketPath("data", split[0], "tags/items", split[1] + ".json"),
                GSON.toJson(tagJson)
            );
        });
        
        // Generate Client Resources (Assets)
        slabMap.forEach((parentSlab, verticalSlab) -> {
            ResourceLocation verticalId = verticalSlab.getRegistryName();
            ResourceLocation parentId = parentSlab.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();
            String parentPath = parentId.getPath();
            String parentNamespace = parentId.getNamespace();

            // 1. Blockstate
            // Path: assets/<modid>/blockstates/<name>.json
            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            // Double state - use parent slab's double model if possible
            // Most slabs use the full block for double state.
            // We'll guess the full block model path.
            String fullBlockModel = parentNamespace + ":block/" + parentPath.replace("_slab", "");
            // Special cases for vanilla
            if (parentNamespace.equals("minecraft")) {
                if (parentPath.contains("plank")) {
                    fullBlockModel = "minecraft:block/" + parentPath.replace("_slab", "s");
                } else if (parentPath.equals("oak_slab")) fullBlockModel = "minecraft:block/oak_planks";
                else if (parentPath.equals("spruce_slab")) fullBlockModel = "minecraft:block/spruce_planks";
                else if (parentPath.equals("birch_slab")) fullBlockModel = "minecraft:block/birch_planks";
                else if (parentPath.equals("jungle_slab")) fullBlockModel = "minecraft:block/jungle_planks";
                else if (parentPath.equals("acacia_slab")) fullBlockModel = "minecraft:block/acacia_planks";
                else if (parentPath.equals("dark_oak_slab")) fullBlockModel = "minecraft:block/dark_oak_planks";
                else if (parentPath.equals("crimson_slab")) fullBlockModel = "minecraft:block/crimson_planks";
                else if (parentPath.equals("warped_slab")) fullBlockModel = "minecraft:block/warped_planks";
            }

            // Single states - rotate the parent slab model
            String slabModel = parentNamespace + ":block/" + parentPath;

            // Variants for type=bottom and type=top (treated same)
            String[] types = {"bottom", "top"};
            for (String type : types) {
                variants.add("facing=north,type=" + type + ",waterlogged=false", createVariant(slabModel, 90, 180));
                variants.add("facing=north,type=" + type + ",waterlogged=true", createVariant(slabModel, 90, 180));
                
                variants.add("facing=south,type=" + type + ",waterlogged=false", createVariant(slabModel, 90, 0));
                variants.add("facing=south,type=" + type + ",waterlogged=true", createVariant(slabModel, 90, 0));
                
                variants.add("facing=east,type=" + type + ",waterlogged=false", createVariant(slabModel, 90, 270));
                variants.add("facing=east,type=" + type + ",waterlogged=true", createVariant(slabModel, 90, 270));
                
                variants.add("facing=west,type=" + type + ",waterlogged=false", createVariant(slabModel, 90, 90));
                variants.add("facing=west,type=" + type + ",waterlogged=true", createVariant(slabModel, 90, 90));
            }

            // Double state
            variants.add("type=double,waterlogged=false", createVariant(fullBlockModel, 0, 0));
            variants.add("type=double,waterlogged=true", createVariant(fullBlockModel, 0, 0));
            // Add catch-all for double with any facing (even though facing is ignored)
            variants.add("facing=north,type=double,waterlogged=false", createVariant(fullBlockModel, 0, 0));
            variants.add("facing=south,type=double,waterlogged=false", createVariant(fullBlockModel, 0, 0));
            variants.add("facing=east,type=double,waterlogged=false", createVariant(fullBlockModel, 0, 0));
            variants.add("facing=west,type=double,waterlogged=false", createVariant(fullBlockModel, 0, 0));

            blockstate.add("variants", variants);
            cachedResources.put(
                PacketPath("assets", verticalId.getNamespace(), "blockstates", path + ".json"),
                GSON.toJson(blockstate)
            );

            // 2. Item Model
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", slabModel);
            cachedResources.put(
                PacketPath("assets", verticalId.getNamespace(), "models/item", path + ".json"),
                GSON.toJson(itemModel)
            );
        });

        stairMap.forEach((parentStair, verticalStair) -> {
            ResourceLocation verticalId = verticalStair.getRegistryName();
            ResourceLocation parentId = parentStair.getRegistryName();
            if (verticalId == null || parentId == null) return;

            String path = verticalId.getPath();
            String parentPath = parentId.getPath();
            String parentNamespace = parentId.getNamespace();

            JsonObject blockstate = new JsonObject();
            JsonObject variants = new JsonObject();

            String stairModel = parentNamespace + ":block/" + parentPath;

            // Vertical stairs only use facing (4 directions) + waterlogged.
            // Rotate the original stair model upright and around for each direction.
            // Rotate upright (x:90) and orient (y:[0,90,180,270])
            variants.add("facing=north,waterlogged=false", createVariant(stairModel, 90, 180));
            variants.add("facing=north,waterlogged=true", createVariant(stairModel, 90, 180));

            variants.add("facing=south,waterlogged=false", createVariant(stairModel, 90, 0));
            variants.add("facing=south,waterlogged=true", createVariant(stairModel, 90, 0));

            variants.add("facing=east,waterlogged=false", createVariant(stairModel, 90, 270));
            variants.add("facing=east,waterlogged=true", createVariant(stairModel, 90, 270));

            variants.add("facing=west,waterlogged=false", createVariant(stairModel, 90, 90));
            variants.add("facing=west,waterlogged=true", createVariant(stairModel, 90, 90));

            blockstate.add("variants", variants);
            cachedResources.put(
                PacketPath("assets", verticalId.getNamespace(), "blockstates", path + ".json"),
                GSON.toJson(blockstate)
            );

            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", stairModel);
            cachedResources.put(
                PacketPath("assets", verticalId.getNamespace(), "models/item", path + ".json"),
                GSON.toJson(itemModel)
            );
        });
        } catch (Exception e) {
            BuildScape.LOGGER.error("CRITICAL ERROR in Dynamic Content Generation", e);
        }
        
        BuildScape.LOGGER.info("Generated " + cachedResources.size() + " dynamic resources.");
    }

    private JsonObject createVariant(String model, int x, int y) {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", model);
        if (x != 0) obj.addProperty("x", x);
        if (y != 0) obj.addProperty("y", y);
        return obj;
    }

    private String PacketPath(String root, String namespace, String category, String path) {
        return root + "/" + namespace + "/" + category + "/" + path;
    }

    @Override
    public InputStream getRootResource(String fileName) throws IOException {
        if ("pack.mcmeta".equals(fileName)) {
            JsonObject meta = new JsonObject();
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 9); // 1.18.2
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
        // Only return minecraft for SERVER_DATA (for tags) to minimize impact
        if (type == PackType.SERVER_DATA) {
            return Set.of(BuildScape.MODID, "minecraft");
        }
        return Set.of(BuildScape.MODID);
    }

    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        JsonObject jsonobject = null;
        try (InputStream inputstream = this.getRootResource("pack.mcmeta")) {
            jsonobject = GSON.fromJson(new java.io.InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonObject.class);
        } catch (Exception exception) {
            // ignore
        }

        if (jsonobject != null && jsonobject.has(deserializer.getMetadataSectionName())) {
            return deserializer.fromJson(jsonobject.getAsJsonObject(deserializer.getMetadataSectionName()));
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return "BuildScape Dynamic Resources";
    }

    @Override
    public void close() {
        cachedResources.clear();
    }
}
