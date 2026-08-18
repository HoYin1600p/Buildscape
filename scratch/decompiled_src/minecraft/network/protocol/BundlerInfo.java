package net.minecraft.network.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public interface BundlerInfo {
   int BUNDLE_SIZE_LIMIT = 4096;

   static BundlerInfo createForPacket(final PacketType bundlePacketType, final Function constructor, final BundleDelimiterPacket delimiterPacket) {
      return new BundlerInfo() {
         public void unbundlePacket(final Packet packet, final Consumer output) {
            if (packet.type() == bundlePacketType) {
               BundlePacket bundlerPacket = (P)((BundlePacket)packet);
               output.accept(delimiterPacket);
               bundlerPacket.subPackets().forEach(output);
               output.accept(delimiterPacket);
            } else {
               output.accept(packet);
            }

         }

         public BundlerInfo.@Nullable Bundler startPacketBundling(final Packet packet) {
            return packet == delimiterPacket ? new BundlerInfo.Bundler(this) {
               private final List bundlePackets;

               {
                  Objects.requireNonNull(this$0);
                  this.bundlePackets = new ArrayList();
               }

               public @Nullable Packet addPacket(final Packet packet) {
                  if (packet == delimiterPacket) {
                     return (Packet)constructor.apply(this.bundlePackets);
                  } else if (this.bundlePackets.size() >= 4096) {
                     throw new IllegalStateException("Too many packets in a bundle");
                  } else {
                     this.bundlePackets.add(packet);
                     return null;
                  }
               }
            } : null;
         }
      };
   }

   void unbundlePacket(Packet packet, Consumer output);

   BundlerInfo.@Nullable Bundler startPacketBundling(Packet packet);

   public interface Bundler {
      @Nullable Packet addPacket(Packet packet);
   }
}
