package com.kingodogo.buildscape.particle;
public final class ParticleProvider {

    private ParticleProvider() {}

    /**
     * Delegates to ParticleFactory to register all providers.
     */
    public static void registerAll() {
        ParticleFactory.registerProviders();
    }
}
