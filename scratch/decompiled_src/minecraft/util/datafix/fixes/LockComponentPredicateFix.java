package net.minecraft.util.datafix.fixes;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public class LockComponentPredicateFix extends DataComponentRemainderFix {
   public static final Escaper ESCAPER = Escapers.builder().addEscape('"', "\\\"").addEscape('\\', "\\\\").build();

   public LockComponentPredicateFix(final Schema outputSchema) {
      super(outputSchema, "LockComponentPredicateFix", "minecraft:lock");
   }

   protected @Nullable Dynamic fixComponent(final Dynamic input) {
      return fixLock(input);
   }

   public static @Nullable Dynamic fixLock(final Dynamic input) {
      Optional name = input.asString().result();
      if (name.isEmpty()) {
         return null;
      } else if (((String)name.get()).isEmpty()) {
         return null;
      } else {
         Dynamic nameComponent = input.createString("\"" + ESCAPER.escape((String)name.get()) + "\"");
         Dynamic components = input.emptyMap().set("minecraft:custom_name", nameComponent);
         return input.emptyMap().set("components", components);
      }
   }
}
