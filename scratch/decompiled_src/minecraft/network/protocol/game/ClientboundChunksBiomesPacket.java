package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record ClientboundChunksBiomesPacket(List chunkBiomeData) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ClientboundChunksBiomesPacket.ChunkBiomeData.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundChunksBiomesPacket::chunkBiomeData, ClientboundChunksBiomesPacket::new);
   private static final int TWO_MEGABYTES = 2097152;

   public static ClientboundChunksBiomesPacket forChunks(final List chunks) {
      return new ClientboundChunksBiomesPacket(chunks.stream().map(ClientboundChunksBiomesPacket.ChunkBiomeData::new).toList());
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_CHUNKS_BIOMES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChunksBiomes(this);
   }

   public static record ChunkBiomeData(ChunkPos pos, byte[] buffer) {
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ChunkPos.STREAM_CODEC, ClientboundChunksBiomesPacket.ChunkBiomeData::pos, ByteBufCodecs.byteArray(2097152), ClientboundChunksBiomesPacket.ChunkBiomeData::buffer, ClientboundChunksBiomesPacket.ChunkBiomeData::new);

      public ChunkBiomeData(final LevelChunk chunk) {
         this(chunk.getPos(), new byte[calculateChunkSize(chunk)]);
         extractChunkData(new FriendlyByteBuf(this.getWriteBuffer()), chunk);
      }

      private static int calculateChunkSize(final LevelChunk chunk) {
         int total = 0;

         for(LevelChunkSection section : chunk.getSections()) {
            total += section.getBiomes().getSerializedSize();
         }

         return total;
      }

      public FriendlyByteBuf getReadBuffer() {
         return new FriendlyByteBuf(Unpooled.wrappedBuffer(this.buffer));
      }

      private ByteBuf getWriteBuffer() {
         ByteBuf buffer = Unpooled.wrappedBuffer(this.buffer);
         buffer.writerIndex(0);
         return buffer;
      }

      public static void extractChunkData(final FriendlyByteBuf buffer, final LevelChunk chunk) {
         for(LevelChunkSection section : chunk.getSections()) {
            section.getBiomes().write(buffer);
         }

         if (buffer.writerIndex() != buffer.capacity()) {
            throw new IllegalStateException("Didn't fill biome buffer: expected " + buffer.capacity() + " bytes, got " + buffer.writerIndex());
         }
      }
   }
}
