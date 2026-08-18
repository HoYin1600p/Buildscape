package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public interface TickAccess {
   void schedule(ScheduledTick tick);

   boolean hasScheduledTick(BlockPos pos, Object type);

   int count();
}
