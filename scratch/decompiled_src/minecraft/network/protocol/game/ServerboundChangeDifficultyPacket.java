package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public record ServerboundChangeDifficultyPacket(Difficulty difficulty) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Difficulty.STREAM_CODEC, ServerboundChangeDifficultyPacket::difficulty, ServerboundChangeDifficultyPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CHANGE_DIFFICULTY;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleChangeDifficulty(this);
   }
}
