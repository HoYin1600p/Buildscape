package net.minecraft.network.protocol.common;

import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.ServerLinks;

public record ClientboundServerLinksPacket(List links) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ServerLinks.UNTRUSTED_LINKS_STREAM_CODEC, ClientboundServerLinksPacket::links, ClientboundServerLinksPacket::new);

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_SERVER_LINKS;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleServerLinks(this);
   }
}
