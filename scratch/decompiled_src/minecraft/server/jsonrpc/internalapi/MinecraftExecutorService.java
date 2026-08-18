package net.minecraft.server.jsonrpc.internalapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface MinecraftExecutorService {
   CompletableFuture submit(final Supplier supplier);

   CompletableFuture submit(final Runnable runnable);
}
