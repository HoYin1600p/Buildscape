package net.minecraft.data.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;

public interface BootstrapContext extends BootstrapContextAccess {
   Holder.Reference register(ResourceKey key, Object value);
}
