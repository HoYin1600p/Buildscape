package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener implements PreparableReloadListener {
   public final CompletableFuture reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      return CompletableFuture.supplyAsync(() -> this.prepare(manager, Profiler.get()), taskExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync((preparations) -> this.apply(preparations, manager, Profiler.get()), reloadExecutor);
   }

   protected abstract Object prepare(final ResourceManager manager, final ProfilerFiller profiler);

   protected abstract void apply(final Object preparations, final ResourceManager manager, final ProfilerFiller profiler);
}
