package net.minecraft.data.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

public class RegistriesDatapackGenerator implements DataProvider {
   private final PackOutput output;
   private final String name;
   private final Collection registryData;
   private final CompletableFuture registries;

   public RegistriesDatapackGenerator(final PackOutput output, final String name, final Collection registryData, final CompletableFuture registryContents) {
      this.name = name;
      this.registryData = registryData;
      this.registries = registryContents;
      this.output = output;
   }

   public static RegistriesDatapackGenerator forWorldLayer(final PackOutput output, final CompletableFuture registryContents) {
      return new RegistriesDatapackGenerator(output, "world", RegistryDataLoader.WORLD_REGISTRIES, registryContents);
   }

   public static DataProvider forReloadableLayer(final PackOutput output, final CompletableFuture registryContents) {
      return new RegistriesDatapackGenerator(output, "reloadable", RegistryDataLoader.RELOADABLE_REGISTRIES, registryContents);
   }

   public CompletableFuture run(final CachedOutput cache) {
      return this.registries.thenCompose((access) -> {
         DynamicOps registryOps = access.createSerializationContext(JsonOps.INSTANCE);
         return CompletableFuture.allOf((CompletableFuture[])this.registryData.stream().flatMap((v) -> this.dumpRegistryCap(cache, access, registryOps, v).stream()).toArray((x$0) -> new CompletableFuture[x$0]));
      });
   }

   private Optional dumpRegistryCap(final CachedOutput cache, final HolderLookup.Provider registries, final DynamicOps writeOps, final RegistryDataLoader.RegistryData v) {
      ResourceKey registryKey = v.key();
      return registries.lookup(registryKey).map((registry) -> {
         PackOutput.PathProvider pathProvider = this.output.createRegistryElementsPathProvider(registryKey);
         return CompletableFuture.allOf((CompletableFuture[])registry.listElements().map((e) -> dumpValue(pathProvider.json(e.key().identifier()), cache, writeOps, v.elementCodec(), e.value())).toArray((x$0) -> new CompletableFuture[x$0]));
      });
   }

   private static CompletableFuture dumpValue(final Path path, final CachedOutput cache, final DynamicOps ops, final Encoder codec, final Object value) {
      return (CompletableFuture)codec.encodeStart(ops, value).mapOrElse((result) -> DataProvider.saveStable(cache, result, path), (error) -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + String.valueOf(path) + "': " + error.message())));
   }

   public final String getName() {
      return "Registries for " + this.name;
   }
}
