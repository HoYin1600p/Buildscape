package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundBlockChangedAckPacket(int sequence) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundBlockChangedAckPacket::write, ClientboundBlockChangedAckPacket::new);

   private ClientboundBlockChangedAckPacket(final FriendlyByteBuf input) {
      this(input.readVarInt());
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.sequence);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_CHANGED_ACK;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleBlockChangedAck(this);
   }
}
