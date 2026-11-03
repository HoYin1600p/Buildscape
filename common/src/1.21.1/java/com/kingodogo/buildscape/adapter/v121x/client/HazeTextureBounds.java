package com.kingodogo.buildscape.adapter.v121x.client;

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
        int minX = width, minY = height, maxX = -1, maxY = -1;
        try {
            for (int frame : contents.getUniqueFrames().toArray()) {
                for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
                    if (!contents.isTransparent(frame, x, y)) {
                        minX = Math.min(minX, x); minY = Math.min(minY, y);
                        maxX = Math.max(maxX, x); maxY = Math.max(maxY, y);
                    }
                }
            }
        } catch (IllegalStateException closedImage) {
            return FULL;
        }
        HazeTextureBounds bounds = maxX < 0 ? EMPTY : new HazeTextureBounds(
                Math.max(0, minX - 1) / (float) width, Math.min(width, maxX + 2) / (float) width,
                Math.max(0, minY - 1) / (float) height, Math.min(height, maxY + 2) / (float) height);
        CACHE.put(sprite, bounds);
        return bounds;
    }

    boolean isEmpty() { return minU == maxU || minV == maxV; }
}
