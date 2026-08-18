package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      public Optional getSection(final MetadataSectionType serializer) {
         return Optional.empty();
      }
   };
   IoSupplier EMPTY_SUPPLIER = () -> EMPTY;

   static ResourceMetadata fromJsonStream(final InputStream inputStream) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject metadata = GsonHelper.parse(reader);
         var3 = new ResourceMetadata() {
            public Optional getSection(final MetadataSectionType serializer) {
               String name = serializer.name();
               JsonElement rawSection = metadata.get(name);
               if (rawSection != null) {
                  Object section = (T)serializer.codec().parse(JsonOps.INSTANCE, rawSection).getOrThrow(JsonParseException::new);
                  return Optional.of(section);
               } else {
                  return Optional.empty();
               }
            }
         };
      } catch (Throwable var5) {
         try {
            reader.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      reader.close();
      return var3;
   }

   Optional getSection(MetadataSectionType serializer);

   default Optional getTypedSection(final MetadataSectionType type) {
      return this.getSection(type).map(type::withValue);
   }

   static ResourceMetadata of(final MetadataSectionType k, final Object v) {
      return new ResourceMetadata.MapBased(Map.of(k, v));
   }

   static ResourceMetadata of(final MetadataSectionType k1, final Object v1, final MetadataSectionType k2, final Object v2) {
      return new ResourceMetadata.MapBased(Map.of(k1, v1, k2, v2));
   }

   default List getTypedSections(final Collection types) {
      return (List)types.stream().map(this::getTypedSection).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static class MapBased implements ResourceMetadata {
      private final Map values;

      private MapBased(final Map values) {
         this.values = values;
      }

      public Optional getSection(final MetadataSectionType serializer) {
         return Optional.ofNullable(this.values.get(serializer));
      }
   }
}
