package net.minecraft.network.protocol.game;

import java.time.Instant;
import java.util.Optional;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatPacket(String message, Instant timeStamp, long salt, Optional signature, LastSeenMessages.Update lastSeenMessages) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.stringUtf8(256), ServerboundChatPacket::message, ByteBufCodecs.INSTANT, ServerboundChatPacket::timeStamp, ByteBufCodecs.LONG, ServerboundChatPacket::salt, MessageSignature.STREAM_CODEC.apply(ByteBufCodecs::optional), ServerboundChatPacket::signature, LastSeenMessages.Update.STREAM_CODEC, ServerboundChatPacket::lastSeenMessages, ServerboundChatPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHAT;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChat(this);
   }
}
