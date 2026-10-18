package com.kingodogo.buildscape.config;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.recipe.framework.cache.RecipeCacheRegressionTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ConfigPersistence405Test {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @BeforeClass
    public static void bootstrap() {
        RecipeCacheRegressionTest.bootstrap();
    }

    @Test
    public void clientConfigAppendsMissingDefaultsAndExplicitSaveKeepsUnknownSettings() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        String original = "# my settings\nHideBuildscapeConfig = true\nMaxPipeNetworkSize = 23\n"
                + "UITheme = light\nLastActiveTab = 4\nSidePanelExpanded = true\n";
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertTrue(Files.readString(file).startsWith(original));
        assertEquals("true", ConfigLayoutIO.readEntries(Files.readString(file)).get("isCakeStack"));
        assertTrue(config.isConfigButtonHidden());
        assertEquals(23, config.getMaxPipeNetworkSize());
        config.save();
        String output = Files.readString(file);
        assertTrue(output.contains("# my settings"));
        assertTrue(output.contains("# --- Gameplay ---"));
        assertEquals("light", ConfigLayoutIO.readEntries(output).get("UITheme"));
        assertEquals("4", ConfigLayoutIO.readEntries(output).get("LastActiveTab"));
        assertEquals("true", ConfigLayoutIO.readEntries(output).get("SidePanelExpanded"));
        assertFalse(Files.exists(root.resolve("buildscape/ui-state.cfg")));
        assertEquals(original, Files.readString(file.resolveSibling("buildscape.cfg.pre-4.0.5.bak")));
        config.save();
        assertEquals(output, Files.readString(file));
        Files.writeString(file, "MaxPipeNetworkSize = 31");
        config.save();
        assertEquals("MaxPipeNetworkSize = 31", Files.readString(file));
    }

    @Test
    public void ambiguousClientConfigIsSalvagedButReadOnly() throws Exception {
        Path root = temporary.newFolder().toPath();
        Path file = root.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        String original = "HideBuildscapeConfig = true\nlegacy line\nMaxPipeNetworkSize = 12\nMaxPipeNetworkSize = 19\n";
        Files.writeString(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(root);
        assertTrue(config.isConfigButtonHidden());
        assertEquals(19, config.getMaxPipeNetworkSize());
        config.save();
        assertEquals(original, Files.readString(file));
    }

    @Test
    public void propertiesKeepAliasesPrecisionCommentsAndUnknownPolicy() throws Exception {
        Path directory = temporary.newFolder().toPath();
        Path file = directory.resolve("pillar-properties.json");
        String original = "{\n// user note\n\"template\":\"spiral\",\"use_template\":true,"
                + "\"template_speed\":0.12345678901234567890,\"particle_speed\":0.02000,"
                + "\"particle_color\":[\"#abcdef\"],\"max_particle_color\":1,"
                + "\"edit_permission_mode\":\"OPEN\",\"custom\":{\"n\":12345678901234567890.12500}}";
        Files.writeString(file, original);
        PillarParticleConfig config = new PillarParticleConfig(directory);
        loadProperties(config);
        assertEquals(original, Files.readString(file));
        assertTrue(config.use_pattern);
        assertEquals("spiral", config.pattern);
        config.savePropertiesToDisk();
        String first = Files.readString(file);
        JsonObject output = ConfigLayoutIO.readProperties(first);
        assertTrue(first.contains("// user note"));
        assertTrue(first.contains("// --- Visuals ---"));
        assertEquals("0.02000", output.get("particle_speed").getAsString());
        assertEquals("0.12345678901234567890", output.get("pattern_speed").getAsString());
        assertEquals("12345678901234567890.12500", output.getAsJsonObject("custom").get("n").getAsString());
        assertEquals("OPEN", output.get("edit_permission_mode").getAsString());
        assertEquals("spiral", output.get("template").getAsString());
        config.pattern = "quote\"slash\\";
        config.savePropertiesToDisk();
        assertEquals(config.pattern, ConfigLayoutIO.readProperties(Files.readString(file)).get("pattern").getAsString());
        assertEquals(original, Files.readString(file.resolveSibling("pillar-properties.json.pre-4.0.5.bak")));
        Files.writeString(file, "{\"pattern\":\"external\"}");
        config.savePropertiesToDisk();
        assertEquals("{\"pattern\":\"external\"}", Files.readString(file));
    }

    @Test
    public void lenientAndBrokenPropertiesAreNeverOverwritten() throws Exception {
        for (String[] fixture : new String[][]{
                {"{pattern:'spiral',use_template:true,particle_color:'#abcdef'}", "spiral"},
                // The legacy map reader rejects duplicate keys and retains the default pattern.
                {"{\"pattern\":\"ring\",\"pattern\":\"spiral\"}", "ring"},
                {"{broken", "ring"}}) {
            String original = fixture[0];
            Path directory = temporary.newFolder().toPath();
            Path file = directory.resolve("pillar-properties.json");
            Files.writeString(file, original);
            PillarParticleConfig config = new PillarParticleConfig(directory);
            loadProperties(config);
            assertEquals(fixture[1], config.pattern);
            assertEquals(original, Files.readString(file));
            config.pattern = "fountain";
            config.savePropertiesToDisk();
            assertEquals(original, Files.readString(file));
            assertFalse(Files.exists(file.resolveSibling("pillar-properties.json.pre-4.0.5.bak")));
        }
    }

    @Test
    public void remoteMirrorsCannotWriteEvenWithoutAnActiveConnection() throws Exception {
        Path directory = temporary.newFolder().toPath().resolve("never-created");
        PillarParticleConfig config = new PillarParticleConfig(directory);
        Field remote = PillarParticleConfig.class.getDeclaredField("remote");
        remote.setAccessible(true);
        remote.setBoolean(config, true);
        config.saveItemsToDisk();
        config.savePropertiesToDisk();
        config.saveItems();
        config.saveProperties();
        assertFalse(Files.exists(directory));
    }

    private static void loadProperties(PillarParticleConfig config) throws Exception {
        Method load = PillarParticleConfig.class.getDeclaredMethod("loadPropertiesInternal");
        load.setAccessible(true);
        load.invoke(config);
    }
}
