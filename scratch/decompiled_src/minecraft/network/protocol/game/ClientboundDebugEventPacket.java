package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ClientboundDebugEventPacket(DebugSubscription.Event event) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(DebugSubscription.Event.STREAM_CODEC, ClientboundDebugEventPacket::event, ClientboundDebugEventPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_EVENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDebugEvent(this);
   }
}
