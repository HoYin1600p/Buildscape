package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public record ClientboundSetPlayerInventoryPacket(int slot, ItemStack contents) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundSetPlayerInventoryPacket::slot, ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSetPlayerInventoryPacket::contents, ClientboundSetPlayerInventoryPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_SET_PLAYER_INVENTORY;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetPlayerInventory(this);
   }
}
