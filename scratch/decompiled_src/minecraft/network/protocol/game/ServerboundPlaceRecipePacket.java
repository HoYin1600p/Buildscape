package net.minecraft.network.protocol.game;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ServerboundPlaceRecipePacket(int containerId, RecipeDisplayId recipe, boolean useMaxItems) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ServerboundPlaceRecipePacket::containerId, RecipeDisplayId.STREAM_CODEC, ServerboundPlaceRecipePacket::recipe, ByteBufCodecs.BOOL, ServerboundPlaceRecipePacket::useMaxItems, ServerboundPlaceRecipePacket::new);

   public PacketType type() {
      return GamePacketTypes.SERVERBOUND_PLACE_RECIPE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePlaceRecipe(this);
   }
}
