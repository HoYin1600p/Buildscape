package com.kingodogo.buildscape.mixinsupport;

import it.unimi.dsi.fastutil.ints.IntList;

/** Implemented by the client starter's private-method invoker. */
public interface FireworkSparkAccess {
    void buildscape$spawnSpark(double x, double y, double z, double vx, double vy, double vz,
            IntList colors, IntList fades, boolean trail, boolean flicker);
}
