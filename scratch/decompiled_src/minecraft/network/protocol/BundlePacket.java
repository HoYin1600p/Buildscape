package net.minecraft.network.protocol;

public abstract class BundlePacket implements Packet {
   private final Iterable packets;

   protected BundlePacket(final Iterable packets) {
      this.packets = packets;
   }

   public final Iterable subPackets() {
      return this.packets;
   }

   public abstract PacketType type();
}
