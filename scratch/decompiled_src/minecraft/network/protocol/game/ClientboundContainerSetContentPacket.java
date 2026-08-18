package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.ItemStack;

public record ClientboundContainerSetContentPacket(int containerId, int stateId, List items, ItemStack carriedItem) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ClientboundContainerSetContentPacket::containerId, ByteBufCodecs.VAR_INT, ClientboundContainerSetContentPacket::stateId, ItemStack.OPTIONAL_LIST_STREAM_CODEC, ClientboundContainerSetContentPacket::items, ItemStack.OPTIONAL_STREAM_CODEC, ClientboundContainerSetContentPacket::carriedItem, ClientboundContainerSetContentPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_SET_CONTENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleContainerContent(this);
   }
}
