package net.minecraft.client.data.models.blockstates;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.renderer.block.dispatch.multipart.Condition;
import net.minecraft.client.renderer.block.dispatch.multipart.KeyValueCondition;
import net.minecraft.world.level.block.state.properties.Property;

public class ConditionBuilder {
   private final ImmutableMap.Builder terms = ImmutableMap.builder();

   private void putValue(final Property property, final KeyValueCondition.Terms term) {
      this.terms.put(property.getName(), term);
   }

   public final ConditionBuilder term(final Property property, final Comparable value) {
      this.putValue(property, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(property.getName(value), false))));
      return this;
   }

   @SafeVarargs
   public final ConditionBuilder term(final Property property, final Comparable value, final Comparable... values) {
      List terms = Stream.concat(Stream.of(value), Stream.of(values)).map(property::getName).sorted().distinct().map((v) -> new KeyValueCondition.Term(v, false)).toList();
      this.putValue(property, new KeyValueCondition.Terms(terms));
      return this;
   }

   public final ConditionBuilder negatedTerm(final Property property, final Comparable value) {
      this.putValue(property, new KeyValueCondition.Terms(List.of(new KeyValueCondition.Term(property.getName(value), true))));
      return this;
   }

   public Condition build() {
      return new KeyValueCondition(this.terms.buildOrThrow());
   }
}
