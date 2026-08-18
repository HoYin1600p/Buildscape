package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class WallTorchBlock extends TorchBlock {
   public static final EnumProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map SHAPES = Shapes.rotateHorizontal(Block.boxZ(5.0D, 3.0D, 13.0D, 11.0D, 16.0D));

   protected WallTorchBlock(final SimpleParticleType flameParticle, final BlockBehaviour.Properties properties) {
      super(flameParticle, properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return getShape(state);
   }

   public static VoxelShape getShape(final BlockState state) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return canSurvive(level, pos, (Direction)state.getValue(FACING));
   }

   public static boolean canSurvive(final LevelReader level, final BlockPos pos, final Direction facing) {
      BlockPos relativePos = pos.relative(facing.getOpposite());
      BlockState relativeState = level.getBlockState(relativePos);
      return relativeState.isFaceSturdy(level, relativePos, facing);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = this.defaultBlockState();
      LevelReader level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction[] directions = context.getNearestLookingDirections();

      for(Direction direction : directions) {
         if (direction.getAxis().isHorizontal()) {
            Direction facing = direction.getOpposite();
            state = (BlockState)state.setValue(FACING, facing);
            if (state.canSurvive(level, pos)) {
               return state;
            }
         }
      }

      return null;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      Direction direction = (Direction)state.getValue(FACING);
      double x = (double)pos.getX() + 0.5D;
      double y = (double)pos.getY() + 0.7D;
      double z = (double)pos.getZ() + 0.5D;
      double h = 0.22D;
      double r = 0.27D;
      Direction opposite = direction.getOpposite();
      level.addParticle(ParticleTypes.SMOKE, x + 0.27D * (double)opposite.getStepX(), y + 0.22D, z + 0.27D * (double)opposite.getStepZ(), 0.0D, 0.0D, 0.0D);
      level.addParticle(this.flameParticle, x + 0.27D * (double)opposite.getStepX(), y + 0.22D, z + 0.27D * (double)opposite.getStepZ(), 0.0D, 0.0D, 0.0D);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING);
   }
}
