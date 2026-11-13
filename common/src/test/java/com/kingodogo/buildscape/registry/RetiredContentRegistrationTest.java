package com.kingodogo.buildscape.registry;

import com.google.gson.JsonParser;
import com.kingodogo.buildscape.block.ModBlocks;
import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.entity.ModEntities;
import com.kingodogo.buildscape.platform.Services;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RetiredContentRegistrationTest {
    private static Path repositoryRoot() {
        for (Path path = Path.of("").toAbsolutePath(); path != null; path = path.getParent()) {
            if (Files.isRegularFile(path.resolve("sync/26.2/removals.json"))) return path;
        }
        throw new IllegalStateException("Cannot locate sync/26.2/removals.json");
    }

    private static List<String> ids(String source, String expression) {
        var matcher = Pattern.compile(expression).matcher(source);
        var result = new ArrayList<String>();
        while (matcher.find()) result.add(matcher.group(1));
        return result;
    }

    @Test
    void gatedRegistrationsMatchTheRetirementManifestAndReferenceOrder() throws Exception {
        Path root = repositoryRoot();
        var manifest = JsonParser.parseString(Files.readString(root.resolve("sync/26.2/removals.json")))
                .getAsJsonObject().getAsJsonArray("removed_ids");
        for (String kind : List.of("block", "item")) {
            var expected = new LinkedHashSet<String>();
            for (var entry : manifest) {
                var object = entry.getAsJsonObject();
                if (object.get("kind").getAsString().contains(kind)) {
                    assertTrue(expected.add(object.get("id").getAsString().substring("buildscape:".length())));
                }
            }
            String className = kind.equals("block") ? "ModBlocks" : "ModItems";
            String sourcePath = "java/com/kingodogo/buildscape/" + kind + "/" + className + ".java";
            String source = Files.readString(root.resolve("common/src/main/" + sourcePath));
            String gate = kind.equals("block") ? "registerLegacyBlock" : "registerLegacy";
            List<String> actual = ids(source, "\\b" + gate + "\\(\\\"([^\\\"]+)\\\"");
            assertEquals(expected, new LinkedHashSet<>(actual), kind + " registrations differ from the removal policy");
            assertEquals(expected.size(), actual.size(), kind + " registrations contain duplicates");
            assertTrue(source.contains("if (!Services.isVersionCluster(\"minecraft1182\") && (!Services.isVersionCluster(\"minecraft1211\")"));
            assertTrue(source.contains("ContentRemovalPolicy.removes(\"minecraft1211\", " + (kind.equals("block") ? "id" : "name") + ")"));
        }
    }

    @Test
    void supportingTypesAndCreativeEntriesMatchTheReference() throws Exception {
        Path root = repositoryRoot();
        List<String> blockEntityIds = List.of("mangrove_sign_block_entity", "bamboo_sign_block_entity",
                "copper_chest", "potent_sulfur");
        String registry = Files.readString(root.resolve(
                "common/src/main/java/com/kingodogo/buildscape/registry/BaseRegistryAdapter.java"));
        assertEquals(blockEntityIds, ids(registry, "registerClusterBlockEntity\\(\\\"([^\\\"]+)\\\""));

        String entities = Files.readString(root.resolve(
                "common/src/main/java/com/kingodogo/buildscape/entity/ModEntities.java"));
        assertEquals(List.of("mangrove_boat"), ids(entities, "registerLegacy\\(\\\"([^\\\"]+)\\\""));
        assertTrue(entities.indexOf("registerLegacy(\"mangrove_boat\")")
                < entities.indexOf("register(\"colored_item_frame\")"));

        String tabs = Files.readString(root.resolve(
                "common/src/main/java/com/kingodogo/buildscape/item/ModCreativeTabs.java"));
        String orderMethod = tabs.substring(tabs.indexOf("private static List<String> legacyItemOrder()"),
                tabs.indexOf("private static List<String> loadOrderedItemIds()"));
        List<String> order = ids(orderMethod, "\\\"([a-z0-9_]+)\\\"");
        assertFalse(order.isEmpty(), "legacyItemOrder should not be empty");
        assertEquals(new LinkedHashSet<>(order).size(), order.size(), "legacyItemOrder contains duplicates");
        assertTrue(tabs.contains("if (Services.isVersionCluster(\"minecraft1182\")) return legacyItemOrder();"));
    }

    @Test
    void retiredIdsAreAbsentFromRegistrationQueues() throws Exception {
        assertFalse(Services.isVersionCluster("minecraft1182"));
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        Path root = repositoryRoot();
        var manifest = JsonParser.parseString(Files.readString(root.resolve("sync/26.2/removals.json")))
                .getAsJsonObject().getAsJsonArray("removed_ids");
        var registered = new LinkedHashSet<String>();
        Services.REGISTERED_BLOCKS.forEach(entry -> registered.add(entry.getId().toString()));
        Services.REGISTERED_ITEMS.forEach(entry -> registered.add(entry.getId().toString()));
        for (var entry : manifest) {
            String id = entry.getAsJsonObject().get("id").getAsString();
            assertFalse(registered.contains(id), id + " must stay retired on minecraft262");
        }
        assertTrue(Services.REGISTERED_ENTITIES.stream().noneMatch(entry -> entry.getId().equals("buildscape:mangrove_boat")));
    }
}
