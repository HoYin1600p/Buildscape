package net.minecraft.client.color.item;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public class ItemTintSources {
   private static final ExtraCodecs.LateBoundIdMapper ID_MAPPER = new ExtraCodecs.LateBoundIdMapper();
   public static final Codec CODEC = ID_MAPPER.codec(Identifier.CODEC).dispatch(ItemTintSource::type, (c) -> c);

   public static void bootstrap() {
      ID_MAPPER.put(Identifier.withDefaultNamespace("custom_model_data"), CustomModelDataSource.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("constant"), Constant.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("dye"), Dye.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("grass"), GrassColorSource.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("firework"), Firework.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("potion"), Potion.MAP_CODEC);
      ID_MAPPER.put(Identifier.withDefaultNamespace("team"), TeamColor.MAP_CODEC);
   }
}
