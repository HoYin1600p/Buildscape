package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;

public interface TimerCallback {
   void handle(final Object context, TimerQueue queue, long time);

   MapCodec codec();
}
