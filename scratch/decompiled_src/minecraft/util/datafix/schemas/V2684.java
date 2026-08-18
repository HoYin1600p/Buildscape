package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2684 extends NamespacedSchema {
   public V2684(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(final Schema schema, final Map entityTypes, final Map blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      schema.registerType(false, References.GAME_EVENT_NAME, () -> DSL.constType(namespacedString()));
   }

   public Map registerBlockEntities(final Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:sculk_sensor", () -> DSL.optionalFields("listener", DSL.optionalFields("event", DSL.optionalFields("game_event", References.GAME_EVENT_NAME.in(schema)))));
      return map;
   }
}
