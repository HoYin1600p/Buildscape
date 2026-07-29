package com.kingodogo.buildscape.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGameRulesPacket {
    public final boolean fastLeafDecay;
    public final boolean disableEndermanGriefing;
    public final boolean disableCreeperGriefing;
    public final boolean disableGhastGriefing;

    public SyncGameRulesPacket(boolean fastLeafDecay, boolean disableEndermanGriefing, boolean disableCreeperGriefing, boolean disableGhastGriefing) {
        this.fastLeafDecay = fastLeafDecay;
        this.disableEndermanGriefing = disableEndermanGriefing;
        this.disableCreeperGriefing = disableCreeperGriefing;
        this.disableGhastGriefing = disableGhastGriefing;
    }

    public SyncGameRulesPacket(FriendlyByteBuf buffer) {
        this.fastLeafDecay = buffer.readBoolean();
        this.disableEndermanGriefing = buffer.readBoolean();
        this.disableCreeperGriefing = buffer.readBoolean();
        this.disableGhastGriefing = buffer.readBoolean();
    }

    public static SyncGameRulesPacket decode(FriendlyByteBuf buffer) {
        return new SyncGameRulesPacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(fastLeafDecay);
        buffer.writeBoolean(disableEndermanGriefing);
        buffer.writeBoolean(disableCreeperGriefing);
        buffer.writeBoolean(disableGhastGriefing);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level != null) {
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.FAST_LEAF_DECAY).set(fastLeafDecay, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.DISABLE_ENDERMAN_GRIEFING).set(disableEndermanGriefing, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.DISABLE_CREEPER_GRIEFING).set(disableCreeperGriefing, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.DISABLE_GHAST_GRIEFING).set(disableGhastGriefing, null);
                }
            });
        });
        context.setPacketHandled(true);
    }
}
