package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ActionTypes {
   public static MapCodec bootstrap(final Registry registry) {
      StaticAction.WRAPPED_CODECS.forEach((action, codec) -> Registry.register(registry, Identifier.withDefaultNamespace(action.getSerializedName()), codec));
      Registry.register(registry, Identifier.withDefaultNamespace("dynamic/run_command"), CommandTemplate.MAP_CODEC);
      return (MapCodec)Registry.register(registry, Identifier.withDefaultNamespace("dynamic/custom"), CustomAll.MAP_CODEC);
   }
}
