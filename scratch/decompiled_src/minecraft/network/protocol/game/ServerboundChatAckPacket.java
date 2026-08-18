package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatAckPacket(int offset) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundChatAckPacket::write, ServerboundChatAckPacket::new);

   private ServerboundChatAckPacket(final FriendlyByteBuf input) {
      this(input.readVarInt());
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.offset);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHAT_ACK;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChatAck(this);
   }
}
