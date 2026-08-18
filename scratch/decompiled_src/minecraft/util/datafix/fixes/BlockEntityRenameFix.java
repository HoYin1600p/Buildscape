package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.function.UnaryOperator;

public class BlockEntityRenameFix extends DataFix {
   private final String name;
   private final UnaryOperator nameChangeLookup;

   private BlockEntityRenameFix(final Schema outputSchema, final String name, final UnaryOperator nameChangeLookup) {
      super(outputSchema, true);
      this.name = name;
      this.nameChangeLookup = nameChangeLookup;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoice.TaggedChoiceType oldType = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      TaggedChoice.TaggedChoiceType newType = this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
      return this.fixTypeEverywhere(this.name, oldType, newType, (ops) -> (input) -> input.mapFirst(this.nameChangeLookup));
   }

   public static DataFix create(final Schema outputSchema, final String name, final UnaryOperator nameChangeLookup) {
      return new BlockEntityRenameFix(outputSchema, name, nameChangeLookup);
   }
}
