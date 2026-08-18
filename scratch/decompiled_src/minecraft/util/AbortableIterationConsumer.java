package net.minecraft.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbortableIterationConsumer {
   Continuation accept(Object entry);

   static AbortableIterationConsumer forConsumer(final Consumer consumer) {
      return (e) -> {
         consumer.accept(e);
         return Continuation.CONTINUE;
      };
   }
}
