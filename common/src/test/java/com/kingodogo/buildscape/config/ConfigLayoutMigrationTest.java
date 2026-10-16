package com.kingodogo.buildscape.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLayoutMigrationTest {
    @TempDir Path directory;

    // These resources are verbatim copies of the retained 4.0.x server files.
    private String sample(String name) throws IOException {
        try (var stream = getClass().getResourceAsStream("/config-migration/" + name)) {
            assertNotNull(stream);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Path mainFile() throws IOException {
        Path file = directory.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        return file;
    }

    private static Map<String, String> entries(String text) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String line : text.lines().toList()) {
            if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
            String[] pair = line.split("=", 2);
            assertEquals(2, pair.length, line);
            assertNull(values.put(pair[0].trim(), pair[1].trim()), "duplicate key");
        }
        return values;
    }

    private static void assertOriginalEntries(Map<String, String> original, Map<String, String> migrated) {
        original.forEach((key, value) -> assertEquals(value, migrated.get(key), key));
    }

    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    private static void assertOriginalJson(JsonObject original, JsonObject migrated) {
        for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
            assertEquals(entry.getValue(), migrated.get(entry.getKey()), entry.getKey());
        }
    }

    private static PillarParticleConfig loadProperties(Path dir) throws Exception {
        PillarParticleConfig config = new PillarParticleConfig(dir);
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
        return config;
    }

    @Test
    void oldServerConfigRetainsEveryValueAndUnknownKeyAcrossRepeatedMigrations() throws Exception {
        String original = sample("buildscape.cfg")
                .replace("MaxPipeNetworkSize = 64", "MaxPipeNetworkSize = 137")
                .replace("isCakeStack = true", "isCakeStack = false")
                .replace("isWaterbottleStack = true", "isWaterbottleStack = false")
                .replace("OptimizeBuildscapeModelLoading = true", "OptimizeBuildscapeModelLoading = false")
                .replace("UITheme = dark", "UITheme = custom")
                .replace("AnimationSpeed = 0", "AnimationSpeed = 2")
                + "\nFutureSetting = a=b:c\\d\nEmptySetting =\n";
        Path file = mainFile();
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, false);
        assertEquals(137, config.getMaxPipeNetworkSize());
        assertFalse(config.isCakeStackingEnabled());
        assertFalse(config.isWaterBottleStackingEnabled());
        assertFalse(config.isParallelModelLoadingEnabled());
        config.save();
        String first = Files.readString(file);
        assertOriginalEntries(entries(original), entries(first));
        assertEquals(original, Files.readString(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        int previous = -1;
        for (String section : new String[]{"Gameplay", "Performance", "Visuals & particles", "Interface", "Other"}) {
            int position = first.indexOf("# --- " + section + " ---");
            assertTrue(position > previous, section);
            previous = position;
        }
        String preceding = "";
        for (String line : first.lines().toList()) {
            if (line.contains(" = ")) assertTrue(preceding.startsWith("# "), line);
            preceding = line;
        }
        new BuildscapeClientConfig(directory, false).save();
        assertEquals(first, Files.readString(file));
        assertEquals(original, Files.readString(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        try (var files = Files.list(file.getParent())) {
            assertEquals(1L, files.filter(path -> path.toString().endsWith(".bak")).count());
        }
        assertFalse(Files.exists(directory.resolve("buildscape/ui-state.cfg")));
    }

    @Test
    void legacyRootSampleLoadsWithoutChangingItsSource() throws Exception {
        String original = sample("legacy-buildscape.cfg");
        Path legacy = directory.resolve("buildscape.cfg");
        Files.writeString(legacy, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, false);
        config.save();
        assertOriginalEntries(entries(original), entries(Files.readString(mainFile())));
        assertEquals(original, Files.readString(legacy));
        assertEquals(original, Files.readString(mainFile().resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
    }

    @Test
    void clientStateMigratesOnceAndClicksOnlyWriteTheStateFile() throws Exception {
        String original = sample("buildscape.cfg").replace("LastActiveTab = 0", "LastActiveTab = 5")
                .replace("SidePanelExpanded = false", "SidePanelExpanded = true");
        Path file = mainFile();
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, true);
        assertEquals(5, config.getLastActiveTab());
        assertTrue(config.isSidePanelExpanded());
        config.save();
        Path state = file.resolveSibling("ui-state.cfg");
        Map<String, String> main = entries(Files.readString(file));
        assertFalse(main.containsKey("LastActiveTab"));
        assertFalse(main.containsKey("SidePanelExpanded"));
        Map<String, String> combined = new LinkedHashMap<>(main);
        combined.putAll(entries(Files.readString(state)));
        assertOriginalEntries(entries(original), combined);
        assertEquals(original, Files.readString(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        String grouped = Files.readString(file);
        Files.setLastModifiedTime(file, FileTime.fromMillis(123456000L));
        FileTime timestamp = Files.getLastModifiedTime(file);
        BuildscapeClientConfig reloaded = new BuildscapeClientConfig(directory, true);
        assertEquals(5, reloaded.getLastActiveTab());
        reloaded.setLastActiveTab(2);
        reloaded.setSidePanelExpanded(false);
        assertEquals(grouped, Files.readString(file));
        assertEquals(timestamp, Files.getLastModifiedTime(file));
        assertEquals("2", entries(Files.readString(state)).get("LastActiveTab"));
        reloaded.save();
        assertEquals(grouped, Files.readString(file));
    }

    @Test
    void dedicatedServerDoesNotReadOrMoveClientState() throws Exception {
        Path file = mainFile();
        Files.writeString(file, sample("buildscape.cfg"));
        // The headless platform reports isClient=false, exercising the production constructor.
        BuildscapeClientConfig server = new BuildscapeClientConfig(directory);
        server.save();
        assertTrue(entries(Files.readString(file)).containsKey("LastActiveTab"));
        assertTrue(entries(Files.readString(file)).containsKey("SidePanelExpanded"));
        assertFalse(Files.exists(file.resolveSibling("ui-state.cfg")));
        Path fresh = directory.resolve("fresh");
        new BuildscapeClientConfig(fresh).save();
        assertFalse(Files.exists(fresh.resolve("buildscape/ui-state.cfg")));
    }

    @Test
    void malformedConfigIsReadOnlyWhileUsableValuesStillLoad() throws Exception {
        String original = "MaxPipeNetworkSize = 321\nbroken line\nLastActiveTab = 4\n";
        Path file = mainFile();
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, true);
        assertEquals(321, config.getMaxPipeNetworkSize());
        config.setLastActiveTab(2);
        config.setUiTheme("light");
        config.save();
        assertEquals(original, Files.readString(file));
        assertFalse(Files.exists(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        assertFalse(Files.exists(file.resolveSibling("ui-state.cfg")));
    }

    @Test
    void oldPillarPropertiesRoundTripWithCommentsUnknownValuesAndOneBackup() throws Exception {
        String original = sample("pillar-properties.json").replace("\"particle_speed\": 0.02", "\"particle_speed\": 0.123")
                .replace("\"pattern_speed\": 0.05", "\"pattern_speed\": 0.321")
                .replace("\"pattern\": \"ring\"", "\"pattern\": \"spiral\"");
        original = original.substring(0, original.lastIndexOf('}'))
                + ",\n\"future\": {\"nullValue\": null, \"nested\": [true, 12345678901234567890, \"// literal\"]}\n}";
        Path file = directory.resolve("pillar-properties.json");
        Files.writeString(file, original);
        PillarParticleConfig config = loadProperties(directory);
        assertEquals(0.123, config.particle_speed);
        assertEquals("spiral", config.pattern);
        config.savePropertiesToDisk();
        String first = Files.readString(file);
        assertOriginalJson(json(original), json(first));
        assertTrue(first.contains("// Hexadecimal particle color palette (up to 7 colors)"));
        assertTrue(first.contains("// Pattern system (overrides basic particle parameters)"));
        assertTrue(first.indexOf("// --- Visuals ---") < first.indexOf("// --- Timing & patterns ---"));
        assertTrue(first.indexOf("\"future\"") < first.indexOf("\"edit_permission_mode\""));
        Path backup = file.resolveSibling("pillar-properties.json.pre-4.0.5.bak");
        assertEquals(original, Files.readString(backup));
        loadProperties(directory).savePropertiesToDisk();
        assertEquals(first, Files.readString(file));
        config = loadProperties(directory);
        config.pattern_speed = 0.75;
        config.savePropertiesToDisk();
        assertTrue(Files.readString(file).contains("// Pattern system (overrides basic particle parameters)"));
        assertEquals(0.75, json(Files.readString(file)).get("pattern_speed").getAsDouble());
        assertEquals(json(original).get("future"), json(Files.readString(file)).get("future"));
        assertEquals(original, Files.readString(backup));
        try (var files = Files.list(directory)) {
            assertEquals(1L, files.filter(path -> path.toString().endsWith(".bak")).count());
        }
    }

    @Test
    void runtimeNormalizationDoesNotRewriteTheOwnersStoredValues() throws Exception {
        String original = "{\"particle_speed\":-1,\"particle_density\":0,\"particle_color\":[\"#aabbcc\"],"
                + "\"max_particle_color\":99,\"pattern\":null,\"edit_permission_mode\":\"open\"}";
        Path file = directory.resolve("pillar-properties.json");
        Files.writeString(file, original);
        PillarParticleConfig config = loadProperties(directory);
        assertEquals(0.02, config.particle_speed);
        assertEquals(PillarParticleConfig.EditPermissionMode.OPEN, config.edit_permission_mode);
        config.savePropertiesToDisk();
        assertOriginalJson(json(original), json(Files.readString(file)));
        String first = Files.readString(file);
        loadProperties(directory).savePropertiesToDisk();
        assertEquals(first, Files.readString(file));
    }

    @Test
    void propertyStringsAreEscapedAndDefaultCommentsSurviveAllSaves() throws Exception {
        PillarParticleConfig config = loadProperties(directory);
        Path file = directory.resolve("pillar-properties.json");
        String defaults = Files.readString(file);
        config.pattern = "quote\" slash\\ newline\n // literal";
        config.savePropertiesToDisk();
        assertEquals(config.pattern, json(Files.readString(file)).get("pattern").getAsString());
        for (String line : defaults.lines().filter(value -> value.stripLeading().startsWith("//")).toList()) {
            assertTrue(Files.readString(file).contains(line), line);
        }
        String first = Files.readString(file);
        loadProperties(directory).savePropertiesToDisk();
        assertEquals(first, Files.readString(file));
    }

    @Test
    void legacyTrailingCommaLoadsWithoutChangingTheFile() throws Exception {
        assertLegacyPropertiesRemainReadOnly("{\n\"particle_speed\":0.127,\"pattern\":\"spiral\","
                + "\"particle_color\":[\"#AABBCC\",],\"max_particle_color\":1,\"use_pattern\":false\n}");
    }

    @Test
    void legacySingleQuotedStringsLoadWithoutChangingTheFile() throws Exception {
        assertLegacyPropertiesRemainReadOnly("{\n'particle_speed':0.127,'pattern':'spiral',"
                + "'particle_color':['#AABBCC'],'max_particle_color':1,'use_pattern':false\n}");
    }

    private void assertLegacyPropertiesRemainReadOnly(String original) throws Exception {
        // Reproduce the 4.0.4 parser independently of the strict migration reader.
        Method strip = PillarParticleConfig.class.getDeclaredMethod("stripComments", String.class);
        strip.setAccessible(true);
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        java.lang.reflect.Type mapType = new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> oldMap = gson.fromJson((String) strip.invoke(null, original), mapType);
        PillarParticleConfig expected = gson.fromJson(gson.toJson(oldMap), PillarParticleConfig.class);
        assertEquals(0.127, expected.particle_speed);
        Path file = directory.resolve("pillar-properties.json");
        byte[] bytes = original.getBytes(StandardCharsets.UTF_8);
        Files.write(file, bytes);
        PillarParticleConfig config = loadProperties(directory);
        assertEquals(expected.particle_speed, config.particle_speed);
        assertEquals(expected.pattern, config.pattern);
        assertEquals(expected.use_pattern, config.use_pattern);
        // The old loader ignores the null element produced by a lenient trailing array comma.
        assertEquals(expected.particle_color.stream().filter(java.util.Objects::nonNull).toList(), config.particle_color);
        assertEquals(expected.max_particle_color, config.max_particle_color);
        assertArrayEquals(bytes, Files.readAllBytes(file));
        config.pattern = "ring";
        config.savePropertiesToDisk();
        config.savePropertiesToDisk();
        assertArrayEquals(bytes, Files.readAllBytes(file));
        try (var files = Files.list(directory)) {
            assertEquals(java.util.List.of(file), files.toList());
        }
    }

    @Test
    void malformedPropertiesAreNeverRewrittenEvenWithoutAnInitialLoad() throws Exception {
        Path file = directory.resolve("pillar-properties.json");
        for (String original : new String[]{"{invalid", "{\"pattern\":\"ring\"} trailing", "{} /* unfinished",
                "{\"particle_speed\":1,\"particle_speed\":2}", "{\"particle_color\":{}}"}) {
            Files.writeString(file, original);
            PillarParticleConfig config = loadProperties(directory);
            config.savePropertiesToDisk();
            assertEquals(original, Files.readString(file));
            config = new PillarParticleConfig(directory);
            config.savePropertiesToDisk();
            config.savePropertiesToDisk();
            assertEquals(original, Files.readString(file));
            assertFalse(Files.exists(file.resolveSibling("pillar-properties.json.pre-4.0.5.bak")));
        }
    }

    @Test
    void anExistingBackupIsNeverOverwrittenAndExternalEditsAreNotLost() throws Exception {
        Path file = mainFile();
        Files.writeString(file, sample("buildscape.cfg"));
        Path backup = file.resolveSibling("buildscape.cfg.pre-4.0.5.bak");
        Files.writeString(backup, "retained backup");
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, false);
        config.save();
        assertEquals("retained backup", Files.readString(backup));
        Files.writeString(file, "MaxPipeNetworkSize = 345\n");
        config.save();
        assertEquals("MaxPipeNetworkSize = 345\n", Files.readString(file));
        Path properties = directory.resolve("pillar-properties.json");
        Files.writeString(properties, sample("pillar-properties.json"));
        PillarParticleConfig pillar = loadProperties(directory);
        Files.writeString(properties, "{\"particle_speed\":0.7}");
        pillar.savePropertiesToDisk();
        assertEquals("{\"particle_speed\":0.7}", Files.readString(properties));
    }

    @Test
    void templateAliasesAndUnloadedSavesRetainTheirValues() throws Exception {
        Path file = directory.resolve("pillar-properties.json");
        String original = "{\"use_template\":false,\"template\":\"spiral\",\"template_speed\":0.2,\"future\":null}";
        Files.writeString(file, original);
        new PillarParticleConfig(directory).savePropertiesToDisk();
        JsonObject migrated = json(Files.readString(file));
        assertOriginalJson(json(original), migrated);
        assertFalse(migrated.get("use_pattern").getAsBoolean());
        assertEquals("spiral", migrated.get("pattern").getAsString());
        assertEquals(0.2, migrated.get("pattern_speed").getAsDouble());
        String first = Files.readString(file);
        loadProperties(directory).savePropertiesToDisk();
        assertEquals(first, Files.readString(file));
    }
}
