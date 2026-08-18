package net.minecraft.network.protocol.ping;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class PingPacketTypes {
   public static final PacketType CLIENTBOUND_PONG_RESPONSE = createClientbound("pong_response");
   public static final PacketType SERVERBOUND_PING_REQUEST = createServerbound("ping_request");

   private static PacketType createClientbound(final String id) {
      return new PacketType(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
   }

   private static PacketType createServerbound(final String id) {
      return new PacketType(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
