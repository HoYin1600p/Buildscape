package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.Difficulty;

public record ClientboundChangeDifficultyPacket(Difficulty difficulty, boolean locked) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Difficulty.STREAM_CODEC, ClientboundChangeDifficultyPacket::difficulty, ByteBufCodecs.BOOL, ClientboundChangeDifficultyPacket::locked, ClientboundChangeDifficultyPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_CHANGE_DIFFICULTY;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleChangeDifficulty(this);
   }
}
