package net.minecraft.advancements.predicates;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;

public interface SingleComponentItemPredicate extends DataComponentPredicate {
   default boolean matches(final DataComponentGetter components) {
      Object value = (T)components.get(this.componentType());
      return value != null && this.matches(value);
   }

   DataComponentType componentType();

   boolean matches(Object value);
}
