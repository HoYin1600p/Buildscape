package net.minecraft.util.context;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public final class ContextMap {
   public static final ContextMap EMPTY = new ContextMap(Map.of());
   private final Map params;

   private ContextMap(final Map params) {
      this.params = params;
   }

   public static ContextMap.Builder builder() {
      return new ContextMap.Builder();
   }

   public boolean has(final ContextKey key) {
      return this.params.containsKey(key);
   }

   public Object getOrThrow(final ContextKey key) {
      Object value = (T)this.get(key);
      if (value == null) {
         throw new NoSuchElementException(key.name().toString());
      } else {
         return value;
      }
   }

   public @Nullable Object get(final ContextKey key) {
      return this.params.get(key);
   }

   @Contract("_,!null->!null; _,_->_")
   public @Nullable Object getOrDefault(final ContextKey param, final @Nullable Object _default) {
      return this.params.getOrDefault(param, _default);
   }

   public static class Builder {
      private final Map params = new Reference2ObjectOpenHashMap();

      private Builder() {
      }

      public ContextMap.Builder set(final ContextKey param, final @Nullable Object value) {
         if (value == null) {
            this.params.remove(param);
         } else {
            this.params.put(param, value);
         }

         return this;
      }

      public @Nullable Object get(final ContextKey param) {
         return this.params.get(param);
      }

      public ContextMap build() {
         return new ContextMap(new Reference2ObjectOpenHashMap(this.params));
      }

      public ContextMap buildAndValidate(final ContextKeySet paramSet) {
         Set notAllowed = Sets.difference(this.params.keySet(), paramSet.allowed());
         if (!notAllowed.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + String.valueOf(notAllowed));
         } else {
            Set missingRequired = Sets.difference(paramSet.required(), this.params.keySet());
            if (!missingRequired.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + String.valueOf(missingRequired));
            } else {
               return this.build();
            }
         }
      }
   }
}
