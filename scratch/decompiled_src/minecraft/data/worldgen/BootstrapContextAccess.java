package net.minecraft.data.worldgen;

import java.util.stream.Stream;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;

public interface BootstrapContextAccess {
   HolderGetter lookup(ResourceKey key);

   /** @deprecated */
   @Deprecated
   Stream listContextElements(ResourceKey key);
}
