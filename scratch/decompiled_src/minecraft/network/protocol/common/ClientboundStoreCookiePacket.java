package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundStoreCookiePacket(Identifier key, byte[] payload) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundStoreCookiePacket::write, ClientboundStoreCookiePacket::new);
   private static final int MAX_PAYLOAD_SIZE = 5120;
   public static final StreamCodec PAYLOAD_STREAM_CODEC = ByteBufCodecs.byteArray(5120);

   private ClientboundStoreCookiePacket(final FriendlyByteBuf input) {
      this(input.readIdentifier(), (byte[])PAYLOAD_STREAM_CODEC.decode(input));
   }

   private void write(final FriendlyByteBuf output) {
      output.writeIdentifier(this.key);
      PAYLOAD_STREAM_CODEC.encode(output, this.payload);
   }

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_STORE_COOKIE;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleStoreCookie(this);
   }
}
