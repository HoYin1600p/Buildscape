package net.minecraft.network.protocol.common;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundClearDialogPacket implements Packet {
   public static final ClientboundClearDialogPacket INSTANCE = new ClientboundClearDialogPacket();
   public static final StreamCodec STREAM_CODEC = StreamCodec.unit(INSTANCE);

   private ClientboundClearDialogPacket() {
   }

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_CLEAR_DIALOG;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleClearDialog(this);
   }
}
