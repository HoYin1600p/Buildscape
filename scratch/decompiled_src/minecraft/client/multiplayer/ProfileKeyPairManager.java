package net.minecraft.client.multiplayer;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.User;

public interface ProfileKeyPairManager {
   ProfileKeyPairManager EMPTY_KEY_MANAGER = new ProfileKeyPairManager() {
      public CompletableFuture prepareKeyPair() {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      public boolean shouldRefreshKeyPair() {
         return false;
      }
   };

   static ProfileKeyPairManager create(final UserApiService userApiService, final User user, final Path gameDirectory) {
      return new AccountProfileKeyPairManager(userApiService, user.getProfileId(), gameDirectory);
   }

   CompletableFuture prepareKeyPair();

   boolean shouldRefreshKeyPair();
}
