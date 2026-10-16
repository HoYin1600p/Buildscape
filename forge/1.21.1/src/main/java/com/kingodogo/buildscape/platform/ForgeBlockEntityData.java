package com.kingodogo.buildscape.platform;

import net.minecraft.nbt.CompoundTag;

/** Mutable block-entity data retained across saves on Forge 52, which removed Forge's old API. */
public interface ForgeBlockEntityData {
    CompoundTag buildscape$getPersistentData();
}