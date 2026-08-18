package net.minecraft.server.players;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.minecraft.SessionService;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.datafixers.util.Either;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.StringUtil;

public interface ProfileResolver {
   Optional fetchByName(String name);

   Optional fetchById(UUID id);

   default Optional fetchByNameOrId(final Either nameOrId) {
      return (Optional)nameOrId.map(this::fetchByName, this::fetchById);
   }

   public static class Cached implements ProfileResolver {
      private final LoadingCache profileCacheByName;
      private final LoadingCache profileCacheById;

      public Cached(final SessionService sessionService, final UserNameToIdResolver nameToIdCache) {
         this.profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public Optional load(final UUID profileId) {
               ProfileResult result = sessionService.fetchProfile(profileId, true);
               return Optional.ofNullable(result).map(ProfileResult::profile);
            }
         });
         this.profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader() {
            {
               Objects.requireNonNull(Cached.this);
            }

            public Optional load(final String name) {
               return nameToIdCache.get(name).flatMap((nameAndId) -> (Optional)Cached.this.profileCacheById.getUnchecked(nameAndId.id()));
            }
         });
      }

      public Optional fetchByName(final String name) {
         return StringUtil.isValidPlayerName(name) ? (Optional)this.profileCacheByName.getUnchecked(name) : Optional.empty();
      }

      public Optional fetchById(final UUID id) {
         return (Optional)this.profileCacheById.getUnchecked(id);
      }
   }
}
