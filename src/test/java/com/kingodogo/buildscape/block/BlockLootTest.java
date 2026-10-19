package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlockLootTest {
    private static final List<String> FACES = List.of("down", "up", "north", "south", "west", "east");
    private static final List<String> SLABS = List.of(
            "stripped_ashpen_log_slab", "stripped_ashpen_log_vertical_slab", "stripped_ashpen_wood_vertical_slab",
            "acacia_wood_slab", "acacia_wood_vertical_slab", "birch_wood_slab", "birch_wood_vertical_slab",
            "crimson_hyphae_slab", "crimson_hyphae_vertical_slab", "crying_obsidian_slab",
            "crying_obsidian_vertical_slab", "cut_red_sandstone_vertical_slab", "cut_sandstone_vertical_slab",
            "dark_oak_wood_slab", "dark_oak_wood_vertical_slab", "jungle_wood_slab", "jungle_wood_vertical_slab",
            "oak_wood_slab", "oak_wood_vertical_slab", "sandstone_vertical_slab", "smooth_red_sandstone_vertical_slab",
            "smooth_sandstone_vertical_slab", "spruce_wood_slab", "spruce_wood_vertical_slab",
            "stripped_acacia_wood_slab", "stripped_acacia_wood_vertical_slab", "stripped_birch_wood_slab",
            "stripped_birch_wood_vertical_slab", "stripped_crimson_hyphae_slab", "stripped_crimson_hyphae_vertical_slab",
            "stripped_dark_oak_wood_slab", "stripped_dark_oak_wood_vertical_slab", "stripped_jungle_wood_slab",
            "stripped_jungle_wood_vertical_slab", "stripped_oak_wood_slab", "stripped_oak_wood_vertical_slab",
            "stripped_spruce_wood_slab", "stripped_spruce_wood_vertical_slab", "stripped_warped_hyphae_slab",
            "stripped_warped_hyphae_vertical_slab", "tinted_glass_slab", "tinted_glass_vertical_slab",
            "warped_hyphae_slab", "warped_hyphae_vertical_slab", "sculk_slab", "sculk_vertical_slab",
            "sulfur_slab", "polished_sulfur_slab", "sulfur_bricks_slab", "cinnabar_slab",
            "polished_cinnabar_slab", "cinnabar_bricks_slab", "poplar_slab");

    @Test
    public void slabsReturnEveryPlacedHalf() throws Exception {
        for (String id : SLABS) {
            JsonObject entry = loadEntry(id);
            for (String type : List.of("bottom", "top", "double")) {
                int count = evaluateCount(id, entry, Map.of("type", type));
                assertEquals(id + " " + type, "double".equals(type) ? 2 : 1, count);
            }
            if (id.startsWith("sculk_")) assertRequiresSilkTouch(entry);
        }
    }

    @Test
    public void sculkVeinsReturnExactlyTheirAttachedFaces() throws Exception {
        JsonObject entry = loadEntry("sculk_vein");
        assertRequiresSilkTouch(entry);
        for (int mask = 0; mask < 64; mask++) {
            Map<String, String> state = new java.util.HashMap<>();
            for (int face = 0; face < FACES.size(); face++) {
                state.put(FACES.get(face), Boolean.toString((mask & (1 << face)) != 0));
            }
            assertEquals("sculk_vein face mask " + mask, Integer.bitCount(mask),
                    evaluateCount("sculk_vein", entry, state));
        }
    }

    private JsonObject loadEntry(String id) throws Exception {
        String resource = "/data/buildscape/loot_tables/blocks/" + id + ".json";
        try (InputStream stream = getClass().getResourceAsStream(resource)) {
            assertNotNull(resource, stream);
            JsonObject table = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertEquals("minecraft:block", table.get("type").getAsString());
            assertEquals(id, 1, table.getAsJsonArray("pools").size());
            JsonObject pool = table.getAsJsonArray("pools").get(0).getAsJsonObject();
            assertEquals(id, 1, pool.get("rolls").getAsInt());
            assertEquals(id, 1, pool.getAsJsonArray("entries").size());
            JsonObject entry = pool.getAsJsonArray("entries").get(0).getAsJsonObject();
            assertEquals("minecraft:item", entry.get("type").getAsString());
            assertEquals("buildscape:" + id, entry.get("name").getAsString());
            return entry;
        }
    }

    private int evaluateCount(String id, JsonObject entry, Map<String, String> state) {
        int count = 1;
        assertTrue(id + " missing count functions", entry.has("functions"));
        for (JsonElement element : entry.getAsJsonArray("functions")) {
            JsonObject function = element.getAsJsonObject();
            String name = function.get("function").getAsString();
            if ("minecraft:explosion_decay".equals(name)) continue;
            assertEquals("minecraft:set_count", name);
            boolean matches = true;
            if (function.has("conditions")) {
                for (JsonElement conditionElement : function.getAsJsonArray("conditions")) {
                    JsonObject condition = conditionElement.getAsJsonObject();
                    assertEquals("minecraft:block_state_property", condition.get("condition").getAsString());
                    assertEquals("buildscape:" + id, condition.get("block").getAsString());
                    for (Map.Entry<String, JsonElement> property : condition.getAsJsonObject("properties").entrySet()) {
                        assertTrue(id + " unknown property " + property.getKey(), state.containsKey(property.getKey()));
                        matches &= property.getValue().getAsString().equals(state.get(property.getKey()));
                    }
                }
            }
            if (matches) {
                assertTrue(id + " count must be constant", function.get("count").isJsonPrimitive());
                int amount = function.get("count").getAsInt();
                count = function.has("add") && function.get("add").getAsBoolean() ? count + amount : amount;
            }
        }
        return count;
    }

    private void assertRequiresSilkTouch(JsonObject entry) {
        boolean silkTouch = false;
        assertTrue(entry.has("conditions"));
        for (JsonElement element : entry.getAsJsonArray("conditions")) {
            JsonObject condition = element.getAsJsonObject();
            if ("minecraft:match_tool".equals(condition.get("condition").getAsString())) {
                for (JsonElement enchantmentElement : condition.getAsJsonObject("predicate").getAsJsonArray("enchantments")) {
                    JsonObject enchantment = enchantmentElement.getAsJsonObject();
                    if ("minecraft:silk_touch".equals(enchantment.get("enchantment").getAsString())) {
                        silkTouch |= enchantment.getAsJsonObject("levels").get("min").getAsInt() >= 1;
                    }
                }
            }
        }
        assertTrue("Silk Touch requirement missing", silkTouch);
    }
}
