package com.kingodogo.buildscape.data;

import net.minecraft.nbt.CompoundTag;

/**
 * Loader hook installed before common initialization. Return the owner's persistent attachment,
 * never a copy or a global cache. Owners may be entities (including players), block entities or levels.
 */
@FunctionalInterface
public interface PersistentDataAccess {
    CompoundTag getOrCreate(Object owner);
}
