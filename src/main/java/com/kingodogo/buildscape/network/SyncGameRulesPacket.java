package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.client.Minecraft;
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
    public final boolean cakeStacking;
    public final boolean waterBottleStacking;

    public SyncGameRulesPacket(boolean fastLeafDecay, boolean disableEndermanGriefing, boolean disableCreeperGriefing, boolean disableGhastGriefing, boolean cakeStacking, boolean waterBottleStacking) {
        this.fastLeafDecay = fastLeafDecay;
        this.disableEndermanGriefing = disableEndermanGriefing;
        this.disableCreeperGriefing = disableCreeperGriefing;
        this.disableGhastGriefing = disableGhastGriefing;
        this.cakeStacking = cakeStacking;
        this.waterBottleStacking = waterBottleStacking;
    }

    public SyncGameRulesPacket(FriendlyByteBuf buffer) {
        this.fastLeafDecay = buffer.readBoolean();
        this.disableEndermanGriefing = buffer.readBoolean();
        this.disableCreeperGriefing = buffer.readBoolean();
        this.disableGhastGriefing = buffer.readBoolean();
        this.cakeStacking = buffer.readBoolean();
        this.waterBottleStacking = buffer.readBoolean();
    }

    public static SyncGameRulesPacket decode(FriendlyByteBuf buffer) {
        return new SyncGameRulesPacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(fastLeafDecay);
        buffer.writeBoolean(disableEndermanGriefing);
        buffer.writeBoolean(disableCreeperGriefing);
        buffer.writeBoolean(disableGhastGriefing);
        buffer.writeBoolean(cakeStacking);
        buffer.writeBoolean(waterBottleStacking);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null) {
                    mc.level.getGameRules().getRule(ModGameRules.FAST_LEAF_DECAY).set(fastLeafDecay, null);
                    mc.level.getGameRules().getRule(ModGameRules.DISABLE_ENDERMAN_GRIEFING).set(disableEndermanGriefing, null);
                    mc.level.getGameRules().getRule(ModGameRules.DISABLE_CREEPER_GRIEFING).set(disableCreeperGriefing, null);
                    mc.level.getGameRules().getRule(ModGameRules.DISABLE_GHAST_GRIEFING).set(disableGhastGriefing, null);
                    mc.level.getGameRules().getRule(ModGameRules.IS_CAKE_STACK).set(cakeStacking, null);
                    mc.level.getGameRules().getRule(ModGameRules.IS_WATER_BOTTLE_STACK).set(waterBottleStacking, null);
                    ModGameRules.setClientStackingRules(cakeStacking, waterBottleStacking);
                    ModGameRules.clientFastLeafDecay = fastLeafDecay;
                    ModGameRules.clientDisableEndermanGriefing = disableEndermanGriefing;
                    ModGameRules.clientDisableCreeperGriefing = disableCreeperGriefing;
                    ModGameRules.clientDisableGhastGriefing = disableGhastGriefing;
                }
            });
        });
        context.setPacketHandled(true);
    }
}
