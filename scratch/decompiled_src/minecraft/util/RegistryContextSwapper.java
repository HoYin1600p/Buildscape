package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;

public interface RegistryContextSwapper {
   DataResult swapTo(Codec codec, Object value, HolderLookup.Provider newContext);
}
