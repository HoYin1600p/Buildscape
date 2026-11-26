package com.kingodogo.buildscape.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings("deprecation")
public final class GeyserParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserParticleOptions> type;
    private final int waterBlocks;
    public static final Codec<GeyserParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("water_blocks").forGetter(GeyserParticleOptions::getWaterBlocks)
    ).apply(instance, waterBlocks -> new GeyserParticleOptions(null, waterBlocks)));
    public static final ParticleOptions.Deserializer<GeyserParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override public GeyserParticleOptions fromCommand(ParticleType<GeyserParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' '); return new GeyserParticleOptions(type, reader.readInt());
        }
        @Override public GeyserParticleOptions fromNetwork(ParticleType<GeyserParticleOptions> type, FriendlyByteBuf buffer) {
            return new GeyserParticleOptions(type, buffer.readInt());
        }
    };
    public GeyserParticleOptions(ParticleType<GeyserParticleOptions> type, int waterBlocks) { this.type = type; this.waterBlocks = waterBlocks; }
    @Override public ParticleType<GeyserParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER.get(); }
    @Override public void writeToNetwork(FriendlyByteBuf buffer) { buffer.writeInt(waterBlocks); }
    @Override public String writeToString() { return Registry.PARTICLE_TYPE.getKey(getType()) + " " + waterBlocks; }
    public int getWaterBlocks() { return waterBlocks; }
}
