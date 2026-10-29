package com.kingodogo.buildscape.particle;

import net.minecraft.core.particles.ParticleType;

public final class GeyserParticleTypes {
    private GeyserParticleTypes() {}
    public static ParticleType<GeyserParticleOptions> geyser() {
        return new ParticleType<GeyserParticleOptions>(true, GeyserParticleOptions.DESERIALIZER) {
            @Override public com.mojang.serialization.Codec<GeyserParticleOptions> codec() { return GeyserParticleOptions.CODEC; }
        };
    }
    public static ParticleType<GeyserBaseParticleOptions> geyserBase() {
        return new ParticleType<GeyserBaseParticleOptions>(true, GeyserBaseParticleOptions.DESERIALIZER) {
            @Override public com.mojang.serialization.Codec<GeyserBaseParticleOptions> codec() { return GeyserBaseParticleOptions.CODEC; }
        };
    }
}
