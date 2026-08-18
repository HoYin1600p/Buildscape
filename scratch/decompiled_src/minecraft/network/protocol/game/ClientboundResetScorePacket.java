package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import org.jspecify.annotations.Nullable;

public record ClientboundResetScorePacket(String owner, @Nullable String objectiveName) implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ClientboundResetScorePacket::write, ClientboundResetScorePacket::new);

   private ClientboundResetScorePacket(final FriendlyByteBuf input) {
      this(input.readUtf(), (String)input.readNullable(FriendlyByteBuf::readUtf));
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUtf(this.owner);
      output.writeNullable(this.objectiveName, FriendlyByteBuf::writeUtf);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_RESET_SCORE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleResetScore(this);
   }
}
