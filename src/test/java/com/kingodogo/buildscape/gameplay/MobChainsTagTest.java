package com.kingodogo.buildscape.gameplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MobChainsTagTest {
    @Test
    public void includesVanillaAndEveryChainFamilyWithoutDuplicates() throws IOException {
        JsonObject tag = readTag();
        JsonArray values = tag.getAsJsonArray("values");
        Set<String> chains = new HashSet<>();
        for (JsonElement value : values) {
            assertTrue("duplicate chain: " + value, chains.add(value.getAsString()));
        }

        assertTrue(chains.contains("minecraft:chain"));
        for (String material : new String[]{"gold", "diamond", "emerald", "ancient_steel", "netherite"}) {
            assertTrue(chains.contains("buildscape:" + material + "_chain"));
            assertTrue(chains.contains("buildscape:large_" + material + "_chain"));
        }
        assertTrue(chains.contains("buildscape:large_iron_chain"));
        for (String age : new String[]{"", "exposed_", "weathered_", "oxidized_"}) {
            assertTrue(chains.contains("buildscape:" + age + "copper_chain"));
            assertTrue(chains.contains("buildscape:large_" + age + "copper_chain"));
        }
        assertEquals(20, chains.size());
    }

    @Test
    public void allowsDataPacksToExtendTheTag() throws IOException {
        assertFalse(readTag().get("replace").getAsBoolean());
    }

    private static JsonObject readTag() throws IOException {
        InputStream stream = MobChainsTagTest.class.getClassLoader()
                .getResourceAsStream("data/buildscape/tags/blocks/mob_chains.json");
        assertNotNull("missing mob_chains block tag", stream);
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }
}
