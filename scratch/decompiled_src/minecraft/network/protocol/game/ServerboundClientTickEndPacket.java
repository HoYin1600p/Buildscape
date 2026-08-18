package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundClientTickEndPacket() implements Packet {
   public static final ServerboundClientTickEndPacket INSTANCE = new ServerboundClientTickEndPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CLIENT_TICK_END;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleClientTickEnd(this);
   }
}
