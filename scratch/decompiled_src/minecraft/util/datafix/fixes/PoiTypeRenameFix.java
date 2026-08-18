package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;
import java.util.stream.Stream;

public class PoiTypeRenameFix extends AbstractPoiSectionFix {
   private final Function renamer;

   public PoiTypeRenameFix(final Schema outputSchema, final String name, final Function renamer) {
      super(outputSchema, name);
      this.renamer = renamer;
   }

   protected Stream processRecords(final Stream stream) {
      return stream.map((element) -> element.update("type", (type) -> (Dynamic)DataFixUtils.orElse(type.asString().map(this.renamer).map(type::createString).result(), type)));
   }
}
