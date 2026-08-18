package net.minecraft.core.registries;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class ConcurrentHolderGetter implements HolderGetter {
   private final Object lock;
   private final HolderGetter original;
   private final Map elementCache = new ConcurrentHashMap();
   private final Map tagCache = new ConcurrentHashMap();

   public ConcurrentHolderGetter(final Object lock, final HolderGetter original) {
      this.lock = lock;
      this.original = original;
   }

   public Optional get(final ResourceKey elementId) {
      return (Optional)this.elementCache.computeIfAbsent(elementId, (id) -> {
         synchronized(this.lock) {
            return this.original.get(id);
         }
      });
   }

   public Optional get(final TagKey tagId) {
      return (Optional)this.tagCache.computeIfAbsent(tagId, (id) -> {
         synchronized(this.lock) {
            return this.original.get(id);
         }
      });
   }

   public boolean canSerialize(final HolderOwner owner) {
      return this.original.canSerialize(owner);
   }
}
