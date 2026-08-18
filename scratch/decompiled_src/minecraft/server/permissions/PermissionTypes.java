package net.minecraft.server.permissions;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class PermissionTypes {
   public static MapCodec bootstrap(final Registry registry) {
      Registry.register(registry, Identifier.withDefaultNamespace("atom"), Permission.Atom.MAP_CODEC);
      return (MapCodec)Registry.register(registry, Identifier.withDefaultNamespace("command_level"), Permission.HasCommandLevel.MAP_CODEC);
   }
}
