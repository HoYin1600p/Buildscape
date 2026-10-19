package com.kingodogo.buildscape.feature.pillar;

import com.kingodogo.buildscape.network.UpdatePillarDataPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PillarDetailPacketTest {
    @Test
    public void consecutiveAppliesKeepCurrentMaximumIndependentOfPaletteLength() throws Exception {
        Field maximum = UpdatePillarDataPacket.class.getDeclaredField("maxParticleColor");
        maximum.setAccessible(true);
        Field colors = UpdatePillarDataPacket.class.getDeclaredField("dyeColors");
        colors.setAccessible(true);
        List<String> palette = new ArrayList<>(List.of("#111111", "#222222", "#333333", "#444444", "#555555"));
        for (int value : new int[] {3, 1, 7}) {
            UpdatePillarDataPacket packet = new UpdatePillarDataPacket("S-P1", "spiral", true,
                    1.0, 1.0, 1.0, value, palette);
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            try {
                packet.encode(buffer);
                UpdatePillarDataPacket decoded = UpdatePillarDataPacket.decode(buffer);
                assertEquals(value, maximum.get(decoded));
                assertEquals(palette, colors.get(decoded));
                assertEquals(0, buffer.readableBytes());
            } finally {
                buffer.release();
            }
        }
    }
}
