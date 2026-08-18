package net.minecraft.client.renderer.block.dispatch.multipart;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.StateDefinition;

public record CombinedCondition(CombinedCondition.Operation operation, List terms) implements Condition {
   public Predicate instantiate(final StateDefinition definition) {
      return this.operation.apply(Lists.transform(this.terms, (c) -> c.instantiate(definition)));
   }

   public static enum Operation implements StringRepresentable {
      AND("AND") {
         public Predicate apply(final List terms) {
            return Util.allOf(terms);
         }
      },
      OR("OR") {
         public Predicate apply(final List terms) {
            return Util.anyOf(terms);
         }
      };

      public static final Codec CODEC = StringRepresentable.fromEnum(CombinedCondition.Operation::values);
      private final String name;

      private Operation(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract Predicate apply(List terms);

      // $FF: synthetic method
      private static CombinedCondition.Operation[] $values() {
         return new CombinedCondition.Operation[]{AND, OR};
      }
   }
}
