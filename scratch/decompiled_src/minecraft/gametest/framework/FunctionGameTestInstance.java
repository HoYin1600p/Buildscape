package net.minecraft.gametest.framework;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;

public class FunctionGameTestInstance extends GameTestInstance {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.TEST_FUNCTION).fieldOf("function").forGetter(FunctionGameTestInstance::function), TestData.CODEC.forGetter(GameTestInstance::info)).apply(i, FunctionGameTestInstance::new));
   private final ResourceKey function;

   public FunctionGameTestInstance(final ResourceKey function, final TestData info) {
      super(info);
      this.function = function;
   }

   public void run(final GameTestHelper helper) {
      ((Consumer)helper.getLevel().registryAccess().get(this.function).map(Holder.Reference::value).orElseThrow(() -> new IllegalStateException("Trying to access missing test function: " + String.valueOf(this.function.identifier())))).accept(helper);
   }

   private ResourceKey function() {
      return this.function;
   }

   public MapCodec codec() {
      return CODEC;
   }

   protected MutableComponent typeDescription() {
      return Component.translatable("test_instance.type.function");
   }

   public Component describe() {
      return this.describeType().append(this.descriptionRow("test_instance.description.function", this.function.identifier().toString())).append(this.describeInfo());
   }
}
