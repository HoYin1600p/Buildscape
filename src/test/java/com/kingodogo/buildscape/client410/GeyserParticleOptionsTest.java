package com.kingodogo.buildscape.client410;

import com.google.gson.JsonObject;
import com.kingodogo.buildscape.particle.GeyserBaseParticleOptions;
import com.kingodogo.buildscape.particle.GeyserParticleOptions;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeyserParticleOptionsTest {
    @Test
    public void constructorsClampUnsupportedCountsAndPreserveSupportedCounts() {
        for (int count : new int[]{Integer.MIN_VALUE, -1, 0, 1, 2, 3, 4, 5, Integer.MAX_VALUE}) {
            int expected = Math.max(0, Math.min(4, count));
            assertEquals(expected, new GeyserParticleOptions(null, count).getWaterBlocks());
            assertEquals(expected, new GeyserBaseParticleOptions(null, count, 1.5f).getWaterBlocks());
        }
    }

    @Test
    public void commandAndCodecInputsCannotBypassTheBound() throws Exception {
        assertEquals(4, GeyserParticleOptions.DESERIALIZER
                .fromCommand(null, new StringReader(" 1000000")).getWaterBlocks());
        assertEquals(0, GeyserBaseParticleOptions.DESERIALIZER
                .fromCommand(null, new StringReader(" -1000000 1.5")).getWaterBlocks());

        JsonObject input = new JsonObject();
        input.addProperty("water_blocks", Integer.MAX_VALUE);
        input.addProperty("burst_impulse_base", 1.5f);
        assertEquals(4, GeyserParticleOptions.CODEC.parse(JsonOps.INSTANCE, input)
                .result().orElseThrow().getWaterBlocks());
        assertEquals(4, GeyserBaseParticleOptions.CODEC.parse(JsonOps.INSTANCE, input)
                .result().orElseThrow().getWaterBlocks());
    }

    @Test
    public void networkInputsCannotBypassTheBound() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeInt(Integer.MAX_VALUE);
            assertEquals(4, GeyserParticleOptions.DESERIALIZER.fromNetwork(null, buffer).getWaterBlocks());
            buffer.clear();
            buffer.writeInt(Integer.MIN_VALUE);
            buffer.writeFloat(1.5f);
            GeyserBaseParticleOptions options = GeyserBaseParticleOptions.DESERIALIZER.fromNetwork(null, buffer);
            assertEquals(0, options.getWaterBlocks());
            assertEquals(1.5f, options.getBurstImpulseBase(), 0.0f);
        } finally {
            buffer.release();
        }
    }
}
