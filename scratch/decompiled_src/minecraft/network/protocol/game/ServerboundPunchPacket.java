package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPunchPacket() implements Packet {
   public static final ServerboundPunchPacket INSTANCE = new ServerboundPunchPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_PUNCH;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePunch(this);
   }
}
