package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public record ClientboundSetCursorItemPacket(ItemStack contents) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSetCursorItemPacket::contents, ClientboundSetCursorItemPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_CURSOR_ITEM;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetCursorItem(this);
   }
}
