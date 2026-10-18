package com.kingodogo.buildscape.particle;

public record ParticlePositionKey(long x, long y, long z) {
    public static ParticlePositionKey of(double x, double y, double z) {
        return new ParticlePositionKey(Math.round(x * 10000.0), Math.round(y * 10000.0), Math.round(z * 10000.0));
    }
}
