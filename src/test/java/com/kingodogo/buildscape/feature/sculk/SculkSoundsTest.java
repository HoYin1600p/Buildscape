package com.kingodogo.buildscape.feature.sculk;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SculkSoundsTest {
    private static final String ROOT = "assets/buildscape/";

    @Test
    public void soundDefinitionsUseVanillaVariantsAndTranslatedSubtitles() throws IOException {
        JsonObject sounds = readJson(ROOT + "sounds.json");
        JsonObject lang = readJson(ROOT + "lang/en_us.json");
        JsonArray bloom = variants(sounds, lang, "block.sculk_catalyst.bloom", "Sculk Catalyst blooms", 13);
        for (int i = 0; i < bloom.size(); i++) {
            JsonObject sound = bloom.get(i).getAsJsonObject();
            assertEquals("minecraft:enchant/soulspeed/soulspeed" + (i + 1), sound.get("name").getAsString());
            assertEquals(0.25F, sound.get("volume").getAsFloat(), 0.0F);
            assertEquals(12, sound.get("attenuation_distance").getAsInt());
        }
        JsonArray spread = variants(sounds, lang, "block.sculk.spread", "Sculk spreads", 5);
        for (int i = 0; i < spread.size(); i++) {
            JsonObject sound = spread.get(i).getAsJsonObject();
            assertEquals("buildscape:block/sculk/spread" + (i + 1), sound.get("name").getAsString());
            assertEquals(0.9F, sound.get("pitch").getAsFloat(), 0.0F);
        }
        JsonArray charge = variants(sounds, lang, "block.sculk.charge", "Sculk charges", 5);
        for (int i = 0; i < charge.size(); i++) {
            assertEquals("buildscape:block/sculk/charge" + (i + 1), charge.get(i).getAsString());
        }
        for (String event : new String[]{"block.sculk_catalyst.bloom", "block.sculk.spread", "block.sculk.charge"}) {
            for (JsonElement element : sounds.getAsJsonObject(event).getAsJsonArray("sounds")) {
                String name = element.isJsonPrimitive() ? element.getAsString()
                        : element.getAsJsonObject().get("name").getAsString();
                if (name.startsWith("buildscape:")) {
                    try (InputStream stream = resource(ROOT + "sounds/" + name.substring("buildscape:".length()) + ".ogg")) {
                        assertTrue("empty sound: " + name, stream.read() != -1);
                    }
                }
            }
        }
    }

    @Test
    public void recordingsMatchMinecraft119AssetIndex() throws IOException, NoSuchAlgorithmException {
        String[] spread = {
                "db6baadfe50569202fbbc9312b8e8ca4592d116e",
                "7b4ef660f407d0c1d10d722a10f0694617f00662",
                "ba20eabc37670971914474ea333bf37fef54d4f8",
                "7fb1c7ec4d2022bc1532819894abacfc319e6feb",
                "456b47c7a7ca032fa0f2f06926bf17231f8065f5"
        };
        String[] charge = {
                "39df0f211e29870ba32a227b0812be295ae0e48d",
                "00b9b316015118f4b9be09e02bca0859519cd0aa",
                "76f9fca45051f432eb913b3af0c44acf479a3585",
                "92a2c051ffff44b4ac2b50e471663d4e7f68c1f6",
                "3467f0cdc7291802787d3ad51915a4372145c27d"
        };
        checkRecordings("spread", spread);
        checkRecordings("charge", charge);
    }

    private static JsonArray variants(JsonObject sounds, JsonObject lang, String event, String subtitle, int count) {
        JsonObject definition = sounds.getAsJsonObject(event);
        assertNotNull("missing sound event: " + event, definition);
        String key = "subtitles.buildscape." + event;
        assertEquals(key, definition.get("subtitle").getAsString());
        assertEquals(subtitle, lang.get(key).getAsString());
        JsonArray variants = definition.getAsJsonArray("sounds");
        assertEquals(count, variants.size());
        return variants;
    }

    private static void checkRecordings(String prefix, String[] hashes) throws IOException, NoSuchAlgorithmException {
        for (int i = 0; i < hashes.length; i++) {
            String path = ROOT + "sounds/block/sculk/" + prefix + (i + 1) + ".ogg";
            try (InputStream stream = resource(path)) {
                byte[] digest = MessageDigest.getInstance("SHA-1").digest(stream.readAllBytes());
                assertEquals(path, hashes[i], HexFormat.of().formatHex(digest));
            }
        }
    }

    private static JsonObject readJson(String path) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(resource(path), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static InputStream resource(String path) {
        InputStream stream = SculkSoundsTest.class.getClassLoader().getResourceAsStream(path);
        assertNotNull("missing resource: " + path, stream);
        return stream;
    }
}
