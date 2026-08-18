package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class SingleKeyCache {
   private final Function computeValue;
   private @Nullable Object cacheKey = null;
   private @Nullable Object cachedValue;

   public SingleKeyCache(final Function computeValue) {
      this.computeValue = computeValue;
   }

   public Object getValue(final Object cacheKey) {
      if (this.cachedValue == null || !Objects.equals(this.cacheKey, cacheKey)) {
         this.cachedValue = this.computeValue.apply(cacheKey);
         this.cacheKey = cacheKey;
      }

      return this.cachedValue;
   }
}
