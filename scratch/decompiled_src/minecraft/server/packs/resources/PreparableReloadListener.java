package net.minecraft.server.packs.resources;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@FunctionalInterface
public interface PreparableReloadListener {
   CompletableFuture reload(PreparableReloadListener.SharedState currentReload, Executor taskExecutor, PreparableReloadListener.PreparationBarrier preparationBarrier, Executor reloadExecutor);

   default void prepareSharedState(final PreparableReloadListener.SharedState currentReload) {
   }

   default String getName() {
      return this.getClass().getSimpleName();
   }

   @FunctionalInterface
   public interface PreparationBarrier {
      CompletableFuture wait(Object t);
   }

   public static final class SharedState {
      private final ResourceManager manager;
      private final Map state = new IdentityHashMap();

      public SharedState(final ResourceManager manager) {
         this.manager = manager;
      }

      public ResourceManager resourceManager() {
         return this.manager;
      }

      public void set(final PreparableReloadListener.StateKey key, final Object value) {
         this.state.put(key, value);
      }

      public Object get(final PreparableReloadListener.StateKey key) {
         return Objects.requireNonNull(this.state.get(key));
      }
   }

   public static final class StateKey {
   }
}
