package com.kingodogo.buildscape.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

@SuppressWarnings("deprecation")
public class GeyserParticleOptions implements ParticleOptions {
    private final ParticleType<GeyserParticleOptions> type;
    private final int waterBlocks;

    public static final Codec<GeyserParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("water_blocks").forGetter(o -> o.waterBlocks)
        ).apply(instance, waterBlocks -> new GeyserParticleOptions(null, waterBlocks))
    );

    public static final ParticleOptions.Deserializer<GeyserParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<GeyserParticleOptions>() {
        @Override
        public GeyserParticleOptions fromCommand(ParticleType<GeyserParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            int waterBlocks = reader.readInt();
            return new GeyserParticleOptions(type, waterBlocks);
        }

        @Override
        public GeyserParticleOptions fromNetwork(ParticleType<GeyserParticleOptions> type, FriendlyByteBuf buffer) {
            return new GeyserParticleOptions(type, buffer.readInt());
        }
    };

    public GeyserParticleOptions(ParticleType<GeyserParticleOptions> type, int waterBlocks) {
        this.type = type;
        this.waterBlocks = waterBlocks;
    }

    @Override
    public ParticleType<GeyserParticleOptions> getType() {
        return this.type != null ? this.type : ModParticles.GEYSER.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(this.waterBlocks);
    }

    @Override
    public String writeToString() {
        return net.minecraft.core.Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + this.waterBlocks;
    }

    public int getWaterBlocks() {
        return this.waterBlocks;
    }
}
