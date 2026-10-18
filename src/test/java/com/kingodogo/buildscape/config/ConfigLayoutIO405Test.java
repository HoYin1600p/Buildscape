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

    @Test
    public void commentsFollowReorderedKeysAndSortedItemsIncludingInlineAndNestedComments() throws Exception {
        String source = "{\n// speed note\n\"speed\":1, // inline speed\n"
                + "// palette note\n\"items\":[\n// late section\n\"z\", // inline z\n"
                + "// early section\n\"a\"],\n// gone note\n\"gone\":2,\n"
                + "\"custom\":{\"speed\":3 // nested speed\n}}";
        String generated = "{\n  \"items\": [\n    \"a\",\n    \"z\"\n  ],\n"
                + "  \"custom\": {\"speed\": 3},\n  \"speed\": 1\n}\n";
        String output = ConfigLayoutIO.retainComments(source, generated, true);
        assertTrue(output.indexOf("// gone note") < output.indexOf('{'));
        assertTrue(output.indexOf("// palette note") < output.indexOf("\"items\""));
        assertTrue(output.indexOf("// late section") > output.indexOf("\"a\""));
        assertTrue(output.indexOf("// late section") < output.indexOf("\"z\""));
        assertTrue(output.indexOf("// inline z") < output.indexOf("\"z\""));
        assertTrue(output.indexOf("// early section") < output.indexOf("\"a\""));
        assertTrue(output.indexOf("// nested speed") > output.indexOf("\"custom\""));
        assertTrue(output.indexOf("// nested speed") < output.indexOf("\"speed\""));
        assertTrue(output.indexOf("// inline speed") > output.indexOf("\"custom\""));
        assertTrue(output.indexOf("// inline speed") < output.lastIndexOf("\"speed\""));
        assertEquals(ConfigLayoutIO.readProperties(generated), ConfigLayoutIO.readProperties(output));
        assertEquals(output, ConfigLayoutIO.retainComments(output, generated, true));
    }

    @Test
    public void missingItemCommentsStayInOrderAboveTheListAndRepeatedCommentsStayAttached() throws Exception {
        String source = "{\n// list note\n\"items\":[\n// removed section\n\"gone\", // removed inline\n"
                + "// kept section\n\"kept\"],\n// duplicate\n\"x\":1,\n// duplicate\n\"y\":2}";
        String generated = "{\n\"y\":2,\n\"items\":[\"kept\"],\n\"x\":1}";
        String output = ConfigLayoutIO.retainComments(source, generated, true);
        int list = output.indexOf("\"items\"");
        assertTrue(output.indexOf("// list note") < output.indexOf("// removed section"));
        assertTrue(output.indexOf("// removed section") < output.indexOf("// removed inline"));
        assertTrue(output.indexOf("// removed inline") < list);
        assertTrue(output.indexOf("// kept section") > list);
        assertEquals(2, output.split("// duplicate", -1).length - 1);
        assertEquals(ConfigLayoutIO.readProperties(generated), ConfigLayoutIO.readProperties(output));
    }

    @Test
    public void cfgCommentsStayWithKeysIncludingDuplicateTextAndRemovedKeys() {
        String source = "# old x\nx = 1\n# repeated\ny = 2\n# repeated\nz = 3\n# removed\nmissing = 0\n";
        String generated = "z = 3\nx = 1\ny = 2\n";
        String output = ConfigLayoutIO.retainComments(source, generated, false);
        assertTrue(output.startsWith("# removed\n"));
        assertTrue(output.contains("# old x\nx = 1"));
        assertTrue(output.contains("# repeated\ny = 2"));
        assertTrue(output.contains("# repeated\nz = 3"));
    }

    @Test
    public void writeAllowsMissingFilesButNeverReplacementDecodedBytes() throws Exception {
        Path file = temporary.getRoot().toPath().resolve("config.json");
        ConfigLayoutIO.write(file, "new", "remembered deleted contents", true);
        assertEquals("new", ConfigLayoutIO.readText(file));
        Files.write(file, new byte[]{(byte) 0x81});
        String decoded = ConfigLayoutIO.readText(file);
        org.junit.Assume.assumeTrue(ConfigLayoutIO.hasReplacementCharacters(decoded));
        assertThrows(IOException.class, () -> ConfigLayoutIO.write(file, "replacement", decoded, true));
        assertArrayEquals(new byte[]{(byte) 0x81}, Files.readAllBytes(file));
    }

    @Test
    public void commentsBetweenKeyAndValueOrAfterArrayOpeningKeepTheirSetting() throws Exception {
        String source = "{\"a\":1,\"b\":/* b note */2,\"items\":[/* z section */\"z\",\"a\"]}";
        String generated = "{\n\"items\":[\"a\",\"z\"],\n\"b\":2,\n\"a\":1}";
        String output = ConfigLayoutIO.retainComments(source, generated, true);
        assertTrue(output.indexOf("/* b note */") < output.indexOf("\"b\""));
        assertTrue(output.indexOf("/* b note */") > output.indexOf("\"items\""));
        assertTrue(output.indexOf("/* z section */") > output.indexOf("\"a\""));
        assertTrue(output.indexOf("/* z section */") < output.indexOf("\"z\""));
        assertEquals(ConfigLayoutIO.readProperties(generated), ConfigLayoutIO.readProperties(output));
    }
}
