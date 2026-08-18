package net.minecraft.world.level.gameevent;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EntityPositionSource implements PositionSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(UUIDUtil.CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), Codec.FLOAT.optionalFieldOf("y_offset", 0.0F).forGetter((o) -> o.yOffset)).apply(i, (uuid, offset) -> new EntityPositionSource(Either.right(Either.left(uuid)), offset)));
   public static final StreamCodec STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, EntityPositionSource::getId, ByteBufCodecs.FLOAT, (o) -> o.yOffset, (id, offset) -> new EntityPositionSource(Either.right(Either.right(id)), offset));
   private Either entityOrUuidOrId;
   private final float yOffset;

   public EntityPositionSource(final Entity entity, final float yOffset) {
      this(Either.left(entity), yOffset);
   }

   private EntityPositionSource(final Either entityOrUuidOrId, final float yOffset) {
      this.entityOrUuidOrId = entityOrUuidOrId;
      this.yOffset = yOffset;
   }

   public Optional getPosition(final Level level) {
      if (this.entityOrUuidOrId.left().isEmpty()) {
         this.resolveEntity(level);
      }

      return this.entityOrUuidOrId.left().map((entity) -> entity.position().add(0.0D, (double)this.yOffset, 0.0D));
   }

   private void resolveEntity(final Level level) {
      ((Optional)this.entityOrUuidOrId.map(Optional::of, (uuidOrId) -> Optional.ofNullable((Entity)uuidOrId.map((uuid) -> {
            Entity var10000;
            if (level instanceof ServerLevel serverLevel) {
               var10000 = serverLevel.getEntity(uuid);
            } else {
               var10000 = null;
            }

            return var10000;
         }, level::getEntity)))).ifPresent((entity) -> this.entityOrUuidOrId = Either.left(entity));
   }

   public UUID getUuid() {
      return (UUID)this.entityOrUuidOrId.map(Entity::getUUID, (uuidOrId) -> (UUID)uuidOrId.map(Function.identity(), (id) -> {
            throw new RuntimeException("Unable to get entityId from uuid");
         }));
   }

   private int getId() {
      return this.entityOrUuidOrId.map(Entity::getId, (uuidOrId) -> (Integer)uuidOrId.map((uuid) -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
         }, Function.identity()));
   }

   public PositionSourceType getType() {
      return PositionSourceType.ENTITY;
   }

   public static class Type implements PositionSourceType {
      public MapCodec codec() {
         return EntityPositionSource.CODEC;
      }

      public StreamCodec streamCodec() {
         return EntityPositionSource.STREAM_CODEC;
      }
   }
}
