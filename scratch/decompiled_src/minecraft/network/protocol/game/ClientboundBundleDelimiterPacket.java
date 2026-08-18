package net.minecraft.network.protocol.game;

import net.minecraft.network.protocol.BundleDelimiterPacket;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBundleDelimiterPacket extends BundleDelimiterPacket {
   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_BUNDLE_DELIMITER;
   }
}
