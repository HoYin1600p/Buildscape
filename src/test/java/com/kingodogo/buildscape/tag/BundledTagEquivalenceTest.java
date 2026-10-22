package com.kingodogo.buildscape.tag;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.adapter.TagAdapter;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BundledTagEquivalenceTest {

    private static final byte[] DIGEST = new byte[32];

    @BeforeEach
    void setup() {
        BundledResourceRegistry.getInstance().reset();
    }

    private static final class MockPack implements PackResources {
        private final String name;
        private final Map<ResourceLocation, String> jsonResources = new HashMap<>();

        MockPack(String name) {
            this.name = name;
        }

        MockPack withJson(ResourceLocation loc, String json) {
            jsonResources.put(loc, json);
            return this;
        }

        @Override
        public String getName() {
            return name;
        }
        @Override
        public InputStream getRootResource(String s) {
            return null;
        }
        @Override
        public InputStream getResource(PackType packType, ResourceLocation location) {
            String json = jsonResources.get(location);
            if (json != null) {
                return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        }
        @Override
        public java.util.Collection<ResourceLocation> getResources(PackType packType, String s, String s1, int i,
                java.util.function.Predicate<String> predicate) {
            return List.of();
        }
        @Override
        public boolean hasResource(PackType packType, ResourceLocation location) {
            if ("buildscape".equals(name)
                    && ResourcePackPrecedenceTracker.SERVER_DATA_BUNDLE_LOCATION.equals(location)) {
                return true;
            }
            return jsonResources.containsKey(location);
        }
        @Override
        public java.util.Set<String> getNamespaces(PackType packType) {
            java.util.Set<String> namespaces = new java.util.HashSet<>();
            jsonResources.keySet().forEach(location -> namespaces.add(location.getNamespace()));
            if ("buildscape".equals(name)) {
                namespaces.add("buildscape");
            }
            return namespaces;
        }
        @Override
        public <T> T getMetadataSection(net.minecraft.server.packs.metadata.MetadataSectionSerializer<T> serializer) {
            return null;
        }
        @Override
        public void close() {}
    }

    private static final class MockResourceManager implements ResourceManager {
        private final List<PackResources> packs;
        MockResourceManager(List<PackResources> packs) {
            this.packs = packs;
        }
        @Override
        public java.util.Set<String> getNamespaces() {
            return java.util.Set.of();
        }
        @Override
        public boolean hasResource(ResourceLocation location) {
            return false;
        }
        @Override
        public net.minecraft.server.packs.resources.Resource getResource(ResourceLocation location) {
            return null;
        }
        @Override
        public java.util.List<net.minecraft.server.packs.resources.Resource> getResources(ResourceLocation location) {
            return List.of();
        }
        @Override
        public java.util.Collection<ResourceLocation> listResources(String s,
                java.util.function.Predicate<String> predicate) {
            return List.of();
        }
        @Override
        public Stream<PackResources> listPacks() {
            return packs.stream();
        }
    }

    @Test
    void missingBundleOwnerMustNotInjectBundledTags() throws IOException {
        var writer = new ResourceBundleContainer.Writer();
        writer.addTag(new TagIR("blocks", "buildscape:disabled_pack_tag", false,
                List.of(new TagIR.TagEntryIR("minecraft:stone", false, true))));
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));
        Map<ResourceLocation, Tag.Builder> tags = new HashMap<>();
        var manager = new MockResourceManager(List.of(new MockPack("vanilla")));

        TagAdapter.applyTags("tags/blocks", tags, manager);

        assertFalse(tags.containsKey(new ResourceLocation("buildscape:disabled_pack_tag")),
                "An inactive bundle-owning pack must not contribute tags after reload");
    }

    @Test
    void testLowerPackEntriesSurviveWhenBundleReplaceFalse() throws IOException {
        TagIR bundleTag = new TagIR(
                "blocks",
                "buildscape:test_tag",
                false, // replace = false
                List.of(new TagIR.TagEntryIR("buildscape:bundled_block", false, true)));

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addTag(bundleTag);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        ResourceLocation fileLoc = new ResourceLocation("buildscape", "tags/blocks/test_tag.json");
        PackResources lowerPack = new MockPack("vanilla")
                .withJson(fileLoc, "{\"replace\": false, \"values\": [\"minecraft:stone\"]}");
        PackResources modPack = new MockPack("buildscape");
        PackResources higherPack = new MockPack("user_pack");
        ResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack, higherPack));

        Map<ResourceLocation, Tag.Builder> tagMap = new HashMap<>();

        TagAdapter.applyTags("tags/blocks", tagMap, rm);

        List<Tag.BuilderEntry> entries = tagMap.get(new ResourceLocation("buildscape:test_tag")).getEntries().toList();
        assertEquals(2, entries.size());
        assertEquals("vanilla", entries.get(0).source());
        assertEquals("buildscape", entries.get(1).source());
    }

    @Test
    void testLowerPackEntriesClearedWhenBundleReplaceTrue() throws IOException {
        TagIR bundleTag = new TagIR(
                "blocks",
                "buildscape:test_replace",
                true, // replace = true
                List.of(new TagIR.TagEntryIR("buildscape:replacement_block", false, true)));

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addTag(bundleTag);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        ResourceLocation fileLoc = new ResourceLocation("buildscape", "tags/blocks/test_replace.json");
        PackResources lowerPack = new MockPack("vanilla")
                .withJson(fileLoc, "{\"replace\": false, \"values\": [\"minecraft:cobblestone\"]}");
        PackResources modPack = new MockPack("buildscape");
        ResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack));

        Map<ResourceLocation, Tag.Builder> tagMap = new HashMap<>();

        TagAdapter.applyTags("tags/blocks", tagMap, rm);

        List<Tag.BuilderEntry> entries = tagMap.get(new ResourceLocation("buildscape:test_replace")).getEntries()
                .toList();
        assertEquals(1, entries.size(), "Lower pack entries must be cleared by replace=true");
        assertEquals("buildscape", entries.get(0).source());
    }

    @Test
    void testHigherPackEntriesSurviveWhenBundleReplaceTrue() throws IOException {
        TagIR bundleTag = new TagIR(
                "blocks",
                "buildscape:stacked_tag",
                true, // replace = true
                List.of(new TagIR.TagEntryIR("buildscape:middle_block", false, true)));

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addTag(bundleTag);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        ResourceLocation fileLoc = new ResourceLocation("buildscape", "tags/blocks/stacked_tag.json");
        PackResources lowerPack = new MockPack("vanilla")
                .withJson(fileLoc, "{\"replace\": false, \"values\": [\"minecraft:lower_stone\"]}");
        PackResources modPack = new MockPack("buildscape");
        PackResources higherPack = new MockPack("user_pack")
                .withJson(fileLoc, "{\"replace\": false, \"values\": [\"minecraft:higher_stone\"]}");
        ResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack, higherPack));

        Map<ResourceLocation, Tag.Builder> tagMap = new HashMap<>();

        TagAdapter.applyTags("tags/blocks", tagMap, rm);

        List<Tag.BuilderEntry> entries = tagMap.get(new ResourceLocation("buildscape:stacked_tag")).getEntries()
                .toList();
        assertEquals(2, entries.size(), "Lower entry cleared, bundled + higher entry preserved in stack order");
        assertEquals("buildscape", entries.get(0).source());
        assertEquals("user_pack", entries.get(1).source());
    }

    @Test
    void testHigherPackReplaceTrueClearsBundleEntries() throws IOException {
        TagIR bundleTag = new TagIR(
                "blocks",
                "buildscape:overridden_tag",
                false, // bundle does not replace lower
                List.of(new TagIR.TagEntryIR("buildscape:bundle_block", false, true)));

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addTag(bundleTag);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        ResourceLocation fileLoc = new ResourceLocation("buildscape", "tags/blocks/overridden_tag.json");
        PackResources lowerPack = new MockPack("vanilla")
                .withJson(fileLoc, "{\"replace\": false, \"values\": [\"minecraft:lower_stone\"]}");
        PackResources modPack = new MockPack("buildscape");
        PackResources higherPack = new MockPack("user_pack")
                .withJson(fileLoc, "{\"replace\": true, \"values\": [\"minecraft:user_stone\"]}");
        ResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack, higherPack));

        Map<ResourceLocation, Tag.Builder> tagMap = new HashMap<>();

        TagAdapter.applyTags("tags/blocks", tagMap, rm);

        List<Tag.BuilderEntry> entries = tagMap.get(new ResourceLocation("buildscape:overridden_tag")).getEntries()
                .toList();
        assertEquals(1, entries.size(), "Higher pack replace=true must clear bundle and lower entries");
        assertEquals("user_pack", entries.get(0).source());
    }
}
