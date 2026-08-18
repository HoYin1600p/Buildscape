package net.minecraft.core.component;

import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public interface DataComponentHolder extends DataComponentGetter {
   DataComponentMap getComponents();

   default @Nullable Object get(final DataComponentType type) {
      return this.getComponents().get(type);
   }

   default Stream getAllOfType(final Class valueClass) {
      return this.getComponents().stream().map(TypedDataComponent::value).filter((value) -> valueClass.isAssignableFrom(value.getClass())).map((value) -> value);
   }

   default Object getOrDefault(final DataComponentType type, final Object defaultValue) {
      return this.getComponents().getOrDefault(type, defaultValue);
   }

   default boolean has(final DataComponentType type) {
      return this.getComponents().has(type);
   }
}
