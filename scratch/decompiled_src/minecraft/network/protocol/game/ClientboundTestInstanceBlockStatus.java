package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTestInstanceBlockStatus(Component status, Optional size) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, ClientboundTestInstanceBlockStatus::status, ByteBufCodecs.optional(Vec3i.STREAM_CODEC), ClientboundTestInstanceBlockStatus::size, ClientboundTestInstanceBlockStatus::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_TEST_INSTANCE_BLOCK_STATUS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleTestInstanceBlockStatus(this);
   }
}
