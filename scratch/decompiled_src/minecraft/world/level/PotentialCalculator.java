package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;

public class PotentialCalculator {
   private final List charges = Lists.newArrayList();

   public void addCharge(final BlockPos pos, final double charge) {
      if (charge != 0.0D) {
         this.charges.add(new PotentialCalculator.PointCharge(pos, charge));
      }

   }

   public double getPotentialEnergyChange(final BlockPos pos, final double charge) {
      if (charge == 0.0D) {
         return 0.0D;
      } else {
         double potentialChange = 0.0D;

         for(PotentialCalculator.PointCharge point : this.charges) {
            potentialChange += point.getPotentialChange(pos);
         }

         return potentialChange * charge;
      }
   }

   private static class PointCharge {
      private final BlockPos pos;
      private final double charge;

      public PointCharge(final BlockPos pos, final double charge) {
         this.pos = pos;
         this.charge = charge;
      }

      public double getPotentialChange(final BlockPos pos) {
         double distSqr = this.pos.distSqr(pos);
         return distSqr == 0.0D ? Double.POSITIVE_INFINITY : this.charge / Math.sqrt(distSqr);
      }
   }
}
