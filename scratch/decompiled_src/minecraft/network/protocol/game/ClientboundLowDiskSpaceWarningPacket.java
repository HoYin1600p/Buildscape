package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLowDiskSpaceWarningPacket implements Packet {
   public static final ClientboundLowDiskSpaceWarningPacket INSTANCE = new ClientboundLowDiskSpaceWarningPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundLowDiskSpaceWarningPacket() {
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_LOW_DISK_SPACE_WARNING;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleLowDiskSpaceWarning(this);
   }
}
