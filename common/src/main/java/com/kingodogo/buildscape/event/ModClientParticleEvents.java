package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.platform.Services;

public class ModClientParticleEvents {

    public static void registerParticleFactories() {
        Services.PLATFORM.registerCopperFireFlameParticle();
    }
}
