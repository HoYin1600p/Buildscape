package net.minecraft.network.protocol.configuration;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundUpdateEnabledFeaturesPacket(Set features) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ClientboundUpdateEnabledFeaturesPacket::features, ClientboundUpdateEnabledFeaturesPacket::new);

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_UPDATE_ENABLED_FEATURES;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleEnabledFeatures(this);
   }
}
