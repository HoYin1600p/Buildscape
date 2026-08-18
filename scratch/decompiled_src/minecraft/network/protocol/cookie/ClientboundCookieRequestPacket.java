package net.minecraft.network.protocol.cookie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundCookieRequestPacket(Identifier key) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundCookieRequestPacket::write, ClientboundCookieRequestPacket::new);

   private ClientboundCookieRequestPacket(final FriendlyByteBuf input) {
      this(input.readIdentifier());
   }

   private void write(final FriendlyByteBuf output) {
      output.writeIdentifier(this.key);
   }

   public PacketType type() {
      return CookiePacketTypes.CLIENTBOUND_COOKIE_REQUEST;
   }

   public void handle(final ClientCookiePacketListener listener) {
      listener.handleRequestCookie(this);
   }
}
