package net.minecraft.world.level.block.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public abstract class StateHolder {
   private static final int VALUE_NOT_FOUND = -1;
   public static final String ID_TAG = "id";
   public static final String PROPERTIES_TAG = "properties";
   protected final Object owner;
   private final Property[] propertyKeys;
   private final Comparable[] propertyValues;
   private Object[][] neighbors;

   protected StateHolder(final Object owner, final Property[] propertyKeys, final Comparable[] propertyValues) {
      assert propertyKeys.length == propertyValues.length;

      this.owner = owner;
      this.propertyKeys = propertyKeys;
      this.propertyValues = propertyValues;
   }

   public Object cycle(final Property property) {
      return this.setValue(property, (Comparable)findNextInCollection(property.getPossibleValues(), this.getValue(property)));
   }

   protected static Object findNextInCollection(final List values, final Object current) {
      int nextIndex = values.indexOf(current) + 1;
      return nextIndex == values.size() ? values.getFirst() : values.get(nextIndex);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.owner);
      if (!this.isSingletonState()) {
         builder.append('[');
         builder.append((String)this.getValues().map(Property.Value::toString).collect(Collectors.joining(",")));
         builder.append(']');
      }

      return builder.toString();
   }

   public final boolean equals(final Object obj) {
      return super.equals(obj);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public Collection getProperties() {
      return List.of(this.propertyKeys);
   }

   private int valueIndex(final Property property) {
      for(int i = 0; i < this.propertyKeys.length; ++i) {
         if (this.propertyKeys[i] == property) {
            return i;
         }
      }

      return -1;
   }

   public boolean hasProperty(final Property property) {
      return this.valueIndex(property) != -1;
   }

   private @Nullable Comparable getNullableValue(final Property property) {
      int index = this.valueIndex(property);
      return index == -1 ? null : (Comparable)property.getValueClass().cast(this.propertyValues[index]);
   }

   public Comparable getValue(final Property property) {
      Comparable value = (T)this.getNullableValue(property);
      if (value == null) {
         throw new IllegalArgumentException("Cannot get property " + String.valueOf(property) + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return value;
      }
   }

   public Optional getOptionalValue(final Property property) {
      return Optional.ofNullable(this.getNullableValue(property));
   }

   public Comparable getValueOrElse(final Property property, final Comparable defaultValue) {
      return (Comparable)Objects.requireNonNullElse(this.getNullableValue(property), defaultValue);
   }

   public Object setValue(final Property property, final Comparable value) {
      int index = this.valueIndex(property);
      if (index == -1) {
         throw new IllegalArgumentException("Cannot set property " + String.valueOf(property) + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return this.setValueInternal(property, index, value);
      }
   }

   public Object trySetValue(final Property property, final Comparable value) {
      int index = this.valueIndex(property);
      return index == -1 ? this : this.setValueInternal(property, index, value);
   }

   private Object setValueInternal(final Property property, final int propertyIndex, final Comparable value) {
      int valueIndex = property.getInternalIndex(value);
      if (valueIndex < 0) {
         throw new IllegalArgumentException("Cannot set property " + String.valueOf(property) + " to " + String.valueOf(value) + " on " + String.valueOf(this.owner) + ", it is not an allowed value");
      } else {
         return this.neighbors[propertyIndex][valueIndex];
      }
   }

   void initializeNeighbors(final Object[][] neighbors) {
      if (this.neighbors != null) {
         throw new IllegalStateException();
      } else {
         this.neighbors = neighbors;
      }
   }

   public boolean isSingletonState() {
      return this.propertyKeys.length == 0;
   }

   public Stream getValues() {
      int length = this.propertyKeys.length;
      return length == 0 ? Stream.empty() : IntStream.range(0, length).mapToObj((i) -> createValue(this.propertyKeys[i], this.propertyValues[i]));
   }

   private static Property.Value createValue(final Property propertyKey, final Comparable propertyValue) {
      return new Property.Value(propertyKey, propertyValue);
   }

   protected static Codec codec(final Codec ownerCodec, final Function defaultState, final Function stateDefinition) {
      return ownerCodec.dispatch("id", (s) -> s.owner, (o) -> {
         StateDefinition definition = (StateDefinition)stateDefinition.apply(o);
         StateHolder defaultValue = (S)((StateHolder)defaultState.apply(o));
         return definition.isSingletonState() ? MapCodec.unit(defaultValue) : definition.propertiesCodec().codec().lenientOptionalFieldOf("properties").xmap((oo) -> (StateHolder)oo.orElse(defaultValue), Optional::of);
      });
   }
}
