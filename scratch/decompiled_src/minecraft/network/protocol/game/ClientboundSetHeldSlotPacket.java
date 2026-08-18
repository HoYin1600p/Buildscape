package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetHeldSlotPacket(int slot) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundSetHeldSlotPacket::slot, ClientboundSetHeldSlotPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_HELD_SLOT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetHeldSlot(this);
   }
}
