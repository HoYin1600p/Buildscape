package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSystemChatPacket(Component content, boolean overlay) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSystemChatPacket::content, ByteBufCodecs.BOOL, ClientboundSystemChatPacket::overlay, ClientboundSystemChatPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SYSTEM_CHAT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSystemChat(this);
   }

   public boolean isSkippable() {
      return true;
   }
}
