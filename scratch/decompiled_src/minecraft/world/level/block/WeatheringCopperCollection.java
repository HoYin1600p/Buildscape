package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

public record WeatheringCopperCollection(WeatheringCopperCollection.ByState weathering, WeatheringCopperCollection.ByState waxed) {
   public static final WeatheringCopperCollection.ByState STATES = new WeatheringCopperCollection.ByState(WeatheringCopper.WeatherState.UNAFFECTED, WeatheringCopper.WeatherState.EXPOSED, WeatheringCopper.WeatherState.WEATHERED, WeatheringCopper.WeatherState.OXIDIZED);
   public static final WeatheringCopperCollection PREFIXES = new WeatheringCopperCollection(new WeatheringCopperCollection.ByState("", "exposed_", "weathered_", "oxidized_"), new WeatheringCopperCollection.ByState("waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"));

   public static WeatheringCopperCollection prefixWithState(final WeatheringCopperCollection ids) {
      return zipMap(PREFIXES, ids, (state, id) -> state + id);
   }

   public static WeatheringCopperCollection create(final String name) {
      return same(WeatheringCopperCollection.ByState.create(name));
   }

   public static WeatheringCopperCollection same(final WeatheringCopperCollection.ByState byState) {
      return new WeatheringCopperCollection(byState, byState);
   }

   public static WeatheringCopperCollection registerBlocks(final WeatheringCopperCollection ids, final TriFunction register, final BiFunction waxedBlockFactory, final BiFunction weatheringFactory, final Function propertiesSupplier) {
      return ids.apply((weatheringIds) -> zipMap(STATES, weatheringIds, (state, id) -> (Block)register.apply(id, (Function)(p) -> (Block)weatheringFactory.apply(state, p), (BlockBehaviour.Properties)propertiesSupplier.apply(state))), (waxedIds) -> zipMap(STATES, waxedIds, (state, id) -> (Block)register.apply(id, (Function)(p) -> (Block)waxedBlockFactory.apply(state, p), (BlockBehaviour.Properties)propertiesSupplier.apply(state))));
   }

   public static WeatheringCopperCollection registerItems(final WeatheringCopperCollection ids, final WeatheringCopperCollection blocks, final BiFunction itemFactory) {
      return zipMap(ids, blocks, itemFactory);
   }

   public static WeatheringCopperCollection createFamily(final BiFunction waxedProvider, final BiFunction weatheringProvider) {
      return PREFIXES.apply((weatheringPrefixes) -> zipMap(weatheringPrefixes, STATES, weatheringProvider), (waxedPrefixes) -> zipMap(waxedPrefixes, STATES, waxedProvider));
   }

   public List asList() {
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(8);
      this.forEach(builder::add);
      return builder.build();
   }

   public void forEach(final Consumer consumer) {
      this.weathering.forEach(consumer);
      this.waxed.forEach(consumer);
   }

   public WeatheringCopperCollection map(final Function mapper) {
      return new WeatheringCopperCollection(this.weathering.map(mapper), this.waxed.map(mapper));
   }

   public WeatheringCopperCollection apply(final Function mapper) {
      return this.apply(mapper, mapper);
   }

   public WeatheringCopperCollection apply(final Function weatheringMapper, final Function waxedMapper) {
      return new WeatheringCopperCollection((WeatheringCopperCollection.ByState)weatheringMapper.apply(this.weathering), (WeatheringCopperCollection.ByState)waxedMapper.apply(this.waxed));
   }

   public static void zipApply(final WeatheringCopperCollection first, final WeatheringCopperCollection second, final BiConsumer consumer) {
      zipApply(first.weathering, second.weathering, consumer);
      zipApply(first.waxed, second.waxed, consumer);
   }

   public static WeatheringCopperCollection zipMap(final WeatheringCopperCollection first, final WeatheringCopperCollection second, final BiFunction operation) {
      return new WeatheringCopperCollection(zipMap(first.weathering, second.weathering, operation), zipMap(first.waxed, second.waxed, operation));
   }

   public void zipUnwaxedWaxed(final BiConsumer consumer) {
      zipApply(this.weathering, this.waxed, consumer);
   }

   public static void zipApply(final WeatheringCopperCollection.ByState first, final WeatheringCopperCollection.ByState second, final BiConsumer consumer) {
      consumer.accept(first.unaffected, second.unaffected);
      consumer.accept(first.exposed, second.exposed);
      consumer.accept(first.weathered, second.weathered);
      consumer.accept(first.oxidized, second.oxidized);
   }

   public static WeatheringCopperCollection.ByState zipMap(final WeatheringCopperCollection.ByState first, final WeatheringCopperCollection.ByState second, final BiFunction operation) {
      return new WeatheringCopperCollection.ByState(operation.apply(first.unaffected, second.unaffected), operation.apply(first.exposed, second.exposed), operation.apply(first.weathered, second.weathered), operation.apply(first.oxidized, second.oxidized));
   }

   public static record ByState(Object unaffected, Object exposed, Object weathered, Object oxidized) {
      public static WeatheringCopperCollection.ByState create(final Object value) {
         return new WeatheringCopperCollection.ByState(value, value, value, value);
      }

      public WeatheringCopperCollection.ByState map(final Function mapper) {
         return new WeatheringCopperCollection.ByState(mapper.apply(this.unaffected), mapper.apply(this.exposed), mapper.apply(this.weathered), mapper.apply(this.oxidized));
      }

      public Object pick(final WeatheringCopper.WeatherState state) {
         Object var10000;
         switch (state) {
            case UNAFFECTED:
               var10000 = this.unaffected;
               break;
            case EXPOSED:
               var10000 = this.exposed;
               break;
            case WEATHERED:
               var10000 = this.weathered;
               break;
            case OXIDIZED:
               var10000 = this.oxidized;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void forEach(final Consumer consumer) {
         consumer.accept(this.unaffected);
         consumer.accept(this.exposed);
         consumer.accept(this.weathered);
         consumer.accept(this.oxidized);
      }

      public void progressMapping(final BiConsumer consumer) {
         consumer.accept(this.unaffected, this.exposed);
         consumer.accept(this.exposed, this.weathered);
         consumer.accept(this.weathered, this.oxidized);
      }
   }
}
