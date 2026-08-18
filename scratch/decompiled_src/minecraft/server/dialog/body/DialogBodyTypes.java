package net.minecraft.server.dialog.body;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class DialogBodyTypes {
   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, Identifier.withDefaultNamespace("item"), ItemBody.MAP_CODEC);
      return (MapCodec)Registry.register(registry, Identifier.withDefaultNamespace("plain_message"), PlainMessage.MAP_CODEC);
   }
}
