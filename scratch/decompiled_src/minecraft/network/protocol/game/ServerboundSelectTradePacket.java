package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundSelectTradePacket implements Packet {
   public static final StreamCodec STREAM_CODEC = Packet.codec(ServerboundSelectTradePacket::write, ServerboundSelectTradePacket::new);
   private final int item;

   public ServerboundSelectTradePacket(final int item) {
      this.item = item;
   }

   private ServerboundSelectTradePacket(final FriendlyByteBuf input) {
      this.item = input.readVarInt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.item);
   }

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_SELECT_TRADE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}
