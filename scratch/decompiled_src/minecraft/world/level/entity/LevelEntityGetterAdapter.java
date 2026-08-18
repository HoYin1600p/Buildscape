package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class LevelEntityGetterAdapter implements LevelEntityGetter {
   private final EntityLookup visibleEntities;
   private final EntitySectionStorage sectionStorage;

   public LevelEntityGetterAdapter(final EntityLookup visibleEntities, final EntitySectionStorage sectionStorage) {
      this.visibleEntities = visibleEntities;
      this.sectionStorage = sectionStorage;
   }

   public @Nullable EntityAccess get(final int id) {
      return this.visibleEntities.getEntity(id);
   }

   public @Nullable EntityAccess get(final UUID id) {
      return this.visibleEntities.getEntity(id);
   }

   public Iterable getAll() {
      return this.visibleEntities.getAllEntities();
   }

   public void get(final EntityTypeTest type, final AbortableIterationConsumer consumer) {
      this.visibleEntities.getEntities(type, consumer);
   }

   public void get(final AABB bb, final Consumer output) {
      this.sectionStorage.getEntities(bb, AbortableIterationConsumer.forConsumer(output));
   }

   public void get(final EntityTypeTest type, final AABB bb, final AbortableIterationConsumer consumer) {
      this.sectionStorage.getEntities(type, bb, consumer);
   }
}
