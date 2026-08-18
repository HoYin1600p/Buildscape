package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundGameTestHighlightPosPacket(BlockPos absolutePos, BlockPos relativePos) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundGameTestHighlightPosPacket::absolutePos, BlockPos.STREAM_CODEC, ClientboundGameTestHighlightPosPacket::relativePos, ClientboundGameTestHighlightPosPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_GAME_TEST_HIGHLIGHT_POS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleGameTestHighlightPos(this);
   }
}
