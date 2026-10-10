package com.kingodogo.buildscape.adapter.v26x;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.adapter.v26x.client.ExperienceFluidModel;
import com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceCauldronInteractions;
import com.kingodogo.buildscape.fluid.ExperienceFluidProperties;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static com.kingodogo.buildscape.adapter.v26x.fluid.ExperienceCauldronInteractions.Transfer.*;
import static org.junit.jupiter.api.Assertions.*;

class ExperienceFluidTest {
    // Read Buildscape's own files: the vanilla jar on the test classpath has
    // resources at the same paths (data/minecraft/tags/fluid/water.json).
    private static final Path RESOURCES = Path.of(System.getProperty("buildscape.mainResources", "src/main/resources"));

    private static JsonObject resource(String path) throws Exception {
        Path file = RESOURCES.resolve(path.substring(1));
        assertTrue(Files.isRegularFile(file), path);
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    @Test void fluidUsesTheReferencePhysicalAndSpreadProperties() {
        assertEquals(15, ExperienceFluidProperties.LIGHT_LEVEL);
        assertEquals(1000, ExperienceFluidProperties.DENSITY);
        assertEquals(1000, ExperienceFluidProperties.VISCOSITY);
        assertEquals(5, ExperienceFluidProperties.TICK_DELAY);
        assertEquals(4, ExperienceFluidProperties.SLOPE_FIND_DISTANCE);
        assertEquals(1, ExperienceFluidProperties.DROP_OFF);
        assertEquals(1.0F, ExperienceFluidProperties.EXPLOSION_RESISTANCE);
        assertFalse(ExperienceFluidProperties.CONVERTS_TO_SOURCE);
    }

    @Test void bothFluidsAreRequiredWaterTagEntries() throws Exception {
        JsonObject tag = resource("/data/minecraft/tags/fluid/water.json");
        assertFalse(tag.get("replace").getAsBoolean());
        Set<String> ids = new HashSet<>();
        for (var entry : tag.getAsJsonArray("values")) {
            if (entry.isJsonPrimitive()) {
                assertTrue(entry.getAsJsonPrimitive().isString(), "Tag IDs must be strings");
                ids.add(entry.getAsString());
            } else {
                JsonObject value = entry.getAsJsonObject();
                assertTrue(!value.has("required") || value.get("required").getAsBoolean(),
                        "Experience tag entries must not be optional");
                ids.add(value.get("id").getAsString());
            }
        }
        assertEquals(Set.of("buildscape:experience_still", "buildscape:experience_flowing"), ids);
        assertEquals(2, tag.getAsJsonArray("values").size());
    }

    @Test void bothLoadersUseColoredExperienceTexturesWithoutBiomeTint() throws Exception {
        var model = ExperienceFluidModel.create();
        assertEquals("buildscape:fluid/experience_still", model.stillMaterial().sprite().toString());
        assertEquals("buildscape:fluid/experience_flow", model.flowingMaterial().sprite().toString());
        assertTrue(model.stillMaterial().forceTranslucent());
        assertTrue(model.flowingMaterial().forceTranslucent());
        assertEquals(0xFFFFFFFF, model.tintSource().color(null));
        assertNull(model.overlayMaterial());
        for (String texture : new String[] {"experience_still", "experience_flow"}) {
            try (var stream = getClass().getResourceAsStream("/assets/buildscape/textures/fluid/" + texture + ".png")) {
                assertNotNull(stream, texture);
                assertArrayEquals(new byte[] {(byte) 137, 80, 78, 71, 13, 10, 26, 10}, stream.readNBytes(8));
            }
            assertTrue(resource("/assets/buildscape/textures/fluid/" + texture + ".png.mcmeta").has("animation"));
        }
        assertEquals("buildscape:block/experience_liquid",
                resource("/assets/buildscape/blockstates/experience_liquid.json")
                        .getAsJsonObject("variants").getAsJsonObject("").get("model").getAsString());
        assertEquals("buildscape:fluid/experience_still",
                resource("/assets/buildscape/models/block/experience_liquid.json")
                        .getAsJsonObject("textures").get("particle").getAsString());
    }

    @Test void cauldronsTransferThreeBottlesPerBucketAndRefuseOverflowOrPartialBuckets() {
        for (int level = 1; level <= 3; level++) {
            assertEquals(level - 1, ExperienceCauldronInteractions.nextLevel(level, TAKE_BOTTLE));
            assertEquals(level == 3 ? 0 : -1, ExperienceCauldronInteractions.nextLevel(level, TAKE_BUCKET));
            assertEquals(level == 3 ? -1 : level + 1, ExperienceCauldronInteractions.nextLevel(level, ADD_BOTTLE));
            assertEquals(level == 3 ? -1 : 3, ExperienceCauldronInteractions.nextLevel(level, ADD_BUCKET));
        }
        assertThrows(IllegalArgumentException.class, () -> ExperienceCauldronInteractions.nextLevel(0, TAKE_BOTTLE));
        assertThrows(IllegalArgumentException.class, () -> ExperienceCauldronInteractions.nextLevel(4, ADD_BOTTLE));
    }
}
