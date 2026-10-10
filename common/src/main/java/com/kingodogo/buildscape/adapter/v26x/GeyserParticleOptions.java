package com.kingodogo.buildscape.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class GeyserParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserParticleOptions> type;
    private final int waterBlocks;
    public static final MapCodec<GeyserParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.INT.fieldOf("water_blocks").forGetter(GeyserParticleOptions::getWaterBlocks)
    ).apply(instance, waterBlocks -> new GeyserParticleOptions(null, waterBlocks)));
    public static final StreamCodec<RegistryFriendlyByteBuf, GeyserParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeyserParticleOptions::getWaterBlocks,
            waterBlocks -> new GeyserParticleOptions(null, waterBlocks));
    public GeyserParticleOptions(ParticleType<GeyserParticleOptions> type, int waterBlocks) { this.type = type; this.waterBlocks = waterBlocks; }
    @Override public ParticleType<GeyserParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER.get(); }
    public int getWaterBlocks() { return waterBlocks; }
}
