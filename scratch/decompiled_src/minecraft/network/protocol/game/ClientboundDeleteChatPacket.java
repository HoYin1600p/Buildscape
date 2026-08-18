package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDeleteChatPacket(MessageSignature.Packed messageSignature) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(MessageSignature.Packed.STREAM_CODEC, ClientboundDeleteChatPacket::messageSignature, ClientboundDeleteChatPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DELETE_CHAT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDeleteChat(this);
   }
}
