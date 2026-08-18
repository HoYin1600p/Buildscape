package net.minecraft.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public record ClientboundUpdateRecipesPacket(Map itemSets, SelectableRecipe.SingleInputSet stonecutterRecipes) implements Packet {
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, ResourceKey.streamCodec(RecipePropertySet.TYPE_KEY), RecipePropertySet.STREAM_CODEC), ClientboundUpdateRecipesPacket::itemSets, SelectableRecipe.SingleInputSet.noRecipeCodec(), ClientboundUpdateRecipesPacket::stonecutterRecipes, ClientboundUpdateRecipesPacket::new);

   public PacketType type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_RECIPES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleUpdateRecipes(this);
   }
}
