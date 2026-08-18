package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.player.Input;

public record ServerboundPlayerInputPacket(Input input) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Input.STREAM_CODEC, ServerboundPlayerInputPacket::input, ServerboundPlayerInputPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_INPUT;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePlayerInput(this);
   }
}
