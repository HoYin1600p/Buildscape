package net.minecraft.network.protocol.login;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class LoginPacketTypes {
   public static final PacketType CLIENTBOUND_CUSTOM_QUERY = createClientbound("custom_query");
   public static final PacketType CLIENTBOUND_LOGIN_FINISHED = createClientbound("login_finished");
   public static final PacketType CLIENTBOUND_HELLO = createClientbound("hello");
   public static final PacketType CLIENTBOUND_LOGIN_COMPRESSION = createClientbound("login_compression");
   public static final PacketType CLIENTBOUND_LOGIN_DISCONNECT = createClientbound("login_disconnect");
   public static final PacketType SERVERBOUND_CUSTOM_QUERY_ANSWER = createServerbound("custom_query_answer");
   public static final PacketType SERVERBOUND_HELLO = createServerbound("hello");
   public static final PacketType SERVERBOUND_KEY = createServerbound("key");
   public static final PacketType SERVERBOUND_LOGIN_ACKNOWLEDGED = createServerbound("login_acknowledged");

   private static PacketType createClientbound(final String id) {
      return new PacketType(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
   }

   private static PacketType createServerbound(final String id) {
      return new PacketType(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
