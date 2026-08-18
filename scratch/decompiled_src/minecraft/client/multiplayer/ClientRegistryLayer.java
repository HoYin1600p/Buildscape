package net.minecraft.client.multiplayer;

import java.util.List;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public enum ClientRegistryLayer {
   STATIC,
   REMOTE;

   private static final List VALUES = List.of(values());
   private static final RegistryAccess.Frozen STATIC_ACCESS = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

   public static LayeredRegistryAccess createRegistryAccess() {
      return (new LayeredRegistryAccess(VALUES)).replaceFrom(STATIC, STATIC_ACCESS);
   }

   // $FF: synthetic method
   private static ClientRegistryLayer[] $values() {
      return new ClientRegistryLayer[]{STATIC, REMOTE};
   }
}
