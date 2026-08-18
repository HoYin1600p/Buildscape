package net.minecraft.core;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface DefaultedRegistry extends Registry {
   @NonNull Identifier getKey(Object thing);

   @NonNull Object getValue(@Nullable Identifier key);

   @NonNull Object byId(int id);

   Identifier getDefaultKey();
}
