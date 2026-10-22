package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.Assert.*;

public class BundledGeometryParityTest {
    @Test
    public void shippedGeometryPreservesCullFacesAndUvsAfterBinaryRoundTrip() throws Exception {
        Path root = Path.of("src/main/resources/assets/buildscape/models");
        int checked = 0;
        try (var paths = Files.walk(root)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".json")).toList()) {
                String json = Files.readString(path);
                String id = "buildscape:" + root.relativize(path).toString().replace('\\', '/').replace(".json", "");
                BlockModel vanilla;
                try {
                    vanilla = BlockModel.fromString(json);
                } catch (com.google.gson.JsonParseException invalidOriginal) {
                    System.out.println("Original model rejected by vanilla parser: " + id);
                    continue;
                }
                var writer = new ResourceBundleContainer.Writer();
                writer.addModel(ModelCompiler.compile(id, json));
                var reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(writer.write(new byte[32])));
                BlockModel bundled = ModelAdapter.toBlockModel(reader.getModels().get(id));
                assertEquals(id, vanilla.getElements().size(), bundled.getElements().size());
                for (int i = 0; i < vanilla.getElements().size(); i++) {
                    var expected = vanilla.getElements().get(i);
                    var actual = bundled.getElements().get(i);
                    assertEquals(id, expected.from, actual.from);
                    assertEquals(id, expected.to, actual.to);
                    assertEquals(id, expected.shade, actual.shade);
                    assertEquals(id, expected.faces.keySet(), actual.faces.keySet());
                    for (var direction : expected.faces.keySet()) {
                        var ef = expected.faces.get(direction);
                        var af = actual.faces.get(direction);
                        assertEquals(id, ef.cullForDirection, af.cullForDirection);
                        assertEquals(id, ef.texture, af.texture);
                        assertEquals(id, ef.tintIndex, af.tintIndex);
                        assertArrayEquals(id, ef.uv.uvs, af.uv.uvs, 0.0f);
                        assertEquals(id, ef.uv.rotation, af.uv.rotation);
                    }
                }
                checked++;
            }
        }
        assertTrue("Expected the full shipped model corpus", checked > 1000);
    }

    @Test
    public void omittedLeftHandTransformsInheritRightHandTransforms() {
        String json = "{\"display\":{\"thirdperson_righthand\":{\"rotation\":[10,20,30]},"
                + "\"firstperson_righthand\":{\"rotation\":[40,50,60]}}}";
        var vanilla = BlockModel.fromString(json).getTransforms();
        var bundled = ModelAdapter.toBlockModel(ModelCompiler.compile("example:test", json)).getTransforms();
        assertEquals(vanilla.thirdPersonLeftHand.rotation, bundled.thirdPersonLeftHand.rotation);
        assertEquals(vanilla.firstPersonLeftHand.rotation, bundled.firstPersonLeftHand.rotation);
    }
}
