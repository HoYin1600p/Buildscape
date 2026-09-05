package com.kingodogo.buildscape.recipe.framework.cache;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BundledRecipeCacheSync {

    private static final String[] CATEGORIES = {
            "crafting", "stonecutting", "smelting", "blasting",
            "smoking", "campfire", "smithing", "special"
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected source directory, local cache, and bundled cache paths");
        }

        Path sourceDirectory = Path.of(args[0]);
        Path localCache = Path.of(args[1]);
        Path bundledCache = Path.of(args[2]);
        Map<String, byte[]> sourceData = new LinkedHashMap<>();
        for (String category : CATEGORIES) {
            Path sourceFile = sourceDirectory.resolve(category + ".json");
            if (Files.isRegularFile(sourceFile)) {
                sourceData.put(category, Files.readAllBytes(sourceFile));
            }
        }

        String expectedHash = BinaryRecipeCache.computeSourceHash(CATEGORIES, sourceData);
        if (!BinaryRecipeCache.isCacheValid(localCache, expectedHash)) {
            throw new IllegalStateException("The local recipe cache is missing or does not match the compact sources");
        }

        Path parent = bundledCache.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        Path temporaryFile = Files.createTempFile(parent, bundledCache.getFileName().toString(), ".tmp");
        try {
            Files.copy(localCache, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
            try {
                Files.move(temporaryFile, bundledCache,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, bundledCache, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
        System.out.println("Bundled recipe cache synchronized: " + bundledCache.toAbsolutePath());
    }
}
