package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.world.ModGameRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateGameRulePacket {
    private final String ruleName;
    private final boolean value;

    public UpdateGameRulePacket(String ruleName, boolean value) {
        this.ruleName = ruleName;
        this.value = value;
    }

    public UpdateGameRulePacket(FriendlyByteBuf buffer) {
        this.ruleName = buffer.readUtf();
        this.value = buffer.readBoolean();
    }

    public static UpdateGameRulePacket decode(FriendlyByteBuf buffer) {
        return new UpdateGameRulePacket(buffer);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(ruleName);
        buffer.writeBoolean(value);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !player.hasPermissions(2)) {
                return;
            }

            GameRules rules = player.getLevel().getGameRules();
            if (ruleName.equals("fastLeafDecay")) {
                rules.getRule(ModGameRules.FAST_LEAF_DECAY).set(value, player.getServer());
            } else if (ruleName.equals("creativeTreeBreaker")) {
                rules.getRule(ModGameRules.CREATIVE_TREE_BREAKER).set(value, player.getServer());
            }
        });
        context.setPacketHandled(true);
    }
}
