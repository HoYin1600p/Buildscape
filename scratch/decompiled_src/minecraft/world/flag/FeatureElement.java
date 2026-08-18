package net.minecraft.world.flag;

import java.util.Set;
import net.minecraft.core.registries.Registries;

public interface FeatureElement {
   Set FILTERED_REGISTRIES = Set.of(Registries.ITEM, Registries.BLOCK, Registries.ENTITY_TYPE, Registries.GAME_RULE, Registries.MENU, Registries.POTION, Registries.MOB_EFFECT);

   FeatureFlagSet requiredFeatures();

   default boolean isEnabled(final FeatureFlagSet enabledFeatures) {
      return this.requiredFeatures().isSubsetOf(enabledFeatures);
   }
}
