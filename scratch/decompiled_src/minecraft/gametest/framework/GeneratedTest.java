package net.minecraft.gametest.framework;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record GeneratedTest(Map tests, ResourceKey functionKey, Consumer function) {
   public GeneratedTest(final Map tests, final Identifier functionId, final Consumer function) {
      this(tests, ResourceKey.create(Registries.TEST_FUNCTION, functionId), function);
   }

   public GeneratedTest(final Identifier id, final TestData testData, final Consumer function) {
      this(Map.of(id, testData), id, function);
   }
}
