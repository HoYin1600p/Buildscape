package net.minecraft.network.protocol.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisconnectPacket(Component reason) implements Packet {
   public static final StreamCodec STREAM_CODEC = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.map(ClientboundDisconnectPacket::new, ClientboundDisconnectPacket::reason);

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_DISCONNECT;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleDisconnect(this);
   }
}
