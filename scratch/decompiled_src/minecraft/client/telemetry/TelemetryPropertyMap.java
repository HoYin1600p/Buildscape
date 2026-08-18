package net.minecraft.client.telemetry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public class TelemetryPropertyMap {
   private final Map entries;

   private TelemetryPropertyMap(final Map entries) {
      this.entries = entries;
   }

   public static TelemetryPropertyMap.Builder builder() {
      return new TelemetryPropertyMap.Builder();
   }

   public static MapCodec createCodec(final List properties) {
      return new MapCodec() {
         public RecordBuilder encode(final TelemetryPropertyMap input, final DynamicOps ops, final RecordBuilder prefix) {
            RecordBuilder result = prefix;

            for(TelemetryProperty property : properties) {
               result = this.encodeProperty(input, result, property);
            }

            return result;
         }

         private RecordBuilder encodeProperty(final TelemetryPropertyMap input, final RecordBuilder result, final TelemetryProperty property) {
            Object value = (V)input.get(property);
            return value != null ? result.add(property.id(), value, property.codec()) : result;
         }

         public DataResult decode(final DynamicOps ops, final MapLike input) {
            DataResult result = DataResult.success(new TelemetryPropertyMap.Builder());

            for(TelemetryProperty property : properties) {
               result = this.decodeProperty(result, ops, input, property);
            }

            return result.map(TelemetryPropertyMap.Builder::build);
         }

         private DataResult decodeProperty(final DataResult result, final DynamicOps ops, final MapLike input, final TelemetryProperty property) {
            Object value = (T)input.get(property.id());
            if (value != null) {
               DataResult parse = property.codec().parse(ops, value);
               return result.apply2stable((b, v) -> b.put(property, v), parse);
            } else {
               return result;
            }
         }

         public Stream keys(final DynamicOps ops) {
            return properties.stream().map(TelemetryProperty::id).map(ops::createString);
         }
      };
   }

   public @Nullable Object get(final TelemetryProperty property) {
      return this.entries.get(property);
   }

   public String toString() {
      return this.entries.toString();
   }

   public Set propertySet() {
      return this.entries.keySet();
   }

   public static class Builder {
      private final Map entries = new Reference2ObjectOpenHashMap();

      private Builder() {
      }

      public TelemetryPropertyMap.Builder put(final TelemetryProperty property, final Object value) {
         this.entries.put(property, value);
         return this;
      }

      public TelemetryPropertyMap.Builder putIfNotNull(final TelemetryProperty property, final @Nullable Object value) {
         if (value != null) {
            this.entries.put(property, value);
         }

         return this;
      }

      public TelemetryPropertyMap.Builder putAll(final TelemetryPropertyMap properties) {
         this.entries.putAll(properties.entries);
         return this;
      }

      public TelemetryPropertyMap build() {
         return new TelemetryPropertyMap(this.entries);
      }
   }
}
