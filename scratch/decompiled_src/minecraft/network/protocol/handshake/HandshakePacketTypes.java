package net.minecraft.network.protocol.handshake;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class HandshakePacketTypes {
   public static final PacketType CLIENT_INTENTION = createServerbound("intention");

   private static PacketType createServerbound(final String id) {
      return new PacketType(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
