package com.kingodogo.buildscape.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import io.netty.buffer.Unpooled;

/** Descriptor decoding and thread dispatch remain in the shared v118x factory. */
public final class FabricPacketFactory extends com.kingodogo.buildscape.adapter.v118x.PacketFactory {
    @Override public void handleServerbound(com.kingodogo.buildscape.util.CommonId id, byte[] data, ServerPlayer player) {
        var buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        try { handleServerbound(id, buffer, player); }
        finally { buffer.release(); }
    }
    @Override public void handleClientbound(com.kingodogo.buildscape.util.CommonId id, byte[] data,
            net.minecraft.world.entity.player.Player player) {
        var buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        try { handleClientbound(id, buffer, player); }
        finally { buffer.release(); }
    }
    public void installReceivers() {
        for (var descriptor : getRegisteredDescriptors()) {
            if (descriptor.direction() != PacketDirection.CLIENT_TO_SERVER) continue;
            var id = new ResourceLocation(descriptor.id().getNamespace(), descriptor.id().getPath());
            ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buffer, sender) ->
                    handleServerbound(descriptor.id(), buffer, player));
        }
    }
    @Override public void sendToPlayer(ServerPlayer player, CommonPacket packet) {
        if (player == null || player.connection == null) return;
        var id = new ResourceLocation(packet.getId().getNamespace(), packet.getId().getPath());
        if (!ServerPlayNetworking.canSend(player, id)) return;
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buffer);
        ServerPlayNetworking.send(player, id, buffer);
    }
    @Override public void sendToAll(CommonPacket packet) {
        var server = getServer();
        if (server != null) server.getPlayerList().getPlayers().forEach(player -> sendToPlayer(player, packet));
    }
}
