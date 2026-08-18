package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandPacket(String command) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundChatCommandPacket::write, ServerboundChatCommandPacket::new);

   private ServerboundChatCommandPacket(final FriendlyByteBuf input) {
      this(input.readUtf());
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUtf(this.command);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHAT_COMMAND;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChatCommand(this);
   }
}
