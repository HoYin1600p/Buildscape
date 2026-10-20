package com.kingodogo.buildscape.core.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AtomicFilesTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writesNewAndReplacesExistingFilesAndCreatesParents() throws IOException {
        Path target = folder.getRoot().toPath().resolve("a").resolve("b").resolve("data.bin");
        AtomicFiles.write(target, "one".getBytes(StandardCharsets.UTF_8));
        assertEquals("one", Files.readString(target));
        AtomicFiles.write(target, "two".getBytes(StandardCharsets.UTF_8));
        assertEquals("two", Files.readString(target));
        try (Stream<Path> siblings = Files.list(target.getParent())) {
            assertEquals("no temporary file may stay behind", 1, siblings.count());
        }
    }

    @Test
    public void failedWriteKeepsOldContentAndRemovesTemporaryFile() throws IOException {
        Path target = folder.getRoot().toPath().resolve("data.txt");
        Files.writeString(target, "old");
        assertThrows(IOException.class, () -> AtomicFiles.write(target, temporary -> {
            Files.writeString(temporary, "half");
            throw new IOException("boom");
        }));
        assertEquals("old", Files.readString(target));
        try (Stream<Path> siblings = Files.list(folder.getRoot().toPath())) {
            assertEquals(1, siblings.count());
        }
    }
}
