package net.minecraft.gametest.framework;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class BuiltinTestFunctions implements TestFunctionLoader {
   public static final ResourceKey ALWAYS_PASS = create("always_pass");
   public static final Consumer ALWAYS_PASS_INSTANCE = GameTestHelper::succeed;

   private static ResourceKey create(final String name) {
      return ResourceKey.create(Registries.TEST_FUNCTION, Identifier.withDefaultNamespace(name));
   }

   public static Consumer bootstrap(final Registry registry) {
      TestFunctionLoader.registerLoader(new BuiltinTestFunctions());
      TestFunctionLoader.runLoaders(registry);
      return ALWAYS_PASS_INSTANCE;
   }

   public void load(final BiConsumer register) {
      register.accept(ALWAYS_PASS, ALWAYS_PASS_INSTANCE);
   }
}
