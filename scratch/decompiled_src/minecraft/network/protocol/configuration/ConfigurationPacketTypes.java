package net.minecraft.network.protocol.configuration;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class ConfigurationPacketTypes {
   public static final PacketType CLIENTBOUND_CODE_OF_CONDUCT = createClientbound("code_of_conduct");
   public static final PacketType CLIENTBOUND_FINISH_CONFIGURATION = createClientbound("finish_configuration");
   public static final PacketType CLIENTBOUND_REGISTRY_DATA = createClientbound("registry_data");
   public static final PacketType CLIENTBOUND_RESET_CHAT = createClientbound("reset_chat");
   public static final PacketType CLIENTBOUND_SELECT_KNOWN_PACKS = createClientbound("select_known_packs");
   public static final PacketType CLIENTBOUND_UPDATE_ENABLED_FEATURES = createClientbound("update_enabled_features");
   public static final PacketType SERVERBOUND_ACCEPT_CODE_OF_CONDUCT = createServerbound("accept_code_of_conduct");
   public static final PacketType SERVERBOUND_FINISH_CONFIGURATION = createServerbound("finish_configuration");
   public static final PacketType SERVERBOUND_SELECT_KNOWN_PACKS = createServerbound("select_known_packs");

   private static PacketType createClientbound(final String id) {
      return new PacketType(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
   }

   private static PacketType createServerbound(final String id) {
      return new PacketType(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
