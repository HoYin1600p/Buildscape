package com.kingodogo.buildscape.particle;

import net.minecraft.core.particles.ParticleType;

public final class GeyserParticleTypes {
    private GeyserParticleTypes() {}
    public static ParticleType<GeyserParticleOptions> geyser() {
        // Decoded options must keep the type they were decoded for (geyser vs geyser_plume), so each type builds its own codecs.
        return new ParticleType<GeyserParticleOptions>(true) {
            private final com.mojang.serialization.MapCodec<GeyserParticleOptions> mapCodec = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("water_blocks").forGetter(GeyserParticleOptions::getWaterBlocks)
            ).apply(instance, waterBlocks -> new GeyserParticleOptions(this, waterBlocks)));
            private final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GeyserParticleOptions> streamCodec = net.minecraft.network.codec.StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.INT, GeyserParticleOptions::getWaterBlocks,
                    waterBlocks -> new GeyserParticleOptions(this, waterBlocks));
            @Override public com.mojang.serialization.MapCodec<GeyserParticleOptions> codec() { return mapCodec; }
            @Override public net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, GeyserParticleOptions> streamCodec() { return streamCodec; }
        };
    }
    public static ParticleType<GeyserBaseParticleOptions> geyserBase() {
        return new ParticleType<GeyserBaseParticleOptions>(true) {
            private final com.mojang.serialization.MapCodec<GeyserBaseParticleOptions> mapCodec = com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(instance -> instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("water_blocks").forGetter(GeyserBaseParticleOptions::getWaterBlocks),
                    com.mojang.serialization.Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(GeyserBaseParticleOptions::getBurstImpulseBase)
            ).apply(instance, (waterBlocks, impulse) -> new GeyserBaseParticleOptions(this, waterBlocks, impulse)));
            private final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GeyserBaseParticleOptions> streamCodec = net.minecraft.network.codec.StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.INT, GeyserBaseParticleOptions::getWaterBlocks,
                    net.minecraft.network.codec.ByteBufCodecs.FLOAT, GeyserBaseParticleOptions::getBurstImpulseBase,
                    (waterBlocks, impulse) -> new GeyserBaseParticleOptions(this, waterBlocks, impulse));
            @Override public com.mojang.serialization.MapCodec<GeyserBaseParticleOptions> codec() { return mapCodec; }
            @Override public net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, GeyserBaseParticleOptions> streamCodec() { return streamCodec; }
        };
    }
}
