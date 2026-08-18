package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ServerboundRecipeBookSeenRecipePacket(RecipeDisplayId recipe) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC, ServerboundRecipeBookSeenRecipePacket::recipe, ServerboundRecipeBookSeenRecipePacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_RECIPE_BOOK_SEEN_RECIPE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleRecipeBookSeenRecipePacket(this);
   }
}
