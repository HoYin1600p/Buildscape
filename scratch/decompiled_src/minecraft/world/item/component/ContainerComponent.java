package net.minecraft.world.item.component;

import java.util.stream.Stream;
import net.minecraft.world.item.slot.SlotCollection;

public interface ContainerComponent {
   Stream itemCopies();

   int size();

   ContainerComponent copyWithContents(Stream newContents);

   ContainerComponent.Mutable asMutable();

   public interface Mutable extends SlotCollection {
      Object toImmutable();
   }
}
