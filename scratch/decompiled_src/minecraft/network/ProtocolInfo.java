package net.minecraft.network;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.VisibleForDebug;
import org.jspecify.annotations.Nullable;

public interface ProtocolInfo {
   ConnectionProtocol id();

   PacketFlow flow();

   StreamCodec codec();

   @Nullable BundlerInfo bundlerInfo();

   public interface Details {
      ConnectionProtocol id();

      PacketFlow flow();

      @VisibleForDebug
      void listPackets(ProtocolInfo.Details.PacketVisitor output);

      @FunctionalInterface
      public interface PacketVisitor {
         void accept(PacketType type, int networkId);
      }
   }

   public interface DetailsProvider {
      ProtocolInfo.Details details();
   }
}
