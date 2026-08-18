package net.minecraft.world.item.equipment;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;

@FunctionalInterface
public interface AllowedEntitiesProvider {
   HolderSet get(HolderGetter entityGetter);
}
