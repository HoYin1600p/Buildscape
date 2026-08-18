package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4533 extends NamespacedSchema {
   public V4533(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerBlockEntities(final Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:shelf", () -> DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema))));
      return map;
   }
}
