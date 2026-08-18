package net.minecraft.network.protocol;

import net.minecraft.network.PacketListener;

public abstract class BundleDelimiterPacket implements Packet {
   public final void handle(final PacketListener listener) {
      throw new AssertionError("This packet should be handled by pipeline");
   }

   public abstract PacketType type();
}
