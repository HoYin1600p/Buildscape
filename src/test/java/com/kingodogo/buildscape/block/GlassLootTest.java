package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class GlassLootTest {
    private static final Path DIRECTORY = Path.of("src/main/resources/data/buildscape/loot_tables/blocks");

    @Test
    public void glassShapesDropLikeTheirBaseBlocks() throws Exception {
        List<Path> shapes;
        try (var paths = Files.list(DIRECTORY)) {
            shapes = paths.filter(path -> path.getFileName().toString()
                    .matches(".*glass_(vertical_slab|slab|stairs|pane)\\.json")).sorted().toList();
        }
        assertEquals("All glass families: 66 sets of three shapes and 48 panes", 246, shapes.size());
        for (Path path : shapes) {
            String id = path.getFileName().toString().replace(".json", "");
            String base = id.replaceFirst("_(vertical_slab|slab|stairs|pane)$", "");
            JsonObject table = load(id);
            for (boolean silk : new boolean[]{false, true}) {
                int expectedBaseCount = baseCount(base, silk);
                for (String type : List.of("bottom", "top", "double")) {
                    int halves = id.endsWith("_slab") && "double".equals(type) ? 2 : 1;
                    assertEquals(id + " " + type + " silk=" + silk,
                            expectedBaseCount * halves, drops(table, id, silk, type));
                }
            }
        }
    }

    @Test
    public void vanillaEquivalentGlassSlabsRequireSilkTouch() throws Exception {
        List<String> colors = List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
                "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
        var bases = new java.util.ArrayList<String>();
        bases.add("glass");
        for (String color : colors) {
            bases.add(color + "_stained_glass");
        }
        for (String base : bases) {
            for (String suffix : List.of("_slab", "_vertical_slab")) {
                String id = base + suffix;
                JsonObject table = load(id);
                assertEquals(id, 0, drops(table, id, false, "bottom"));
                assertEquals(id, 0, drops(table, id, false, "double"));
                assertEquals(id, 1, drops(table, id, true, "bottom"));
                assertEquals(id, 2, drops(table, id, true, "double"));
            }
        }
    }

    @Test
    public void glazedGlassNeedsSilkTouchInEveryShape() throws Exception {
        List<String> colors = List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
                "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black");
        for (String color : colors) {
            String base = color + "_glazed_glass";
            for (String suffix : List.of("", "_pane", "_stairs", "_slab", "_vertical_slab")) {
                String id = base + suffix;
                JsonObject table = load(id);
                boolean slab = suffix.endsWith("slab");
                for (String type : List.of("bottom", "top", "double")) {
                    assertEquals(id + " " + type + " without Silk Touch", 0, drops(table, id, false, type));
                    int expected = slab && "double".equals(type) ? 2 : 1;
                    assertEquals(id + " " + type + " with Silk Touch", expected, drops(table, id, true, type));
                }
            }
        }
    }

    private int baseCount(String id, boolean silk) throws Exception {
        if (Files.exists(DIRECTORY.resolve(id + ".json"))) {
            return drops(load(id), id, silk, "bottom");
        }
        // These full blocks belong to vanilla rather than BuildScape.
        assertTrue(id, id.equals("glass") || id.endsWith("_stained_glass") || id.equals("tinted_glass"));
        return id.equals("tinted_glass") || silk ? 1 : 0;
    }

    private JsonObject load(String id) throws Exception {
        JsonObject table = JsonParser.parseString(Files.readString(DIRECTORY.resolve(id + ".json"),
                StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals(id, "minecraft:block", table.get("type").getAsString());
        return table;
    }

    private int drops(JsonObject table, String id, boolean silk, String type) {
        int count = 0;
        for (JsonElement element : table.getAsJsonArray("pools")) {
            JsonObject pool = element.getAsJsonObject();
            assertEquals(id, 1, pool.get("rolls").getAsInt());
            if (!matches(pool, silk, type)) {
                continue;
            }
            for (JsonElement entry : pool.getAsJsonArray("entries")) {
                count += entryCount(entry.getAsJsonObject(), id, silk, type);
            }
        }
        return count;
    }

    private int entryCount(JsonObject entry, String id, boolean silk, String type) {
        if (!matches(entry, silk, type)) {
            return 0;
        }
        String entryType = entry.get("type").getAsString();
        if ("minecraft:empty".equals(entryType)) {
            return 0;
        }
        if ("minecraft:alternatives".equals(entryType)) {
            for (JsonElement element : entry.getAsJsonArray("children")) {
                JsonObject child = element.getAsJsonObject();
                if (matches(child, silk, type)) {
                    return entryCount(child, id, silk, type);
                }
            }
            return 0;
        }
        assertEquals(id, "minecraft:item", entryType);
        assertEquals("buildscape:" + id, entry.get("name").getAsString());
        int count = 1;
        if (entry.has("functions")) {
            for (JsonElement element : entry.getAsJsonArray("functions")) {
                JsonObject function = element.getAsJsonObject();
                if ("minecraft:explosion_decay".equals(function.get("function").getAsString())) {
                    continue;
                }
                assertEquals(id, "minecraft:set_count", function.get("function").getAsString());
                if (matches(function, silk, type)) {
                    int value = function.get("count").getAsInt();
                    count = function.has("add") && function.get("add").getAsBoolean() ? count + value : value;
                }
            }
        }
        return count;
    }

    private boolean matches(JsonObject owner, boolean silk, String type) {
        if (!owner.has("conditions")) {
            return true;
        }
        for (JsonElement element : owner.getAsJsonArray("conditions")) {
            JsonObject condition = element.getAsJsonObject();
            switch (condition.get("condition").getAsString()) {
                case "minecraft:survives_explosion" -> {
                    /* Ordinary block breaking has no explosion. */ }
                case "minecraft:block_state_property" -> {
                    if (!type.equals(condition.getAsJsonObject("properties").get("type").getAsString())) {
                        return false;
                    }
                }
                case "minecraft:match_tool" -> {
                    for (JsonElement enchantmentElement : condition.getAsJsonObject("predicate")
                            .getAsJsonArray("enchantments")) {
                        JsonObject enchantment = enchantmentElement.getAsJsonObject();
                        assertEquals("minecraft:silk_touch", enchantment.get("enchantment").getAsString());
                        assertEquals(1, enchantment.getAsJsonObject("levels").get("min").getAsInt());
                        if (!silk) {
                            return false;
                        }
                    }
                }
                default -> fail("Unsupported condition: " + condition);
            }
        }
        return true;
    }
}
