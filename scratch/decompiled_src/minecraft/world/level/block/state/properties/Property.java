package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateHolder;
import org.jspecify.annotations.Nullable;

public abstract class Property {
   private final Class clazz;
   private final String name;
   private @Nullable Integer hashCode;
   private final Codec codec = Codec.STRING.comapFlatMap((namex) -> (DataResult)this.getValue(namex).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unable to read property: " + String.valueOf(this) + " with value: " + namex)), this::getName);
   private final Codec valueCodec = this.codec.xmap(this::value, Property.Value::value);

   protected Property(final String name, final Class clazz) {
      this.clazz = clazz;
      this.name = name;
   }

   public Property.Value value(final Comparable value) {
      return new Property.Value(this, value);
   }

   public Property.Value value(final StateHolder stateHolder) {
      return new Property.Value(this, stateHolder.getValue(this));
   }

   public Stream getAllValues() {
      return this.getPossibleValues().stream().map(this::value);
   }

   public Codec codec() {
      return this.codec;
   }

   public Codec valueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class getValueClass() {
      return this.clazz;
   }

   public abstract List getPossibleValues();

   public abstract String getName(final Comparable value);

   public abstract Optional getValue(final String name);

   public abstract int getInternalIndex(final Comparable value);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Property)) {
         return false;
      } else {
         Property that = (Property)o;
         return this.clazz.equals(that.clazz) && this.name.equals(that.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.generateHashCode();
      }

      return this.hashCode;
   }

   public int generateHashCode() {
      return 31 * this.clazz.hashCode() + this.name.hashCode();
   }

   public DataResult parseValue(final DynamicOps ops, final StateHolder state, final Object value) {
      DataResult parsed = this.codec.parse(ops, value);
      return parsed.map((v) -> (StateHolder)state.setValue(this, v)).setPartial(state);
   }

   public static record Value(Property property, Comparable value) {
      public Value {
         if (!property.getPossibleValues().contains(value)) {
            throw new IllegalArgumentException("Value " + String.valueOf(value) + " does not belong to property " + String.valueOf(property));
         }
      }

      public String toString() {
         return this.property.getName() + "=" + this.property.getName(this.value);
      }

      public String valueName() {
         return this.property.getName(this.value);
      }
   }
}
