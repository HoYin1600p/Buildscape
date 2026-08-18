package net.minecraft.client.data.models.blockstates;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch {
   private final Map values = new HashMap();

   protected void putValue(final PropertyValueList key, final Object variant) {
      Object previous = (V)this.values.put(key, variant);
      if (previous != null) {
         throw new IllegalStateException("Value " + String.valueOf(key) + " is already defined");
      }
   }

   Map getEntries() {
      this.verifyComplete();
      return Map.copyOf(this.values);
   }

   private void verifyComplete() {
      List properties = this.getDefinedProperties();
      Stream valuesToCover = Stream.of(PropertyValueList.EMPTY);

      for(Property property : properties) {
         valuesToCover = valuesToCover.flatMap((current) -> property.getAllValues().map(current::extend));
      }

      List undefinedCombinations = valuesToCover.filter((f) -> !this.values.containsKey(f)).toList();
      if (!undefinedCombinations.isEmpty()) {
         throw new IllegalStateException("Missing definition for properties: " + String.valueOf(undefinedCombinations));
      }
   }

   abstract List getDefinedProperties();

   public static PropertyDispatch.C1 initial(final Property property1) {
      return new PropertyDispatch.C1(property1);
   }

   public static PropertyDispatch.C2 initial(final Property property1, final Property property2) {
      return new PropertyDispatch.C2(property1, property2);
   }

   public static PropertyDispatch.C3 initial(final Property property1, final Property property2, final Property property3) {
      return new PropertyDispatch.C3(property1, property2, property3);
   }

   public static PropertyDispatch.C4 initial(final Property property1, final Property property2, final Property property3, final Property property4) {
      return new PropertyDispatch.C4(property1, property2, property3, property4);
   }

   public static PropertyDispatch.C5 initial(final Property property1, final Property property2, final Property property3, final Property property4, final Property property5) {
      return new PropertyDispatch.C5(property1, property2, property3, property4, property5);
   }

   public static PropertyDispatch.C1 modify(final Property property1) {
      return new PropertyDispatch.C1(property1);
   }

   public static PropertyDispatch.C2 modify(final Property property1, final Property property2) {
      return new PropertyDispatch.C2(property1, property2);
   }

   public static PropertyDispatch.C3 modify(final Property property1, final Property property2, final Property property3) {
      return new PropertyDispatch.C3(property1, property2, property3);
   }

   public static PropertyDispatch.C4 modify(final Property property1, final Property property2, final Property property3, final Property property4) {
      return new PropertyDispatch.C4(property1, property2, property3, property4);
   }

   public static PropertyDispatch.C5 modify(final Property property1, final Property property2, final Property property3, final Property property4, final Property property5) {
      return new PropertyDispatch.C5(property1, property2, property3, property4, property5);
   }

   public static class C1 extends PropertyDispatch {
      private final Property property1;

      private C1(final Property property1) {
         this.property1 = property1;
      }

      public List getDefinedProperties() {
         return List.of(this.property1);
      }

      public PropertyDispatch.C1 select(final Comparable value1, final Object variants) {
         PropertyValueList key = PropertyValueList.of(this.property1.value(value1));
         this.putValue(key, variants);
         return this;
      }

      public PropertyDispatch generate(final Function generator) {
         this.property1.getPossibleValues().forEach((value1) -> this.select(value1, generator.apply(value1)));
         return this;
      }
   }

   public static class C2 extends PropertyDispatch {
      private final Property property1;
      private final Property property2;

      private C2(final Property property1, final Property property2) {
         this.property1 = property1;
         this.property2 = property2;
      }

      public List getDefinedProperties() {
         return List.of(this.property1, this.property2);
      }

      public PropertyDispatch.C2 select(final Comparable value1, final Comparable value2, final Object variants) {
         PropertyValueList key = PropertyValueList.of(this.property1.value(value1), this.property2.value(value2));
         this.putValue(key, variants);
         return this;
      }

      public PropertyDispatch generate(final BiFunction generator) {
         this.property1.getPossibleValues().forEach((value1) -> this.property2.getPossibleValues().forEach((value2) -> this.select(value1, value2, generator.apply(value1, value2))));
         return this;
      }
   }

   public static class C3 extends PropertyDispatch {
      private final Property property1;
      private final Property property2;
      private final Property property3;

      private C3(final Property property1, final Property property2, final Property property3) {
         this.property1 = property1;
         this.property2 = property2;
         this.property3 = property3;
      }

      public List getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3);
      }

      public PropertyDispatch.C3 select(final Comparable value1, final Comparable value2, final Comparable value3, final Object variants) {
         PropertyValueList key = PropertyValueList.of(this.property1.value(value1), this.property2.value(value2), this.property3.value(value3));
         this.putValue(key, variants);
         return this;
      }

      public PropertyDispatch generate(final Function3 generator) {
         this.property1.getPossibleValues().forEach((value1) -> this.property2.getPossibleValues().forEach((value2) -> this.property3.getPossibleValues().forEach((value3) -> this.select(value1, value2, value3, generator.apply(value1, value2, value3)))));
         return this;
      }
   }

   public static class C4 extends PropertyDispatch {
      private final Property property1;
      private final Property property2;
      private final Property property3;
      private final Property property4;

      private C4(final Property property1, final Property property2, final Property property3, final Property property4) {
         this.property1 = property1;
         this.property2 = property2;
         this.property3 = property3;
         this.property4 = property4;
      }

      public List getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3, this.property4);
      }

      public PropertyDispatch.C4 select(final Comparable value1, final Comparable value2, final Comparable value3, final Comparable value4, final Object variants) {
         PropertyValueList key = PropertyValueList.of(this.property1.value(value1), this.property2.value(value2), this.property3.value(value3), this.property4.value(value4));
         this.putValue(key, variants);
         return this;
      }

      public PropertyDispatch generate(final Function4 generator) {
         this.property1.getPossibleValues().forEach((value1) -> this.property2.getPossibleValues().forEach((value2) -> this.property3.getPossibleValues().forEach((value3) -> this.property4.getPossibleValues().forEach((value4) -> this.select(value1, value2, value3, value4, generator.apply(value1, value2, value3, value4))))));
         return this;
      }
   }

   public static class C5 extends PropertyDispatch {
      private final Property property1;
      private final Property property2;
      private final Property property3;
      private final Property property4;
      private final Property property5;

      private C5(final Property property1, final Property property2, final Property property3, final Property property4, final Property property5) {
         this.property1 = property1;
         this.property2 = property2;
         this.property3 = property3;
         this.property4 = property4;
         this.property5 = property5;
      }

      public List getDefinedProperties() {
         return List.of(this.property1, this.property2, this.property3, this.property4, this.property5);
      }

      public PropertyDispatch.C5 select(final Comparable value1, final Comparable value2, final Comparable value3, final Comparable value4, final Comparable value5, final Object variants) {
         PropertyValueList key = PropertyValueList.of(this.property1.value(value1), this.property2.value(value2), this.property3.value(value3), this.property4.value(value4), this.property5.value(value5));
         this.putValue(key, variants);
         return this;
      }

      public PropertyDispatch generate(final Function5 generator) {
         this.property1.getPossibleValues().forEach((value1) -> this.property2.getPossibleValues().forEach((value2) -> this.property3.getPossibleValues().forEach((value3) -> this.property4.getPossibleValues().forEach((value4) -> this.property5.getPossibleValues().forEach((value5) -> this.select(value1, value2, value3, value4, value5, generator.apply(value1, value2, value3, value4, value5)))))));
         return this;
      }
   }
}
