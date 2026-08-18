package net.minecraft.world.entity.vehicle.boat;

import java.util.function.Supplier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class Raft extends AbstractBoat {
   public Raft(final EntityType type, final Level level, final Supplier dropItem) {
      super(type, level, dropItem);
   }

   protected double rideHeight(final EntityDimensions dimensions) {
      return (double)(dimensions.height() * 0.8888889F);
   }
}
