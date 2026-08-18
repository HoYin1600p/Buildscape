package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.stats.Stat;

public record ClientboundAwardStatsPacket(Object2IntMap stats) implements Packet {
   private static final StreamCodec STAT_VALUES_STREAM_CODEC = ByteBufCodecs.map(Object2IntOpenHashMap::new, Stat.STREAM_CODEC, ByteBufCodecs.VAR_INT);
   public static final StreamCodec STREAM_CODEC = STAT_VALUES_STREAM_CODEC.map(ClientboundAwardStatsPacket::new, ClientboundAwardStatsPacket::stats);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_AWARD_STATS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleAwardStats(this);
   }
}
