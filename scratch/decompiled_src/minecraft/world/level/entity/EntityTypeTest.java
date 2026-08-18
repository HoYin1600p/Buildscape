package net.minecraft.world.level.entity;

import org.jspecify.annotations.Nullable;

public interface EntityTypeTest {
   static EntityTypeTest forClass(final Class cls) {
      return new EntityTypeTest() {
         public @Nullable Object tryCast(final Object entity) {
            return cls.isInstance(entity) ? entity : null;
         }

         public Class getBaseClass() {
            return cls;
         }
      };
   }

   static EntityTypeTest forExactClass(final Class cls) {
      return new EntityTypeTest() {
         public @Nullable Object tryCast(final Object entity) {
            return cls.equals(entity.getClass()) ? entity : null;
         }

         public Class getBaseClass() {
            return cls;
         }
      };
   }

   @Nullable Object tryCast(Object entity);

   Class getBaseClass();
}
