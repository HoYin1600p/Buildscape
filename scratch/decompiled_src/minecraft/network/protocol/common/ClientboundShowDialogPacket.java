package net.minecraft.network.protocol.common;

import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.dialog.Dialog;

public record ClientboundShowDialogPacket(Holder dialog) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(Dialog.STREAM_CODEC, ClientboundShowDialogPacket::dialog, ClientboundShowDialogPacket::new);
   public static final StreamCodec CONTEXT_FREE_STREAM_CODEC = StreamCodec.composite(Dialog.CONTEXT_FREE_STREAM_CODEC.map(Holder::direct, Holder::value), ClientboundShowDialogPacket::dialog, ClientboundShowDialogPacket::new);

   public PacketType type() {
      return CommonPacketTypes.CLIENTBOUND_SHOW_DIALOG;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleShowDialog(this);
   }
}
