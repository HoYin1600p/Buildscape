package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.AbortableIterationConsumer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class EntityLookup {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Int2ObjectMap byId = new Int2ObjectLinkedOpenHashMap();
   private final Map byUuid = Maps.newHashMap();

   public void getEntities(final EntityTypeTest type, final AbortableIterationConsumer consumer) {
      ObjectIterator var3 = this.byId.values().iterator();

      while(var3.hasNext()) {
         EntityAccess entity = (T)((EntityAccess)var3.next());
         EntityAccess maybeEntity = (U)((EntityAccess)type.tryCast(entity));
         if (maybeEntity != null && consumer.accept(maybeEntity).shouldAbort()) {
            return;
         }
      }

   }

   public Iterable getAllEntities() {
      return Iterables.unmodifiableIterable(this.byId.values());
   }

   public void add(final EntityAccess entity) {
      UUID uuid = entity.getUUID();
      if (this.byUuid.containsKey(uuid)) {
         LOGGER.warn("Duplicate entity UUID {}: {}", uuid, entity);
      } else {
         this.byUuid.put(uuid, entity);
         this.byId.put(entity.getId(), entity);
      }
   }

   public void remove(final EntityAccess entity) {
      this.byUuid.remove(entity.getUUID());
      this.byId.remove(entity.getId());
   }

   public @Nullable EntityAccess getEntity(final int id) {
      return (EntityAccess)this.byId.get(id);
   }

   public @Nullable EntityAccess getEntity(final UUID id) {
      return (EntityAccess)this.byUuid.get(id);
   }

   public int count() {
      return this.byUuid.size();
   }
}
