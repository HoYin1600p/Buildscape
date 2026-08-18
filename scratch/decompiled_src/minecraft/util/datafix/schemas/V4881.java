package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V4881 extends NamespacedSchema {
   public V4881(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(final Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:sulfur_cube");
      return map;
   }

   public Map registerBlockEntities(final Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.registerSimple(map, "minecraft:potent_sulfur");
      return map;
   }
}
