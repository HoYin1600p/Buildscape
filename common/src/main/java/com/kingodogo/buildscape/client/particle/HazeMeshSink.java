package com.kingodogo.buildscape.client.particle;

/** Receives a camera-relative snapshot during particle extraction. */
public interface HazeMeshSink {
    record Mesh(float[] vertices, int color, int light) { }
    void buildscape$addHazeMesh(float[] vertices, int color, int light);
}
