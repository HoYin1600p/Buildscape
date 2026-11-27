package com.kingodogo.buildscape.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public final class GeyserBaseParticleOptions implements ParticleOptions {
    @SuppressWarnings("deprecation")
    public static final ParticleOptions.Deserializer<GeyserBaseParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public GeyserBaseParticleOptions fromCommand(ParticleType<GeyserBaseParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            int waterBlocks = 0;
            float burstImpulse = 0.0F;
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                waterBlocks = reader.readInt();
            }
            if (reader.canRead() && reader.peek() == ' ') {
                reader.skip();
                burstImpulse = reader.readFloat();
            }
            return new GeyserBaseParticleOptions(type, waterBlocks, burstImpulse);
        }

        @Override
        public GeyserBaseParticleOptions fromNetwork(ParticleType<GeyserBaseParticleOptions> type, FriendlyByteBuf buf) {
            return new GeyserBaseParticleOptions(type, buf.readVarInt(), buf.readFloat());
        }
    };

    private final ParticleType<GeyserBaseParticleOptions> type;
    private final int waterBlocks;
    private final float burstImpulseBase;

    public GeyserBaseParticleOptions(ParticleType<GeyserBaseParticleOptions> type, int waterBlocks, float burstImpulseBase) {
        this.type = type;
        this.waterBlocks = waterBlocks;
        this.burstImpulseBase = burstImpulseBase;
    }

    @Override public ParticleType<GeyserBaseParticleOptions> getType() { return type != null ? type : ModParticles.GEYSER_BASE.get(); }
    public int getWaterBlocks() { return waterBlocks; }
    public float getBurstImpulseBase() { return burstImpulseBase; }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(waterBlocks);
        buf.writeFloat(burstImpulseBase);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + waterBlocks + " " + burstImpulseBase;
    }
}
