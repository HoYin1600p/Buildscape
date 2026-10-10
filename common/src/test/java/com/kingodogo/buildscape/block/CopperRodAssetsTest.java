package com.kingodogo.buildscape.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CopperRodAssetsTest {
    private static final Path ASSETS = Path.of(System.getProperty("buildscape.mainResources"))
            .resolve("assets/buildscape");

    private static JsonObject json(String path) throws Exception {
        return JsonParser.parseString(Files.readString(ASSETS.resolve(path))).getAsJsonObject();
    }

    private static JsonObject model(String id) throws Exception {
        assertTrue(id.startsWith("buildscape:"), id);
        return json("models/" + id.substring("buildscape:".length()) + ".json");
    }

    @Test void allOxidationAndWaxStatesAndItemsResolveToExistingRodTextures() throws Exception {
        for (String wax : new String[]{"", "waxed_"}) {
            for (String stage : new String[]{"", "exposed_", "weathered_", "oxidized_"}) {
                String id = wax + stage + "copper_rod";
                JsonObject variants = json("blockstates/" + id + ".json").getAsJsonObject("variants");
                assertEquals(12, variants.size(), id);
                for (String direction : new String[]{"up", "down", "north", "south", "east", "west"}) {
                    for (boolean powered : new boolean[]{false, true}) {
                        String modelId = variants.getAsJsonObject("facing=" + direction + ",powered=" + powered)
                                .get("model").getAsString();
                        assertTexture(modelId, stage + "copper_rod" + (powered ? "_on" : ""));
                    }
                }
                JsonObject item = json("items/" + id + ".json").getAsJsonObject("model");
                assertEquals("minecraft:model", item.get("type").getAsString());
                assertTexture(item.get("model").getAsString(), stage + "copper_rod");
            }
        }
    }

    private static void assertTexture(String id, String expected) throws Exception {
        String texture = null;
        for (int depth = 0; depth < 8; depth++) {
            JsonObject object = model(id);
            if (texture == null && object.has("textures") && object.getAsJsonObject("textures").has("texture")) {
                texture = object.getAsJsonObject("textures").get("texture").getAsString();
            }
            String parent = object.get("parent").getAsString();
            if (parent.startsWith("minecraft:")) {
                assertEquals("minecraft:block/template_lightning_rod", parent);
                assertEquals("buildscape:block/" + expected, texture);
                assertTrue(Files.isRegularFile(ASSETS.resolve("textures/block/" + expected + ".png")), expected);
                return;
            }
            id = parent;
        }
        fail("Model parent cycle: " + id);
    }
}
