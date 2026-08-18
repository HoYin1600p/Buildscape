package net.minecraft.network.protocol.configuration;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundResetChatPacket implements Packet {
   public static final ClientboundResetChatPacket INSTANCE = new ClientboundResetChatPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundResetChatPacket() {
   }

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_RESET_CHAT;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleResetChat(this);
   }
}
