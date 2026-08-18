package net.minecraft.server.level;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface ChunkResult {
   static ChunkResult of(final Object value) {
      return new ChunkResult.Success(value);
   }

   static ChunkResult error(final String error) {
      return error((Supplier)(() -> error));
   }

   static ChunkResult error(final Supplier errorSupplier) {
      return new ChunkResult.Fail(errorSupplier);
   }

   boolean isSuccess();

   @Nullable Object orElse(@Nullable Object orElse);

   static @Nullable Object orElse(final ChunkResult chunkResult, final @Nullable Object orElse) {
      Object result = (R)chunkResult.orElse((Object)null);
      return result != null ? result : orElse;
   }

   @Nullable String getError();

   ChunkResult ifSuccess(Consumer consumer);

   ChunkResult map(Function map);

   Object orElseThrow(Supplier exceptionSupplier) throws Throwable;

   public static record Fail(Supplier error) implements ChunkResult {
      public boolean isSuccess() {
         return false;
      }

      public @Nullable Object orElse(final @Nullable Object orElse) {
         return orElse;
      }

      public String getError() {
         return (String)this.error.get();
      }

      public ChunkResult ifSuccess(final Consumer consumer) {
         return this;
      }

      public ChunkResult map(final Function map) {
         return new ChunkResult.Fail(this.error);
      }

      public Object orElseThrow(final Supplier exceptionSupplier) throws Throwable {
         throw (Throwable)exceptionSupplier.get();
      }
   }

   public static record Success(Object value) implements ChunkResult {
      public boolean isSuccess() {
         return true;
      }

      public Object orElse(final @Nullable Object orElse) {
         return this.value;
      }

      public @Nullable String getError() {
         return null;
      }

      public ChunkResult ifSuccess(final Consumer consumer) {
         consumer.accept(this.value);
         return this;
      }

      public ChunkResult map(final Function map) {
         return new ChunkResult.Success(map.apply(this.value));
      }

      public Object orElseThrow(final Supplier exceptionSupplier) throws Throwable {
         return this.value;
      }
   }
}
