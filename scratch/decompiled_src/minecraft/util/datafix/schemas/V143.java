package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V143 extends Schema {
   public V143(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(final Schema schema) {
      Map map = super.registerEntities(schema);
      map.remove("TippedArrow");
      return map;
   }
}
