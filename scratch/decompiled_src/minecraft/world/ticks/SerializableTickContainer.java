package net.minecraft.world.ticks;

import java.util.List;

public interface SerializableTickContainer {
   List pack(long currentTick);
}
