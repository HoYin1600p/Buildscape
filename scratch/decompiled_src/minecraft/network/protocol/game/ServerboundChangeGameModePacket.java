package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.GameType;

public record ServerboundChangeGameModePacket(GameType mode) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(GameType.STREAM_CODEC, ServerboundChangeGameModePacket::mode, ServerboundChangeGameModePacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHANGE_GAME_MODE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChangeGameMode(this);
   }
}
