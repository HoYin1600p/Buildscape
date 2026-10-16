package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.ConfettiBurstClient;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class ConfettiBurstPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "confetti_burst");

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

    public ConfettiBurstPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.lookX = buf.readFloat();
        this.lookY = buf.readFloat();
        this.lookZ = buf.readFloat();
        this.burstLevel = buf.readByte();
        this.seed = buf.readLong();
    }

    public static ConfettiBurstPacket decode(FriendlyByteBuf buf) {
        return new ConfettiBurstPacket(buf);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.SERVER_TO_CLIENT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.lookX);
        buf.writeFloat(this.lookY);
        buf.writeFloat(this.lookZ);
        buf.writeByte(this.burstLevel);
        buf.writeLong(this.seed);
    }

    @Override
    public void handle(Player player) {
        if (player != null) {
            net.minecraft.world.level.Level level = com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityLevel(player);
            if (level != null) {
                ConfettiBurstClient.spawn(level, x, y, z, lookX, lookY, lookZ, burstLevel, seed);
            }
        }
    }
}
