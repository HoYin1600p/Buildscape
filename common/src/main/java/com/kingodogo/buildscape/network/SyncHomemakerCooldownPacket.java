package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.HomemakerCooldownTracker;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class SyncHomemakerCooldownPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "sync_homemaker_cooldown");

    private final long cooldownTime;

    public SyncHomemakerCooldownPacket(long cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public SyncHomemakerCooldownPacket(FriendlyByteBuf buffer) {
        this.cooldownTime = buffer.readLong();
    }

    public static SyncHomemakerCooldownPacket decode(FriendlyByteBuf buffer) {
        return new SyncHomemakerCooldownPacket(buffer);
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
    public void write(FriendlyByteBuf buffer) {
        buffer.writeLong(cooldownTime);
    }

    @Override
    public void handle(Player player) {
        HomemakerCooldownTracker.cooldownEndTime = this.cooldownTime;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }
}
