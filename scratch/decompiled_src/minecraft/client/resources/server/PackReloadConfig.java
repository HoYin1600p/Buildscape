package net.minecraft.client.resources.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface PackReloadConfig {
   void scheduleReload(PackReloadConfig.Callbacks callbacks);

   public interface Callbacks {
      void onSuccess();

      void onFailure(boolean isRecovery);

      List packsToLoad();
   }

   public static record IdAndPath(UUID id, Path path) {
   }
}
