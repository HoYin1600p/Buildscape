package net.minecraft.resources;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.ConcurrentHolderGetter;
import net.minecraft.nbt.Tag;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StrictJsonParser;

public abstract class RegistryLoadTask {
   private final Object registryWriteLock = new Object();
   protected final RegistryDataLoader.RegistryData data;
   private final WritableRegistry registry;
   protected final ConcurrentHolderGetter concurrentRegistrationGetter;
   protected final Map loadingErrors;
   private volatile boolean elementsRegistered;

   protected RegistryLoadTask(final RegistryDataLoader.RegistryData data, final Lifecycle lifecycle, final Map loadingErrors) {
      this.data = data;
      this.registry = new MappedRegistry(data.key(), lifecycle);
      this.loadingErrors = loadingErrors;
      this.concurrentRegistrationGetter = new ConcurrentHolderGetter(this.registryWriteLock, this.registry.createRegistrationLookup());
   }

   protected ResourceKey registryKey() {
      return this.registry.key();
   }

   protected Registry readOnlyRegistry() {
      if (!this.elementsRegistered) {
         throw new IllegalStateException("Elements not registered");
      } else {
         return this.registry;
      }
   }

   public abstract CompletableFuture load(RegistryOps.RegistryInfoLookup context, Executor executor);

   protected void registerElements(final Stream elements) {
      synchronized(this.registryWriteLock) {
         elements.forEach((element) -> element.value.ifLeft((value) -> this.registry.register(element.key, value, element.registrationInfo)).ifRight((error) -> this.loadingErrors.put(element.key, error)));
         this.elementsRegistered = true;
      }
   }

   protected void registerTags(final Map pendingTags) {
      synchronized(this.registryWriteLock) {
         this.registry.bindTags(pendingTags);
      }
   }

   public boolean freezeRegistry(final Map loadingErrors) {
      try {
         this.registry.freeze();
         return true;
      } catch (Exception var3) {
         loadingErrors.put(this.registry.key(), var3);
         return false;
      }
   }

   public Optional validateRegistry(final Map loadingErrors) {
      Map registryErrors = new HashMap();
      this.data.validator().validate(this.registry, registryErrors);
      if (registryErrors.isEmpty()) {
         return Optional.of(this.registry);
      } else {
         loadingErrors.putAll(registryErrors);
         return Optional.empty();
      }
   }

   protected static record PendingRegistration(ResourceKey key, Either value, RegistrationInfo registrationInfo) {
      public static Either loadFromResource(final Decoder elementDecoder, final RegistryOps ops, final ResourceKey elementKey, final Resource thunk) {
         try {
            Reader reader = thunk.openAsReader();

            Either var6;
            try {
               JsonElement json = StrictJsonParser.parse(reader);
               var6 = Either.left(elementDecoder.parse(ops, json).getOrThrow());
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return var6;
         } catch (Exception var9) {
            return Either.right(new IllegalStateException(String.format(Locale.ROOT, "Failed to parse %s from pack %s", elementKey.identifier(), thunk.sourcePackId()), var9));
         }
      }

      public static Either findAndLoadFromResource(final Decoder elementDecoder, final RegistryOps ops, final ResourceKey elementKey, final FileToIdConverter converter, final ResourceProvider resourceProvider) {
         Identifier resourceId = converter.idToFile(elementKey.identifier());
         return (Either)resourceProvider.getResource(resourceId).map((resource) -> loadFromResource(elementDecoder, ops, elementKey, resource)).orElseGet(() -> Either.right(new IllegalStateException(String.format(Locale.ROOT, "Failed to find resource %s for element %s", resourceId, elementKey.identifier()))));
      }

      public static Either loadFromNetwork(final Decoder elementDecoder, final RegistryOps ops, final ResourceKey elementKey, final Tag contents) {
         try {
            DataResult parseResult = elementDecoder.parse(ops, contents);
            return Either.left(parseResult.getOrThrow());
         } catch (Exception var5) {
            return Either.right(new IllegalStateException(String.format(Locale.ROOT, "Failed to parse value %s for key %s from server", contents, elementKey.identifier()), var5));
         }
      }
   }
}
