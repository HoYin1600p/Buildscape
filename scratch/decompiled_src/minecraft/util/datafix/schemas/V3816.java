package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V3816 extends NamespacedSchema {
   public V3816(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(final Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:bogged");
      return map;
   }
}
