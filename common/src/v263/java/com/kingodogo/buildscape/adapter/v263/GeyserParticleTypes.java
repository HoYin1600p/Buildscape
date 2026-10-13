package com.kingodogo.buildscape.particle;

import net.minecraft.core.particles.ParticleType;

public final class GeyserParticleTypes {
    private GeyserParticleTypes() {}
    public static ParticleType<GeyserParticleOptions> geyser() {
        return new ParticleType<GeyserParticleOptions>(true) {
            @Override public com.mojang.serialization.MapCodec<GeyserParticleOptions> codec() { return GeyserParticleOptions.CODEC; }
            @Override public net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, GeyserParticleOptions> streamCodec() { return GeyserParticleOptions.STREAM_CODEC; }
        };
    }
    public static ParticleType<GeyserBaseParticleOptions> geyserBase() {
        return new ParticleType<GeyserBaseParticleOptions>(true) {
            @Override public com.mojang.serialization.MapCodec<GeyserBaseParticleOptions> codec() { return GeyserBaseParticleOptions.CODEC; }
            @Override public net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, GeyserBaseParticleOptions> streamCodec() { return GeyserBaseParticleOptions.STREAM_CODEC; }
        };
    }
}
