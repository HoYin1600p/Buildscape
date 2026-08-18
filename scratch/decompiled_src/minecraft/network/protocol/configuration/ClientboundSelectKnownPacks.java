package net.minecraft.network.protocol.configuration;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ClientboundSelectKnownPacks(List knownPacks) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSelectKnownPacks::knownPacks, ClientboundSelectKnownPacks::new);

   public PacketType type() {
      return ConfigurationPacketTypes.CLIENTBOUND_SELECT_KNOWN_PACKS;
   }

   public void handle(final ClientConfigurationPacketListener listener) {
      listener.handleSelectKnownPacks(this);
   }
}
