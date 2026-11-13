package com.kingodogo.buildscape.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The 1.18.2 resources ship the sounds, subtitles, tag and loot the sculk backport uses. */
class SculkResourcesTest {
    private static final String[] SOUNDS = {"block_sculk_catalyst_bloom", "block_sculk_spread", "block_sculk_charge"};

    @Test
    void everySculkSoundHasASubtitleAndExistingFiles() throws Exception {
        Path assets = resources().resolve("assets/buildscape");
        JsonObject sounds = JsonParser.parseString(Files.readString(assets.resolve("sounds.json"))).getAsJsonObject();
        JsonObject lang = JsonParser.parseString(Files.readString(assets.resolve("lang/en_us.json"))).getAsJsonObject();
        for (String event : SOUNDS) {
            JsonObject definition = sounds.getAsJsonObject(event);
            assertNotNull(definition, event);
            assertTrue(lang.has(definition.get("subtitle").getAsString()), event);
            for (JsonElement sound : definition.getAsJsonArray("sounds")) {
                String name = sound.isJsonObject() ? sound.getAsJsonObject().get("name").getAsString() : sound.getAsString();
                if (name.startsWith("buildscape:")) {
                    Path file = assets.resolve("sounds/" + name.substring("buildscape:".length()) + ".ogg");
                    assertTrue(Files.isRegularFile(file), file.toString());
                }
            }
        }
    }

    @Test
    void soundEventNamesMatchTheRegisteredSanitizedIds() throws Exception {
        String source = Files.readString(resources().getParent().getParent().getParent()
                .resolve("src/main/java/com/kingodogo/buildscape/sound/ModSounds.java"));
        for (String dotted : new String[]{"block.sculk_catalyst.bloom", "block.sculk.spread", "block.sculk.charge"}) {
            assertTrue(source.contains("registerLegacySound(\"" + dotted + "\")"), dotted);
        }
    }

    @Test
    void replaceableTagAndDoubleSlabLootArePresent() throws Exception {
        Path data = resources().resolve("data/buildscape");
        JsonObject tag = JsonParser.parseString(Files.readString(data.resolve("tags/blocks/sculk_replaceable.json")))
                .getAsJsonObject();
        assertTrue(tag.getAsJsonArray("values").size() > 0);
        for (String slab : new String[]{"sculk_slab", "sculk_vertical_slab"}) {
            String loot = Files.readString(data.resolve("loot_tables/blocks/" + slab + ".json"));
            assertTrue(loot.contains("\"type\": \"double\""), slab);
        }
        String vein = Files.readString(data.resolve("loot_tables/blocks/sculk_vein.json"));
        assertEquals(1, vein.split("\"count\": 0,", -1).length - 1, "the vein drop starts from zero faces");
    }

    private static Path resources() {
        return Path.of(System.getProperty("buildscape.minecraft1182MixinConfig", "src/1.18.2/resources/buildscape.mixins.json"))
                .toAbsolutePath().getParent();
    }
}
