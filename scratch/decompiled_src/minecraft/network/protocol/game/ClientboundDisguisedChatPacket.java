package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundDisguisedChatPacket(Component message, ChatType.Bound chatType) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundDisguisedChatPacket::message, ChatType.Bound.STREAM_CODEC, ClientboundDisguisedChatPacket::chatType, ClientboundDisguisedChatPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DISGUISED_CHAT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDisguisedChat(this);
   }

   public boolean isSkippable() {
      return true;
   }
}
