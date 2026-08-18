package net.minecraft.network.protocol.common;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundPostEffectsPacket(List postEffects) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundPostEffectsPacket::postEffects, ClientboundPostEffectsPacket::new);

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_POST_EFFECTS;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handlePostEffects(this);
   }
}
