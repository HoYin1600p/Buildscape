package net.minecraft.world.level.levelgen.structure.templatesystem;

import java.util.List;

public class StructureProcessorList {
   private final List list;

   public StructureProcessorList(final List list) {
      this.list = list;
   }

   public List list() {
      return this.list;
   }

   public String toString() {
      return "ProcessorList[" + String.valueOf(this.list) + "]";
   }
}
