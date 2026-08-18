package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ClientboundRecipeBookRemovePacket(List recipes) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRecipeBookRemovePacket::recipes, ClientboundRecipeBookRemovePacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_REMOVE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleRecipeBookRemove(this);
   }
}
