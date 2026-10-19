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
    public final boolean cakeStacking;
    public final boolean waterBottleStacking;
    public final boolean biomeBrushClearsSelection;

    public SyncGameRulesPacket(boolean fastLeafDecay, boolean disableEndermanGriefing, boolean disableCreeperGriefing, boolean disableGhastGriefing, boolean cakeStacking, boolean waterBottleStacking, boolean biomeBrushClearsSelection) {
        this.biomeBrushClearsSelection = biomeBrushClearsSelection;
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
        this.biomeBrushClearsSelection = buffer.readBoolean();
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
        buffer.writeBoolean(biomeBrushClearsSelection);
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
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.IS_CAKE_STACK).set(cakeStacking, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.IS_WATER_BOTTLE_STACK).set(waterBottleStacking, null);
                    mc.level.getGameRules().getRule(com.kingodogo.buildscape.world.ModGameRules.BIOME_BRUSH_CLEARS_SELECTION).set(biomeBrushClearsSelection, null);
                    com.kingodogo.buildscape.world.ModGameRules.clientBiomeBrushClearsSelection = biomeBrushClearsSelection;
                    com.kingodogo.buildscape.world.ModGameRules.setClientStackingRules(cakeStacking, waterBottleStacking);
                    com.kingodogo.buildscape.world.ModGameRules.clientFastLeafDecay = fastLeafDecay;
                    com.kingodogo.buildscape.world.ModGameRules.clientDisableEndermanGriefing = disableEndermanGriefing;
                    com.kingodogo.buildscape.world.ModGameRules.clientDisableCreeperGriefing = disableCreeperGriefing;
                    com.kingodogo.buildscape.world.ModGameRules.clientDisableGhastGriefing = disableGhastGriefing;
                }
            });
        });
        context.setPacketHandled(true);
    }
}
