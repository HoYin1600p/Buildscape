package com.kingodogo.buildscape.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public final class GeyserParticleOptions implements ParticleOptions {
    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<GeyserParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public GeyserParticleOptions fromCommand(ParticleType<GeyserParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            int waterBlocks = 0;
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                waterBlocks = reader.readInt();
            }
            return new GeyserParticleOptions(type, waterBlocks);
        }

        @Override
        public GeyserParticleOptions fromNetwork(ParticleType<GeyserParticleOptions> type, FriendlyByteBuf buf) {
            return new GeyserParticleOptions(type, buf.readVarInt());
        }
    };

    private final ParticleType<GeyserParticleOptions> type;
    private final int waterBlocks;

    public GeyserParticleOptions(ParticleType<GeyserParticleOptions> type, int waterBlocks) {
        this.type = type;
        this.waterBlocks = waterBlocks;
    }

    @Override public ParticleType<GeyserParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER.get(); }
    public int getWaterBlocks() { return waterBlocks; }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(waterBlocks);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + waterBlocks;
    }
}
