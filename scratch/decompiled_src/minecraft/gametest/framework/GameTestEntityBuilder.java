package net.minecraft.gametest.framework;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class GameTestEntityBuilder {
   protected final GameTestHelper testHelper;
   private final EntityType entityType;
   private final Vec3 position;
   private @Nullable EntitySpawnReason spawnReason;
   private @Nullable Rotation rotation;
   private boolean requirePersistence = true;

   public GameTestEntityBuilder(final GameTestHelper testHelper, final EntityType entityType, final Vec3 position) {
      this.testHelper = testHelper;
      this.entityType = entityType;
      this.position = position;
   }

   public GameTestEntityBuilder spawnReason(final @Nullable EntitySpawnReason spawnReason) {
      this.spawnReason = spawnReason;
      return this;
   }

   public GameTestEntityBuilder rotation(final @Nullable Rotation rotation) {
      this.rotation = rotation == null ? null : this.testHelper.getTestRotation().getRotated(rotation);
      return this;
   }

   public GameTestEntityBuilder requirePersistence(final boolean requirePersistence) {
      this.requirePersistence = requirePersistence;
      return this;
   }

   public Entity spawn() {
      ServerLevel level = this.testHelper.getLevel();
      Entity entity = (E)this.entityType.create(level, EntitySpawnReason.STRUCTURE);
      if (entity == null) {
         throw this.testHelper.assertionException(BlockPos.containing(this.position), "test.error.spawn_failure", this.entityType.builtInRegistryHolder().getRegisteredName());
      } else {
         if (this.requirePersistence && entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.setPersistenceRequired();
         }

         Vec3 absoluteVec = this.testHelper.absoluteVec(this.position);
         float yRot = entity.rotate(this.rotation == null ? this.testHelper.getTestRotation() : this.rotation);
         entity.snapTo(absoluteVec.x, absoluteVec.y, absoluteVec.z, yRot, entity.getXRot());
         entity.setYBodyRot(yRot);
         entity.setYHeadRot(yRot);
         if (this.spawnReason != null && entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), this.spawnReason, (SpawnGroupData)null);
         }

         level.addFreshEntityWithPassengers(entity);
         return entity;
      }
   }

   public List spawn(final int amount) {
      List entities = new ArrayList();

      for(int i = 0; i < amount; ++i) {
         entities.add(this.spawn());
      }

      return entities;
   }
}
