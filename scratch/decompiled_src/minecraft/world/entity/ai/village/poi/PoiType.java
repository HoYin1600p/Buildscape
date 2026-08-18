package net.minecraft.world.entity.ai.village.poi;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.BlockState;

public record PoiType(Set matchingStates, int maxTickets, int validRange) {
   public static final Predicate NONE = (poiType) -> false;

   public PoiType {
      matchingStates = Set.copyOf(matchingStates);
   }

   public boolean is(final BlockState state) {
      return this.matchingStates.contains(state);
   }
}
