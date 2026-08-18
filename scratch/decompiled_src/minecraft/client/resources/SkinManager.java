package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Services;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SkinManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Services services;
   private final SkinTextureDownloader skinTextureDownloader;
   private final LoadingCache skinCache;
   private final SkinManager.TextureCache skinTextures;
   private final SkinManager.TextureCache capeTextures;
   private final SkinManager.TextureCache elytraTextures;

   public SkinManager(final Path skinsDirectory, final Services services, final SkinTextureDownloader skinTextureDownloader, final Executor mainThreadExecutor) {
      this.services = services;
      this.skinTextureDownloader = skinTextureDownloader;
      this.skinTextures = new SkinManager.TextureCache(skinsDirectory, Type.SKIN);
      this.capeTextures = new SkinManager.TextureCache(skinsDirectory, Type.CAPE);
      this.elytraTextures = new SkinManager.TextureCache(skinsDirectory, Type.ELYTRA);
      this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build(new CacheLoader() {
         {
            Objects.requireNonNull(SkinManager.this);
         }

         public CompletableFuture load(final SkinManager.CacheKey key) {
            return CompletableFuture.supplyAsync(() -> {
               Property packedTextures = key.packedTextures();
               if (packedTextures == null) {
                  return MinecraftProfileTextures.EMPTY;
               } else {
                  MinecraftProfileTextures textures = services.sessionService().unpackTextures(packedTextures);
                  if (textures.signatureState() == SignatureState.INVALID) {
                     SkinManager.LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", key.profileId());
                  }

                  return textures;
               }
            }, Util.backgroundExecutor().forName("unpackSkinTextures")).thenComposeAsync((textures) -> SkinManager.this.registerTextures(key.profileId(), textures), mainThreadExecutor).handle((playerSkin, throwable) -> {
               if (throwable != null) {
                  SkinManager.LOGGER.warn("Failed to load texture for profile {}", key.profileId, throwable);
               }

               return Optional.ofNullable(playerSkin);
            });
         }
      });
   }

   public Supplier createLookup(final GameProfile profile, final boolean requireSecure) {
      CompletableFuture future = this.get(profile);
      PlayerSkin defaultSkin = DefaultPlayerSkin.get(profile);
      if (SharedConstants.DEBUG_DEFAULT_SKIN_OVERRIDE) {
         return () -> defaultSkin;
      } else {
         Optional currentValue = (Optional)future.getNow((Object)null);
         if (currentValue != null) {
            PlayerSkin playerSkin = (PlayerSkin)currentValue.filter((skin) -> !requireSecure || skin.secure()).orElse(defaultSkin);
            return () -> playerSkin;
         } else {
            return () -> (PlayerSkin)((Optional)future.getNow(Optional.empty())).filter((skin) -> !requireSecure || skin.secure()).orElse(defaultSkin);
         }
      }
   }

   public CompletableFuture get(final GameProfile profile) {
      if (SharedConstants.DEBUG_DEFAULT_SKIN_OVERRIDE) {
         PlayerSkin defaultSkin = DefaultPlayerSkin.get(profile);
         return CompletableFuture.completedFuture(Optional.of(defaultSkin));
      } else {
         Property packedTextures = this.services.sessionService().getPackedTextures(profile);
         return (CompletableFuture)this.skinCache.getUnchecked(new SkinManager.CacheKey(profile.id(), packedTextures));
      }
   }

   private CompletableFuture registerTextures(final UUID profileId, final MinecraftProfileTextures textures) {
      MinecraftProfileTexture skinInfo = textures.skin();
      CompletableFuture skinTexture;
      PlayerModelType model;
      if (skinInfo != null) {
         skinTexture = this.skinTextures.getOrLoad(skinInfo);
         model = PlayerModelType.byLegacyServicesName(skinInfo.getMetadata("model"));
      } else {
         PlayerSkin defaultSkin = DefaultPlayerSkin.get(profileId);
         skinTexture = CompletableFuture.completedFuture(defaultSkin.body());
         model = defaultSkin.model();
      }

      MinecraftProfileTexture capeInfo = textures.cape();
      CompletableFuture capeTexture = capeInfo != null ? this.capeTextures.getOrLoad(capeInfo) : CompletableFuture.completedFuture((Object)null);
      MinecraftProfileTexture elytraInfo = textures.elytra();
      CompletableFuture elytraTexture = elytraInfo != null ? this.elytraTextures.getOrLoad(elytraInfo) : CompletableFuture.completedFuture((Object)null);
      return CompletableFuture.allOf(skinTexture, capeTexture, elytraTexture).thenApply((unused) -> new PlayerSkin((ClientAsset.Texture)skinTexture.join(), (ClientAsset.Texture)capeTexture.join(), (ClientAsset.Texture)elytraTexture.join(), model, textures.signatureState() == SignatureState.SIGNED));
   }

   private static record CacheKey(UUID profileId, @Nullable Property packedTextures) {
   }

   private class TextureCache {
      private final Path root;
      private final MinecraftProfileTexture.Type type;
      private final Map textures;

      private TextureCache(final Path root, final MinecraftProfileTexture.Type type) {
         Objects.requireNonNull(SkinManager.this);
         super();
         this.textures = new Object2ObjectOpenHashMap();
         this.root = root;
         this.type = type;
      }

      public CompletableFuture getOrLoad(final MinecraftProfileTexture texture) {
         String hash = texture.getHash();
         CompletableFuture future = (CompletableFuture)this.textures.get(hash);
         if (future == null) {
            future = this.registerTexture(texture);
            this.textures.put(hash, future);
         }

         return future;
      }

      private CompletableFuture registerTexture(final MinecraftProfileTexture textureInfo) {
         String hash = Hashing.sha1().hashUnencodedChars(textureInfo.getHash()).toString();
         Identifier textureId = this.getTextureLocation(hash);
         Path file = this.root.resolve(hash.length() > 2 ? hash.substring(0, 2) : "xx").resolve(hash);
         return SkinManager.this.skinTextureDownloader.downloadAndRegisterSkin(textureId, file, textureInfo.getUrl(), this.type == Type.SKIN);
      }

      private Identifier getTextureLocation(final String textureHash) {
         String var10000;
         switch (this.type) {
            case SKIN:
               var10000 = "skins";
               break;
            case CAPE:
               var10000 = "capes";
               break;
            case ELYTRA:
               var10000 = "elytra";
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         String root = var10000;
         return Identifier.withDefaultNamespace(root + "/" + textureHash);
      }
   }
}
