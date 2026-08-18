package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public interface EntityGetter {
   List getEntities(@Nullable Entity except, AABB bb, Predicate selector);

   List getEntities(final EntityTypeTest type, final AABB bb, final Predicate selector);

   default List getEntitiesOfClass(final Class baseClass, final AABB bb, final Predicate selector) {
      return this.getEntities(EntityTypeTest.forClass(baseClass), bb, selector);
   }

   List players();

   default List getEntities(final @Nullable Entity except, final AABB bb) {
      return this.getEntities(except, bb, EntitySelector.NO_SPECTATORS);
   }

   default boolean isUnobstructed(final @Nullable Entity source, final VoxelShape shape) {
      if (shape.isEmpty()) {
         return true;
      } else {
         for(Entity entity : this.getEntities(source, shape.bounds())) {
            if (!entity.isRemoved() && entity.blocksBuilding && (source == null || !entity.isPassengerOfSameVehicle(source)) && Shapes.joinIsNotEmpty(shape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   default List getEntitiesOfClass(final Class baseClass, final AABB bb) {
      return this.getEntitiesOfClass(baseClass, bb, EntitySelector.NO_SPECTATORS);
   }

   default List getEntityCollisions(final @Nullable Entity source, final AABB testArea) {
      if (testArea.getSize() < 1.0E-7D) {
         return List.of();
      } else {
         Predicate canCollide = source == null ? EntitySelector.CAN_BE_COLLIDED_WITH : EntitySelector.NO_SPECTATORS.and(source::canCollideWith);
         List collidingEntities = this.getEntities(source, testArea.inflate(1.0E-7D), canCollide);
         if (collidingEntities.isEmpty()) {
            return List.of();
         } else {
            ImmutableList.Builder shapes = ImmutableList.builderWithExpectedSize(collidingEntities.size());

            for(Entity entity : collidingEntities) {
               shapes.add(Shapes.create(entity.getBoundingBox()));
            }

            return shapes.build();
         }
      }
   }

   default @Nullable Player getNearestPlayer(final double x, final double y, final double z, final double range, final @Nullable Predicate predicate) {
      double best = -1.0D;
      Player result = null;

      for(Player player : this.players()) {
         if (predicate == null || predicate.test(player)) {
            double dist = player.distanceToSqr(x, y, z);
            if ((range < 0.0D || dist < range * range) && (best == -1.0D || dist < best)) {
               best = dist;
               result = player;
            }
         }
      }

      return result;
   }

   default @Nullable Player getNearestPlayer(final Entity source, final double maxDist) {
      return this.getNearestPlayer(source.getX(), source.getY(), source.getZ(), maxDist, false);
   }

   default @Nullable Player getNearestPlayer(final double x, final double y, final double z, final double maxDist, final boolean filterOutCreative) {
      Predicate predicate = filterOutCreative ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
      return this.getNearestPlayer(x, y, z, maxDist, predicate);
   }

   default boolean hasNearbyAlivePlayer(final double x, final double y, final double z, final double range) {
      for(Player player : this.players()) {
         if (EntitySelector.NO_SPECTATORS.test(player) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player)) {
            double playerDist = player.distanceToSqr(x, y, z);
            if (range < 0.0D || playerDist < range * range) {
               return true;
            }
         }
      }

      return false;
   }

   default @Nullable Player getPlayerByUUID(final UUID uuid) {
      for(int i = 0; i < this.players().size(); ++i) {
         Player player = (Player)this.players().get(i);
         if (uuid.equals(player.getUUID())) {
            return player;
         }
      }

      return null;
   }
}
