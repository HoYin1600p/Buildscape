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
public final class GeyserBaseParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserBaseParticleOptions> type;
    private final int waterBlocks;
    private final float burstImpulseBase;
    public static final Codec<GeyserBaseParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("water_blocks").forGetter(GeyserBaseParticleOptions::getWaterBlocks),
            Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(GeyserBaseParticleOptions::getBurstImpulseBase)
    ).apply(instance, (waterBlocks, impulse) -> new GeyserBaseParticleOptions(null, waterBlocks, impulse)));
    public static final ParticleOptions.Deserializer<GeyserBaseParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override public GeyserBaseParticleOptions fromCommand(ParticleType<GeyserBaseParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' '); int waterBlocks = reader.readInt(); reader.expect(' '); return new GeyserBaseParticleOptions(type, waterBlocks, reader.readFloat());
        }
        @Override public GeyserBaseParticleOptions fromNetwork(ParticleType<GeyserBaseParticleOptions> type, FriendlyByteBuf buffer) {
            return new GeyserBaseParticleOptions(type, buffer.readInt(), buffer.readFloat());
        }
    };
    public GeyserBaseParticleOptions(ParticleType<GeyserBaseParticleOptions> type, int waterBlocks, float burstImpulseBase) { this.type = type; this.waterBlocks = waterBlocks; this.burstImpulseBase = burstImpulseBase; }
    @Override public ParticleType<GeyserBaseParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER_BASE.get(); }
    @Override public void writeToNetwork(FriendlyByteBuf buffer) { buffer.writeInt(waterBlocks); buffer.writeFloat(burstImpulseBase); }
    @Override public String writeToString() { return Registry.PARTICLE_TYPE.getKey(getType()) + " " + waterBlocks + " " + burstImpulseBase; }
    public int getWaterBlocks() { return waterBlocks; }
    public float getBurstImpulseBase() { return burstImpulseBase; }
}
