package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

public class StateDefinition {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private static final Comparable[] EMPTY_VALUES = new Comparable[0];
   private static final Property[] EMPTY_KEYS = new Property[0];
   private static final StateHolder[][] EMPTY_NEIGHBORS = new StateHolder[0][];
   private final Object owner;
   private final ImmutableSortedMap propertiesByName;
   private final ImmutableList states;
   private final MapCodec propertiesCodec;

   protected StateDefinition(final Function defaultState, final Object owner, final StateDefinition.Factory factory, final Map properties) {
      this.owner = owner;
      int propertyCount = properties.size();
      if (propertyCount == 0) {
         this.propertiesByName = ImmutableSortedMap.of();
         this.propertiesCodec = createCodec(owner, defaultState, this.propertiesByName);
         this.states = createSingletonState(owner, factory);
      } else {
         this.propertiesByName = ImmutableSortedMap.copyOf(properties);
         this.propertiesCodec = createCodec(owner, defaultState, this.propertiesByName);
         if (propertyCount == 1) {
            this.states = createSinglePropertyStates(owner, factory, this.propertiesByName);
         } else {
            this.states = createMultiPropertyStates(owner, factory, this.propertiesByName);
         }
      }

   }

   private static MapCodec createCodec(final Object owner, final Function defaultState, final Map propertiesByName) {
      Supplier defaultSupplier = () -> (StateHolder)defaultState.apply(owner);
      MapCodec codec = MapCodec.unit(defaultSupplier);

      for(Map.Entry entry : propertiesByName.entrySet()) {
         codec = appendPropertyCodec(codec, defaultSupplier, (String)entry.getKey(), (Property)entry.getValue());
      }

      return codec;
   }

   private static ImmutableList createSingletonState(final Object owner, final StateDefinition.Factory factory) {
      StateHolder singletonState = (S)((StateHolder)factory.create(owner, EMPTY_KEYS, EMPTY_VALUES));
      singletonState.initializeNeighbors(emptyNeighbors());
      return ImmutableList.of(singletonState);
   }

   private static ImmutableList createSinglePropertyStates(final Object owner, final StateDefinition.Factory factory, final Map propertiesByName) {
      return createSinglePropertyStates(owner, factory, (Property)Iterables.getOnlyElement(propertiesByName.values()));
   }

   private static ImmutableList createSinglePropertyStates(final Object owner, final StateDefinition.Factory factory, final Property property) {
      Property[] propertyKeys = new Property[]{property};
      List propertyValues = property.getPossibleValues();
      int valueCount = propertyValues.size();
      ImmutableList.Builder states = ImmutableList.builderWithExpectedSize(valueCount);
      StateHolder[] propertyNeighbours = (S[])(new StateHolder[valueCount]);
      StateHolder[][] neighbours = (S[][])(new StateHolder[][]{propertyNeighbours});

      for(int i = 0; i < valueCount; ++i) {
         Comparable propertyValue = (T)((Comparable)propertyValues.get(i));

         assert property.getInternalIndex(propertyValue) == i;

         StateHolder blockState = (S)((StateHolder)factory.create(owner, propertyKeys, new Comparable[]{propertyValue}));
         states.add(blockState);
         propertyNeighbours[i] = blockState;
         blockState.initializeNeighbors(neighbours);
      }

      return states.build();
   }

   private static ImmutableList createMultiPropertyStates(final Object owner, final StateDefinition.Factory factory, final Map propertiesByName) {
      Property[] propertyKeys = (Property[])propertiesByName.values().toArray(EMPTY_KEYS);
      List allPropertyValues = new ArrayList(propertyKeys.length);

      for(Property property : propertyKeys) {
         allPropertyValues.add(property.getPossibleValues());
      }

      List stateValues = Lists.cartesianProduct(allPropertyValues);
      Map statesByValues = new HashMap();
      ImmutableList.Builder states = ImmutableList.builderWithExpectedSize(stateValues.size());

      for(List values : stateValues) {
         List valuesCopy = List.copyOf(values);
         StateHolder blockState = (S)((StateHolder)factory.create(owner, propertyKeys, (Comparable[])valuesCopy.toArray(EMPTY_VALUES)));
         statesByValues.put(valuesCopy, blockState);
         states.add(blockState);
      }

      StateDefinition.StateCollection stateCollection = new StateDefinition.StateCollection(statesByValues, new HashMap());
      statesByValues.forEach((valuesx, state) -> state.initializeNeighbors(stateCollection.fillNeighborsForState(propertyKeys, valuesx)));
      return states.build();
   }

   private static StateHolder[][] emptyNeighbors() {
      return EMPTY_NEIGHBORS;
   }

   private static MapCodec appendPropertyCodec(final MapCodec codec, final Supplier defaultSupplier, final String name, final Property property) {
      return Codec.mapPair(codec, property.valueCodec().fieldOf(name).orElseGet((var0) -> {
      }, () -> property.value((StateHolder)defaultSupplier.get()))).xmap((pair) -> (StateHolder)((StateHolder)pair.getFirst()).setValue(property, ((Property.Value)pair.getSecond()).value()), (state) -> Pair.of(state, property.value(state)));
   }

   public ImmutableList getPossibleStates() {
      return this.states;
   }

   public StateHolder any() {
      return (StateHolder)this.states.getFirst();
   }

   public MapCodec propertiesCodec() {
      return this.propertiesCodec;
   }

   public Object getOwner() {
      return this.owner;
   }

   public Collection getProperties() {
      return this.propertiesByName.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
   }

   public @Nullable Property getProperty(final String name) {
      return (Property)this.propertiesByName.get(name);
   }

   public boolean isSingletonState() {
      return this.propertiesByName.isEmpty();
   }

   public static class Builder {
      private final Object owner;
      private final Map properties = Maps.newHashMap();

      public Builder(final Object owner) {
         this.owner = owner;
      }

      public StateDefinition.Builder add(final Property... properties) {
         for(Property property : properties) {
            this.validateProperty(property);
            this.properties.put(property.getName(), property);
         }

         return this;
      }

      private void validateProperty(final Property property) {
         String name = property.getName();
         if (!StateDefinition.NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.valueOf(this.owner) + " has invalidly named property: " + name);
         } else {
            Collection values = property.getPossibleValues();
            if (values.size() <= 1) {
               throw new IllegalArgumentException(String.valueOf(this.owner) + " attempted use property " + name + " with <= 1 possible values");
            } else {
               for(Comparable comparable : values) {
                  String valueName = property.getName(comparable);
                  if (!StateDefinition.NAME_PATTERN.matcher(valueName).matches()) {
                     throw new IllegalArgumentException(String.valueOf(this.owner) + " has property: " + name + " with invalidly named value: " + valueName);
                  }
               }

               if (this.properties.containsKey(name)) {
                  throw new IllegalArgumentException(String.valueOf(this.owner) + " has duplicate property: " + name);
               }
            }
         }
      }

      public StateDefinition create(final Function defaultState, final StateDefinition.Factory factory) {
         return new StateDefinition(defaultState, this.owner, factory, this.properties);
      }
   }

   public interface Factory {
      Object create(Object type, Property[] propertyKeys, Comparable[] propertyValues);
   }

   static record StateCollection(Map statesByValues, Map statesByPivotCache) {
      public StateHolder[][] fillNeighborsForState(final Property[] propertyKeys, final List propertyValues) {
         StateHolder[][] neighbors = (S[][])(new StateHolder[propertyKeys.length][]);
         List valuesKey = new ArrayList(propertyValues);

         for(int i = 0; i < propertyKeys.length; ++i) {
            neighbors[i] = (S[])this.fillStatesForPivot(valuesKey, propertyKeys[i], i);
         }

         return neighbors;
      }

      private StateHolder[] fillStatesForPivot(final List valuesKey, final Property pivot, final int pivotIndex) {
         Comparable ownPivotValue = (Comparable)valuesKey.set(pivotIndex, StateDefinition.StateCollection.Wildcard.INSTANCE);

         StateHolder[] neighbourStatesForPivot;
         try {
            StateHolder[] cachedResult = (S[])((StateHolder[])this.statesByPivotCache.get(valuesKey));
            if (cachedResult == null) {
               neighbourStatesForPivot = (S[])this.computeStatesForPivot(valuesKey, pivot, pivotIndex);
               valuesKey.set(pivotIndex, StateDefinition.StateCollection.Wildcard.INSTANCE);
               this.statesByPivotCache.put(List.copyOf(valuesKey), neighbourStatesForPivot);
               return neighbourStatesForPivot;
            }

            neighbourStatesForPivot = cachedResult;
         } finally {
            valuesKey.set(pivotIndex, ownPivotValue);
         }

         return neighbourStatesForPivot;
      }

      private StateHolder[] computeStatesForPivot(final List valuesKey, final Property pivot, final int pivotIndex) {
         List possiblePivotValues = pivot.getPossibleValues();
         int pivotValuesCount = possiblePivotValues.size();
         StateHolder[] result = (S[])(new StateHolder[pivotValuesCount]);

         for(int pivotValueIndex = 0; pivotValueIndex < pivotValuesCount; ++pivotValueIndex) {
            Comparable possiblePivotValue = (T)((Comparable)possiblePivotValues.get(pivotValueIndex));

            assert pivot.getInternalIndex(possiblePivotValue) == pivotValueIndex;

            valuesKey.set(pivotIndex, possiblePivotValue);
            StateHolder neighbourState = (S)((StateHolder)Objects.requireNonNull((StateHolder)this.statesByValues.get(valuesKey)));
            result[pivotValueIndex] = neighbourState;
         }

         return result;
      }

      private static enum Wildcard {
         INSTANCE;

         // $FF: synthetic method
         private static StateDefinition.StateCollection.Wildcard[] $values() {
            return new StateDefinition.StateCollection.Wildcard[]{INSTANCE};
         }
      }
   }
}
