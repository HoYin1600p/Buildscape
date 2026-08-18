package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final EnumProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   protected RedstoneWallTorchBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, Boolean.valueOf(true)));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return WallTorchBlock.getShape(state);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return WallTorchBlock.canSurvive(level, pos, (Direction)state.getValue(FACING));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = Blocks.WALL_TORCH.getStateForPlacement(context);
      return state == null ? null : (BlockState)this.defaultBlockState().setValue(FACING, (Direction)state.getValue(FACING));
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(LIT)) {
         Direction opposite = ((Direction)state.getValue(FACING)).getOpposite();
         double r = 0.27D;
         double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)opposite.getStepX();
         double y = (double)pos.getY() + 0.7D + (random.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)opposite.getStepZ();
         level.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean hasNeighborSignal(final Level level, final BlockPos pos, final BlockState state) {
      Direction opposite = ((Direction)state.getValue(FACING)).getOpposite();
      return level.hasSignal(pos.relative(opposite), opposite);
   }

   protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return state.getValue(FACING) != direction ? this.ownSignal(state, level, pos) : 0;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING, LIT);
   }

   protected @Nullable Orientation randomOrientation(final Level level, final BlockState state) {
      return ExperimentalRedstoneUtils.initialOrientation(level, ((Direction)state.getValue(FACING)).getOpposite(), Direction.UP);
   }
}
