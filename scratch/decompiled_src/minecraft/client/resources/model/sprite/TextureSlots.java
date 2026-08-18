package net.minecraft.client.resources.model.sprite;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class TextureSlots {
   public static final TextureSlots EMPTY = new TextureSlots(Map.of());
   private static final char REFERENCE_CHAR = '#';
   private final Map resolvedValues;

   private TextureSlots(final Map resolvedValues) {
      this.resolvedValues = resolvedValues;
   }

   public @Nullable Material getMaterial(String reference) {
      if (isTextureReference(reference)) {
         reference = reference.substring(1);
      }

      return (Material)this.resolvedValues.get(reference);
   }

   private static boolean isTextureReference(final String texture) {
      return texture.charAt(0) == '#';
   }

   public static TextureSlots.Data parseTextureMap(final JsonObject texturesObject) {
      TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder();

      for(Map.Entry entry : texturesObject.entrySet()) {
         parseEntry((String)entry.getKey(), (JsonElement)entry.getValue(), builder);
      }

      return builder.build();
   }

   private static void parseEntry(final String slot, final JsonElement value, final TextureSlots.Data.Builder output) {
      if (GsonHelper.isStringValue(value) && isTextureReference(value.getAsString())) {
         output.addReference(slot, value.getAsString().substring(1));
      } else {
         output.addTexture(slot, (Material)Material.CODEC.parse(JsonOps.INSTANCE, value).getOrThrow(JsonParseException::new));
      }

   }

   public static record Data(Map values) {
      public static final TextureSlots.Data EMPTY = new TextureSlots.Data(Map.of());

      public static class Builder {
         private final Map textureMap = new HashMap();

         public TextureSlots.Data.Builder addReference(final String slot, final String reference) {
            this.textureMap.put(slot, new TextureSlots.Reference(reference));
            return this;
         }

         public TextureSlots.Data.Builder addTexture(final String slot, final Material material) {
            this.textureMap.put(slot, new TextureSlots.Value(material));
            return this;
         }

         public TextureSlots.Data build() {
            return this.textureMap.isEmpty() ? TextureSlots.Data.EMPTY : new TextureSlots.Data(Map.copyOf(this.textureMap));
         }
      }
   }

   private static record Reference(String target) implements TextureSlots.SlotContents {
   }

   public static class Resolver {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final List entries = new ArrayList();

      public TextureSlots.Resolver addLast(final TextureSlots.Data data) {
         this.entries.addLast(data);
         return this;
      }

      public TextureSlots.Resolver addFirst(final TextureSlots.Data data) {
         this.entries.addFirst(data);
         return this;
      }

      public TextureSlots resolve(final ModelDebugName debugNameProvider) {
         if (this.entries.isEmpty()) {
            return TextureSlots.EMPTY;
         } else {
            Object2ObjectMap resolved = new Object2ObjectArrayMap();
            Object2ObjectMap unresolved = new Object2ObjectArrayMap();

            for(TextureSlots.Data data : Lists.reverse(this.entries)) {
               data.values.forEach((slot, contents) -> {
                  Objects.requireNonNull(contents);
                  int index$1 = 0;
                  switch (contents.typeSwitch<invokedynamic>(contents, index$1)) {
                     case 0:
                        TextureSlots.Value value = (TextureSlots.Value)contents;
                        unresolved.remove(slot);
                        resolved.put(slot, value.material());
                        break;
                     case 1:
                        TextureSlots.Reference reference = (TextureSlots.Reference)contents;
                        resolved.remove(slot);
                        unresolved.put(slot, reference);
                        break;
                     default:
                        throw new MatchException((String)null, (Throwable)null);
                  }

               });
            }

            if (unresolved.isEmpty()) {
               return new TextureSlots(resolved);
            } else {
               boolean hasChanges = true;

               while(hasChanges) {
                  hasChanges = false;
                  ObjectIterator iterator = Object2ObjectMaps.fastIterator(unresolved);

                  while(iterator.hasNext()) {
                     Object2ObjectMap.Entry entry = (Object2ObjectMap.Entry)iterator.next();
                     Material maybeResolved = (Material)resolved.get(((TextureSlots.Reference)entry.getValue()).target);
                     if (maybeResolved != null) {
                        resolved.put((String)entry.getKey(), maybeResolved);
                        iterator.remove();
                        hasChanges = true;
                     }
                  }
               }

               if (!unresolved.isEmpty()) {
                  LOGGER.warn("Unresolved texture references in {}:\n{}", debugNameProvider.debugName(), unresolved.entrySet().stream().map((e) -> "\t#" + (String)e.getKey() + "-> #" + ((TextureSlots.Reference)e.getValue()).target + "\n").collect(Collectors.joining()));
               }

               return new TextureSlots(resolved);
            }
         }
      }
   }

   public sealed interface SlotContents permits TextureSlots.Value, TextureSlots.Reference {
   }

   private static record Value(Material material) implements TextureSlots.SlotContents {
   }
}
