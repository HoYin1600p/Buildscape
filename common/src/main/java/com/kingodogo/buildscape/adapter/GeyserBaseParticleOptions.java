package com.kingodogo.buildscape.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class GeyserBaseParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserBaseParticleOptions> type;
    private final int waterBlocks;
    private final float burstImpulseBase;
    public static final MapCodec<GeyserBaseParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("water_blocks").forGetter(GeyserBaseParticleOptions::getWaterBlocks),
            com.mojang.serialization.Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(GeyserBaseParticleOptions::getBurstImpulseBase)
    ).apply(instance, (waterBlocks, impulse) -> new GeyserBaseParticleOptions(null, waterBlocks, impulse)));
    public static final StreamCodec<RegistryFriendlyByteBuf, GeyserBaseParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeyserBaseParticleOptions::getWaterBlocks,
            ByteBufCodecs.FLOAT, GeyserBaseParticleOptions::getBurstImpulseBase,
            (waterBlocks, impulse) -> new GeyserBaseParticleOptions(null, waterBlocks, impulse));
    public GeyserBaseParticleOptions(ParticleType<GeyserBaseParticleOptions> type, int waterBlocks, float burstImpulseBase) { this.type = type; this.waterBlocks = waterBlocks; this.burstImpulseBase = burstImpulseBase; }
    @Override public ParticleType<GeyserBaseParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER_BASE.get(); }
    public int getWaterBlocks() { return waterBlocks; }
    public float getBurstImpulseBase() { return burstImpulseBase; }
}
