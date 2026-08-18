package net.minecraft.network.protocol.configuration;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ServerboundSelectKnownPacks(List knownPacks) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list(64)), ServerboundSelectKnownPacks::knownPacks, ServerboundSelectKnownPacks::new);

   public PacketType type() {
      return ConfigurationPacketTypes.SERVERBOUND_SELECT_KNOWN_PACKS;
   }

   public void handle(final ServerConfigurationPacketListener listener) {
      listener.handleSelectKnownPacks(this);
   }
}
