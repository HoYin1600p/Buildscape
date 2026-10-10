package com.kingodogo.buildscape.adapter.v26x;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SpecialItemModelsTest {
    private static JsonObject definition(String item) throws Exception {
        try (var stream = SpecialItemModelsTest.class.getResourceAsStream("/assets/buildscape/items/" + item + ".json")) {
            assertNotNull(stream, item);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject()
                    .getAsJsonObject("model");
        }
    }


    @Test void everyJarVariantUsesTheSpecialRenderer() throws Exception {
        for (String item : new String[] {"template_glass_jar", "glass_jar", "tinted_glass_jar",
                "white_glass_jar", "orange_glass_jar", "magenta_glass_jar", "light_blue_glass_jar",
                "yellow_glass_jar", "lime_glass_jar", "pink_glass_jar", "gray_glass_jar", "light_gray_glass_jar",
                "cyan_glass_jar", "purple_glass_jar", "blue_glass_jar", "brown_glass_jar", "green_glass_jar",
                "red_glass_jar", "black_glass_jar"}) {
            JsonObject model = definition(item);
            assertEquals("minecraft:special", model.get("type").getAsString());
            assertEquals("buildscape:item/" + item, model.get("base").getAsString());
            assertEquals("buildscape:glass_jar", model.getAsJsonObject("model").get("type").getAsString());
        }
    }
}
