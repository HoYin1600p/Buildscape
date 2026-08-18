package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPos;

public class WorldGenTickAccess implements LevelTickAccess {
   private final Function containerGetter;

   public WorldGenTickAccess(final Function containerGetter) {
      this.containerGetter = containerGetter;
   }

   public boolean hasScheduledTick(final BlockPos pos, final Object type) {
      return ((TickContainerAccess)this.containerGetter.apply(pos)).hasScheduledTick(pos, type);
   }

   public void schedule(final ScheduledTick tick) {
      ((TickContainerAccess)this.containerGetter.apply(tick.pos())).schedule(tick);
   }

   public boolean willTickThisTick(final BlockPos pos, final Object type) {
      return false;
   }

   public int count() {
      return 0;
   }
}
