package com.kingodogo.buildscape.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Map;
import java.util.WeakHashMap;

/** Texture coverage across animation frames, padded for texture filtering. */
record HazeTextureBounds(float minU, float maxU, float minV, float maxV) {
    static final HazeTextureBounds FULL = new HazeTextureBounds(0, 1, 0, 1);
    private static final HazeTextureBounds EMPTY = new HazeTextureBounds(0, 0, 0, 0);
    private static final Map<TextureAtlasSprite, HazeTextureBounds> CACHE = new WeakHashMap<>();

    static HazeTextureBounds forSprite(TextureAtlasSprite sprite) {
        HazeTextureBounds cached = CACHE.get(sprite);
        if (cached != null) return cached;
        var contents = sprite.contents();
        int width = contents.width(), height = contents.height();
        HazeTextureBounds bounds = scan(width, height, contents.getUniqueFrames().toIntArray(), contents::isTransparent);
        if (bounds == null) return FULL;
        CACHE.put(sprite, bounds);
        return bounds;
    }

    @FunctionalInterface
    interface PixelTransparency { boolean isTransparent(int frame, int x, int y); }

    static HazeTextureBounds scan(int width, int height, int[] frames, PixelTransparency pixels) {
        int minX = width, minY = height, maxX = -1, maxY = -1;
        try {
            for (int frame : frames) {
                for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
                    if (!pixels.isTransparent(frame, x, y)) {
                        minX = Math.min(minX, x); minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
                    }
                }
            }
        } catch (IllegalStateException closedImage) {
            return null;
        }
        return maxX < 0 ? EMPTY : new HazeTextureBounds(
                Math.max(0, minX - 1) / (float) width, Math.min(width, maxX + 2) / (float) width,
                Math.max(0, minY - 1) / (float) height, Math.min(height, maxY + 2) / (float) height);
    }

    boolean isEmpty() { return minU == maxU || minV == maxV; }
}
