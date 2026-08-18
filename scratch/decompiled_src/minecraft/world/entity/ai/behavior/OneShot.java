package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public abstract class OneShot implements BehaviorControl, Trigger {
   private Behavior.Status status = Behavior.Status.STOPPED;

   public final Behavior.Status getStatus() {
      return this.status;
   }

   public final boolean tryStart(final ServerLevel level, final LivingEntity body, final long timestamp) {
      if (this.trigger(level, body, timestamp)) {
         this.status = Behavior.Status.RUNNING;
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.doStop(level, body, timestamp);
   }

   public final void doStop(final ServerLevel level, final LivingEntity body, final long timestamp) {
      this.status = Behavior.Status.STOPPED;
   }

   public String debugString() {
      return this.getClass().getSimpleName();
   }
}
