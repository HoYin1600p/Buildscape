package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetSubtitleTextPacket(Component text) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetSubtitleTextPacket::text, ClientboundSetSubtitleTextPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_SUBTITLE_TEXT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.setSubtitleText(this);
   }
}
