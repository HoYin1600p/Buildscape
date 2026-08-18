package net.minecraft.network.protocol.configuration;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCodeOfConductPacket(String codeOfConduct) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClientboundCodeOfConductPacket::codeOfConduct, ClientboundCodeOfConductPacket::new);

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_CODE_OF_CONDUCT;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleCodeOfConduct(this);
   }
}
