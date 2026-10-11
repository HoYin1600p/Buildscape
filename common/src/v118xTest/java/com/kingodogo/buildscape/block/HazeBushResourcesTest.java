package com.kingodogo.buildscape.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class HazeBushResourcesTest {
    private static final String[] COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    private HazeBushResourcesTest() {
    }

    public static void main(String[] args) {
        JsonObject lang = readResource("assets/buildscape/lang/en_us.json");
        for (String color : COLORS) {
            String bush = color + "_haze_bush";
            checkBushLoot(bush);
            checkPottedLoot(bush);
            require(!lang.has("item.buildscape." + bush), bush + " has an unused item name");
            require(!lang.has("item.buildscape." + bush + ".drained"), bush + " has an unused drained item name");
            require(lang.has("block.buildscape." + bush), bush + " is missing its full name");
            require(lang.has("block.buildscape." + bush + ".drained"), bush + " is missing its drained name");
            require(lang.has("block.buildscape.potted_" + bush), bush + " is missing its pot name");
        }
        require(lang.has("tooltip.buildscape.haze_bush.drained"), "missing drained tooltip");
        for (String key : lang.keySet()) {
            require(!key.matches("item\\.buildscape\\.[a-z_]+_haze_bush(?:\\.drained)?"),
                    "unused haze item translation: " + key);
        }
        System.out.println("Haze bush resource tests passed: all 16 colors");
    }

    private static void checkBushLoot(String bush) {
        JsonObject loot = readResource("data/buildscape/loot_tables/blocks/" + bush + ".json");
        checkCommonLoot(loot, bush);
        JsonArray pools = loot.getAsJsonArray("pools");
        require(pools.size() == 1, bush + " must drop one bush");
        JsonObject pool = pools.get(0).getAsJsonObject();
        require(pool.get("rolls").getAsInt() == 1, bush + " must roll once");
        require(!pool.has("conditions"), bush + " drop must be unconditional");
        require(!loot.has("functions") && !pool.has("functions"), bush + " must not tag full drops");
        JsonArray entries = pool.getAsJsonArray("entries");
        require(entries.size() == 1, bush + " must have one drop entry");
        JsonObject entry = entries.get(0).getAsJsonObject();
        require("minecraft:item".equals(entry.get("type").getAsString()), bush + " must drop an item");
        require(("buildscape:" + bush).equals(entry.get("name").getAsString()), bush + " drops the wrong item");
        require(!entry.has("conditions"), bush + " item must be unconditional");
        JsonArray functions = entry.getAsJsonArray("functions");
        require(functions.size() == 1, bush + " must only tag drained drops");
        JsonObject function = functions.get(0).getAsJsonObject();
        require("minecraft:set_nbt".equals(function.get("function").getAsString()), bush + " must set drained NBT");
        require("{BlockStateTag:{has_haze:\"false\"},HasHaze:0b}".equals(function.get("tag").getAsString()),
                bush + " must preserve both drained tags used by pot removal and pick block");
        JsonArray conditions = function.getAsJsonArray("conditions");
        require(conditions.size() == 1, bush + " tag must depend only on the drained state");
        JsonObject condition = conditions.get(0).getAsJsonObject();
        require("minecraft:block_state_property".equals(condition.get("condition").getAsString()),
                bush + " tag must check block state");
        require(("buildscape:" + bush).equals(condition.get("block").getAsString()), bush + " checks the wrong block");
        JsonObject properties = condition.getAsJsonObject("properties");
        require(properties.size() == 1 && "false".equals(properties.get("has_haze").getAsString()),
                bush + " full drops must remain untagged");
    }

    private static void checkPottedLoot(String bush) {
        JsonObject loot = readResource("data/buildscape/loot_tables/blocks/potted_" + bush + ".json");
        checkCommonLoot(loot, "potted_" + bush);
        require(!loot.has("functions"), bush + " pot must not tag full drops");
        JsonArray pools = loot.getAsJsonArray("pools");
        require(pools.size() == 2, bush + " pot must drop a pot and bush");
        String[] expectedItems = {"minecraft:flower_pot", "buildscape:" + bush};
        for (int i = 0; i < pools.size(); i++) {
            JsonObject pool = pools.get(i).getAsJsonObject();
            require(pool.get("rolls").getAsInt() == 1, bush + " pot must roll once per item");
            require(!pool.has("functions"), bush + " pot must not tag full drops");
            JsonArray entries = pool.getAsJsonArray("entries");
            require(entries.size() == 1, bush + " pot must have one entry per pool");
            JsonObject entry = entries.get(0).getAsJsonObject();
            require("minecraft:item".equals(entry.get("type").getAsString()), bush + " pot must drop an item");
            require(expectedItems[i].equals(entry.get("name").getAsString()), bush + " pot drops the wrong item");
            require(!entry.has("functions"), bush + " pot must leave tagging to getDrops");
        }
    }

    private static void checkCommonLoot(JsonObject loot, String bush) {
        require("minecraft:block".equals(loot.get("type").getAsString()), bush + " must use block loot");
        String json = loot.toString();
        for (String forbidden : new String[]{"minecraft:copy_state", "minecraft:match_tool", "minecraft:shears", "minecraft:silk_touch"}) {
            require(!json.contains(forbidden), bush + " contains " + forbidden);
        }
    }

    private static JsonObject readResource(String path) {
        InputStream stream = HazeBushResourcesTest.class.getClassLoader().getResourceAsStream(path);
        require(stream != null, "missing resource: " + path);
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException exception) {
            throw new UncheckedIOException("cannot read " + path, exception);
        }
    }

    private static void require(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }
}
