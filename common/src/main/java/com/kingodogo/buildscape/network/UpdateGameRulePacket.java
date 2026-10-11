package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class UpdateGameRulePacket implements CommonPacket {

    public static final CommonId ID = new CommonId("buildscape", "update_game_rule");

    private final String ruleName;
    private final boolean value;

    public UpdateGameRulePacket(String ruleName, boolean value) {
        this.ruleName = ruleName;
        this.value = value;
    }

    public UpdateGameRulePacket(FriendlyByteBuf buffer) {
        this.ruleName = NetworkPacketLimits.readUtf(buffer, NetworkPacketLimits.MAX_RULE_NAME_LENGTH, "game rule name");
        this.value = buffer.readBoolean();
    }

    public static UpdateGameRulePacket decode(FriendlyByteBuf buffer) {
        return new UpdateGameRulePacket(buffer);
    }

    @Override
    public CommonId getId() {
        return ID;
    }

    @Override
    public PacketDirection getDirection() {
        return PacketDirection.CLIENT_TO_SERVER;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        NetworkPacketLimits.writeUtf(buffer, ruleName, NetworkPacketLimits.MAX_RULE_NAME_LENGTH, "game rule name");
        buffer.writeBoolean(value);
    }

    @Override
    public void handle(Player player) {
        Services.PLATFORM.updateGameRule(player, this.ruleName, this.value);
    }

    public String getRuleName() {
        return ruleName;
    }

    public boolean getValue() {
        return value;
    }
}
