package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPlayerLoadedPacket() implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(new ServerboundPlayerLoadedPacket());

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_LOADED;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleAcceptPlayerLoad(this);
   }
}
