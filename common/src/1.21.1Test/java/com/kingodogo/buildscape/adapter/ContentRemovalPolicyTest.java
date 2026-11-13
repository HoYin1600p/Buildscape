package com.kingodogo.buildscape.adapter;

import com.google.gson.JsonParser;
import com.kingodogo.buildscape.registry.ContentRemovalPolicy;
import com.kingodogo.buildscape.registry.VanillaReplacementAliases;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Prevents a later vanilla release's removal policy from leaking into 1.21.1. */
class ContentRemovalPolicyTest {
    @Test
    void codeTablesMatchTheOwnerManifestsExactly() throws IOException {
        Path root = repositoryRoot();
        for (String cluster : List.of("minecraft1211", "minecraft262")) {
            var expected = new ArrayList<ContentRemovalPolicy.Removal>();
            String version = cluster.equals("minecraft1211") ? "1.21.1" : "26.2";
            try (var reader = Files.newBufferedReader(root.resolve("sync/" + version + "/removals.json"))) {
                for (var element : JsonParser.parseReader(reader).getAsJsonObject().getAsJsonArray("removed_ids")) {
                    var entry = element.getAsJsonObject();
                    var target = entry.get("vanilla_equivalent");
                    expected.add(new ContentRemovalPolicy.Removal(
                            entry.get("id").getAsString(), entry.get("kind").getAsString(),
                            target == null || target.isJsonNull() ? null : target.getAsString()));
                }
            }
            assertEquals(expected, ContentRemovalPolicy.removals(cluster), cluster);
            assertEquals(expected.size(), new HashSet<>(expected.stream()
                    .map(ContentRemovalPolicy.Removal::id).toList()).size(), "Duplicate removal IDs");
        }
        assertTrue(ContentRemovalPolicy.removals("minecraft1182").isEmpty());
    }

    @Test
    void replacementAliasesCoverExactlyTheVanillaReplacements() {
        var expected = new ArrayList<>(ContentRemovalPolicy.MINECRAFT_1211.stream()
                .filter(removal -> removal.vanillaEquivalent() != null)
                .map(removal -> new VanillaReplacementAliases.Alias(
                        removal.id(), removal.vanillaEquivalent(), removal.block(), removal.item()))
                .toList());
        expected.add(new VanillaReplacementAliases.Alias("buildscape:mangrove_boat", "minecraft:boat", false, false, true));
        assertEquals(expected, VanillaReplacementAliases.forCluster("minecraft1211"));
        assertSame(VanillaReplacementAliases.ALL, VanillaReplacementAliases.forCluster("minecraft262"));
        assertTrue(VanillaReplacementAliases.forCluster("minecraft1182").isEmpty());
    }

    @Test
    void contentAddedAfter1211RemainsAvailable() {
        for (String path : List.of("pale_oak_planks", "closed_eyeblossom", "bush",
                "oxidized_copper_rod", "copper_lantern", "copper_chest", "oak_shelf")) {
            assertFalse(ContentRemovalPolicy.removes("minecraft1211", path), path);
            assertTrue(ContentRemovalPolicy.removes("minecraft262", path), path);
        }
        assertTrue(ContentRemovalPolicy.removes("minecraft1211", "copper_rod"));
        assertTrue(ContentRemovalPolicy.removes("minecraft1211", "buildscape:pink_petal"));
        assertFalse(ContentRemovalPolicy.removes("minecraft1182", "copper_rod"));
    }

    private static Path repositoryRoot() {
        Path root = Path.of("").toAbsolutePath();
        while (root != null && !Files.isRegularFile(root.resolve("sync/1.21.1/removals.json"))) {
            root = root.getParent();
        }
        assertNotNull(root, "Cannot find repository removal manifests");
        return root;
    }
}
