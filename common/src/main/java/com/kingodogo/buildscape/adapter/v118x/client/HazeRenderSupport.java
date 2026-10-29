package com.kingodogo.buildscape.adapter.v118x.client;

import java.util.function.DoubleToIntFunction;

/** Loader-installed shader support; the safe default retains the full texture. */
public final class HazeRenderSupport {
    private static DoubleToIntFunction alphaCutoff = alpha -> 0;
    private HazeRenderSupport() { }
    public static void installAlphaCutoffProvider(DoubleToIntFunction provider) { alphaCutoff = provider; }
    static int alphaCutoff(float alpha) { return alphaCutoff.applyAsInt(alpha); }
}
