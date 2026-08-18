package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.SegmentedAnglePrecision;

public class RotationSegment {
   private static final SegmentedAnglePrecision SEGMENTED_ANGLE16 = new SegmentedAnglePrecision(4);
   private static final int MAX_SEGMENT_INDEX = SEGMENTED_ANGLE16.getMask();
   private static final int NORTH_0 = 0;
   private static final int EAST_90 = 4;
   private static final int SOUTH_180 = 8;
   private static final int WEST_270 = 12;

   public static int getMaxSegmentIndex() {
      return MAX_SEGMENT_INDEX;
   }

   public static int convertToSegment(final Direction direction) {
      return SEGMENTED_ANGLE16.fromDirection(direction);
   }

   public static int convertToSegment(final float rotDegrees) {
      return SEGMENTED_ANGLE16.fromDegrees(rotDegrees);
   }

   public static Optional convertToDirection(final int segment) {
      Direction var10000;
      switch (segment) {
         case 0:
            var10000 = Direction.NORTH;
            break;
         case 4:
            var10000 = Direction.EAST;
            break;
         case 8:
            var10000 = Direction.SOUTH;
            break;
         case 12:
            var10000 = Direction.WEST;
            break;
         default:
            var10000 = null;
      }

      Direction result = var10000;
      return Optional.ofNullable(result);
   }

   public static float convertToDegrees(final int segment) {
      return SEGMENTED_ANGLE16.toDegrees(segment);
   }
}
