package net.minecraft.client.multiplayer;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public class CacheSlot {
   private final Function operation;
   private CacheSlot.@Nullable Cleaner context;
   private @Nullable Object value;

   public CacheSlot(final Function operation) {
      this.operation = operation;
   }

   public Object compute(final CacheSlot.Cleaner context) {
      if (context == this.context && this.value != null) {
         return this.value;
      } else {
         Object newValue = (D)this.operation.apply(context);
         this.value = newValue;
         this.context = context;
         context.registerForCleaning(this);
         return newValue;
      }
   }

   public void clear() {
      this.value = null;
      this.context = null;
   }

   @FunctionalInterface
   public interface Cleaner {
      void registerForCleaning(CacheSlot slot);
   }
}
