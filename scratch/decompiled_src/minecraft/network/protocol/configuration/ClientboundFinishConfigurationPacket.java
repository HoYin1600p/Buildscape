package net.minecraft.network.protocol.configuration;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundFinishConfigurationPacket implements Packet {
   public static final ClientboundFinishConfigurationPacket INSTANCE = new ClientboundFinishConfigurationPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundFinishConfigurationPacket() {
   }

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_FINISH_CONFIGURATION;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleConfigurationFinished(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
