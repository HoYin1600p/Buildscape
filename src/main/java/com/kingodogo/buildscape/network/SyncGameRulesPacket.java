package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGameRulesPacket {
    public final boolean fastLeafDecay;
    public final boolean creativeTreeBreaker;

    public SyncGameRulesPacket(boolean fastLeafDecay, boolean creativeTreeBreaker) {
        this.fastLeafDecay = fastLeafDecay;
        this.creativeTreeBreaker = creativeTreeBreaker;
    }

    public SyncGameRulesPacket(FriendlyByteBuf buffer) {
        this.fastLeafDecay = buffer.readBoolean();
        this.creativeTreeBreaker = buffer.readBoolean();
    }

    public static SyncGameRulesPacket decode(FriendlyByteBuf buffer) {
        return new SyncGameRulesPacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(fastLeafDecay);
        buffer.writeBoolean(creativeTreeBreaker);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY).set(fastLeafDecay, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.CREATIVE_TREE_BREAKER).set(creativeTreeBreaker, null);
                }
            });
        });
        context.setPacketHandled(true);
    }
}
