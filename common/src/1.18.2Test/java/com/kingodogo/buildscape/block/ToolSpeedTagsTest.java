package com.kingodogo.buildscape.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tool bonuses come from vanilla tool tags in every cluster, not from custom break-speed code. */
class ToolSpeedTagsTest {
    private static final List<String> TAG_TREES = List.of("v118x/resources/data/%s/tags/blocks/%s.json",
            "v121x/resources/data/%s/tags/block/%s.json", "main/resources/data/%s/tags/block/%s.json");

    @Test
    void shearsCutHedgesAndLeafLayersAtLeafSpeedKeepingEfficiency() {
        assertEquals(15.0F, LeafMiningSpeed.scale(1.0F, 1.0F, 0), 1.0E-6F);
        assertEquals(25.0F, LeafMiningSpeed.scale(1.0F, 1.0F, 3), 1.0E-6F);
        // Haste and other multipliers already in the speed are kept.
        assertEquals(30.0F, LeafMiningSpeed.scale(2.0F, 1.0F, 0), 1.0E-6F);
        // Shears already at leaf speed are unchanged.
        assertEquals(4.0F, LeafMiningSpeed.scale(4.0F, 15.0F, 0), 1.0E-6F);
        assertEquals(4.0F, LeafMiningSpeed.scale(4.0F, 15.0F, 3), 1.0E-6F);
        assertEquals(4.0F, LeafMiningSpeed.scale(4.0F, 0.0F, 0), 1.0E-6F);
    }

    @Test
    void everyClusterTagsHoeShovelAndWoolBlocksForTheirTools() throws Exception {
        for (String tree : TAG_TREES) {
            Set<String> hoe = values(tree, "minecraft", "mineable/hoe");
            assertTrue(hoe.containsAll(Set.of("#buildscape:hedges", "#buildscape:leaf_layers",
                    "buildscape:moss_block_slab", "buildscape:moss_block_vertical_slab")), tree);
            Set<String> shovel = values(tree, "minecraft", "mineable/shovel");
            assertTrue(shovel.containsAll(Set.of("buildscape:dirt_slab", "buildscape:snow_slab",
                    "buildscape:snow_bricks_vertical_slab", "buildscape:snowy_grass_block_slab")), tree);
            Set<String> wool = values(tree, "minecraft", "wool");
            assertTrue(wool.containsAll(Set.of("buildscape:black_wool_slab", "buildscape:white_layered_wool_vertical_slab")),
                    tree);
            assertFalse(wool.stream().anyMatch(id -> id.endsWith("_carpet_layers")),
                    "carpet layers break like vanilla carpet, not wool: " + tree);
            assertEquals(Set.of("#buildscape:hedges", "#buildscape:leaf_layers"),
                    values(tree, "buildscape", "shears_mineable"), tree);
        }
    }

    @Test
    void theOldCustomBreakSpeedCodeIsGone() throws Exception {
        Path blocks = common().resolve("src/main/java/com/kingodogo/buildscape/block");
        for (String type : new String[]{"FallingSandBlock", "LeafHedgeBlock", "LeafLayersBlock", "ModBlock",
                "ModStairBlock", "ModWallBlock", "MossLayersBlock", "MossOverlayBlock", "SilkTouchOnlyGlassBlock",
                "SilkTouchOnlyPaneBlock", "CopperBulbBlock"}) {
            assertFalse(Files.readString(blocks.resolve(type + ".java")).contains("getDestroyProgress"), type);
        }
    }

    @Test
    void everyLoaderGivesShearsLeafSpeed() throws Exception {
        Path repo = common().getParent();
        for (String module : new String[]{"forge/1.18.2", "forge/1.21.1", "forge/26.2", "forge/26.3",
                "neoforge/1.21.1", "neoforge/26.2", "neoforge/26.3"}) {
            String loader = module.startsWith("neoforge") ? "NeoForge" : "Forge";
            String events = Files.readString(repo.resolve(module
                    + "/src/main/java/com/kingodogo/buildscape/event/" + loader + "GameplayEvents.java"));
            assertTrue(events.contains("PlayerEvent.BreakSpeed") && events.contains("LeafMiningSpeed.adjust("), module);
        }
        for (String module : new String[]{"fabric/1.18.2", "fabric/1.21.1", "fabric/26.2", "fabric/26.3"}) {
            Path resources = repo.resolve(module + "/src/main/resources");
            boolean listed = false;
            try (var configs = Files.list(resources)) {
                for (Path config : (Iterable<Path>) configs.filter(p -> p.toString().endsWith(".mixins.json"))::iterator) {
                    listed |= Files.readString(config).contains("LeafShearsSpeedMixin\"");
                }
            }
            assertTrue(listed, module);
            assertTrue(Files.isRegularFile(repo.resolve(module
                    + "/src/main/java/com/kingodogo/buildscape/mixin/fabric/LeafShearsSpeedMixin.java")), module);
        }
    }

    private static Set<String> values(String tree, String namespace, String tag) throws Exception {
        Path path = common().resolve("src").resolve(String.format(tree, namespace, tag));
        JsonArray array = JsonParser.parseString(Files.readString(path)).getAsJsonObject().getAsJsonArray("values");
        Set<String> values = new HashSet<>();
        for (JsonElement element : array) {
            values.add(element.getAsString());
        }
        return values;
    }

    private static Path common() {
        return Path.of(System.getProperty("buildscape.v118xMixinConfig", "src/1.18.2/resources/buildscape.mixins.json"))
                .toAbsolutePath().getParent().getParent().getParent().getParent();
    }
}
