package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public class BlackholeTickAccess {
   private static final TickContainerAccess CONTAINER_BLACKHOLE = new TickContainerAccess() {
      public void schedule(final ScheduledTick tick) {
      }

      public boolean hasScheduledTick(final BlockPos pos, final Object type) {
         return false;
      }

      public int count() {
         return 0;
      }
   };
   private static final LevelTickAccess LEVEL_BLACKHOLE = new LevelTickAccess() {
      public void schedule(final ScheduledTick tick) {
      }

      public boolean hasScheduledTick(final BlockPos pos, final Object type) {
         return false;
      }

      public boolean willTickThisTick(final BlockPos pos, final Object type) {
         return false;
      }

      public int count() {
         return 0;
      }
   };

   public static TickContainerAccess emptyContainer() {
      return CONTAINER_BLACKHOLE;
   }

   public static LevelTickAccess emptyLevelList() {
      return LEVEL_BLACKHOLE;
   }
}
