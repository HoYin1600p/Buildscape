package net.minecraft.network.protocol.configuration;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundAcceptCodeOfConductPacket() implements Packet {
   public static final ServerboundAcceptCodeOfConductPacket INSTANCE = new ServerboundAcceptCodeOfConductPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   public PacketType type() {
      return ConfigurationPacketTypes.SERVERBOUND_ACCEPT_CODE_OF_CONDUCT;
   }

   public void handle(final ServerConfigurationPacketListener listener) {
      listener.handleAcceptCodeOfConduct(this);
   }
}
