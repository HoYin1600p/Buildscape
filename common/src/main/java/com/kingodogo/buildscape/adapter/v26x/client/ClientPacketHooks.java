package com.kingodogo.buildscape.adapter.v26x.client;

import com.kingodogo.buildscape.network.CommonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

/** Referenced only by client packet paths; decoding and server dispatch stay loader neutral. */
public final class ClientPacketHooks {
    private ClientPacketHooks() {}

    public static void handle(CommonPacket packet, Player player) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> packet.handle(player != null ? player : client.player));
    }

    public static void send(CustomPacketPayload payload) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection != null) connection.send(new ServerboundCustomPayloadPacket(payload));
    }

    public static MinecraftServer integratedServer() { return Minecraft.getInstance().getSingleplayerServer(); }
}
