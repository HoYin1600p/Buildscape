package com.kingodogo.buildscape.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SnifferLootTableTest {

    @Test
    void testMainSnifferDiggingLootTable() throws Exception {
        String mainResourcesProp = System.getProperty("buildscape.mainResources");
        Path mainResources = mainResourcesProp != null ? Path.of(mainResourcesProp) : Path.of("common/src/main/resources");
        Path tablePath = mainResources.resolve("data/minecraft/loot_table/gameplay/sniffer_digging.json");
        assertTrue(Files.exists(tablePath), "sniffer_digging.json must exist in main resources: " + tablePath);

        verifySnifferDiggingJson(tablePath);
    }

    @Test
    void testV121xSnifferDiggingLootTable() throws Exception {
        String mainResourcesProp = System.getProperty("buildscape.mainResources");
        Path mainResources = mainResourcesProp != null ? Path.of(mainResourcesProp) : Path.of("common/src/main/resources").toAbsolutePath();
        Path v121xResources = mainResources.getParent().getParent().resolve("v121x/resources");
        Path tablePath = v121xResources.resolve("data/minecraft/loot_table/gameplay/sniffer_digging.json");
        assertTrue(Files.exists(tablePath), "sniffer_digging.json must exist in v121x resources: " + tablePath);

        verifySnifferDiggingJson(tablePath);
    }

    private void verifySnifferDiggingJson(Path tablePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(tablePath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            assertEquals("minecraft:gift", json.get("type").getAsString());
            assertEquals("minecraft:gameplay/sniffer_digging", json.get("random_sequence").getAsString());

            JsonArray pools = json.getAsJsonArray("pools");
            assertNotNull(pools);
            assertEquals(2, pools.size(), "Expected 2 pools: vanilla drops and rare scroll bonus pool");

            // Pool 1: Vanilla sniffer drops (seeds)
            JsonObject vanillaPool = pools.get(0).getAsJsonObject();
            JsonArray vanillaEntries = vanillaPool.getAsJsonArray("entries");
            assertEquals(2, vanillaEntries.size());
            assertEquals("minecraft:torchflower_seeds", vanillaEntries.get(0).getAsJsonObject().get("name").getAsString());
            assertEquals("minecraft:pitcher_pod", vanillaEntries.get(1).getAsJsonObject().get("name").getAsString());

            // Pool 2: Buildscape bonus pool with 1 in 400 chance for Ancient Ashen Scroll (Ashenking Pillar Scroll)
            JsonObject scrollPool = pools.get(1).getAsJsonObject();
            JsonArray scrollEntries = scrollPool.getAsJsonArray("entries");
            assertEquals(2, scrollEntries.size());

            int emptyWeight = -1;
            int scrollWeight = -1;
            String scrollItem = null;

            for (JsonElement entryElem : scrollEntries) {
                JsonObject entry = entryElem.getAsJsonObject();
                String type = entry.get("type").getAsString();
                if ("minecraft:empty".equals(type)) {
                    emptyWeight = entry.get("weight").getAsInt();
                } else if ("minecraft:item".equals(type)) {
                    scrollWeight = entry.get("weight").getAsInt();
                    scrollItem = entry.get("name").getAsString();
                }
            }

            assertEquals("buildscape:ancient_ashen_scroll", scrollItem);
            assertEquals(1, scrollWeight, "Scroll weight should be 1");
            assertEquals(399, emptyWeight, "Empty weight should be 399 for 1 in 400 chance");
            assertEquals(400, emptyWeight + scrollWeight, "Total weight must be 400 (1 in 400 chance)");
        }
    }
}
