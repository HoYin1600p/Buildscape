package net.minecraft.world.clock;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.Holder;

public record PackedClockStates(Map clocks) {
   public static final PackedClockStates EMPTY = new PackedClockStates(Map.of());
   public static final Codec CODEC = Codec.unboundedMap(WorldClock.CODEC, ClockState.CODEC).xmap(PackedClockStates::new, PackedClockStates::clocks);
}
