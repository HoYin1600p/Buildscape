package net.minecraft.world.level.entity;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public interface EntityAccess extends UniquelyIdentifyable {
   int getId();

   BlockPos blockPosition();

   AABB getBoundingBox();

   void setLevelCallback(EntityInLevelCallback callback);

   Stream getSelfAndPassengers();

   Stream getPassengersAndSelf();

   void setRemoved(Entity.RemovalReason removalReason);

   boolean shouldBeSaved();

   boolean isAlwaysTicking();
}
