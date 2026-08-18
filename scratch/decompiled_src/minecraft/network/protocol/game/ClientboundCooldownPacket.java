package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public record ClientboundCooldownPacket(Identifier cooldownGroup, int duration) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, ClientboundCooldownPacket::cooldownGroup, ByteBufCodecs.VAR_INT, ClientboundCooldownPacket::duration, ClientboundCooldownPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_COOLDOWN;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleItemCooldown(this);
   }
}
