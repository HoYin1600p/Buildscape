package net.minecraft.network.protocol.game;

import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.entity.PositionStep;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public sealed interface VecDelta {
   VecDelta ZERO = new VecDelta.Linear((short)0, (short)0, (short)0);

   int stepCount();

   boolean hasDeltaX();

   boolean hasDeltaZ();

   PositionPath decode(VecDeltaCodec positionCodec);

   static VecDelta read(final FriendlyByteBuf input, final int stepCount) {
      if (stepCount <= 0) {
         short xa = input.readShort();
         short ya = input.readShort();
         short za = input.readShort();
         return new VecDelta.Linear(xa, ya, za);
      } else {
         int maxSteps = input.readableBytes() / 7;
         if (stepCount > maxSteps) {
            throw new DecoderException("VecDelta with size " + stepCount + " is bigger than allowed " + maxSteps);
         } else {
            List steps = new ArrayList(stepCount);

            for(int i = 0; i < stepCount; ++i) {
               int ticks = input.readVarInt();
               short xa = input.readShort();
               short ya = input.readShort();
               short za = input.readShort();
               steps.add(new VecDelta.Stepped.DeltaStep(xa, ya, za, ticks));
            }

            return new VecDelta.Stepped(steps);
         }
      }
   }

   static void write(FriendlyByteBuf param0, VecDelta param1) {
      // $FF: Couldn't be decompiled
   }

   public static record Linear(short xa, short ya, short za) implements VecDelta {
      public int stepCount() {
         return 0;
      }

      public boolean hasDeltaX() {
         return this.xa != 0;
      }

      public boolean hasDeltaZ() {
         return this.za != 0;
      }

      public PositionPath decode(final VecDeltaCodec positionCodec) {
         Vec3 pos = positionCodec.decode((long)this.xa, (long)this.ya, (long)this.za);
         return PositionPath.of(pos);
      }
   }

   public static record Stepped(List steps) implements VecDelta {
      private static final int MIN_BYTES_PER_STEP = 7;

      public int stepCount() {
         return this.steps.size();
      }

      public boolean hasDeltaX() {
         for(VecDelta.Stepped.DeltaStep step : this.steps) {
            if (step.xa != 0) {
               return true;
            }
         }

         return false;
      }

      public boolean hasDeltaZ() {
         for(VecDelta.Stepped.DeltaStep step : this.steps) {
            if (step.za != 0) {
               return true;
            }
         }

         return false;
      }

      public PositionPath decode(final VecDeltaCodec positionCodec) {
         if (this.steps.isEmpty()) {
            return PositionPath.of(positionCodec.getBase());
         } else {
            List output = new ArrayList(this.steps.size());
            Vec3 originalBase = positionCodec.getBase();

            for(VecDelta.Stepped.DeltaStep e : this.steps) {
               Vec3 pos = positionCodec.decode((long)e.xa, (long)e.ya, (long)e.za);
               output.add(new PositionStep(pos, e.ticks));
               positionCodec.setBase(pos);
            }

            positionCodec.setBase(originalBase);
            return PositionPath.stepped(output);
         }
      }

      public static VecDelta.@Nullable Stepped tryEncode(final VecDeltaCodec positionCodec, final List steps) {
         if (steps.isEmpty()) {
            return new VecDelta.Stepped(List.of());
         } else {
            List output = new ArrayList(steps.size());
            Vec3 originalBase = positionCodec.getBase();

            for(PositionStep step : steps) {
               Vec3 pos = step.position();
               long xa = positionCodec.encodeX(pos);
               long ya = positionCodec.encodeY(pos);
               long za = positionCodec.encodeZ(pos);
               if (VecDeltaCodec.isDeltaTooBig(xa, ya, za)) {
                  positionCodec.setBase(originalBase);
                  return null;
               }

               output.add(new VecDelta.Stepped.DeltaStep((short)((int)xa), (short)((int)ya), (short)((int)za), step.tickOffset()));
               positionCodec.setBase(pos);
            }

            positionCodec.setBase(originalBase);
            return new VecDelta.Stepped(output);
         }
      }

      public static record DeltaStep(short xa, short ya, short za, int ticks) {
      }
   }
}
