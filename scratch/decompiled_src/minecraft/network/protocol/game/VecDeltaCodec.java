package net.minecraft.network.protocol.game;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class VecDeltaCodec {
   private static final double TRUNCATION_STEPS = 4096.0D;
   private Vec3 base = Vec3.ZERO;

   @VisibleForTesting
   static long encode(final double input) {
      return Math.round(input * 4096.0D);
   }

   @VisibleForTesting
   static double decode(final long v) {
      return (double)v / 4096.0D;
   }

   public static double encodingPrecisionLoss(final double d) {
      return decode(encode(d)) - d;
   }

   public static boolean isDeltaTooBig(final long xa, final long ya, final long za) {
      return xa < -32768L || xa > 32767L || ya < -32768L || ya > 32767L || za < -32768L || za > 32767L;
   }

   public Vec3 decode(final long xa, final long ya, final long za) {
      if (xa == 0L && ya == 0L && za == 0L) {
         return this.base;
      } else {
         double x = xa == 0L ? this.base.x : decode(encode(this.base.x) + xa);
         double y = ya == 0L ? this.base.y : decode(encode(this.base.y) + ya);
         double z = za == 0L ? this.base.z : decode(encode(this.base.z) + za);
         return new Vec3(x, y, z);
      }
   }

   public @Nullable VecDelta tryEncode(final PositionPath position) {
      Objects.requireNonNull(position);
      byte var3 = 0;
      Throwable var13;
      switch (position.typeSwitch<invokedynamic>(position, var3)) {
         case 0:
            PositionPath.Linear var4 = (PositionPath.Linear)position;
            PositionPath.Linear var17 = var4;

            try {
               var18 = var17.endPosition();
            } catch (Throwable var11) {
               var13 = var11;
               boolean var20 = false;
               break;
            }

            Vec3 var12 = var18;
            return this.tryEncode(var12);
         case 1:
            PositionPath.Stepped var6 = (PositionPath.Stepped)position;
            PositionPath.Stepped var10000 = var6;

            try {
               var10000.endPosition();
            } catch (Throwable var10) {
               var13 = var10;
               boolean var10001 = false;
               break;
            }

            var10000 = var6;

            try {
               var15 = var10000.steps();
            } catch (Throwable var9) {
               var13 = var9;
               boolean var19 = false;
               break;
            }

            List var8 = var15;
            return VecDelta.Stepped.tryEncode(this, var8);
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Throwable var2 = var13;
      throw new MatchException(var2.toString(), var2);
   }

   public @Nullable VecDelta tryEncode(final Vec3 pos) {
      long xa = this.encodeX(pos);
      long ya = this.encodeY(pos);
      long za = this.encodeZ(pos);
      return isDeltaTooBig(xa, ya, za) ? null : new VecDelta.Linear((short)((int)xa), (short)((int)ya), (short)((int)za));
   }

   public long encodeX(final Vec3 pos) {
      return encode(pos.x) - encode(this.base.x);
   }

   public long encodeY(final Vec3 pos) {
      return encode(pos.y) - encode(this.base.y);
   }

   public long encodeZ(final Vec3 pos) {
      return encode(pos.z) - encode(this.base.z);
   }

   public Vec3 delta(final Vec3 pos) {
      return pos.subtract(this.base);
   }

   public void setBase(final Vec3 base) {
      this.base = base;
   }

   public Vec3 getBase() {
      return this.base;
   }
}
