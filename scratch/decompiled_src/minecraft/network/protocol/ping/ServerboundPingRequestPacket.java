package net.minecraft.network.protocol.ping;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPingRequestPacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundPingRequestPacket::write, ServerboundPingRequestPacket::new);
   private final long time;

   public ServerboundPingRequestPacket(final long time) {
      this.time = time;
   }

   private ServerboundPingRequestPacket(final ByteBuf input) {
      this.time = input.readLong();
   }

   private void write(final ByteBuf output) {
      output.writeLong(this.time);
   }

   public PacketType type() {
      return PingPacketTypes.SERVERBOUND_PING_REQUEST;
   }

   public void handle(final ServerPingPacketListener listener) {
      listener.handlePingRequest(this);
   }

   public long getTime() {
      return this.time;
   }
}
