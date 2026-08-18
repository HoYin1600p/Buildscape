package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundContainerClosePacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundContainerClosePacket::write, ServerboundContainerClosePacket::new);
   private final int containerId;

   public ServerboundContainerClosePacket(final int containerId) {
      this.containerId = containerId;
   }

   private ServerboundContainerClosePacket(final FriendlyByteBuf input) {
      this.containerId = input.readContainerId();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeContainerId(this.containerId);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_CLOSE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleContainerClose(this);
   }

   public int getContainerId() {
      return this.containerId;
   }
}
