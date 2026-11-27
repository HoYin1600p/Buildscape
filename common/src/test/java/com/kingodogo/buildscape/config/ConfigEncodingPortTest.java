package com.kingodogo.buildscape.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigEncodingPortTest {
    @TempDir Path directory;

    @Test
    void legacyTextMigratesToUtf8WithoutLosingOriginalBytes() throws Exception {
        Path file = directory.resolve("properties.json");
        String text = "{\"name\":\"Caf\u00e9\"}";
        byte[] original = text.getBytes(Charset.forName("windows-1252"));
        Files.write(file, original);

        assertEquals(text, ConfigLayoutIO.readText(file));
        assertEquals(text, Files.readString(file, StandardCharsets.UTF_8));
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("properties.json.bak")));
        ConfigLayoutIO.write(file, "{\"name\":\"Caf\u00e9 edited\"}", text, true);
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("properties.json.bak")));
    }

    @Test
    void malformedUnicodeCannotBeSilentlyOverwritten() throws Exception {
        Path file = directory.resolve("properties.json");
        String text = "{\"name\":\"\uFFFD\"}";
        Files.writeString(file, text, StandardCharsets.UTF_8);

        assertThrows(java.io.IOException.class,
                () -> ConfigLayoutIO.write(file, "{}", text, true));
        assertEquals(text, Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void clientSettingsPreserveUserCommentsAndLegacyText() throws Exception {
        Path file = directory.resolve("buildscape/buildscape.cfg");
        Files.createDirectories(file.getParent());
        String source = "# Caf\u00e9 display preference\nUITheme = dark\n";
        byte[] original = source.getBytes(Charset.forName("windows-1252"));
        Files.write(file, original);
        BuildscapeClientConfig config = new BuildscapeClientConfig(directory, false);
        config.setUiTheme("light");

        String saved = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(saved.contains("# Caf\u00e9 display preference"));
        assertTrue(saved.contains("UITheme = light"));
        assertArrayEquals(original, Files.readAllBytes(file.resolveSibling("buildscape.cfg.bak")));
    }
}
