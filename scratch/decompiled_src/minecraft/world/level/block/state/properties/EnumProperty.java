package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringRepresentable;

public final class EnumProperty extends Property {
   private final List values;
   private final Map names;
   private final int[] ordinalToIndex;

   private EnumProperty(final String name, final Class clazz, final List values) {
      super(name, clazz);
      if (values.isEmpty()) {
         throw new IllegalArgumentException("Trying to make empty EnumProperty '" + name + "'");
      } else {
         this.values = List.copyOf(values);
         Enum[] allEnumValues = (T[])((Enum[])clazz.getEnumConstants());
         this.ordinalToIndex = new int[allEnumValues.length];

         for(Enum value : allEnumValues) {
            this.ordinalToIndex[value.ordinal()] = values.indexOf(value);
         }

         ImmutableMap.Builder names = ImmutableMap.builder();

         for(Enum value : values) {
            String key = ((StringRepresentable)value).getSerializedName();
            names.put(key, value);
         }

         this.names = names.buildOrThrow();
      }
   }

   public List getPossibleValues() {
      return this.values;
   }

   public Optional getValue(final String name) {
      return Optional.ofNullable((Enum)this.names.get(name));
   }

   public String getName(final Enum value) {
      return ((StringRepresentable)value).getSerializedName();
   }

   public int getInternalIndex(final Enum value) {
      return this.ordinalToIndex[value.ordinal()];
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         if (o instanceof EnumProperty) {
            EnumProperty that = (EnumProperty)o;
            if (super.equals(o)) {
               return this.values.equals(that.values);
            }
         }

         return false;
      }
   }

   public int generateHashCode() {
      int result = super.generateHashCode();
      return 31 * result + this.values.hashCode();
   }

   public static EnumProperty create(final String name, final Class clazz) {
      return create(name, clazz, (Predicate)((t) -> true));
   }

   public static EnumProperty create(final String name, final Class clazz, final Predicate filter) {
      return create(name, clazz, (List)Arrays.stream((Enum[])clazz.getEnumConstants()).filter(filter).collect(Collectors.toList()));
   }

   @SafeVarargs
   public static EnumProperty create(final String name, final Class clazz, final Enum... values) {
      return create(name, clazz, List.of(values));
   }

   public static EnumProperty create(final String name, final Class clazz, final List values) {
      return new EnumProperty(name, clazz, values);
   }
}
