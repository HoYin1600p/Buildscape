package net.minecraft.util;

import org.jspecify.annotations.Nullable;

public class ExceptionCollector {
   private @Nullable Throwable result;

   public void add(final Throwable throwable) {
      if (this.result == null) {
         this.result = throwable;
      } else {
         this.result.addSuppressed(throwable);
      }

   }

   public void throwIfPresent() throws Throwable {
      if (this.result != null) {
         throw this.result;
      }
   }
}
