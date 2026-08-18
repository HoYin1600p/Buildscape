package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundChunkBatchFinishedPacket(int batchSize) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundChunkBatchFinishedPacket::write, ClientboundChunkBatchFinishedPacket::new);

   private ClientboundChunkBatchFinishedPacket(final FriendlyByteBuf input) {
      this(input.readVarInt());
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.batchSize);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_CHUNK_BATCH_FINISHED;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChunkBatchFinished(this);
   }
}
