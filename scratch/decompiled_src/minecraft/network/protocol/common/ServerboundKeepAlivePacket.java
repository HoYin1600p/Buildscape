package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundKeepAlivePacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundKeepAlivePacket::write, ServerboundKeepAlivePacket::new);
   private final long id;

   public ServerboundKeepAlivePacket(final long id) {
      this.id = id;
   }

   private ServerboundKeepAlivePacket(final FriendlyByteBuf input) {
      this.id = input.readLong();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeLong(this.id);
   }

   public PacketType type() {
      return CommonPacketTypes.SERVERBOUND_KEEP_ALIVE;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleKeepAlive(this);
   }

   public long getId() {
      return this.id;
   }
}
