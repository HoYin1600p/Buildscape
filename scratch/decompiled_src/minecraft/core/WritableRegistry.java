package net.minecraft.core;

import java.util.Map;
import net.minecraft.resources.ResourceKey;

public interface WritableRegistry extends Registry {
   Holder.Reference register(ResourceKey key, Object value, RegistrationInfo registrationInfo);

   void bindTags(Map pendingTags);

   boolean isEmpty();

   HolderGetter createRegistrationLookup();
}
