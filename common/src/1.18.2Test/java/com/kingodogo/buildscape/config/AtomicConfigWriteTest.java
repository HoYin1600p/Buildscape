package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.core.io.AtomicFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AtomicConfigWriteTest {
    @TempDir Path directory;

    @Test
    void failedWriterLeavesOriginalAndNoTemporaryFile() throws Exception {
        Path target = directory.resolve("config.json");
        Files.writeString(target, "original");
        assertThrows(IOException.class, () -> AtomicFiles.write(target, temporary -> {
            Files.writeString(temporary, "partial");
            throw new IOException("Interrupted write");
        }));
        assertEquals("original", Files.readString(target));
        try (var siblings = Files.list(directory)) {
            assertEquals(1, siblings.count());
        }
    }

    @Test
    void successfulWriterCreatesParentsAndReplacesExistingFile() throws Exception {
        Path target = directory.resolve("nested/config.json");
        AtomicFiles.write(target, temporary -> Files.writeString(temporary, "first"));
        AtomicFiles.write(target, temporary -> Files.writeString(temporary, "second"));
        assertEquals("second", Files.readString(target));
        try (var siblings = Files.list(target.getParent())) {
            assertEquals(1, siblings.count());
        }
    }
}
