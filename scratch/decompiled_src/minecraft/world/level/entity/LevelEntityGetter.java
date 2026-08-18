package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public interface LevelEntityGetter {
   @Nullable EntityAccess get(final int id);

   @Nullable EntityAccess get(final UUID id);

   Iterable getAll();

   void get(final EntityTypeTest type, final AbortableIterationConsumer consumer);

   void get(final AABB bb, final Consumer output);

   void get(final EntityTypeTest type, final AABB bb, final AbortableIterationConsumer consumer);
}
