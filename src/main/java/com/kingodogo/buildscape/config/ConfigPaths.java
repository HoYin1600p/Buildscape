package com.kingodogo.buildscape.config;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.BuildScape;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/** Resolves config directories under the loader config directory, migrating the old relative location. */
public final class ConfigPaths {
    private static final Path LEGACY_ROOT = Path.of("config");

    private ConfigPaths() {
    }

    public static Path root() {
        try {
            if (FMLPaths.CONFIGDIR != null) {
                return FMLPaths.CONFIGDIR.get();
            }
        } catch (Throwable t) {
            BuildScape.LOGGER.debug("Platform config directory unavailable, using legacy location", t);
        }
        return LEGACY_ROOT;
    }

    /** Returns (creating it) the loader config directory plus sub-path, copying files from the old relative location once. */
    public static Path dir(String... subPath) {
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
            BuildScape.LOGGER.error("ConfigPaths: failed to prepare {}", target, e);
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

    /** Copies a bundled template from classpath resources to destination if destination does not exist. */
    public static boolean copyTemplateIfMissing(Path destination, String resourcePath) {
        if (Files.exists(destination)) {
            return false;
        }
        try {
            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }
            try (InputStream input = BuildScape.class.getResourceAsStream(resourcePath)) {
                if (input != null) {
                    Files.copy(input, destination);
                    return true;
                }
            }
        } catch (IOException e) {
            BuildScape.LOGGER.warn("ConfigPaths: failed to copy template {} to {}", resourcePath, destination, e);
        }
        return false;
    }

    /** Loads default items from bundled JSON template resources without modifying any files on disk. */
    public static Set<String> loadDefaultItems(boolean vaultLoaded) {
        String template = vaultLoaded ? "/data/buildscape/config/vault_pillar_items.json" : "/data/buildscape/config/vanilla_pillar_items.json";
        try (InputStream input = BuildScape.class.getResourceAsStream(template)) {
            if (input != null) {
                String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                return PillarParticleConfig.parseItems(json);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.warn("ConfigPaths: failed to load default items from template {}", template, e);
        }
        return Collections.emptySet();
    }

    static JsonObject loadItemsTemplate(String name) throws IOException {
        String resource = "/data/buildscape/config/" + name + ".json";
        try (InputStream input = BuildScape.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IOException("Missing config template: " + resource);
            }
            String source = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return ConfigLayoutIO.readProperties(ConfigLayoutIO.itemsJson(source));
        }
    }
}
