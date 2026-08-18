package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;

public record ServerboundSetBeaconPacket(Optional primary, Optional secondary) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(MobEffect.STREAM_CODEC.apply(ByteBufCodecs::optional), ServerboundSetBeaconPacket::primary, MobEffect.STREAM_CODEC.apply(ByteBufCodecs::optional), ServerboundSetBeaconPacket::secondary, ServerboundSetBeaconPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_SET_BEACON;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSetBeaconPacket(this);
   }
}
