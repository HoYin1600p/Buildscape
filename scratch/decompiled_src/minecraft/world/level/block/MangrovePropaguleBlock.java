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
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MangrovePropaguleBlock extends SaplingBlock implements SimpleWaterloggedBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
   public static final int MAX_AGE = 4;
   private static final int[] SHAPE_MIN_Y = new int[]{13, 10, 7, 3, 0};
   private static final VoxelShape[] SHAPE_PER_AGE = Block.boxes(4, (age) -> Block.column(2.0D, (double)SHAPE_MIN_Y[age], 16.0D));
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty HANGING = BlockStateProperties.HANGING;

   public MangrovePropaguleBlock(final TreeGrower treeGrower, final BlockBehaviour.Properties properties) {
      super(treeGrower, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, Integer.valueOf(0))).setValue(AGE, Integer.valueOf(0))).setValue(WATERLOGGED, Boolean.valueOf(false))).setValue(HANGING, Boolean.valueOf(false)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(STAGE).add(AGE).add(WATERLOGGED).add(HANGING);
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.is(BlockTags.SUPPORTS_MANGROVE_PROPAGULE);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
      return (BlockState)((BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, Boolean.valueOf(isWaterSource))).setValue(AGE, Integer.valueOf(4));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      int age = state.getValue(HANGING) ? state.getValue(AGE) : 4;
      return SHAPE_PER_AGE[age].move(state.getOffset(pos));
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return isHanging(state) ? level.getBlockState(pos.above()).is(BlockTags.SUPPORTS_HANGING_MANGROVE_PROPAGULE) : super.canSurvive(state, level, pos);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return directionToNeighbour == Direction.UP && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected FluidState getFluidState(final BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!isHanging(state)) {
         if (random.nextInt(7) == 0) {
            this.advanceTree(level, pos, state, random);
         }

      } else {
         if (!isFullyGrown(state)) {
            level.setBlock(pos, (BlockState)state.cycle(AGE), 2);
         }

      }
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return !isHanging(state) || !isFullyGrown(state);
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return isHanging(state) ? !isFullyGrown(state) : super.isBonemealSuccess(level, random, pos, state, source);
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      if (isHanging(state) && !isFullyGrown(state)) {
         level.setBlock(pos, (BlockState)state.cycle(AGE), 2);
      } else {
         super.performBonemeal(level, random, pos, state, source);
      }

   }

   private static boolean isHanging(final BlockState state) {
      return state.getValue(HANGING);
   }

   private static boolean isFullyGrown(final BlockState state) {
      return state.getValue(AGE) == 4;
   }

   public static BlockState createNewHangingPropagule() {
      return createNewHangingPropagule(0);
   }

   public static BlockState createNewHangingPropagule(final int age) {
      return (BlockState)((BlockState)Blocks.MANGROVE_PROPAGULE.defaultBlockState().setValue(HANGING, Boolean.valueOf(true))).setValue(AGE, Integer.valueOf(age));
   }
}
