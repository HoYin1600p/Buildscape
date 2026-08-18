package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundChunkBatchStartPacket implements Packet {
   public static final ClientboundChunkBatchStartPacket INSTANCE = new ClientboundChunkBatchStartPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundChunkBatchStartPacket() {
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_CHUNK_BATCH_START;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChunkBatchStart(this);
   }
}
