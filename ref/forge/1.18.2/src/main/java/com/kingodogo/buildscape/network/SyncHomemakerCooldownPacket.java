package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.client.HomemakerCooldownTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHomemakerCooldownPacket {
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

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeLong(cooldownTime);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> HomemakerCooldownTracker.cooldownEndTime = this.cooldownTime);
        });
        context.setPacketHandled(true);
    }
}
