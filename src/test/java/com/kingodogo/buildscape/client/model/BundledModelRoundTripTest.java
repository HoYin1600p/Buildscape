package com.kingodogo.buildscape.client.model;

import com.kingodogo.buildscape.resources.bundle.ResourceBundleContainer;
import com.kingodogo.buildscape.resources.bundle.adapter.BlockStateAdapter;
import com.kingodogo.buildscape.resources.bundle.adapter.ModelAdapter;
import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import com.kingodogo.buildscape.resources.bundle.compiler.BlockStateCompiler;
import com.kingodogo.buildscape.resources.bundle.compiler.ModelCompiler;
import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.kingodogo.buildscape.resources.bundle.runtime.BundledResourceRegistry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BundledModelRoundTripTest {

    private static final byte[] DIGEST = new byte[32];

    private static ResourceLocation getParentLocation(BlockModel model) {
        try {
            Field f = BlockModel.class.getDeclaredField("parentLocation");
            f.setAccessible(true);
            return (ResourceLocation) f.get(model);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ?> getTextureMap(BlockModel model) {
        try {
            Field f = BlockModel.class.getDeclaredField("textureMap");
            f.setAccessible(true);
            return (Map<String, ?>) f.get(model);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSlabModelSemanticEquivalence() throws IOException {
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

        // 1. Vanilla parsing
        BlockModel vanilla = BlockModel.fromString(json);
        vanilla.name = "buildscape:block/acacia_log_slab";

        // 2. Binary IR compilation + serialization round trip
        ModelIR compiledIR = ModelCompiler.compile("buildscape:block/acacia_log_slab", json);
        ResourceBundleContainer.Writer writer = new ResourceBundleContainer.Writer();
        writer.addModel(compiledIR);
        byte[] bundle = writer.write(DIGEST);

        ResourceBundleContainer.Reader reader = new ResourceBundleContainer.Reader(new ByteArrayInputStream(bundle));
        ModelIR decodedIR = reader.getModels().get("buildscape:block/acacia_log_slab");
        assertNotNull(decodedIR);

        // 3. Native adapter instantiation
        BlockModel nativeModel = ModelAdapter.toBlockModel(decodedIR);

        // 4. Assert strict semantic equivalence
        assertEquals(getParentLocation(vanilla), getParentLocation(nativeModel));
        assertEquals(vanilla.hasAmbientOcclusion(), nativeModel.hasAmbientOcclusion());
        assertEquals(vanilla.getGuiLight(), nativeModel.getGuiLight());
        assertEquals(getTextureMap(vanilla).size(), getTextureMap(nativeModel).size());
        for (String slot : getTextureMap(vanilla).keySet()) {
            assertTrue(nativeModel.hasTexture(slot));
        }
    }

    @Test
    void testGlassStairsGeometryPreserved() {
        String json = """
                {
                  "parent": "block/block",
                  "elements": [
                    {
                      "from": [ 0, 0, 0 ],
                      "to": [ 16, 8, 16 ],
                      "faces": {
                        "down":  { "uv": [ 0, 0, 16, 16 ], "texture": "#bottom", "cullface": "down" },
                        "up":    { "uv": [ 0, 0, 16, 16 ], "texture": "#top" },
                        "north": { "uv": [ 0, 8, 16, 16 ], "texture": "#side", "cullface": "north" }
                      }
                    },
                    {
                      "from": [ 8, 8, 0 ],
                      "to": [ 16, 16, 16 ],
                      "faces": {
                        "up":    { "uv": [ 8, 0, 16, 16 ], "texture": "#top", "cullface": "up" },
                        "north": { "uv": [ 8, 0, 16, 8 ], "texture": "#side", "cullface": "north" }
                      }
                    }
                  ]
                }
                """;

        BlockModel vanilla = BlockModel.fromString(json);
        ModelIR compiledIR = ModelCompiler.compile("buildscape:block/glass_stairs_template", json);
        BlockModel nativeModel = ModelAdapter.toBlockModel(compiledIR);

        assertEquals(vanilla.getElements().size(), nativeModel.getElements().size());
        for (int i = 0; i < vanilla.getElements().size(); i++) {
            BlockElement vEl = vanilla.getElements().get(i);
            BlockElement nEl = nativeModel.getElements().get(i);
            assertEquals(vEl.from, nEl.from);
            assertEquals(vEl.to, nEl.to);
            assertEquals(vEl.faces.size(), nEl.faces.size());
        }
    }

    @Test
    void testStairVariantEquivalence() {
        String json = """
                {
                  "variants": {
                    "facing=east,half=bottom,shape=straight": { "model": "buildscape:block/acacia_stairs" },
                    "facing=west,half=top,shape=inner_left": { "model": "buildscape:block/acacia_stairs_inner", "x": 180, "y": 90, "uvlock": true }
                  }
                }
                """;

        BlockStateIR compiled = BlockStateCompiler.compile("buildscape:acacia_stairs", json);
        BlockModelDefinition def = BlockStateAdapter.toBlockModelDefinition(compiled, null);

        assertNotNull(def);
        assertTrue(def.hasVariant("facing=east,half=bottom,shape=straight"));
        assertTrue(def.hasVariant("facing=west,half=top,shape=inner_left"));
        assertEquals(1, def.getVariant("facing=west,half=top,shape=inner_left").getVariants().size());
        assertTrue(def.getVariant("facing=west,half=top,shape=inner_left").getVariants().get(0).isUvLocked());
    }

    @Test
    void testTransformsSemanticEquivalenceAndScaling() {
        String json = """
                {
                  "display": {
                    "gui": {
                      "rotation": [ 30, 315, 0 ],
                      "translation": [ -2, 0, 4 ],
                      "scale": [ 0.625, 0.625, 0.625 ]
                    },
                    "ground": {
                      "rotation": [ 0, 0, 0 ],
                      "translation": [ 0, 3, 0 ],
                      "scale": [ 0.25, 0.25, 0.25 ]
                    }
                  }
                }
                """;

        // 1. Vanilla parsing
        BlockModel vanilla = BlockModel.fromString(json);

        // 2. Binary IR compilation
        ModelIR compiledIR = ModelCompiler.compile("buildscape:block/test_transforms", json);

        // 3. Native adapter instantiation
        BlockModel nativeModel = ModelAdapter.toBlockModel(compiledIR);

        // 4. Assert strict semantic equivalence for display transforms
        ItemTransform vGui = vanilla.getTransforms().gui;
        ItemTransform nGui = nativeModel.getTransforms().gui;

        assertEquals(vGui.rotation, nGui.rotation);
        assertEquals(vGui.translation, nGui.translation);
        assertEquals(vGui.scale, nGui.scale);

        // Specifically assert that translations were correctly converted to block units (multiplied by 0.0625)
        assertEquals(-2.0f * 0.0625f, nGui.translation.x(), 1e-6f);
        assertEquals(0.0f, nGui.translation.y(), 1e-6f);
        assertEquals(4.0f * 0.0625f, nGui.translation.z(), 1e-6f);

        ItemTransform vGround = vanilla.getTransforms().ground;
        ItemTransform nGround = nativeModel.getTransforms().ground;
        assertEquals(vGround.rotation, nGround.rotation);
        assertEquals(vGround.translation, nGround.translation);
        assertEquals(vGround.scale, nGround.scale);
        assertEquals(3.0f * 0.0625f, nGround.translation.y(), 1e-6f);
    }

    @Test
    void testVerticalSlabDisplayInheritance() throws Exception {
        ModelIR itemIR = compileSourceModel("item/bit_exposed_cut_copper_vertical_slab");
        ModelIR blockIR = compileSourceModel("block/bit_exposed_cut_copper_vertical_slab");
        ModelIR templateIR = compileSourceModel("block/template_vertical_slab");

        assertNotNull(itemIR);
        assertNotNull(blockIR);
        assertNotNull(templateIR);

        BlockModel nativeItem = ModelAdapter.toBlockModel(itemIR);
        BlockModel nativeBlock = ModelAdapter.toBlockModel(blockIR);
        BlockModel nativeTemplate = ModelAdapter.toBlockModel(templateIR);

        Field parentField = BlockModel.class.getDeclaredField("parent");
        parentField.setAccessible(true);
        parentField.set(nativeItem, nativeBlock);
        parentField.set(nativeBlock, nativeTemplate);

        ItemTransforms transforms = nativeItem.getTransforms();
        assertEquals(new com.mojang.math.Vector3f(30.0f, 315.0f, 0.0f), transforms.gui.rotation);
        assertEquals(new com.mojang.math.Vector3f(-0.125f, 0.0f, 0.25f), transforms.gui.translation);
        assertEquals(new com.mojang.math.Vector3f(0.625f, 0.625f, 0.625f), transforms.gui.scale);
    }

    private static ModelIR compileSourceModel(String path) throws IOException {
        var file = java.nio.file.Path.of("src/main/resources/assets/buildscape/models", path + ".json");
        return ModelCompiler.compile("buildscape:" + path, java.nio.file.Files.readString(file));
    }
}
