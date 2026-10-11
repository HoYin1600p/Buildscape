package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfettiBurstPacket {

    private final double x;
    private final double y;
    private final double z;
    private final float lookX;
    private final float lookY;
    private final float lookZ;
    private final int burstLevel;
    private final long seed;

    public ConfettiBurstPacket(double x, double y, double z, float lookX, float lookY, float lookZ, int burstLevel, long seed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
        this.burstLevel = Math.min(5, Math.max(1, burstLevel));
        this.seed = seed;
    }

    public static void encode(ConfettiBurstPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.lookX);
        buf.writeFloat(msg.lookY);
        buf.writeFloat(msg.lookZ);
        buf.writeByte(msg.burstLevel);
        buf.writeLong(msg.seed);
    }

    public static ConfettiBurstPacket decode(FriendlyByteBuf buf) {
        return new ConfettiBurstPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readByte(), buf.readLong());
    }

    public static void handle(ConfettiBurstPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                com.kingodogo.buildscape.client.ConfettiBurstClient.spawn(
                        msg.x, msg.y, msg.z, msg.lookX, msg.lookY, msg.lookZ, msg.burstLevel, msg.seed)));
        ctx.get().setPacketHandled(true);
    }
}
