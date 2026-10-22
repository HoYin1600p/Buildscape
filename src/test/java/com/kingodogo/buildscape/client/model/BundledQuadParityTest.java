package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class BundledQuadParityTest {
    private static class Sprite extends TextureAtlasSprite {
        Sprite() {
            super(null, new Info(new ResourceLocation("example:test"), 16, 16, AnimationMetadataSection.EMPTY),
                    0, 16, 16, 0, 0, new NativeImage(16, 16, false));
        }
    }

    @Test
    public void bakedVerticesAndCullBucketsMatchOriginalJson() throws Exception {
        Path root = Path.of("src/main/resources/assets/buildscape/models/block");
        int models = 0;
        int vertices = 0;
        try (Sprite sprite = new Sprite(); var paths = Files.walk(root)) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".json")).toList()) {
                String json = Files.readString(path);
                BlockModel original;
                try {
                    original = BlockModel.fromString(json);
                } catch (com.google.gson.JsonParseException invalidOriginal) {
                    continue;
                }
                if (original.getElements().isEmpty()) {
                    continue;
                }
                ResourceLocation id = new ResourceLocation("buildscape", "block/" + root.relativize(path).toString()
                        .replace('\\', '/').replace(".json", ""));
                var writer = new ResourceBundleContainer.Writer();
                writer.addModel(ModelCompiler.compile(id.toString(), json));
                var reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(writer.write(new byte[32])));
                BlockModel bundled = ModelAdapter.toBlockModel(reader.getModels().get(id.toString()));
                for (var rotation : BlockModelRotation.values()) {
                    var expected = bake(original, sprite, id, rotation);
                    var actual = bake(bundled, sprite, id, rotation);
                    assertEquals(id.toString(), expected.keySet(), actual.keySet());
                    for (int bucket : expected.keySet()) {
                        assertEquals(id + " cull bucket " + bucket, expected.get(bucket).size(),
                                actual.get(bucket).size());
                        for (int i = 0; i < expected.get(bucket).size(); i++) {
                            assertArrayEquals(id + " vertex data", expected.get(bucket).get(i),
                                    actual.get(bucket).get(i));
                            vertices += 4;
                        }
                    }
                }
                models++;
            }
        }
        assertTrue("Expected shipped models with inline geometry", models > 100);
        System.out.println("Compared " + models + " models and " + vertices + " vertices across all block rotations");
    }

    private static Map<Integer, List<int[]>> bake(BlockModel model, Sprite sprite, ResourceLocation id,
            BlockModelRotation rotation) {
        Map<Integer, List<int[]>> buckets = new java.util.HashMap<>();
        FaceBakery bakery = new FaceBakery();
        for (var element : model.getElements()) {
            for (var entry : element.faces.entrySet()) {
                var face = entry.getValue();
                var quad = bakery.bakeQuad(element.from, element.to, face, sprite, entry.getKey(), rotation,
                        element.rotation, element.shade, id);
                int bucket = face.cullForDirection == null
                        ? -1
                        : Direction.rotate(rotation.getRotation().getMatrix(), face.cullForDirection).ordinal();
                buckets.computeIfAbsent(bucket, key -> new ArrayList<>()).add(quad.getVertices());
            }
        }
        return buckets;
    }
}
