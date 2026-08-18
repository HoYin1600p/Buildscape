package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatSessionUpdatePacket(RemoteChatSession.Data chatSession) implements Packet {
   public static final StreamCodec STREAM_CODEC = RemoteChatSession.Data.STREAM_CODEC.map(ServerboundChatSessionUpdatePacket::new, ServerboundChatSessionUpdatePacket::chatSession);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHAT_SESSION_UPDATE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChatSessionUpdate(this);
   }
}
