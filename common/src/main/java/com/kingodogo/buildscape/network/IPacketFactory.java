package com.kingodogo.buildscape.network;

import com.kingodogo.buildscape.util.CommonId;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.function.Function;

public interface IPacketFactory {
    record PacketDescriptor(CommonId id, PacketDirection direction) {}

    <T extends CommonPacket> void registerPacket(CommonId id, PacketDirection direction, Function<FriendlyByteBuf, T> decoder);
    Collection<PacketDescriptor> getRegisteredDescriptors();

    void handleServerbound(CommonId id, FriendlyByteBuf buf, ServerPlayer player);
    default void handleServerbound(CommonId id, byte[] data, ServerPlayer player) {
        handleServerbound(id, new FriendlyByteBuf(Unpooled.wrappedBuffer(data)), player);
    }

    void handleClientbound(CommonId id, FriendlyByteBuf buf, Player player);
    default void handleClientbound(CommonId id, byte[] data, Player player) {
        handleClientbound(id, new FriendlyByteBuf(Unpooled.wrappedBuffer(data)), player);
    }

    void sendToServer(CommonPacket packet);
    void sendToPlayer(ServerPlayer player, CommonPacket packet);
    void sendToAll(CommonPacket packet);
    void sendToTracking(Level level, BlockPos pos, CommonPacket packet);
}
