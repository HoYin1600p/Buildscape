package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.level.ChunkPos;

public record ClientboundDebugChunkValuePacket(ChunkPos chunkPos, DebugSubscription.Update update) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ChunkPos.STREAM_CODEC, ClientboundDebugChunkValuePacket::chunkPos, DebugSubscription.Update.STREAM_CODEC, ClientboundDebugChunkValuePacket::update, ClientboundDebugChunkValuePacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_CHUNK_VALUE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDebugChunkValue(this);
   }
}
