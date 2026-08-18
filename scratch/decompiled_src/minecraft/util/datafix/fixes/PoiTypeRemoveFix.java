package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PoiTypeRemoveFix extends AbstractPoiSectionFix {
   private final Predicate typesToKeep;

   public PoiTypeRemoveFix(final Schema outputSchema, final String name, final Predicate typesToRemove) {
      super(outputSchema, name);
      this.typesToKeep = typesToRemove.negate();
   }

   protected Stream processRecords(final Stream records) {
      return records.filter(this::shouldKeepRecord);
   }

   private boolean shouldKeepRecord(final Dynamic record) {
      return record.get("type").asString().result().filter(this.typesToKeep).isPresent();
   }
}
