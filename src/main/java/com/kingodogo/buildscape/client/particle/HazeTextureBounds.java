package com.kingodogo.buildscape.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.IntPredicate;

/** Texture-space coverage including a transparent texel of filtering padding. */
record HazeTextureBounds(float minU, float maxU, float minV, float maxV) {
    static final HazeTextureBounds FULL = new HazeTextureBounds(0, 1, 0, 1);
    static final HazeTextureBounds EMPTY = new HazeTextureBounds(0, 0, 0, 0);
    private static final Map<TextureAtlasSprite, Coverage> CACHE = new WeakHashMap<>();

    static HazeTextureBounds forSprite(TextureAtlasSprite sprite) {
        return forSprite(sprite, 0);
    }

    static HazeTextureBounds forSprite(TextureAtlasSprite sprite, int cutoff) {
        Coverage cached = CACHE.get(sprite);
        if (cached == null) {
            int[] alpha = readAlpha(sprite.getWidth(), sprite.getHeight(), sprite.getUniqueFrames().toArray(),
                    sprite::getPixelRGBA);
            if (alpha == null) {
                // A resource reload freed this sprite's image; the particle swaps sprites on its next tick.
                return FULL;
            }
            cached = new Coverage(sprite.getWidth(), sprite.getHeight(), alpha);
            CACHE.put(sprite, cached);
        }
        Coverage coverage = cached;
        if (coverage.bounds[cutoff] == null) {
            coverage.bounds[cutoff] = compute(coverage.width, coverage.height,
                    index -> coverage.alpha[index] <= cutoff);
        }
        return coverage.bounds[cutoff];
    }

    boolean isEmpty() {
        return this.minU == this.maxU || this.minV == this.maxV;
    }

    private static final class Coverage {
        private final int width;
        private final int height;
        private final int[] alpha;
        private final HazeTextureBounds[] bounds = new HazeTextureBounds[256];

        private Coverage(int width, int height, int[] alpha) {
            this.width = width;
            this.height = height;
            this.alpha = alpha;
        }
    }

    interface FramePixels {
        int rgba(int frame, int x, int y);
    }

    /** Highest alpha of each texel across all frames, or null when the image is no longer readable. */
    static int[] readAlpha(int width, int height, int[] frames, FramePixels pixels) {
        int[] alpha = new int[width * height];
        try {
            for (int frame : frames) {
                for (int index = 0; index < alpha.length; index++) {
                    alpha[index] = Math.max(alpha[index], pixels.rgba(frame, index % width, index / width) >>> 24);
                }
            }
        } catch (IllegalStateException closedImage) {
            return null;
        }
        return alpha;
    }

    static HazeTextureBounds compute(int width, int height, IntPredicate transparent) {
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!transparent.test(y * width + x)) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) {
            return EMPTY;
        }
        return new HazeTextureBounds(Math.max(0, minX - 1) / (float) width,
                Math.min(width, maxX + 2) / (float) width,
                Math.max(0, minY - 1) / (float) height,
                Math.min(height, maxY + 2) / (float) height);
    }
}
