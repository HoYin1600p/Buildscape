package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class LegacyBlockPropertyRenameAndFix extends LegacyAbstractBlockPropertyFix {
   private final String blockId;
   private final String oldPropertyName;
   private final String newPropertyName;
   private final UnaryOperator valueFixer;

   public LegacyBlockPropertyRenameAndFix(final Schema outputSchema, final String name, final String blockId, final String oldPropertyName, final String newPropertyName, final UnaryOperator valueFixer) {
      super(outputSchema, name);
      this.blockId = blockId;
      this.oldPropertyName = oldPropertyName;
      this.newPropertyName = newPropertyName;
      this.valueFixer = valueFixer;
   }

   protected boolean shouldFix(final String blockId) {
      return blockId.equals(this.blockId);
   }

   protected Dynamic fixProperties(final String blockId, final Dynamic properties) {
      return properties.renameAndFixField(this.oldPropertyName, this.newPropertyName, (dynamic) -> dynamic.createString((String)this.valueFixer.apply(dynamic.asString(""))));
   }
}
