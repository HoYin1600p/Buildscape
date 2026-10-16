package com.kingodogo.buildscape.adapter.v121x;

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
        for (String cluster : List.of("v121x", "v26x")) {
            var expected = new ArrayList<ContentRemovalPolicy.Removal>();
            try (var reader = Files.newBufferedReader(root.resolve("sync/removals" + cluster.substring(1) + ".json"))) {
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
        assertTrue(ContentRemovalPolicy.removals("v118x").isEmpty());
    }

    @Test
    void replacementAliasesCoverExactlyThe121xVanillaReplacements() {
        var expected = new ArrayList<>(ContentRemovalPolicy.V121X.stream()
                .filter(removal -> removal.vanillaEquivalent() != null)
                .map(removal -> new VanillaReplacementAliases.Alias(
                        removal.id(), removal.vanillaEquivalent(), removal.block(), removal.item()))
                .toList());
        expected.add(new VanillaReplacementAliases.Alias("buildscape:mangrove_boat", "minecraft:boat", false, false, true));
        assertEquals(expected, VanillaReplacementAliases.forCluster("v121x"));
        assertSame(VanillaReplacementAliases.ALL, VanillaReplacementAliases.forCluster("v26x"));
        assertTrue(VanillaReplacementAliases.forCluster("v118x").isEmpty());
    }

    @Test
    void contentAddedAfter1211RemainsAvailable() {
        for (String path : List.of("pale_oak_planks", "closed_eyeblossom", "bush",
                "oxidized_copper_rod", "copper_lantern", "copper_chest", "oak_shelf")) {
            assertFalse(ContentRemovalPolicy.removes("v121x", path), path);
            assertTrue(ContentRemovalPolicy.removes("v26x", path), path);
        }
        assertTrue(ContentRemovalPolicy.removes("v121x", "copper_rod"));
        assertTrue(ContentRemovalPolicy.removes("v121x", "buildscape:pink_petal"));
        assertFalse(ContentRemovalPolicy.removes("v118x", "copper_rod"));
    }

    private static Path repositoryRoot() {
        Path root = Path.of("").toAbsolutePath();
        while (root != null && !Files.isRegularFile(root.resolve("sync/removals121x.json"))) {
            root = root.getParent();
        }
        assertNotNull(root, "Cannot find repository removal manifests");
        return root;
    }
}
