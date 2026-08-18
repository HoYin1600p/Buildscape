package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import java.util.List;

public record SpawnPrioritySelectors(List selectors) {
   public static final SpawnPrioritySelectors EMPTY = new SpawnPrioritySelectors(List.of());
   public static final Codec CODEC = PriorityProvider.Selector.codec(SpawnCondition.CODEC).listOf().xmap(SpawnPrioritySelectors::new, SpawnPrioritySelectors::selectors);

   public static SpawnPrioritySelectors single(final SpawnCondition condition, final int priority) {
      return new SpawnPrioritySelectors(PriorityProvider.single(condition, priority));
   }

   public static SpawnPrioritySelectors fallback(final int priority) {
      return new SpawnPrioritySelectors(PriorityProvider.alwaysTrue(priority));
   }
}
