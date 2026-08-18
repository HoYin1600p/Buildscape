package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ClientboundDebugEntityValuePacket(int entityId, DebugSubscription.Update update) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundDebugEntityValuePacket::entityId, DebugSubscription.Update.STREAM_CODEC, ClientboundDebugEntityValuePacket::update, ClientboundDebugEntityValuePacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_DEBUG_ENTITY_VALUE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleDebugEntityValue(this);
   }
}
