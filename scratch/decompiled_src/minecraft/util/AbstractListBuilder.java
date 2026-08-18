package net.minecraft.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

public abstract class AbstractListBuilder implements ListBuilder {
   private final DynamicOps ops;
   protected DataResult builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

   protected AbstractListBuilder(final DynamicOps ops) {
      this.ops = ops;
   }

   public DynamicOps ops() {
      return this.ops;
   }

   protected abstract Object initBuilder();

   protected abstract Object append(Object builder, Object value);

   protected abstract DataResult build(Object builder, Object prefix);

   public ListBuilder add(final Object value) {
      this.builder = this.builder.map((b) -> this.append(b, value));
      return this;
   }

   public ListBuilder add(final DataResult value) {
      this.builder = this.builder.apply2stable(this::append, value);
      return this;
   }

   public ListBuilder withErrorsFrom(final DataResult result) {
      this.builder = this.builder.flatMap((r) -> result.map((v) -> r));
      return this;
   }

   public ListBuilder mapError(final UnaryOperator onError) {
      this.builder = this.builder.mapError(onError);
      return this;
   }

   public DataResult build(final Object prefix) {
      DataResult result = this.builder.flatMap((b) -> this.build(b, prefix));
      this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
      return result;
   }
}
