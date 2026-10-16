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
            if (Files.isRegularFile(path.resolve("sync/removals26x.json"))) return path;
        }
        throw new IllegalStateException("Cannot locate sync/removals26x.json");
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
        var manifest = JsonParser.parseString(Files.readString(root.resolve("sync/removals26x.json")))
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
            String gate = kind.equals("block") ? "regV118x" : "registerV118x";
            List<String> actual = ids(source, "\\b" + gate + "\\(\\\"([^\\\"]+)\\\"");
            assertEquals(expected, new LinkedHashSet<>(actual), kind + " registrations differ from removals26x.json");
            assertEquals(expected.size(), actual.size(), kind + " registrations contain duplicates");
            assertTrue(source.contains("if (!Services.isVersionCluster(\"v118x\") && (!Services.isVersionCluster(\"v121x\")"));
            assertTrue(source.contains("ContentRemovalPolicy.removes(\"v121x\", " + (kind.equals("block") ? "id" : "name") + ")"));
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
        assertEquals(List.of("mangrove_boat"), ids(entities, "registerV118x\\(\\\"([^\\\"]+)\\\""));
        assertTrue(entities.indexOf("registerV118x(\"mangrove_boat\")")
                < entities.indexOf("register(\"colored_item_frame\")"));

        String tabs = Files.readString(root.resolve(
                "common/src/main/java/com/kingodogo/buildscape/item/ModCreativeTabs.java"));
        String orderMethod = tabs.substring(tabs.indexOf("private static List<String> v118xItemOrder()"),
                tabs.indexOf("private static List<String> loadOrderedItemIds()"));
        List<String> order = ids(orderMethod, "\\\"([a-z0-9_]+)\\\"");
        assertFalse(order.isEmpty(), "v118xItemOrder should not be empty");
        assertEquals(new LinkedHashSet<>(order).size(), order.size(), "v118xItemOrder contains duplicates");
        assertTrue(tabs.contains("if (Services.isVersionCluster(\"v118x\")) return v118xItemOrder();"));
    }

    @Test
    void retiredIdsAreAbsentFromThe26xRegistrationQueues() throws Exception {
        assertFalse(Services.isVersionCluster("v118x"));
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        Path root = repositoryRoot();
        var manifest = JsonParser.parseString(Files.readString(root.resolve("sync/removals26x.json")))
                .getAsJsonObject().getAsJsonArray("removed_ids");
        var registered = new LinkedHashSet<String>();
        Services.REGISTERED_BLOCKS.forEach(entry -> registered.add(entry.getId().toString()));
        Services.REGISTERED_ITEMS.forEach(entry -> registered.add(entry.getId().toString()));
        for (var entry : manifest) {
            String id = entry.getAsJsonObject().get("id").getAsString();
            assertFalse(registered.contains(id), id + " must stay retired on v26x");
        }
        assertTrue(Services.REGISTERED_ENTITIES.stream().noneMatch(entry -> entry.getId().equals("buildscape:mangrove_boat")));
    }
}
