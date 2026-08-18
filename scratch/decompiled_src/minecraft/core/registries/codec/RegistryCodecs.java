package net.minecraft.core.registries.codec;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistryCodecs {
   public static Codec holder(final ResourceKey registryKey) {
      return RegistryFixedCodec.create(registryKey);
   }

   public static Codec holder(final ResourceKey registryKey, final Codec elementCodec) {
      return holder(registryKey, elementCodec, true);
   }

   public static Codec holder(final ResourceKey registryKey, final Codec elementCodec, final boolean allowInline) {
      return RegistryFileCodec.create(registryKey, elementCodec, allowInline);
   }

   public static Codec holderSet(final ResourceKey registryKey, final Codec elementCodec) {
      return holderSet(registryKey, elementCodec, false);
   }

   public static Codec holderSet(final ResourceKey registryKey, final Codec elementCodec, final boolean alwaysUseList) {
      return HolderSetCodec.create(registryKey, holder(registryKey, elementCodec), alwaysUseList);
   }

   public static Codec holderSet(final ResourceKey registryKey) {
      return holderSet(registryKey, false);
   }

   public static Codec holderSet(final ResourceKey registryKey, final boolean alwaysUseList) {
      return HolderSetCodec.create(registryKey, holder(registryKey), alwaysUseList);
   }
}
