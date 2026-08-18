package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandSignedPacket(String command, Instant timeStamp, long salt, ArgumentSignatures argumentSignatures, LastSeenMessages.Update lastSeenMessages) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ServerboundChatCommandSignedPacket::command, ByteBufCodecs.INSTANT, ServerboundChatCommandSignedPacket::timeStamp, ByteBufCodecs.LONG, ServerboundChatCommandSignedPacket::salt, ArgumentSignatures.STREAM_CODEC, ServerboundChatCommandSignedPacket::argumentSignatures, LastSeenMessages.Update.STREAM_CODEC, ServerboundChatCommandSignedPacket::lastSeenMessages, ServerboundChatCommandSignedPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHAT_COMMAND_SIGNED;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSignedChatCommand(this);
   }
}
