package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class SyncGameRulesPacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "sync_game_rules");

    public final boolean fastLeafDecay;
    public final boolean disableEndermanGriefing;
    public final boolean disableCreeperGriefing;
    public final boolean disableGhastGriefing;
    public final boolean cakeStacking;
    public final boolean waterBottleStacking;

    public SyncGameRulesPacket(boolean fastLeafDecay, boolean disableEndermanGriefing,
                               boolean disableCreeperGriefing, boolean disableGhastGriefing,
                               boolean cakeStacking, boolean waterBottleStacking) {
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
        buffer.writeBoolean(fastLeafDecay);
        buffer.writeBoolean(disableEndermanGriefing);
        buffer.writeBoolean(disableCreeperGriefing);
        buffer.writeBoolean(disableGhastGriefing);
        buffer.writeBoolean(cakeStacking);
        buffer.writeBoolean(waterBottleStacking);
    }

    @Override
    public void handle(Player player) {
        ModGameRules.clientFastLeafDecay = this.fastLeafDecay;
        ModGameRules.clientDisableEndermanGriefing = this.disableEndermanGriefing;
        ModGameRules.clientDisableCreeperGriefing = this.disableCreeperGriefing;
        ModGameRules.clientDisableGhastGriefing = this.disableGhastGriefing;
        ModGameRules.clientCakeStacking = this.cakeStacking;
        ModGameRules.clientWaterBottleStacking = this.waterBottleStacking;
    }
}
