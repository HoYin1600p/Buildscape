package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DynamicOps ops;
   private final Codec codec;
   private final FileToIdConverter lister;

   protected SimpleJsonResourceReloadListener(final Codec codec, final FileToIdConverter lister) {
      this(JsonOps.INSTANCE, codec, lister);
   }

   private SimpleJsonResourceReloadListener(final DynamicOps ops, final Codec codec, final FileToIdConverter lister) {
      this.ops = ops;
      this.codec = codec;
      this.lister = lister;
   }

   protected Map prepare(final ResourceManager manager, final ProfilerFiller profiler) {
      Map result = new HashMap();

      for(Map.Entry entry : this.lister.listMatchingResources(manager).entrySet()) {
         Identifier location = (Identifier)entry.getKey();
         Identifier id = this.lister.fileToId(location);

         try {
            Reader reader = ((Resource)entry.getValue()).openAsReader();

            try {
               this.codec.parse(this.ops, StrictJsonParser.parse(reader)).ifSuccess((parsed) -> {
                  if (result.putIfAbsent(id, parsed) != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(id));
                  }
               }).ifError((error) -> LOGGER.error("Couldn't parse data file '{}' from '{}': {}", new Object[]{id, location, error}));
            } catch (Throwable var12) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var13) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", new Object[]{id, location, var13});
         }
      }

      return result;
   }
}
