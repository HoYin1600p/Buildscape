package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundStartConfigurationPacket implements Packet {
   public static final ClientboundStartConfigurationPacket INSTANCE = new ClientboundStartConfigurationPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundStartConfigurationPacket() {
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_START_CONFIGURATION;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleConfigurationStart(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
