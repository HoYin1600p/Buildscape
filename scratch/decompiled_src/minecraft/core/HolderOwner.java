package net.minecraft.core;

public interface HolderOwner {
   default boolean canSerialize(final HolderOwner owner) {
      return owner == this;
   }
}
