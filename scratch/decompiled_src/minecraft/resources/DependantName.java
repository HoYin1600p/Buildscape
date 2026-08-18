package net.minecraft.resources;

@FunctionalInterface
public interface DependantName {
   Object get(ResourceKey id);

   static DependantName fixed(final Object value) {
      return (id) -> value;
   }
}
