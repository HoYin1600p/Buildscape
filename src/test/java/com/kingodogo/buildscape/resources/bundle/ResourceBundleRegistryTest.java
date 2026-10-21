package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceBundleRegistryTest {

    private static final byte[] DIGEST = new byte[32];
    static {
        for (int i = 0; i < 32; i++) {
            DIGEST[i] = (byte) i;
        }
    }

    @BeforeEach
    void setup() {
        BundledResourceRegistry.getInstance().reset();
    }

    private byte[] createDummyAssetsBundle() throws IOException {
        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        ModelIR model = new ModelIR(
                "buildscape:block/test_block",
                "minecraft:block/cube_all",
                true,
                ModelIR.GUI_LIGHT_SIDE,
                Map.of("all", "buildscape:block/test_texture"),
                -1,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap());
        writer.addModel(model);

        BlockStateIR bs = new BlockStateIR(
                "buildscape:test_block",
                BlockStateIR.TYPE_VARIANTS,
                -1,
                null,
                Map.of("", List.of(new BlockStateIR.VariantModelIR("buildscape:block/test_block", 0, 0, false, 1))),
                Collections.emptyList());
        writer.addBlockState(bs);

        return writer.write(DIGEST);
    }

    private byte[] createDummyDataBundle() throws IOException {
        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        TagIR tag = new TagIR(
                "blocks",
                "buildscape:test_tag",
                false,
                List.of(new TagIR.TagEntryIR("buildscape:test_block", false, true)));
        writer.addTag(tag);
        return writer.write(DIGEST);
    }

    @Test
    void testAtomicSingleTransactionLoad() throws IOException {
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        long initialGen = registry.getReloadGeneration();

        byte[] assetsBytes = createDummyAssetsBundle();
        byte[] dataBytes = createDummyDataBundle();

        try (InputStream assetsIn = new ByteArrayInputStream(assetsBytes);
                InputStream dataIn = new ByteArrayInputStream(dataBytes)) {
            registry.loadBundles(assetsIn, dataIn);
        }

        assertEquals(initialGen + 1, registry.getReloadGeneration());
        assertEquals(1, registry.getModelCount());
        assertEquals(1, registry.getBlockStateCount());
        assertEquals(1, registry.getTagCount());

        assertTrue(registry.hasModel("buildscape:block/test_block"));
        assertTrue(registry.hasBlockState("buildscape:test_block"));

        ModelIR m = registry.getModel("buildscape:block/test_block");
        assertNotNull(m);
        assertEquals(1, registry.getServedModelCount());

        BlockStateIR b = registry.getBlockState("buildscape:test_block");
        assertNotNull(b);
        assertEquals(1, registry.getServedBlockStateCount());
    }

    @Test
    void testOverridesPrecedence() throws IOException {
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        byte[] assetsBytes = createDummyAssetsBundle();
        try (InputStream assetsIn = new ByteArrayInputStream(assetsBytes)) {
            registry.loadBundles(assetsIn, null);
        }

        assertTrue(registry.hasModel("buildscape:block/test_block"));

        // Mark as overridden by an active resource pack
        registry.markModelOverridden("buildscape:block/test_block");

        assertFalse(registry.hasModel("buildscape:block/test_block"));
        assertNull(registry.getModel("buildscape:block/test_block")); // Returns null so normal loader handles fallback
        assertEquals(1, registry.getFallbackModelCount());

        // Clear overrides (e.g. on pack reload)
        registry.clearOverrides();
        assertTrue(registry.hasModel("buildscape:block/test_block"));
        assertNotNull(registry.getModel("buildscape:block/test_block"));
    }

    @Test
    void testReloadPreservesImmutableMemorySnapshot() throws IOException {
        BundledResourceRegistry registry = BundledResourceRegistry.getInstance();
        byte[] assetsBytes = createDummyAssetsBundle();
        try (InputStream assetsIn = new ByteArrayInputStream(assetsBytes)) {
            registry.loadBundles(assetsIn, null);
        }

        registry.getModel("buildscape:block/test_block");
        assertEquals(1, registry.getServedModelCount());

        // Prepare next reload
        registry.prepareNextReload();

        // Access counters reset for the new generation
        assertEquals(0, registry.getServedModelCount());

        // But container data is retained in memory without re-reading
        assertTrue(registry.hasModel("buildscape:block/test_block"));
        assertNotNull(registry.getModel("buildscape:block/test_block"));
        assertEquals(1, registry.getServedModelCount());

        String summary = registry.buildReloadSummary();
        assertTrue(summary.contains("reused memory snapshot"));
        assertTrue(summary.contains("0 bytes re-read"));
    }
}
