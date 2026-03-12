package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGameRulesPacket {
    public final boolean fastLeafDecay;

    public SyncGameRulesPacket(boolean fastLeafDecay) {
        this.fastLeafDecay = fastLeafDecay;
    }

    public SyncGameRulesPacket(FriendlyByteBuf buffer) {
        this.fastLeafDecay = buffer.readBoolean();
    }

    public static SyncGameRulesPacket decode(FriendlyByteBuf buffer) {
        return new SyncGameRulesPacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(fastLeafDecay);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY).set(fastLeafDecay, null);
                }
            });
        });
        context.setPacketHandled(true);
    }
}
