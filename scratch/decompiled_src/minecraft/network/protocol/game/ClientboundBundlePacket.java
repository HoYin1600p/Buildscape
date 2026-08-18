package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundlePacket extends BundlePacket {
   public ClientboundBundlePacket(final Iterable packets) {
      super(packets);
   }

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_BUNDLE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleBundlePacket(this);
   }
}
