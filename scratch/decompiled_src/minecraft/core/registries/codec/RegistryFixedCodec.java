package net.minecraft.core.registries.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public final class RegistryFixedCodec implements Codec {
   private final ResourceKey registryKey;

   public static Codec create(final ResourceKey registryKey) {
      return new RegistryFixedCodec(registryKey);
   }

   private RegistryFixedCodec(final ResourceKey registryKey) {
      this.registryKey = registryKey;
   }

   public DataResult encode(final Holder input, final DynamicOps ops, final Object prefix) {
      if (ops instanceof RegistryOps registryOps) {
         Optional maybeOwner = registryOps.getter(this.registryKey);
         if (maybeOwner.isPresent()) {
            if (!input.canSerializeIn((HolderOwner)maybeOwner.get())) {
               return DataResult.error(() -> "Element " + String.valueOf(input) + " is not valid in current registry set");
            }

            return (DataResult)input.unwrap().map((id) -> Identifier.CODEC.encode(id.identifier(), ops, prefix), (value) -> DataResult.error(() -> "Elements from registry " + String.valueOf(this.registryKey) + " can't be serialized to a value"));
         }
      }

      return DataResult.error(() -> "Can't access registry " + String.valueOf(this.registryKey.identifier()));
   }

   public DataResult decode(final DynamicOps ops, final Object input) {
      if (ops instanceof RegistryOps registryOps) {
         Optional lookup = registryOps.getter(this.registryKey);
         if (lookup.isPresent()) {
            return Identifier.CODEC.decode(ops, input).flatMap((pair) -> {
               ResourceKey elementKey = ResourceKey.create(this.registryKey, (Identifier)pair.getFirst());
               return ((DataResult)((HolderGetter)lookup.get()).get(elementKey).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Failed to get element " + String.valueOf(elementKey.identifier()) + " from registry " + String.valueOf(elementKey.registry())))).map((h) -> Pair.of(h, pair.getSecond())).setLifecycle(Lifecycle.stable());
            });
         }
      }

      return DataResult.error(() -> "Can't access registry " + String.valueOf(this.registryKey.identifier()));
   }

   public String toString() {
      return "RegistryFixedCodec[" + String.valueOf(this.registryKey) + "]";
   }
}
