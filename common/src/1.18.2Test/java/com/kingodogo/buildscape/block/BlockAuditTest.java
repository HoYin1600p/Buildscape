package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 4.1.0 block audit: double slabs drop two, plants break without support, placement uses the target block. */
class BlockAuditTest {
    private static final List<String> LOOT_TREES = List.of("src/1.18.2/resources/data/buildscape/loot_tables/blocks",
            "src/1.21.1/resources/data/buildscape/loot_table/blocks", "src/main/resources/data/buildscape/loot_table/blocks");
    private static final List<String> FACTORIES = List.of(
            "src/1.18.2/java/com/kingodogo/buildscape/adapter/BlockFactory.java",
            "src/1.21.1/java/com/kingodogo/buildscape/adapter/BlockFactory.java",
            "src/26.2/java/com/kingodogo/buildscape/adapter/BlockFactory.java",
            "src/26.3/java/com/kingodogo/buildscape/adapter/BlockFactory.java");

    @Test
    void everySlabLootTableDropsTwoForADoubleSlab() throws Exception {
        for (String tree : LOOT_TREES) {
            Path dir = common().resolve(tree);
            int checked = 0;
            try (Stream<Path> files = Files.list(dir)) {
                for (Path file : (Iterable<Path>) files.filter(p -> p.getFileName().toString().matches(
                        ".*_(vertical_)?slab\\.json"))::iterator) {
                    String id = "buildscape:" + file.getFileName().toString().replace(".json", "");
                    if (!dropsItself(file, id)) {
                        continue;
                    }
                    assertTrue(Files.readString(file).contains("\"double\""), file.toString());
                    checked++;
                }
            }
            assertTrue(checked > 100, tree + " checked only " + checked);
        }
    }

    @Test
    void plantsCheckTheirSupportWhenTheBlockBelowChanges() throws Exception {
        for (String factory : FACTORIES) {
            String source = Files.readString(common().resolve(factory));
            assertFalse(source.contains("if (direction == Direction.DOWN) return adjusted;"), factory);
            assertFalse(source.contains("direction != Direction.DOWN && !state.canSurvive"), factory);
            assertFalse(source.contains("if (direction == Direction.DOWN) return onUpdateShape("), factory);
        }
    }

    @Test
    void placementChecksTheBlockBeingPlacedNotTheOneBeyondIt() throws Exception {
        for (String file : new String[]{"src/main/java/com/kingodogo/buildscape/block/OrnamentBlock.java",
                "src/main/java/com/kingodogo/buildscape/block/StarBlock.java",
                "src/main/java/com/kingodogo/buildscape/block/MulticolorGlowLightsBlock.java",
                "src/1.18.2/java/com/kingodogo/buildscape/block/SulfurSpikeLogic.java",
                "src/1.21.1/java/com/kingodogo/buildscape/block/SulfurSpikeLogic.java"}) {
            String source = Files.readString(common().resolve(file));
            assertFalse(source.contains("clickedPos.relative(clickedFace)"), file);
            assertFalse(source.contains("getClickedPos().relative(context.getClickedFace())"), file);
        }
    }

    @Test
    void doubleSlabCountIsConditionedOnTheSlabsOwnState() throws Exception {
        Path file = common().resolve(LOOT_TREES.get(0)).resolve("oak_wood_vertical_slab.json");
        JsonObject entry = JsonParser.parseString(Files.readString(file)).getAsJsonObject().getAsJsonArray("pools")
                .get(0).getAsJsonObject().getAsJsonArray("entries").get(0).getAsJsonObject();
        JsonObject count = entry.getAsJsonArray("functions").get(0).getAsJsonObject();
        assertEquals("minecraft:set_count", count.get("function").getAsString());
        assertEquals(2, count.get("count").getAsInt());
        JsonObject condition = count.getAsJsonArray("conditions").get(0).getAsJsonObject();
        assertEquals("buildscape:oak_wood_vertical_slab", condition.get("block").getAsString());
        assertEquals("double", condition.getAsJsonObject("properties").get("type").getAsString());
    }

    private static boolean dropsItself(Path file, String id) throws Exception {
        JsonObject table = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
        if (!table.has("pools")) {
            return false;
        }
        for (JsonElement pool : table.getAsJsonArray("pools")) {
            for (JsonElement entry : pool.getAsJsonObject().getAsJsonArray("entries")) {
                JsonObject object = entry.getAsJsonObject();
                if (object.has("name") && id.equals(object.get("name").getAsString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Path common() {
        return Path.of(System.getProperty("buildscape.minecraft1182MixinConfig", "src/1.18.2/resources/buildscape.mixins.json"))
                .toAbsolutePath().getParent().getParent().getParent().getParent();
    }
}
