package net.minecraft.world.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.UUIDLookup;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public final class EntityReference {
   private static final Codec CODEC = UUIDUtil.CODEC.xmap(EntityReference::new, EntityReference::getUUID);
   private static final StreamCodec STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(EntityReference::new, EntityReference::getUUID);
   private Either entity;

   public static Codec codec() {
      return CODEC;
   }

   public static StreamCodec streamCodec() {
      return STREAM_CODEC;
   }

   private EntityReference(final UniquelyIdentifyable entity) {
      this.entity = Either.right(entity);
   }

   private EntityReference(final UUID uuid) {
      this.entity = Either.left(uuid);
   }

   @Contract("!null -> !null; null -> null")
   public static @Nullable EntityReference of(final @Nullable UniquelyIdentifyable entity) {
      return entity != null ? new EntityReference(entity) : null;
   }

   public static EntityReference of(final UUID uuid) {
      return new EntityReference(uuid);
   }

   public UUID getUUID() {
      return (UUID)this.entity.map((uuid) -> uuid, UniquelyIdentifyable::getUUID);
   }

   public @Nullable UniquelyIdentifyable getEntity(final UUIDLookup lookup, final Class clazz) {
      Optional stored = this.entity.right();
      if (stored.isPresent()) {
         UniquelyIdentifyable storedEntity = (StoredEntityType)((UniquelyIdentifyable)stored.get());
         if (!storedEntity.isRemoved()) {
            return storedEntity;
         }

         this.entity = Either.left(storedEntity.getUUID());
      }

      Optional uuid = this.entity.left();
      if (uuid.isPresent()) {
         UniquelyIdentifyable resolved = (StoredEntityType)this.resolve(lookup.lookup((UUID)uuid.get()), clazz);
         if (resolved != null && !resolved.isRemoved()) {
            this.entity = Either.right(resolved);
            return resolved;
         }
      }

      return null;
   }

   public @Nullable UniquelyIdentifyable getEntity(final Level level, final Class clazz) {
      return Player.class.isAssignableFrom(clazz) ? this.getEntity(level::getPlayerInAnyDimension, clazz) : this.getEntity(level::getEntityInAnyDimension, clazz);
   }

   private @Nullable UniquelyIdentifyable resolve(final @Nullable UniquelyIdentifyable entity, final Class clazz) {
      return entity != null && clazz.isAssignableFrom(entity.getClass()) ? (UniquelyIdentifyable)clazz.cast(entity) : null;
   }

   public boolean matches(final UniquelyIdentifyable entity) {
      return this.getUUID().equals(entity.getUUID());
   }

   public void store(final ValueOutput output, final String key) {
      output.store(key, UUIDUtil.CODEC, this.getUUID());
   }

   public static void store(final @Nullable EntityReference reference, final ValueOutput output, final String key) {
      if (reference != null) {
         reference.store(output, key);
      }

   }

   public static @Nullable UniquelyIdentifyable get(final @Nullable EntityReference reference, final Level level, final Class clazz) {
      return reference != null ? reference.getEntity(level, clazz) : null;
   }

   public static @Nullable Entity getEntity(final @Nullable EntityReference reference, final Level level) {
      return (Entity)get(reference, level, Entity.class);
   }

   public static @Nullable LivingEntity getLivingEntity(final @Nullable EntityReference reference, final Level level) {
      return (LivingEntity)get(reference, level, LivingEntity.class);
   }

   public static @Nullable Player getPlayer(final @Nullable EntityReference reference, final Level level) {
      return (Player)get(reference, level, Player.class);
   }

   public static @Nullable EntityReference read(final ValueInput input, final String key) {
      return (EntityReference)input.read(key, codec()).orElse((Object)null);
   }

   public static @Nullable EntityReference readWithOldOwnerConversion(final ValueInput input, final String key, final Level level) {
      Optional uuid = input.read(key, UUIDUtil.CODEC);
      return uuid.isPresent() ? of((UUID)uuid.get()) : (EntityReference)input.getString(key).map((oldName) -> OldUsersConverter.convertMobOwnerIfNecessary(level.getServer(), oldName)).map(EntityReference::new).orElse((Object)null);
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         if (obj instanceof EntityReference) {
            EntityReference reference = (EntityReference)obj;
            if (this.getUUID().equals(reference.getUUID())) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return this.getUUID().hashCode();
   }
}
