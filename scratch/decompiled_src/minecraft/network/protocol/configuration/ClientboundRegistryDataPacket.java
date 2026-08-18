package net.minecraft.network.protocol.configuration;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record ClientboundRegistryDataPacket(ResourceKey registry, List entries) implements Packet {
   private static final StreamCodec REGISTRY_KEY_STREAM_CODEC = Identifier.STREAM_CODEC.map(ResourceKey::createRegistryKey, ResourceKey::identifier);
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(REGISTRY_KEY_STREAM_CODEC, ClientboundRegistryDataPacket::registry, RegistrySynchronization.PackedRegistryEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRegistryDataPacket::entries, ClientboundRegistryDataPacket::new);

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_REGISTRY_DATA;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleRegistryData(this);
   }
}
