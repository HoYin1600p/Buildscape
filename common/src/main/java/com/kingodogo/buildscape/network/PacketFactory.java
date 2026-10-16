package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public final class PacketFactory {

    private PacketFactory() {}

    public static <T extends CommonPacket> void register(CommonId id, PacketDirection direction, Function<FriendlyByteBuf, T> decoder) {
        Services.PACKET_FACTORY.registerPacket(id, direction, decoder);
    }

    public static java.util.Collection<IPacketFactory.PacketDescriptor> getRegisteredDescriptors() {
        return Services.PACKET_FACTORY.getRegisteredDescriptors();
    }

    public static void handleServerbound(CommonId id, FriendlyByteBuf buf, ServerPlayer player) {
        Services.PACKET_FACTORY.handleServerbound(id, buf, player);
    }

    public static void handleServerbound(CommonId id, byte[] data, ServerPlayer player) {
        Services.PACKET_FACTORY.handleServerbound(id, data, player);
    }

    public static void handleClientbound(CommonId id, FriendlyByteBuf buf, net.minecraft.world.entity.player.Player player) {
        Services.PACKET_FACTORY.handleClientbound(id, buf, player);
    }

    public static void handleClientbound(CommonId id, byte[] data, net.minecraft.world.entity.player.Player player) {
        Services.PACKET_FACTORY.handleClientbound(id, data, player);
    }

    public static void sendToServer(CommonPacket packet) {
        Services.PACKET_FACTORY.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, CommonPacket packet) {
        Services.PACKET_FACTORY.sendToPlayer(player, packet);
    }

    public static void sendToAll(CommonPacket packet) {
        Services.PACKET_FACTORY.sendToAll(packet);
    }

    public static void sendToTracking(Level level, BlockPos pos, CommonPacket packet) {
        Services.PACKET_FACTORY.sendToTracking(level, pos, packet);
    }
}
