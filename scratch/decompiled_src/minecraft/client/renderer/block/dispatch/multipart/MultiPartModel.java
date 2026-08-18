package net.minecraft.client.renderer.block.dispatch.multipart;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class MultiPartModel implements BlockStateModel {
   private final MultiPartModel.SharedBakedState shared;
   private final BlockState blockState;
   private @Nullable List models;

   private MultiPartModel(final MultiPartModel.SharedBakedState shared, final BlockState blockState) {
      this.shared = shared;
      this.blockState = blockState;
   }

   public Material.Baked particleMaterial() {
      return this.shared.particleMaterial;
   }

   public @BakedQuad.MaterialFlags int materialFlags() {
      return this.shared.materialFlags;
   }

   public void collectParts(final RandomSource random, final List output) {
      if (this.models == null) {
         this.models = this.shared.selectModels(this.blockState);
      }

      long seed = random.nextLong();

      for(BlockStateModel model : this.models) {
         random.setSeed(seed);
         model.collectParts(random, output);
      }

   }

   public static record Selector(Predicate condition, Object model) {
      public MultiPartModel.Selector with(final Object newModel) {
         return new MultiPartModel.Selector(this.condition, newModel);
      }
   }

   private static final class SharedBakedState {
      private final List selectors;
      private final Material.Baked particleMaterial;
      private final @BakedQuad.MaterialFlags int materialFlags;
      private final Map subsets = new ConcurrentHashMap();

      private static BlockStateModel getFirstModel(final List selectors) {
         if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
         } else {
            return (BlockStateModel)((MultiPartModel.Selector)selectors.getFirst()).model();
         }
      }

      private static @BakedQuad.MaterialFlags int computeMaterialFlags(final List selectors) {
         int flags = 0;

         for(MultiPartModel.Selector selector : selectors) {
            flags |= ((BlockStateModel)selector.model).materialFlags();
         }

         return flags;
      }

      public SharedBakedState(final List selectors) {
         this.selectors = selectors;
         BlockStateModel firstModel = getFirstModel(selectors);
         this.particleMaterial = firstModel.particleMaterial();
         this.materialFlags = computeMaterialFlags(selectors);
      }

      public List selectModels(final BlockState state) {
         BitSet selectedModels = new BitSet();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((MultiPartModel.Selector)this.selectors.get(i)).condition.test(state)) {
               selectedModels.set(i);
            }
         }

         return (List)this.subsets.computeIfAbsent(selectedModels, (selected) -> {
            ImmutableList.Builder result = ImmutableList.builder();

            for(int i = 0; i < this.selectors.size(); ++i) {
               if (selected.get(i)) {
                  result.add((BlockStateModel)((MultiPartModel.Selector)this.selectors.get(i)).model);
               }
            }

            return result.build();
         });
      }
   }

   public static class Unbaked implements BlockStateModel.UnbakedRoot {
      private final List selectors;
      private final ModelBaker.SharedOperationKey sharedStateKey = new ModelBaker.SharedOperationKey() {
         {
            Objects.requireNonNull(Unbaked.this);
         }

         public MultiPartModel.SharedBakedState compute(final ModelBaker modelBakery) {
            ImmutableList.Builder selectors = ImmutableList.builderWithExpectedSize(Unbaked.this.selectors.size());

            for(MultiPartModel.Selector selector : Unbaked.this.selectors) {
               selectors.add(selector.with(((BlockStateModel.Unbaked)selector.model).bake(modelBakery)));
            }

            return new MultiPartModel.SharedBakedState(selectors.build());
         }
      };

      public Unbaked(final List selectors) {
         this.selectors = selectors;
      }

      public Object visualEqualityGroup(final BlockState blockState) {
         IntList triggeredSelectors = new IntArrayList();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((MultiPartModel.Selector)this.selectors.get(i)).condition.test(blockState)) {
               triggeredSelectors.add(i);
            }
         }

         record Key(MultiPartModel.Unbaked model, IntList selectors) {
         }

         return new Key(this, triggeredSelectors);
      }

      public void resolveDependencies(final ResolvableModel.Resolver resolver) {
         this.selectors.forEach((s) -> ((BlockStateModel.Unbaked)s.model).resolveDependencies(resolver));
      }

      public BlockStateModel bake(final BlockState blockState, final ModelBaker modelBakery) {
         MultiPartModel.SharedBakedState shared = (MultiPartModel.SharedBakedState)modelBakery.compute(this.sharedStateKey);
         return new MultiPartModel(shared, blockState);
      }
   }
}
