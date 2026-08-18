package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

public class InvalidLockComponentFix extends DataComponentRemainderFix {
   private static final Optional INVALID_LOCK_CUSTOM_NAME = Optional.of("\"\"");

   public InvalidLockComponentFix(final Schema outputSchema) {
      super(outputSchema, "InvalidLockComponentPredicateFix", "minecraft:lock");
   }

   protected @Nullable Dynamic fixComponent(final Dynamic input) {
      return fixLock(input);
   }

   public static @Nullable Dynamic fixLock(final Dynamic input) {
      return isBrokenLock(input) ? null : input;
   }

   private static boolean isBrokenLock(final Dynamic input) {
      return isMapWithOneField(input, "components", (components) -> isMapWithOneField(components, "minecraft:custom_name", (customName) -> customName.asString().result().equals(INVALID_LOCK_CUSTOM_NAME)));
   }

   private static boolean isMapWithOneField(final Dynamic input, final String fieldName, final Predicate predicate) {
      Optional map = input.getMapValues().result();
      return !map.isEmpty() && ((Map)map.get()).size() == 1 ? input.get(fieldName).result().filter(predicate).isPresent() : false;
   }
}
