package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ClientboundDebugBlockValuePacket(BlockPos blockPos, DebugSubscription.Update update) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundDebugBlockValuePacket::blockPos, DebugSubscription.Update.STREAM_CODEC, ClientboundDebugBlockValuePacket::update, ClientboundDebugBlockValuePacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_BLOCK_VALUE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDebugBlockValue(this);
   }
}
