package com.kingodogo.buildscape.recipe.framework.cache;

import com.kingodogo.buildscape.recipe.framework.compiler.RecipeCacheGenerator;

import java.nio.file.Path;

public final class BundledRecipeCacheSync {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Expected source directory and bundled cache paths");
        }

        Path sourceDirectory = Path.of(args[0]);
        Path localCache = args.length > 2 ? Path.of(args[1]) : null;
        Path bundledCache = args.length > 2 ? Path.of(args[2]) : Path.of(args[1]);

        if (localCache != null) {
            RecipeCacheGenerator.main(new String[]{sourceDirectory.toString(), bundledCache.toString(), localCache.toString()});
        } else {
            RecipeCacheGenerator.main(new String[]{sourceDirectory.toString(), bundledCache.toString()});
        }
    }
}
