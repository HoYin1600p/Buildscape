package net.minecraft.client.renderer.block.dispatch;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.slf4j.Logger;

public record BlockStateModelDispatcher(Optional simpleModels, Optional multiPart) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateModelDispatcher.SimpleModelSelectors.CODEC.optionalFieldOf("variants").forGetter(BlockStateModelDispatcher::simpleModels), BlockStateModelDispatcher.MultiPartDefinition.CODEC.optionalFieldOf("multipart").forGetter(BlockStateModelDispatcher::multiPart)).apply(i, BlockStateModelDispatcher::new)).validate((o) -> o.simpleModels().isEmpty() && o.multiPart().isEmpty() ? DataResult.error(() -> "Neither 'variants' nor 'multipart' found") : DataResult.success(o));

   public Map instantiate(final StateDefinition stateDefinition, final Supplier source) {
      Map matchedStates = new IdentityHashMap();
      this.simpleModels.ifPresent((s) -> s.instantiate(stateDefinition, source, (state, model) -> {
            BlockStateModel.UnbakedRoot previousValue = (BlockStateModel.UnbakedRoot)matchedStates.put(state, model);
            if (previousValue != null) {
               throw new IllegalArgumentException("Overlapping definition on state: " + String.valueOf(state));
            }
         }));
      this.multiPart.ifPresent((m) -> {
         List possibleStates = stateDefinition.getPossibleStates();
         BlockStateModel.UnbakedRoot model = m.instantiate(stateDefinition);

         for(BlockState state : possibleStates) {
            matchedStates.putIfAbsent(state, model);
         }

      });
      return matchedStates;
   }

   public static record MultiPartDefinition(List selectors) {
      public static final Codec CODEC = ExtraCodecs.nonEmptyList(Selector.CODEC.listOf()).xmap(BlockStateModelDispatcher.MultiPartDefinition::new, BlockStateModelDispatcher.MultiPartDefinition::selectors);

      public MultiPartModel.Unbaked instantiate(final StateDefinition stateDefinition) {
         ImmutableList.Builder instantiatedSelectors = ImmutableList.builderWithExpectedSize(this.selectors.size());

         for(Selector selector : this.selectors) {
            instantiatedSelectors.add(new MultiPartModel.Selector(selector.instantiate(stateDefinition), selector.variant()));
         }

         return new MultiPartModel.Unbaked(instantiatedSelectors.build());
      }
   }

   public static record SimpleModelSelectors(Map models) {
      public static final Codec CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, BlockStateModel.Unbaked.CODEC)).xmap(BlockStateModelDispatcher.SimpleModelSelectors::new, BlockStateModelDispatcher.SimpleModelSelectors::models);

      public void instantiate(final StateDefinition stateDefinition, final Supplier source, final BiConsumer output) {
         this.models.forEach((selectorString, model) -> {
            try {
               Predicate selector = VariantSelector.predicate(stateDefinition, selectorString);
               BlockStateModel.UnbakedRoot wrapper = model.asRoot();
               Iterator i$ = stateDefinition.getPossibleStates().iterator();

               while(i$.hasNext()) {
                  BlockState state = (BlockState)i$.next();
                  if (selector.test(state)) {
                     output.accept(state, wrapper);
                  }
               }
            } catch (Exception var9) {
               BlockStateModelDispatcher.LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}'", source.get(), selectorString);
            }

         });
      }
   }
}
