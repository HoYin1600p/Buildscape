package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class SeaPickleBlock extends VegetationBlock implements SimpleWaterloggedBlock, BonemealableBlock {
   public static final int MAX_PICKLES = 4;
   public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape SHAPE_ONE = Block.column(4.0D, 0.0D, 6.0D);
   private static final VoxelShape SHAPE_TWO = Block.column(10.0D, 0.0D, 6.0D);
   private static final VoxelShape SHAPE_THREE = Block.column(12.0D, 0.0D, 6.0D);
   private static final VoxelShape SHAPE_FOUR = Block.column(12.0D, 0.0D, 7.0D);

   protected SeaPickleBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PICKLES, Integer.valueOf(1))).setValue(WATERLOGGED, Boolean.valueOf(true)));
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = context.getLevel().getBlockState(context.getClickedPos());
      if (state.is(this)) {
         return (BlockState)state.setValue(PICKLES, Integer.valueOf(Math.min(4, state.getValue(PICKLES) + 1)));
      } else {
         FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
         boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
         return (BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(isWaterSource));
      }
   }

   public static boolean isDead(final BlockState state) {
      return !state.getValue(WATERLOGGED);
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return !state.getCollisionShape(level, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(level, pos, Direction.UP);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos belowPos = pos.below();
      return this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      return !context.isSecondaryUseActive() && context.getItemInHand().is(this.asItem()) && state.getValue(PICKLES) < 4 ? true : super.canBeReplaced(state, context);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape var10000;
      switch (state.getValue(PICKLES)) {
         case 2:
            var10000 = SHAPE_TWO;
            break;
         case 3:
            var10000 = SHAPE_THREE;
            break;
         case 4:
            var10000 = SHAPE_FOUR;
            break;
         default:
            var10000 = SHAPE_ONE;
      }

      return var10000;
   }

   protected FluidState getFluidState(final BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(PICKLES, WATERLOGGED);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return !isDead(state) && level.getBlockState(pos.below()).is(BlockTags.CORAL_BLOCKS);
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      int span = 5;
      int zSpan = 1;
      int height = 2;
      int count = 0;
      int xStart = pos.getX() - 2;
      int zOffSet = 0;

      for(int x = 0; x < 5; ++x) {
         for(int z = 0; z < zSpan; ++z) {
            int endY = 2 + pos.getY() - 1;

            for(int startY = endY - 2; startY < endY; ++startY) {
               BlockPos position = new BlockPos(xStart + x, startY, pos.getZ() - zOffSet + z);
               if (!position.equals(pos) && random.nextInt(6) == 0 && level.getBlockState(position).is(Blocks.WATER)) {
                  BlockState belowState = level.getBlockState(position.below());
                  if (belowState.is(BlockTags.CORAL_BLOCKS)) {
                     level.setBlockAndUpdate(position, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(PICKLES, Integer.valueOf(random.nextInt(4) + 1)));
                  }
               }
            }
         }

         if (count < 2) {
            zSpan += 2;
            ++zOffSet;
         } else {
            zSpan -= 2;
            --zOffSet;
         }

         ++count;
      }

      level.setBlock(pos, (BlockState)state.setValue(PICKLES, Integer.valueOf(4)), 2);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }
}
