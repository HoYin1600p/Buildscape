package com.kingodogo.buildscape.blocks;

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

public class GlowLightsLootTest {
    private static final List<String> FACES = List.of("north", "east", "south", "west", "up", "down");

    @Test
    public void glowLightsReturnOneItemPerFace() throws Exception {
        checkLoot("glow_lights");
    }

    @Test
    public void multicolorGlowLightsReturnOneItemPerFace() throws Exception {
        checkLoot("multicolor_glow_lights");
    }

    private void checkLoot(String id) throws Exception {
        String resource = "/data/buildscape/loot_tables/blocks/" + id + ".json";
        JsonObject table;
        try (InputStream stream = getClass().getResourceAsStream(resource)) {
            assertNotNull(resource, stream);
            table = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        assertEquals("minecraft:block", table.get("type").getAsString());
        assertEquals(1, table.getAsJsonArray("pools").size());
        JsonObject pool = table.getAsJsonArray("pools").get(0).getAsJsonObject();
        assertEquals(1, pool.get("rolls").getAsInt());
        assertEquals(1, pool.getAsJsonArray("entries").size());
        JsonObject entry = pool.getAsJsonArray("entries").get(0).getAsJsonObject();
        assertEquals("buildscape:" + id, entry.get("name").getAsString());

        for (int mask = 0; mask < 64; mask++) {
            int count = 1;
            for (JsonElement element : entry.getAsJsonArray("functions")) {
                JsonObject function = element.getAsJsonObject();
                assertEquals("minecraft:set_count", function.get("function").getAsString());
                boolean matches = true;
                if (function.has("conditions")) {
                    for (JsonElement conditionElement : function.getAsJsonArray("conditions")) {
                        JsonObject condition = conditionElement.getAsJsonObject();
                        assertEquals("minecraft:block_state_property", condition.get("condition").getAsString());
                        assertEquals("buildscape:" + id, condition.get("block").getAsString());
                        for (Map.Entry<String, JsonElement> property : condition.getAsJsonObject("properties").entrySet()) {
                            int face = FACES.indexOf(property.getKey());
                            assertTrue("unknown face " + property.getKey(), face >= 0);
                            boolean attached = (mask & (1 << face)) != 0;
                            matches &= attached == property.getValue().getAsBoolean();
                        }
                    }
                }
                if (matches) {
                    int amount = function.get("count").getAsInt();
                    count = function.has("add") && function.get("add").getAsBoolean() ? count + amount : amount;
                }
            }
            assertEquals(id + " face mask " + mask, Integer.bitCount(mask), count);
        }
    }
}
