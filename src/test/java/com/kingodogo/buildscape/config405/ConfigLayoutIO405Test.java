package com.kingodogo.buildscape.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ConfigLayoutIO405Test {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    public void strictReadersRejectAmbiguousOrLegacyInput() throws Exception {
        for (String text : new String[]{"{\"x\":1,\"x\":2}", "{x:1}", "{\"x\":1,}",
                "{\"x\":1} {}", "null", "{ /* unfinished"}) {
            assertThrows(IOException.class, () -> ConfigLayoutIO.readProperties(text));
        }
        assertThrows(IOException.class, () -> ConfigLayoutIO.readEntries("x = 1\nx = 2"));
        assertThrows(IOException.class, () -> ConfigLayoutIO.readEntries("legacy garbage"));
        assertEquals("#ff00ff", ConfigLayoutIO.readEntries("Color = #ff00ff").get("Color"));
    }

    @Test
    public void commentsDoNotConsumeQuotedUrlsOrEscapeSequences() throws Exception {
        String source = "/* keep block */\n{\"url\":\"https://example.test/a/*b*/\","
                + "\"escaped\":\"quote\\\"//still a string\",\"n\":12345678901234567890.12300 // keep line\n}";
        String output = ConfigLayoutIO.retainComments(source,
                ConfigLayoutIO.readProperties(source).toString(), true);
        assertTrue(output.contains("/* keep block */"));
        assertTrue(output.contains("// keep line"));
        assertEquals("12345678901234567890.12300", ConfigLayoutIO.readProperties(output).get("n").getAsString());
        assertEquals(ConfigLayoutIO.readProperties(source), ConfigLayoutIO.readProperties(output));
        assertEquals(output, ConfigLayoutIO.retainComments(output,
                ConfigLayoutIO.readProperties(source).toString(), true));
        assertEquals("Color = #ff00ff\n", ConfigLayoutIO.retainComments("Color = #ff00ff",
                "Color = #ff00ff\n", false));
    }

    @Test
    public void backupIsOnceAndDiskChangesAlwaysWin() throws Exception {
        Path file = temporary.getRoot().toPath().resolve("pillar-properties.json");
        Files.writeString(file, "original");
        ConfigLayoutIO.write(file, "first save", "original", true);
        Path backup = file.resolveSibling("pillar-properties.json.pre-4.0.5.bak");
        assertEquals("original", Files.readString(backup));
        ConfigLayoutIO.write(file, "second save", "first save", true);
        assertEquals("original", Files.readString(backup));
        Files.writeString(file, "external edit");
        assertThrows(IOException.class, () -> ConfigLayoutIO.write(file, "stale save", "second save", true));
        assertEquals("external edit", Files.readString(file));
    }
}
