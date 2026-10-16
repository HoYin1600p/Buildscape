package com.kingodogo.buildscape.adapter.v121x.client;

import com.kingodogo.buildscape.network.CommonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Client packet operations must not be verified as part of the server packet factory. */
public final class ClientPackets {
    private ClientPackets() {}

    public static void handleOnClientThread(CommonPacket packet, Player player) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> packet.handle(player != null ? player : mc.player));
    }

    public static void sendToServer(Supplier<CustomPacketPayload> payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundCustomPayloadPacket(payload.get()));
        }
    }

    public static void forEachIntegratedServerPlayer(Consumer<ServerPlayer> action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
            mc.getSingleplayerServer().getPlayerList().getPlayers().forEach(action);
        }
    }
}
