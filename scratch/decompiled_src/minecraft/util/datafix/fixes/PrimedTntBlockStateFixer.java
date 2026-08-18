package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;

public class PrimedTntBlockStateFixer extends NamedEntityWriteReadFix {
   public PrimedTntBlockStateFixer(final Schema outputSchema) {
      super(outputSchema, true, "PrimedTnt BlockState fixer", References.ENTITY, "minecraft:tnt");
   }

   private static Dynamic renameFuse(final Dynamic input) {
      Optional fuseValue = input.get("Fuse").get().result();
      return fuseValue.isPresent() ? input.set("fuse", (Dynamic)fuseValue.get()) : input;
   }

   private static Dynamic insertBlockState(final Dynamic input) {
      return input.set("block_state", input.createMap(Map.of(input.createString("Name"), input.createString("minecraft:tnt"))));
   }

   protected Dynamic fix(final Dynamic input) {
      return renameFuse(insertBlockState(input));
   }
}
