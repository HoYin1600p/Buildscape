package net.minecraft.network.protocol.game;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundSetActionBarTextPacket(Component text) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.TRUSTED_STREAM_CODEC, ClientboundSetActionBarTextPacket::text, ClientboundSetActionBarTextPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_ACTION_BAR_TEXT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.setActionBarText(this);
   }
}
