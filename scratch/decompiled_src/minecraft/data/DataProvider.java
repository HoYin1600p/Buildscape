package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public interface DataProvider {
   ToIntFunction FIXED_ORDER_FIELDS = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), (m) -> {
      m.put("type", 0);
      m.put("parent", 1);
      m.defaultReturnValue(2);
   });
   Comparator KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing((e) -> e);
   Logger LOGGER = LogUtils.getLogger();

   CompletableFuture run(CachedOutput cache);

   String getName();

   static CompletableFuture saveAll(final CachedOutput cache, final Codec codec, final PackOutput.PathProvider pathProvider, final Map entries) {
      return saveAll(cache, codec, pathProvider::json, entries);
   }

   static CompletableFuture saveAll(final CachedOutput cache, final Codec codec, final Function pathGetter, final Map contents) {
      return saveAll(cache, (Function)((e) -> (JsonElement)codec.encodeStart(JsonOps.INSTANCE, e).getOrThrow()), pathGetter, contents);
   }

   static CompletableFuture saveAll(final CachedOutput cache, final Function serializer, final Function pathGetter, final Map contents) {
      return CompletableFuture.allOf((CompletableFuture[])contents.entrySet().stream().map((entry) -> {
         Path path = (Path)pathGetter.apply(entry.getKey());
         JsonElement json = (JsonElement)serializer.apply(entry.getValue());
         return saveStable(cache, json, path);
      }).toArray((x$0) -> new CompletableFuture[x$0]));
   }

   static CompletableFuture saveStable(final CachedOutput cache, final HolderLookup.Provider registries, final Codec codec, final Object value, final Path path) {
      RegistryOps ops = registries.createSerializationContext(JsonOps.INSTANCE);
      return saveStable(cache, ops, codec, value, path);
   }

   static CompletableFuture saveStable(final CachedOutput cache, final Codec codec, final Object value, final Path path) {
      return saveStable(cache, JsonOps.INSTANCE, codec, value, path);
   }

   private static CompletableFuture saveStable(final CachedOutput cache, final DynamicOps ops, final Codec codec, final Object value, final Path path) {
      JsonElement json = (JsonElement)codec.encodeStart(ops, value).getOrThrow();
      return saveStable(cache, json, path);
   }

   static CompletableFuture saveStable(final CachedOutput cache, final JsonElement root, final Path path) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            HashingOutputStream hashedBytes = new HashingOutputStream(Hashing.sha1(), bytes);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashedBytes, StandardCharsets.UTF_8));

            try {
               jsonWriter.setSerializeNulls(false);
               jsonWriter.setIndent("  ");
               GsonHelper.writeValue(jsonWriter, root, KEY_COMPARATOR);
            } catch (Throwable var9) {
               try {
                  jsonWriter.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            jsonWriter.close();
            cache.writeIfNeeded(path, bytes.toByteArray(), hashedBytes.hash());
         } catch (IOException var10) {
            LOGGER.error("Failed to save file to {}", path, var10);
         }

      }, Util.backgroundExecutor().forName("saveStable"));
   }

   @FunctionalInterface
   public interface Factory {
      DataProvider create(PackOutput output);
   }
}
