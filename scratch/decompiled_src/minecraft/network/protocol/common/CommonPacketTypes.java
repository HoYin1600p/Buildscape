package net.minecraft.network.protocol.common;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class CommonPacketTypes {
   public static final PacketType CLIENTBOUND_CLEAR_DIALOG = createClientbound("clear_dialog");
   public static final PacketType CLIENTBOUND_CUSTOM_PAYLOAD = createClientbound("custom_payload");
   public static final PacketType CLIENTBOUND_CUSTOM_REPORT_DETAILS = createClientbound("custom_report_details");
   public static final PacketType CLIENTBOUND_DISCONNECT = createClientbound("disconnect");
   public static final PacketType CLIENTBOUND_KEEP_ALIVE = createClientbound("keep_alive");
   public static final PacketType CLIENTBOUND_PING = createClientbound("ping");
   public static final PacketType CLIENTBOUND_RESOURCE_PACK_POP = createClientbound("resource_pack_pop");
   public static final PacketType CLIENTBOUND_RESOURCE_PACK_PUSH = createClientbound("resource_pack_push");
   public static final PacketType CLIENTBOUND_POST_EFFECTS = createClientbound("post_effects");
   public static final PacketType CLIENTBOUND_SERVER_LINKS = createClientbound("server_links");
   public static final PacketType CLIENTBOUND_SHOW_DIALOG = createClientbound("show_dialog");
   public static final PacketType CLIENTBOUND_STORE_COOKIE = createClientbound("store_cookie");
   public static final PacketType CLIENTBOUND_TRANSFER = createClientbound("transfer");
   public static final PacketType CLIENTBOUND_UPDATE_TAGS = createClientbound("update_tags");
   public static final PacketType SERVERBOUND_CLIENT_INFORMATION = createServerbound("client_information");
   public static final PacketType SERVERBOUND_CUSTOM_PAYLOAD = createServerbound("custom_payload");
   public static final PacketType SERVERBOUND_KEEP_ALIVE = createServerbound("keep_alive");
   public static final PacketType SERVERBOUND_PONG = createServerbound("pong");
   public static final PacketType SERVERBOUND_RESOURCE_PACK = createServerbound("resource_pack");
   public static final PacketType SERVERBOUND_CUSTOM_CLICK_ACTION = createServerbound("custom_click_action");

   private static PacketType createClientbound(final String id) {
      return new PacketType(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
   }

   private static PacketType createServerbound(final String id) {
      return new PacketType(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
