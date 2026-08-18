package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.entity.SignTextSlot;

public record ClientboundOpenSignEditorPacket(BlockPos pos, SignTextSlot slot) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ClientboundOpenSignEditorPacket::pos, SignTextSlot.STREAM_CODEC, ClientboundOpenSignEditorPacket::slot, ClientboundOpenSignEditorPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_SIGN_EDITOR;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleOpenSignEditor(this);
   }
}
