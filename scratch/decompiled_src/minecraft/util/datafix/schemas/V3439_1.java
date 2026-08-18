package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V3439_1 extends NamespacedSchema {
   public V3439_1(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerBlockEntities(final Schema schema) {
      Map map = super.registerBlockEntities(schema);
      this.register(map, "minecraft:hanging_sign", () -> V3439.sign(schema));
      return map;
   }
}
