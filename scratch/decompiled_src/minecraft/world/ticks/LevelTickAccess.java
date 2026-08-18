package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public interface LevelTickAccess extends TickAccess {
   boolean willTickThisTick(BlockPos pos, Object type);
}
