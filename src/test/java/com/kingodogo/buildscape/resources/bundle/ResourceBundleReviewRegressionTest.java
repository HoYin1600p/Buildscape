package com.kingodogo.buildscape.resources.bundle;

import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.ResourceBundleCompiler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceBundleReviewRegressionTest {
    @TempDir
    Path directory;

    private static String model(String texture) {
        return "{\"textures\":{\"particle\":\"minecraft:block/stone\"},"
                + "\"elements\":[{\"from\":[0,0,0],\"to\":[16,16,16],"
                + "\"faces\":{\"north\":{\"texture\":\"" + texture + "\"}}}]}";
    }

    @Test
    void omittedFaceUvsMustUseVanillaDefaults() {
        var ir = ModelCompiler.compile("example:block/default_uv", model("#particle"));
        assertDoesNotThrow(() -> ModelAdapter.toBlockModel(ir));
    }

    @Test
    void geometrySharingMustPreserveDifferentFaceTextures() throws Exception {
        Path root = directory.resolve("input");
        Path models = root.resolve("assets/example/models/block");
        Files.createDirectories(models);
        Files.writeString(models.resolve("a.json"), model("#first"));
        Files.writeString(models.resolve("b.json"), model("#second"));
        Path bundle = directory.resolve("resources.bsrb");
        ResourceBundleCompiler.compile(List.of(root),
                bundle, directory.resolve("coverage.manifest"));
        var reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(Files.readAllBytes(bundle)));
        assertEquals("#first",
                reader.getModels().get("example:block/a").elements().get(0).faces().get((byte) 2).texture());
        assertEquals("#second",
                reader.getModels().get("example:block/b").elements().get(0).faces().get((byte) 2).texture());
    }

    @Test
    void nestedBlockstateResourcePathsMustRetainTheirDirectories() throws Exception {
        Path root = directory.resolve("input");
        Path states = root.resolve("assets/example/blockstates/family");
        Files.createDirectories(states);
        Files.writeString(states.resolve("slab.json"),
                "{\"variants\":{\"\":{\"model\":\"example:block/slab\"}}}");
        Path bundle = directory.resolve("resources.bsrb");
        ResourceBundleCompiler.compile(List.of(root),
                bundle, directory.resolve("coverage.manifest"));
        var reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(Files.readAllBytes(bundle)));
        assertTrue(reader.getBlockStates().containsKey("example:family/slab"));
    }
}
