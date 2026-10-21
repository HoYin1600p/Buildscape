package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ResourcePackPrecedenceTracker;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ResourcePackPrecedenceTest {

    private static final byte[] DIGEST = new byte[32];

    @BeforeEach
    void setup() {
        BundledResourceRegistry.getInstance().reset();
    }

    private static final class MockResource implements Resource {
        private final String packName;
        private final ResourceLocation loc;
        private final byte[] data;

        MockResource(String packName, ResourceLocation loc, String content) {
            this.packName = packName;
            this.loc = loc;
            this.data = content.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ResourceLocation getLocation() {
            return loc;
        }
        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }
        @Override
        public boolean hasMetadata() {
            return false;
        }
        @Override
        public <T> T getMetadata(net.minecraft.server.packs.metadata.MetadataSectionSerializer<T> serializer) {
            return null;
        }
        @Override
        public String getSourceName() {
            return packName;
        }
        @Override
        public void close() {}
    }

    private static final class MockPack implements PackResources {
        private final String name;
        private final boolean hasBundle;

        MockPack(String name) {
            this(name, "buildscape".equalsIgnoreCase(name));
        }

        MockPack(String name, boolean hasBundle) {
            this.name = name;
            this.hasBundle = hasBundle;
        }

        @Override
        public String getName() {
            return name;
        }
        @Override
        public java.io.InputStream getRootResource(String s) {
            return null;
        }
        @Override
        public java.io.InputStream getResource(net.minecraft.server.packs.PackType packType,
                ResourceLocation location) {
            return null;
        }
        @Override
        public java.util.Collection<ResourceLocation> getResources(net.minecraft.server.packs.PackType packType,
                String s, String s1, int i, java.util.function.Predicate<String> predicate) {
            return List.of();
        }
        @Override
        public boolean hasResource(net.minecraft.server.packs.PackType packType, ResourceLocation location) {
            if (hasBundle && (location.equals(ResourcePackPrecedenceTracker.CLIENT_BUNDLE_LOCATION)
                    || location.equals(ResourcePackPrecedenceTracker.SERVER_DATA_BUNDLE_LOCATION))) {
                return true;
            }
            return false;
        }
        @Override
        public java.util.Set<String> getNamespaces(net.minecraft.server.packs.PackType packType) {
            return java.util.Set.of();
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
        private final Map<ResourceLocation, List<Resource>> resourceMap = new HashMap<>();

        MockResourceManager(List<PackResources> packs) {
            this.packs = packs;
        }

        void addResource(ResourceLocation loc, Resource res) {
            resourceMap.computeIfAbsent(loc, k -> new ArrayList<>()).add(res);
        }

        @Override
        public java.util.Set<String> getNamespaces() {
            return java.util.Set.of("buildscape", "minecraft");
        }
        @Override
        public boolean hasResource(ResourceLocation location) {
            return resourceMap.containsKey(location);
        }
        @Override
        public Resource getResource(ResourceLocation location) {
            List<Resource> list = resourceMap.get(location);
            return (list != null && !list.isEmpty()) ? list.get(list.size() - 1) : null;
        }
        @Override
        public List<Resource> getResources(ResourceLocation location) {
            return resourceMap.getOrDefault(location, Collections.emptyList());
        }
        @Override
        public java.util.Collection<ResourceLocation> listResources(String s,
                java.util.function.Predicate<String> predicate) {
            return resourceMap.keySet();
        }
        @Override
        public Stream<PackResources> listPacks() {
            return packs.stream();
        }
    }

    @Test
    void testLowerPriorityPackDoesNotOverrideBundledModel() throws IOException {
        String modelId = "buildscape:block/acacia_log_slab";
        ModelIR bundleModel = new ModelIR(
                modelId,
                "minecraft:block/slab",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("side", "minecraft:block/acacia_log"),
                -1,
                List.of(),
                List.of());

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addModel(bundleModel);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        PackResources lowerPack = new MockPack("vanilla");
        PackResources modPack = new MockPack("buildscape");
        MockResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack));

        // Lower priority pack contains a model json
        ResourceLocation fileLoc = new ResourceLocation("buildscape", "models/block/acacia_log_slab.json");
        rm.addResource(fileLoc, new MockResource("vanilla", fileLoc, "{}"));

        ResourcePackPrecedenceTracker.updateOverrides(rm);

        // Lower pack must not override bundled model
        assertFalse(ResourcePackPrecedenceTracker.isResourceOverridden(rm, fileLoc));
        assertTrue(BundledResourceRegistry.getInstance().hasModel(modelId));
        assertNotNull(BundledResourceRegistry.getInstance().getModel(modelId));
    }

    @Test
    void testHigherPriorityPackOverridesBundledModel() throws IOException {
        String modelId = "buildscape:block/acacia_log_slab";
        ModelIR bundleModel = new ModelIR(
                modelId,
                "minecraft:block/slab",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("side", "minecraft:block/acacia_log"),
                -1,
                List.of(),
                List.of(),
                Collections.emptyMap());

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addModel(bundleModel);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        PackResources lowerPack = new MockPack("vanilla");
        PackResources modPack = new MockPack("buildscape");
        PackResources higherPack = new MockPack("user_resource_pack");
        MockResourceManager rm = new MockResourceManager(List.of(lowerPack, modPack, higherPack));

        // Higher priority pack contains an override model json
        ResourceLocation fileLoc = new ResourceLocation("buildscape", "models/block/acacia_log_slab.json");
        rm.addResource(fileLoc, new MockResource("user_resource_pack", fileLoc, "{\"parent\": \"custom\"}"));

        ResourcePackPrecedenceTracker.updateOverrides(rm);

        // Higher pack must override bundled model
        assertTrue(ResourcePackPrecedenceTracker.isResourceOverridden(rm, fileLoc));
        assertFalse(BundledResourceRegistry.getInstance().hasModel(modelId));
        assertNull(BundledResourceRegistry.getInstance().getModel(modelId));
    }

    @Test
    void testReloadWithPackRemovalRestoresBundledModel() throws IOException {
        String modelId = "buildscape:block/acacia_log_slab";
        ModelIR bundleModel = new ModelIR(
                modelId,
                "minecraft:block/slab",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("side", "minecraft:block/acacia_log"),
                -1,
                List.of(),
                List.of(),
                Collections.emptyMap());

        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addModel(bundleModel);
        BundledResourceRegistry.getInstance().initializeFromStream(new ByteArrayInputStream(writer.write(DIGEST)));

        PackResources modPack = new MockPack("buildscape");
        PackResources higherPack = new MockPack("user_resource_pack");
        MockResourceManager rmWithPack = new MockResourceManager(List.of(modPack, higherPack));
        ResourceLocation fileLoc = new ResourceLocation("buildscape", "models/block/acacia_log_slab.json");
        rmWithPack.addResource(fileLoc, new MockResource("user_resource_pack", fileLoc, "{}"));

        ResourcePackPrecedenceTracker.updateOverrides(rmWithPack);
        assertFalse(BundledResourceRegistry.getInstance().hasModel(modelId));

        // Simulate reload after pack removal
        MockResourceManager rmWithoutPack = new MockResourceManager(List.of(modPack));
        ResourcePackPrecedenceTracker.updateOverrides(rmWithoutPack);
        assertTrue(BundledResourceRegistry.getInstance().hasModel(modelId));
    }

    @Test
    void testDirectNativeObjectInstantiationWithoutJsonParser() {
        ModelIR bundleModel = new ModelIR(
                "buildscape:block/custom",
                "minecraft:block/cube",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("all", "minecraft:block/stone"),
                -1,
                List.of(),
                List.of());

        // ModelAdapter decodes directly to BlockModel with no json parse
        BlockModel model = ModelAdapter.toBlockModel(bundleModel);
        assertNotNull(model);
        assertTrue(model.hasAmbientOcclusion());
        assertEquals(new ResourceLocation("minecraft:block/cube"), getParentLocation(model));
    }

    @Test
    void testMisleadingPackNameDoesNotClaimBundleOwnership() {
        // External pack named "buildscape_overrides" or "my_mod_pack" but hasBundle = false
        PackResources misleadingPack = new MockPack("buildscape_overrides", false);
        PackResources actualBundlePack = new MockPack("actual_mod_jar", true);
        List<PackResources> packs = List.of(misleadingPack, actualBundlePack);

        int bundleIdx = ResourcePackPrecedenceTracker.findBundlePackIndex(packs);
        assertEquals(1, bundleIdx, "Bundle index must point to the actual bundle-owning pack, not the misleading name");
    }

    @Test
    void testMissingOwningPackDeactivatesBundles() {
        // No pack has the bundle
        PackResources otherPack1 = new MockPack("vanilla", false);
        PackResources otherPack2 = new MockPack("buildscape_addon", false);
        List<PackResources> packs = List.of(otherPack1, otherPack2);

        int bundleIdx = ResourcePackPrecedenceTracker.findBundlePackIndex(packs);
        assertEquals(-1, bundleIdx, "When owning pack is absent, bundle index must be -1");
    }

    private static ResourceLocation getParentLocation(BlockModel model) {
        try {
            java.lang.reflect.Field f = BlockModel.class.getDeclaredField("parentLocation");
            f.setAccessible(true);
            return (ResourceLocation) f.get(model);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
