package com.kingodogo.buildscape.client.screen;

/** Limits custom background dimensions before and after image decoding. */
public final class BackgroundImageLimits {
    public static final int MAX_DIMENSION = 4096;

    private BackgroundImageLimits() { }

    public static boolean oversized(long width, long height) {
        return width > MAX_DIMENSION || height > MAX_DIMENSION;
    }
}
