package com.kingodogo.buildscape.config;

import com.kingodogo.buildscape.BuildscapeCommon;
import com.kingodogo.buildscape.platform.Services;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/** Resolves config directories under the loader config directory, migrating the old relative location. */
final class ConfigPaths {
    private static final Path LEGACY_ROOT = Path.of("config");

    private ConfigPaths() {
    }

    static Path root() {
        try {
            Path dir = Services.PLATFORM.getConfigDir();
            if (dir != null) {
                return dir;
            }
        } catch (Throwable t) {
            BuildscapeCommon.LOGGER.debug("Platform config directory unavailable, using legacy location", t);
            // Platform not available (for example in unit tests): use the legacy relative location.
        }
        return LEGACY_ROOT;
    }

    /** Returns (creating it) the loader config directory plus sub-path, copying files from the old relative location once. */
    static Path dir(String... subPath) {
        Path target = root();
        Path legacy = LEGACY_ROOT;
        for (String part : subPath) {
            target = target.resolve(part);
            legacy = legacy.resolve(part);
        }
        try {
            Files.createDirectories(target);
            if (!target.toAbsolutePath().normalize().equals(legacy.toAbsolutePath().normalize()) && Files.isDirectory(legacy)) {
                migrate(legacy, target);
            }
        } catch (IOException e) {
            BuildscapeCommon.logError("ConfigPaths: failed to prepare " + target, e);
        }
        return target;
    }

    private static void migrate(Path from, Path to) throws IOException {
        try (Stream<Path> files = Files.list(from)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                Path dest = to.resolve(file.getFileName().toString());
                if (Files.isRegularFile(file) && !Files.exists(dest)) {
                    Files.copy(file, dest, StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        }
    }
}
