package com.kingodogo.buildscape.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class ConfigBackup {
    private ConfigBackup() { }

    /** A failed backup must abort the caller's write so the original remains recoverable. */
    static Path copyCorrupted(Path file) throws IOException {
        Path backup = file.resolveSibling(file.getFileName() + ".corrupted-"
                + java.time.Instant.now().toString().replace(':', '-'));
        return Files.copy(file, backup);
    }
}
