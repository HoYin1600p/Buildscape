package com.kingodogo.buildscape.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SulfurCubeArchetypesTest {

    private static final String[] ARCHETYPES = {
            "bouncy",
            "fast_flat",
            "fast_sliding",
            "light",
            "regular",
            "slow_bouncy",
            "slow_flat",
            "sitable"
    };

    @Test
    void testSulfurCubeArchetypeTagsExistAndValid() throws Exception {
        String mainResourcesProp = System.getProperty("buildscape.mainResources");
        Path mainResources = mainResourcesProp != null ? Path.of(mainResourcesProp) : Path.of("common/src/main/resources").toAbsolutePath();
        Path tagsDir = mainResources.resolve("data/minecraft/tags/item/sulfur_cube_archetype");

        assertTrue(Files.isDirectory(tagsDir), "Archetype tags directory must exist: " + tagsDir);

        Set<String> allTaggedItems = new HashSet<>();

        for (String archetype : ARCHETYPES) {
            Path tagFile = tagsDir.resolve(archetype + ".json");
            assertTrue(Files.exists(tagFile), "Tag file must exist: " + tagFile);

            try (Reader reader = Files.newBufferedReader(tagFile)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                assertFalse(json.has("replace") && json.get("replace").getAsBoolean(), "replace should be false or omitted for merging");

                JsonArray values = json.getAsJsonArray("values");
                assertNotNull(values, "values array must not be null in " + tagFile);
                assertFalse(values.isEmpty(), "values array must not be empty in " + tagFile);

                for (JsonElement elem : values) {
                    String itemId = elem.getAsString();
                    assertTrue(itemId.startsWith("buildscape:"), "Item ID must start with buildscape: " + itemId);
                    String simpleName = itemId.substring("buildscape:".length());

                    // Verify item definition json exists
                    Path itemDef = mainResources.resolve("assets/buildscape/items/" + simpleName + ".json");
                    assertTrue(Files.exists(itemDef), "Item definition must exist for " + itemId + " at " + itemDef);

                    boolean added = allTaggedItems.add(itemId);
                    assertTrue(added, "Item should not be duplicated across archetypes: " + itemId);

                    if ("sitable".equals(archetype)) {
                        assertTrue(itemId.contains("cushion"), "Sitable archetype must only contain cushions: " + itemId);
                    } else {
                        assertFalse(itemId.contains("cushion"), "Cushions must only be in sitable archetype: " + itemId);
                    }
                }
            }
        }

        assertEquals(413, allTaggedItems.size(), "Expected exactly 413 blocks to be tagged across archetypes");

        // Verify specified exclusions are absent
        for (String item : allTaggedItems) {
            assertFalse(item.contains("hollow"), "Hollow logs/stems should not be present: " + item);
            assertFalse(item.contains("glass"), "Glass blocks should not be present: " + item);
            assertNotEquals("buildscape:builders_workbench", item, "Builders workbench should not be present");
            assertNotEquals("buildscape:muff_block", item, "Muff block should not be present");
            if (item.contains("pillar")) {
                assertTrue(
                        item.contains("steel_pillar") || item.equals("buildscape:bone_pillar"),
                        "Only solid cube-column pillars should be present: " + item
                );
            }
        }
    }
}
