package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V702 extends Schema {
   public V702(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(final Schema schema) {
      Map map = super.registerEntities(schema);
      schema.register(map, "ZombieVillager", (name) -> DSL.optionalFields("Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(schema)))));
      schema.registerSimple(map, "Husk");
      return map;
   }
}
