package com.kingodogo.buildscape.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings("deprecation")
public class GeyserBaseParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserBaseParticleOptions> type;
    private final int waterBlocks;
    private final float burstImpulseBase;

    public static final Codec<GeyserBaseParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("water_blocks").forGetter(o -> o.waterBlocks),
            Codec.FLOAT.fieldOf("burst_impulse_base").forGetter(o -> o.burstImpulseBase)
        ).apply(instance, (waterBlocks, burstImpulseBase) -> new GeyserBaseParticleOptions(null, waterBlocks, burstImpulseBase))
    );

    public static final ParticleOptions.Deserializer<GeyserBaseParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<GeyserBaseParticleOptions>() {
        @Override
        public GeyserBaseParticleOptions fromCommand(ParticleType<GeyserBaseParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int waterBlocks = reader.readInt();
            reader.expect(' ');
            float burstImpulseBase = reader.readFloat();
            return new GeyserBaseParticleOptions(type, waterBlocks, burstImpulseBase);
        }

        @Override
        public GeyserBaseParticleOptions fromNetwork(ParticleType<GeyserBaseParticleOptions> type, FriendlyByteBuf buffer) {
            return new GeyserBaseParticleOptions(type, buffer.readInt(), buffer.readFloat());
        }
    };

    public GeyserBaseParticleOptions(ParticleType<GeyserBaseParticleOptions> type, int waterBlocks, float burstImpulseBase) {
        this.type = type;
        this.waterBlocks = waterBlocks;
        this.burstImpulseBase = burstImpulseBase;
    }

    @Override
    public ParticleType<GeyserBaseParticleOptions> getType() {
        return this.type != null ? this.type : ModParticles.GEYSER_BASE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.waterBlocks);
        buffer.writeFloat(this.burstImpulseBase);
    }

    @Override
    public String writeToString() {
        return net.minecraft.core.Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + this.waterBlocks + " " + this.burstImpulseBase;
    }

    public int getWaterBlocks() {
        return this.waterBlocks;
    }

    public float getBurstImpulseBase() {
        return this.burstImpulseBase;
    }
}
