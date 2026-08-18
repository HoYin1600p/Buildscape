package net.minecraft.world.entity.ai.behavior;

import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class DoNothing implements BehaviorControl {
   private final int minDuration;
   private final int maxDuration;
   private Behavior.Status status = Behavior.Status.STOPPED;
   private long endTimestamp;

   public DoNothing(final int minDuration, final int maxDuration) {
      this.minDuration = minDuration;
      this.maxDuration = maxDuration;
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   public Set getRequiredMemories() {
      return Set.of();
   }

   public final boolean tryStart(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.status = Behavior.Status.RUNNING;
      int duration = this.minDuration + level.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
      this.endTimestamp = timestamp + (long)duration;
      return true;
   }

   public final void tickOrStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      if (timestamp > this.endTimestamp) {
         this.doStop(level, body, timestamp);
      }

   }

   public final void doStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.status = Behavior.Status.STOPPED;
   }

   public String debugString() {
      return this.getClass().getSimpleName();
   }
}
