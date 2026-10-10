package com.kingodogo.buildscape.particle;

import com.kingodogo.buildscape.platform.Services;
public final class ParticleFactory {

    private ParticleFactory() {}

    /**
     * Registers all Buildscape particle providers with the client particle engine.
     */
    public static void registerProviders() {
        Services.PLATFORM.registerParticleProviders();
    }
}
