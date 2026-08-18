package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;

public class RegistryOps extends DelegatingOps {
   private final RegistryOps.RegistryInfoLookup lookupProvider;

   public static RegistryOps create(final DynamicOps parent, final HolderLookup.Provider lookupProvider) {
      return create(parent, new RegistryOps.HolderLookupAdapter(lookupProvider));
   }

   public static RegistryOps create(final DynamicOps parent, final RegistryOps.RegistryInfoLookup lookupProvider) {
      return new RegistryOps(parent, lookupProvider);
   }

   public static Dynamic injectRegistryContext(final Dynamic dynamic, final HolderLookup.Provider lookupProvider) {
      return new Dynamic(lookupProvider.createSerializationContext(dynamic.getOps()), dynamic.getValue());
   }

   private RegistryOps(final DynamicOps parent, final RegistryOps.RegistryInfoLookup lookupProvider) {
      super(parent);
      this.lookupProvider = lookupProvider;
   }

   public RegistryOps withParent(final DynamicOps parent) {
      return parent == this.delegate ? this : new RegistryOps(parent, this.lookupProvider);
   }

   public Optional getter(final ResourceKey registryKey) {
      return this.lookupProvider.lookup(registryKey);
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         RegistryOps ops = (RegistryOps)obj;
         return this.delegate.equals(ops.delegate) && this.lookupProvider.equals(ops.lookupProvider);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.delegate.hashCode() * 31 + this.lookupProvider.hashCode();
   }

   public static RecordCodecBuilder retrieveGetter(final ResourceKey registryKey) {
      return ExtraCodecs.retrieveContext((ops) -> {
         if (ops instanceof RegistryOps registryOps) {
            return (DataResult)registryOps.lookupProvider.lookup(registryKey).map((r) -> DataResult.success(r, Lifecycle.stable())).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + String.valueOf(registryKey)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((var0) -> null);
   }

   public static RecordCodecBuilder retrieveElement(final ResourceKey key) {
      ResourceKey registryKey = ResourceKey.createRegistryKey(key.registry());
      return ExtraCodecs.retrieveContext((ops) -> {
         if (ops instanceof RegistryOps registryOps) {
            return (DataResult)registryOps.lookupProvider.lookup(registryKey).flatMap((r) -> r.get(key)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Can't find value: " + String.valueOf(key)));
         } else {
            return DataResult.error(() -> "Not a registry ops");
         }
      }).forGetter((var0) -> null);
   }

   private static final class HolderLookupAdapter implements RegistryOps.RegistryInfoLookup {
      private final HolderLookup.Provider lookupProvider;
      private final Map lookups = new ConcurrentHashMap();

      public HolderLookupAdapter(final HolderLookup.Provider lookupProvider) {
         this.lookupProvider = lookupProvider;
      }

      public Optional lookup(final ResourceKey registryKey) {
         return (Optional)this.lookups.computeIfAbsent(registryKey, this.lookupProvider::lookup);
      }

      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         } else {
            if (obj instanceof RegistryOps.HolderLookupAdapter) {
               RegistryOps.HolderLookupAdapter adapter = (RegistryOps.HolderLookupAdapter)obj;
               if (this.lookupProvider.equals(adapter.lookupProvider)) {
                  return true;
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.lookupProvider.hashCode();
      }
   }

   public interface RegistryInfoLookup {
      Optional lookup(ResourceKey registryKey);
   }
}
