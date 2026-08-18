package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4997 extends NamespacedSchema {
   public V4997(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(final Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:poplar_boat");
      schema.register(map, "minecraft:poplar_chest_boat", (name) -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema))));
      return map;
   }
}
