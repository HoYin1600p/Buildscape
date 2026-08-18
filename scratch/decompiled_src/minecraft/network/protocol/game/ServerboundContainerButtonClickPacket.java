package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerButtonClickPacket(int containerId, int buttonId) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ServerboundContainerButtonClickPacket::containerId, ByteBufCodecs.VAR_INT, ServerboundContainerButtonClickPacket::buttonId, ServerboundContainerButtonClickPacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_BUTTON_CLICK;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleContainerButtonClick(this);
   }
}
