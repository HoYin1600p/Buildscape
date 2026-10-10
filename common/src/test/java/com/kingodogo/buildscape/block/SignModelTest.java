package com.kingodogo.buildscape.block;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Since 26.x standing signs are drawn from their baked block model, not by the block entity renderer. A model
 * without elements makes the board invisible (only the text renders), so every variant must resolve to geometry.
 */
class SignModelTest {
    @Test
    void signBlockstatesResolveToModelsWithGeometry() throws Exception {
        for (String id : new String[] {"mangrove_sign", "bamboo_sign", "mangrove_wall_sign", "bamboo_wall_sign"}) {
            JsonObject variants = read("buildscape", "blockstates", id).getAsJsonObject("variants");
            assertEquals(id.contains("wall") ? 4 : 16, variants.size(), id + " needs one variant per rotation/facing");
            for (var entry : variants.entrySet()) {
                String model = entry.getValue().getAsJsonObject().get("model").getAsString();
                assertTrue(hasElements(model), id + " " + entry.getKey() + " model " + model + " has no elements");
            }
        }
    }

    private static boolean hasElements(String model) throws Exception {
        String current = model;
        for (int depth = 0; depth < 8 && current != null; depth++) {
            JsonObject json = read(namespace(current), "models", path(current));
            if (json.has("elements")) return true;
            current = json.has("parent") ? json.get("parent").getAsString() : null;
        }
        return false;
    }

    private static String namespace(String id) {
        return id.contains(":") ? id.substring(0, id.indexOf(':')) : "minecraft";
    }

    private static String path(String id) {
        return id.contains(":") ? id.substring(id.indexOf(':') + 1) : id;
    }

    private static JsonObject read(String namespace, String folder, String path) throws Exception {
        String resource = "/assets/" + namespace + "/" + folder + "/" + path + ".json";
        try (var in = SignModelTest.class.getResourceAsStream(resource)) {
            assertNotNull(in, "missing resource " + resource);
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
