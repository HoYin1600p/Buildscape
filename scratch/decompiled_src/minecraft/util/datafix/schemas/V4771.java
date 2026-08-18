package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4771 extends NamespacedSchema {
   public V4771(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(final Schema schema, final Map entityTypes, final Map blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      schema.registerType(false, References.LEVEL, () -> References.LIGHTWEIGHT_LEVEL.in(schema));
   }
}
