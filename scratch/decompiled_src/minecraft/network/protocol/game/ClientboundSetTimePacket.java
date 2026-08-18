package net.minecraft.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;

public record ClientboundSetTimePacket(long gameTime, Map clockUpdates) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.LONG, ClientboundSetTimePacket::gameTime, ByteBufCodecs.map(HashMap::new, WorldClock.STREAM_CODEC, ClockNetworkState.STREAM_CODEC), ClientboundSetTimePacket::clockUpdates, ClientboundSetTimePacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_TIME;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetTime(this);
   }
}
