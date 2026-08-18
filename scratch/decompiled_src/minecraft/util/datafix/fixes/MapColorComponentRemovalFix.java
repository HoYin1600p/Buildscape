package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import org.jspecify.annotations.Nullable;

public class MapColorComponentRemovalFix extends DataComponentRemainderFix {
   public MapColorComponentRemovalFix(final Schema outputSchema) {
      super(outputSchema, "MapColorComponentRemovalFix", "minecraft:map_color");
   }

   protected @Nullable Dynamic fixComponent(final Dynamic input) {
      return null;
   }
}
