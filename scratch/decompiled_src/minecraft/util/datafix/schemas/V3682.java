package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V3682 extends NamespacedSchema {
   public V3682(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerBlockEntities(final Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:crafter", () -> V1458.nameableInventory(schema));
      return map;
   }
}
