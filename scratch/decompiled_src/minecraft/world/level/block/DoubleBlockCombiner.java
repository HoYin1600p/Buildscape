package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DoubleBlockCombiner {
   public static DoubleBlockCombiner.NeighborCombineResult combineWithNeigbour(final BlockEntityType entityType, final Function typeResolver, final Function connectionResolver, final Property facingProperty, final BlockState state, final LevelAccessor level, final BlockPos pos, final BiPredicate blockedChecker) {
      BlockEntity blockEntity = (S)entityType.getBlockEntity(level, pos);
      if (blockEntity == null) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else if (blockedChecker.test(level, pos)) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else {
         DoubleBlockCombiner.BlockType type = (DoubleBlockCombiner.BlockType)typeResolver.apply(state);
         boolean single = type == DoubleBlockCombiner.BlockType.SINGLE;
         boolean isFirst = type == DoubleBlockCombiner.BlockType.FIRST;
         if (single) {
            return new DoubleBlockCombiner.NeighborCombineResult.Single(blockEntity);
         } else {
            BlockPos neighborPos = pos.relative((Direction)connectionResolver.apply(state));
            BlockState neighbourState = level.getBlockState(neighborPos);
            if (neighbourState.is(state.getBlock())) {
               DoubleBlockCombiner.BlockType neighbourType = (DoubleBlockCombiner.BlockType)typeResolver.apply(neighbourState);
               if (neighbourType != DoubleBlockCombiner.BlockType.SINGLE && type != neighbourType && neighbourState.getValue(facingProperty) == state.getValue(facingProperty)) {
                  if (blockedChecker.test(level, neighborPos)) {
                     return DoubleBlockCombiner.Combiner::acceptNone;
                  }

                  BlockEntity neighbour = (S)entityType.getBlockEntity(level, neighborPos);
                  if (neighbour != null) {
                     BlockEntity first = (S)(isFirst ? blockEntity : neighbour);
                     BlockEntity second = (S)(isFirst ? neighbour : blockEntity);
                     return new DoubleBlockCombiner.NeighborCombineResult.Double(first, second);
                  }
               }
            }

            return new DoubleBlockCombiner.NeighborCombineResult.Single(blockEntity);
         }
      }
   }

   public static enum BlockType {
      SINGLE,
      FIRST,
      SECOND;

      // $FF: synthetic method
      private static DoubleBlockCombiner.BlockType[] $values() {
         return new DoubleBlockCombiner.BlockType[]{SINGLE, FIRST, SECOND};
      }
   }

   public interface Combiner {
      Object acceptDouble(Object first, Object second);

      Object acceptSingle(Object single);

      Object acceptNone();
   }

   public interface NeighborCombineResult {
      Object apply(DoubleBlockCombiner.Combiner callback);

      public static final class Double implements DoubleBlockCombiner.NeighborCombineResult {
         private final Object first;
         private final Object second;

         public Double(final Object first, final Object second) {
            this.first = first;
            this.second = second;
         }

         public Object apply(final DoubleBlockCombiner.Combiner callback) {
            return callback.acceptDouble(this.first, this.second);
         }
      }

      public static final class Single implements DoubleBlockCombiner.NeighborCombineResult {
         private final Object single;

         public Single(final Object single) {
            this.single = single;
         }

         public Object apply(final DoubleBlockCombiner.Combiner callback) {
            return callback.acceptSingle(this.single);
         }
      }
   }
}
