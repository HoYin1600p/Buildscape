package com.kingodogo.buildscape.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParticlePatternsTest {
    @Test
    public void constantsKeepTheStoredWireStrings() {
        assertEquals(List.of("none", "default", "beam", "spiral", "fountain", "pulse", "ring", "burst", "snowflake"),
                ParticlePatterns.ALL);
        assertEquals("__not_set__", ParticlePatterns.NOT_SET);
        assertFalse(ParticlePatterns.isKnown(ParticlePatterns.NOT_SET));
        assertFalse(ParticlePatterns.isKnown("Ring"));
    }

    @Test
    public void everyPatternInDefaultsConfigsAndPresetsRoundTrips() throws IOException {
        List<String> found = new ArrayList<>();
        found.add(new PillarParticleConfig().pattern);
        try (Stream<Path> files = Stream.concat(
                Stream.concat(Files.walk(Path.of("src", "main", "resources", "data", "buildscape", "config")),
                        Files.walk(Path.of("src", "test", "resources", "config405"))),
                Stream.of(Path.of("pillar-items.json")))) {
            for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".json"))::iterator) {
                try {
                    collectPatterns(JsonParser.parseString(Files.readString(file)), found);
                } catch (JsonParseException ignored) {
                    // some fixtures are deliberately malformed
                }
            }
        }
        assertTrue("expected at least the default pattern values", found.size() >= 2);
        for (String stored : found) {
            assertTrue("unknown stored pattern " + stored, ParticlePatterns.isKnown(stored));
            String constant = ParticlePatterns.ALL.get(ParticlePatterns.ALL.indexOf(stored));
            assertEquals(stored, constant);
        }
    }

    private static void collectPatterns(JsonElement element, List<String> into) {
        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                JsonElement value = entry.getValue();
                if (entry.getKey().equals("pattern") && value.isJsonPrimitive()
                        && value.getAsJsonPrimitive().isString()) {
                    into.add(value.getAsString());
                } else {
                    collectPatterns(value, into);
                }
            }
        } else if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collectPatterns(child, into));
        }
    }
}
