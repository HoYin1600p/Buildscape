package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.compiler.BlockStateCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.ResourceBundleCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.TagCompiler;
import com.kingodogo.buildscape.resources.bundle.manifest.ResourceCoverageManifest;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.tag.TagIR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceBundleCompilerTest {

    @Test
    void testModelCompilerParsing() {
        String json = """
                {
                  "parent": "minecraft:block/slab",
                  "ambientocclusion": true,
                  "gui_light": "side",
                  "textures": {
                    "bottom": "minecraft:block/acacia_log_top",
                    "top": "minecraft:block/acacia_log_top",
                    "side": "minecraft:block/acacia_log"
                  }
                }
                """;
        ModelIR model = ModelCompiler.compile("buildscape:block/acacia_log_slab", json);
        assertEquals("buildscape:block/acacia_log_slab", model.id());
        assertEquals("minecraft:block/slab", model.parent());
        assertTrue(model.ambientOcclusion());
        assertEquals(ModelIR.GUI_LIGHT_SIDE, model.guiLight());
        assertEquals(3, model.textures().size());
        assertEquals("minecraft:block/acacia_log", model.textures().get("side"));
    }

    @Test
    void testBlockStateCompilerVariantsAndMultipart() {
        String variantJson = """
                {
                  "variants": {
                    "type=bottom": { "model": "buildscape:block/acacia_log_slab" },
                    "type=top": { "model": "buildscape:block/acacia_log_slab_top", "x": 180, "y": 90, "uvlock": true }
                  }
                }
                """;
        BlockStateIR bs1 = BlockStateCompiler.compile("buildscape:acacia_log_slab", variantJson);
        assertEquals(BlockStateIR.TYPE_VARIANTS, bs1.type());
        assertEquals(2, bs1.variants().size());
        BlockStateIR.VariantModelIR top = bs1.variants().get("type=top").get(0);
        assertEquals(180, top.x());
        assertEquals(90, top.y());
        assertTrue(top.uvLock());

        String multipartJson = """
                {
                  "multipart": [
                    {
                      "when": { "facing": "north" },
                      "apply": { "model": "buildscape:block/acacia_hedge_post" }
                    },
                    {
                      "when": { "OR": [ { "north": "true" }, { "south": "true" } ] },
                      "apply": { "model": "buildscape:block/acacia_hedge_side", "y": 90 }
                    }
                  ]
                }
                """;
        BlockStateIR bs2 = BlockStateCompiler.compile("buildscape:acacia_hedge", multipartJson);
        assertEquals(BlockStateIR.TYPE_MULTIPART, bs2.type());
        assertEquals(2, bs2.multiparts().size());
        assertEquals(BlockStateIR.ConditionIR.KIND_KEY_VALUE, bs2.multiparts().get(0).condition().kind());
        assertEquals(BlockStateIR.ConditionIR.KIND_OR, bs2.multiparts().get(1).condition().kind());
    }

    @Test
    void testTagCompilerParsing() {
        String json = """
                {
                  "replace": false,
                  "values": [
                    "buildscape:acacia_log_slab",
                    { "id": "buildscape:birch_log_slab", "required": false },
                    "#buildscape:extra_slabs"
                  ]
                }
                """;
        TagIR tag = TagCompiler.compile("blocks", "buildscape:slabs", json);
        assertEquals("blocks", tag.registry());
        assertEquals("buildscape:slabs", tag.id());
        assertFalse(tag.replace());
        assertEquals(3, tag.entries().size());
        assertEquals("buildscape:acacia_log_slab", tag.entries().get(0).id());
        assertFalse(tag.entries().get(0).tag());
        assertTrue(tag.entries().get(0).required());

        assertEquals("buildscape:birch_log_slab", tag.entries().get(1).id());
        assertFalse(tag.entries().get(1).tag());
        assertFalse(tag.entries().get(1).required());

        assertEquals("buildscape:extra_slabs", tag.entries().get(2).id());
        assertTrue(tag.entries().get(2).tag());
    }

    @Test
    void testFullBundleCompilationPipeline(@TempDir Path tempDir) throws IOException {
        Path root = tempDir.resolve("res_root");
        Path modelsDir = root.resolve("assets/buildscape/models/block");
        Path statesDir = root.resolve("assets/buildscape/blockstates");
        Path tagsDir = root.resolve("data/buildscape/tags/blocks");

        Files.createDirectories(modelsDir);
        Files.createDirectories(statesDir);
        Files.createDirectories(tagsDir);

        Files.writeString(modelsDir.resolve("slab.json"), """
                {
                  "parent": "minecraft:block/slab",
                  "textures": { "bottom": "buildscape:block/slab_bottom" }
                }
                """);

        Files.writeString(statesDir.resolve("slab.json"), """
                {
                  "variants": {
                    "": { "model": "buildscape:block/slab" }
                  }
                }
                """);

        Files.writeString(tagsDir.resolve("slabs.json"), """
                {
                  "replace": false,
                  "values": [ "buildscape:slab" ]
                }
                """);

        Path assetsBundle = tempDir.resolve("resources.bsrb");
        Path dataBundle = tempDir.resolve("data.bsrb");
        Path manifestPath = tempDir.resolve("coverage.manifest");

        ResourceBundleCompiler.compileAssetsAndData(
                List.of(root),
                assetsBundle,
                dataBundle,
                manifestPath);

        assertTrue(Files.exists(assetsBundle));
        assertTrue(Files.exists(dataBundle));
        assertTrue(Files.exists(manifestPath));

        // Verify Reader
        ResourceBundleContainer.Reader assetsReader = new ResourceBundleContainer.Reader(
                new ByteArrayInputStream(Files.readAllBytes(assetsBundle)));
        assertEquals(ResourceBundleFormat.CURRENT_VERSION, assetsReader.getVersion());
        assertEquals(1, assetsReader.getModels().size());
        assertEquals(1, assetsReader.getBlockStates().size());

        ResourceBundleContainer.Reader dataReader = new ResourceBundleContainer.Reader(
                new ByteArrayInputStream(Files.readAllBytes(dataBundle)));
        assertEquals(ResourceBundleFormat.CURRENT_VERSION, dataReader.getVersion());
        assertEquals(1, dataReader.getTags().size());

        // Verify Manifest
        ResourceCoverageManifest manifest = ResourceCoverageManifest.readFromFile(manifestPath);
        assertTrue(manifest.covers("assets/buildscape/models/block/slab.json"));
        assertTrue(manifest.covers("assets/buildscape/blockstates/slab.json"));
        assertTrue(manifest.covers("data/buildscape/tags/blocks/slabs.json"));
    }
}
