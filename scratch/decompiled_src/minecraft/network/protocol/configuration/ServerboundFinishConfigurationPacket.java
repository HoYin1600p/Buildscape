package net.minecraft.network.protocol.configuration;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundFinishConfigurationPacket implements Packet {
   public static final ServerboundFinishConfigurationPacket INSTANCE = new ServerboundFinishConfigurationPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ServerboundFinishConfigurationPacket() {
   }

   public PacketType type() {
      return ConfigurationPacketTypes.SERVERBOUND_FINISH_CONFIGURATION;
   }

   public void handle(final ServerConfigurationPacketListener listener) {
      listener.handleConfigurationFinished(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
