package net.minecraft.server.jsonrpc.internalapi;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.notifications.NotificationManager;

public class MinecraftExecutorServiceImpl implements MinecraftExecutorService {
   private final NotificationManager notificationManager;

   public MinecraftExecutorServiceImpl(final NotificationManager notificationManager) {
      this.notificationManager = notificationManager;
   }

   private DedicatedServer server() {
      return (DedicatedServer)Objects.requireNonNull(this.notificationManager.server());
   }

   public CompletableFuture submit(final Supplier supplier) {
      return this.server().submit(supplier);
   }

   public CompletableFuture submit(final Runnable runnable) {
      return this.server().submit(runnable);
   }
}
