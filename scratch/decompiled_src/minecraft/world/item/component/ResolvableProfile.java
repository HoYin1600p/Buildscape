package net.minecraft.world.item.component;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public abstract sealed class ResolvableProfile implements TooltipProvider {
   private static final Codec FULL_CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.mapEither(ExtraCodecs.STORED_GAME_PROFILE, ResolvableProfile.Partial.MAP_CODEC).forGetter(ResolvableProfile::unpack), PlayerSkin.Patch.MAP_CODEC.forGetter(ResolvableProfile::skinPatch)).apply(i, ResolvableProfile::create));
   public static final Codec CODEC = Codec.withAlternative(FULL_CODEC, ExtraCodecs.PLAYER_NAME, ResolvableProfile::createUnresolved);
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.either(ByteBufCodecs.GAME_PROFILE, ResolvableProfile.Partial.STREAM_CODEC), ResolvableProfile::unpack, PlayerSkin.Patch.STREAM_CODEC, ResolvableProfile::skinPatch, ResolvableProfile::create);
   protected final GameProfile partialProfile;
   protected final PlayerSkin.Patch skinPatch;

   private static ResolvableProfile create(final Either value, final PlayerSkin.Patch patch) {
      return (ResolvableProfile)value.map((full) -> new ResolvableProfile.Static(Either.left(full), patch), (partial) -> (ResolvableProfile)(partial.properties.isEmpty() && partial.id.isPresent() != partial.name.isPresent() ? (ResolvableProfile)partial.name.map((s) -> new ResolvableProfile.Dynamic(Either.left(s), patch)).orElseGet(() -> new ResolvableProfile.Dynamic(Either.right((UUID)partial.id.get()), patch)) : new ResolvableProfile.Static(Either.right(partial), patch)));
   }

   public static ResolvableProfile createResolved(final GameProfile gameProfile) {
      return new ResolvableProfile.Static(Either.left(gameProfile), PlayerSkin.Patch.EMPTY);
   }

   public static ResolvableProfile createUnresolved(final String name) {
      return new ResolvableProfile.Dynamic(Either.left(name), PlayerSkin.Patch.EMPTY);
   }

   public static ResolvableProfile createUnresolved(final UUID id) {
      return new ResolvableProfile.Dynamic(Either.right(id), PlayerSkin.Patch.EMPTY);
   }

   protected abstract Either unpack();

   protected ResolvableProfile(final GameProfile partialProfile, final PlayerSkin.Patch skinPatch) {
      this.partialProfile = partialProfile;
      this.skinPatch = skinPatch;
   }

   public abstract CompletableFuture resolveProfile(ProfileResolver profileResolver);

   public GameProfile partialProfile() {
      return this.partialProfile;
   }

   public PlayerSkin.Patch skinPatch() {
      return this.skinPatch;
   }

   private static GameProfile createPartialProfile(final Optional maybeName, final Optional maybeId, final PropertyMap properties) {
      String name = (String)maybeName.orElse("");
      UUID id = (UUID)maybeId.orElseGet(() -> (UUID)maybeName.map(UUIDUtil::createOfflinePlayerUUID).orElse(Util.NIL_UUID));
      return new GameProfile(id, name, properties);
   }

   public abstract Optional name();

   public static final class Dynamic extends ResolvableProfile {
      private static final Component DYNAMIC_TOOLTIP = Component.translatable("component.profile.dynamic").withStyle(ChatFormatting.GRAY);
      private final Either nameOrId;

      private Dynamic(final Either nameOrId, final PlayerSkin.Patch skinPatch) {
         super(ResolvableProfile.createPartialProfile(nameOrId.left(), nameOrId.right(), PropertyMap.EMPTY), skinPatch);
         this.nameOrId = nameOrId;
      }

      public Optional name() {
         return this.nameOrId.left();
      }

      public boolean equals(final Object o) {
         if (this != o) {
            if (o instanceof ResolvableProfile.Dynamic) {
               ResolvableProfile.Dynamic that = (ResolvableProfile.Dynamic)o;
               if (this.nameOrId.equals(that.nameOrId) && this.skinPatch.equals(that.skinPatch)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }

      public int hashCode() {
         int result = 31 + this.nameOrId.hashCode();
         return 31 * result + this.skinPatch.hashCode();
      }

      protected Either unpack() {
         return Either.right(new ResolvableProfile.Partial(this.nameOrId.left(), this.nameOrId.right(), PropertyMap.EMPTY));
      }

      public CompletableFuture resolveProfile(final ProfileResolver profileResolver) {
         return CompletableFuture.supplyAsync(() -> (GameProfile)profileResolver.fetchByNameOrId(this.nameOrId).orElse(this.partialProfile), Util.nonCriticalIoPool());
      }

      public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
         consumer.accept(DYNAMIC_TOOLTIP);
      }
   }

   protected static record Partial(Optional name, Optional id, PropertyMap properties) {
      public static final ResolvableProfile.Partial EMPTY = new ResolvableProfile.Partial(Optional.empty(), Optional.empty(), PropertyMap.EMPTY);
      private static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.PLAYER_NAME.optionalFieldOf("name").forGetter(ResolvableProfile.Partial::name), UUIDUtil.CODEC.optionalFieldOf("id").forGetter(ResolvableProfile.Partial::id), ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", PropertyMap.EMPTY).forGetter(ResolvableProfile.Partial::properties)).apply(i, ResolvableProfile.Partial::new));
      public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.PLAYER_NAME.apply(ByteBufCodecs::optional), ResolvableProfile.Partial::name, UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), ResolvableProfile.Partial::id, ByteBufCodecs.GAME_PROFILE_PROPERTIES, ResolvableProfile.Partial::properties, ResolvableProfile.Partial::new);

      private GameProfile createProfile() {
         return ResolvableProfile.createPartialProfile(this.name, this.id, this.properties);
      }
   }

   public static final class Static extends ResolvableProfile {
      public static final ResolvableProfile.Static EMPTY = new ResolvableProfile.Static(Either.right(ResolvableProfile.Partial.EMPTY), PlayerSkin.Patch.EMPTY);
      private final Either contents;

      private Static(final Either contents, final PlayerSkin.Patch skinPatch) {
         super((GameProfile)contents.map((gameProfile) -> gameProfile, ResolvableProfile.Partial::createProfile), skinPatch);
         this.contents = contents;
      }

      public CompletableFuture resolveProfile(final ProfileResolver profileResolver) {
         return CompletableFuture.completedFuture(this.partialProfile);
      }

      protected Either unpack() {
         return this.contents;
      }

      public Optional name() {
         return (Optional)this.contents.map((gameProfile) -> Optional.of(gameProfile.name()), (partial) -> partial.name);
      }

      public boolean equals(final Object o) {
         if (this != o) {
            if (o instanceof ResolvableProfile.Static) {
               ResolvableProfile.Static that = (ResolvableProfile.Static)o;
               if (this.contents.equals(that.contents) && this.skinPatch.equals(that.skinPatch)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }

      public int hashCode() {
         int result = 31 + this.contents.hashCode();
         return 31 * result + this.skinPatch.hashCode();
      }

      public void addToTooltip(final Item.TooltipContext context, final Consumer consumer, final TooltipFlag flag, final DataComponentGetter components) {
      }
   }
}
