package com.kingodogo.buildscape.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;

public final class GeyserParticleTypes {
    private GeyserParticleTypes() {}
    public static ParticleType<GeyserParticleOptions> geyser() {
        return new ParticleType<GeyserParticleOptions>(true, GeyserParticleOptions.DESERIALIZER) {
            private final Codec<GeyserParticleOptions> codec = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("water_blocks").forGetter(GeyserParticleOptions::getWaterBlocks)
            ).apply(instance, waterBlocks -> new GeyserParticleOptions(this, waterBlocks)));
            @Override public Codec<GeyserParticleOptions> codec() { return codec; }
        };
    }
    public static ParticleType<GeyserBaseParticleOptions> geyserBase() {
        return new ParticleType<GeyserBaseParticleOptions>(true, GeyserBaseParticleOptions.DESERIALIZER) {
            private final Codec<GeyserBaseParticleOptions> codec = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("water_blocks").forGetter(GeyserBaseParticleOptions::getWaterBlocks),
                    Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(GeyserBaseParticleOptions::getBurstImpulseBase)
            ).apply(instance, (waterBlocks, impulse) -> new GeyserBaseParticleOptions(this, waterBlocks, impulse)));
            @Override public Codec<GeyserBaseParticleOptions> codec() { return codec; }
        };
    }
}
