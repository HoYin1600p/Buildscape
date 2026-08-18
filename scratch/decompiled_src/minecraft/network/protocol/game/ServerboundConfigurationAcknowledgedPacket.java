package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundConfigurationAcknowledgedPacket implements Packet {
   public static final ServerboundConfigurationAcknowledgedPacket INSTANCE = new ServerboundConfigurationAcknowledgedPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ServerboundConfigurationAcknowledgedPacket() {
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CONFIGURATION_ACKNOWLEDGED;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleConfigurationAcknowledged(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
