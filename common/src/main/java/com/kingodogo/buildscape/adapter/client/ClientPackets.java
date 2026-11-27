package com.kingodogo.buildscape.adapter.client;

import com.kingodogo.buildscape.network.CommonPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.function.Consumer;

/** Client packet operations must not be verified as part of the server packet factory. */
public final class ClientPackets {
    private ClientPackets() {}

    public static void handleOnClientThread(CommonPacket packet, Player player) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> packet.handle(player != null ? player : mc.player));
    }

    public static void forEachIntegratedServerPlayer(Consumer<ServerPlayer> action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
            mc.getSingleplayerServer().getPlayerList().getPlayers().forEach(action);
        }
    }
}
