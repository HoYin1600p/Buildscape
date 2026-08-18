package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;

public enum Rotation implements StringRepresentable {
   NONE(0, "none", OctahedralGroup.IDENTITY),
   CLOCKWISE_90(1, "clockwise_90", OctahedralGroup.ROT_90_Y_NEG),
   CLOCKWISE_180(2, "180", OctahedralGroup.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90(3, "counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);

   public static final IntFunction BY_ID = ByIdMap.continuous(Rotation::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec CODEC = StringRepresentable.fromEnum(Rotation::values);
   public static final StreamCodec STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Rotation::getIndex);
   /** @deprecated */
   @Deprecated
   public static final Codec LEGACY_CODEC = ExtraCodecs.legacyEnum(Rotation::valueOf);
   private final int index;
   private final String id;
   private final OctahedralGroup rotation;

   private Rotation(final int index, final String id, final OctahedralGroup rotation) {
      this.index = index;
      this.id = id;
      this.rotation = rotation;
   }

   public Rotation getRotated(final Rotation rot) {
      switch (rot.ordinal()) {
         case 1:
            switch (this.ordinal()) {
               case 0:
                  return CLOCKWISE_90;
               case 1:
                  return CLOCKWISE_180;
               case 2:
                  return COUNTERCLOCKWISE_90;
               case 3:
                  return NONE;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 2:
            switch (this.ordinal()) {
               case 0:
                  return CLOCKWISE_180;
               case 1:
                  return COUNTERCLOCKWISE_90;
               case 2:
                  return NONE;
               case 3:
                  return CLOCKWISE_90;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 3:
            switch (this.ordinal()) {
               case 0:
                  return COUNTERCLOCKWISE_90;
               case 1:
                  return NONE;
               case 2:
                  return CLOCKWISE_90;
               case 3:
                  return CLOCKWISE_180;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            return this;
      }
   }

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Direction rotate(final Direction direction) {
      if (direction.getAxis() == Direction.Axis.Y) {
         return direction;
      } else {
         Direction var10000;
         switch (this.ordinal()) {
            case 1:
               var10000 = direction.getClockWise();
               break;
            case 2:
               var10000 = direction.getOpposite();
               break;
            case 3:
               var10000 = direction.getCounterClockWise();
               break;
            default:
               var10000 = direction;
         }

         return var10000;
      }
   }

   public int rotate(final int rotation, final int steps) {
      int var10000;
      switch (this.ordinal()) {
         case 1:
            var10000 = (rotation + steps / 4) % steps;
            break;
         case 2:
            var10000 = (rotation + steps / 2) % steps;
            break;
         case 3:
            var10000 = (rotation + steps * 3 / 4) % steps;
            break;
         default:
            var10000 = rotation;
      }

      return var10000;
   }

   public static Rotation getRandom(final RandomSource random) {
      return (Rotation)Util.getRandom((Object[])values(), random);
   }

   public static List getShuffled(final RandomSource random) {
      return Util.shuffledCopy(values(), random);
   }

   public String getSerializedName() {
      return this.id;
   }

   private int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static Rotation[] $values() {
      return new Rotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
   }
}
