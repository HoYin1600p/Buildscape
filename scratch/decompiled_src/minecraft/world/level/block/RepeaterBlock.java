package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock extends DiodeBlock {
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

   protected RepeaterBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(DELAY, Integer.valueOf(1))).setValue(LOCKED, Boolean.valueOf(false))).setValue(POWERED, Boolean.valueOf(false)));
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!player.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         level.setBlockAndUpdate(pos, (BlockState)state.cycle(DELAY));
         return InteractionResult.SUCCESS;
      }
   }

   protected int getDelay(final BlockState state) {
      return state.getValue(DELAY) * 2;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return (BlockState)state.setValue(LOCKED, Boolean.valueOf(this.isLocked(context.getLevel(), context.getClickedPos(), state)));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.DOWN && !this.canSurviveOn(level, neighbourPos, neighbourState)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return !level.isClientSide() && directionToNeighbour.getAxis() != ((Direction)state.getValue(FACING)).getAxis() ? (BlockState)state.setValue(LOCKED, Boolean.valueOf(this.isLocked(level, pos, state))) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   public boolean isLocked(final LevelReader level, final BlockPos pos, final BlockState state) {
      return this.getAlternateSignal(level, pos, state) > 0;
   }

   protected boolean sideInputDiodesOnly() {
      return true;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (state.getValue(POWERED)) {
         Direction direction = (Direction)state.getValue(FACING);
         double x = (double)pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
         double y = (double)pos.getY() + 0.4D + (random.nextDouble() - 0.5D) * 0.2D;
         double z = (double)pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
         float offset = -5.0F;
         if (random.nextBoolean()) {
            offset = (float)(state.getValue(DELAY) * 2 - 1);
         }

         offset /= 16.0F;
         double xo = (double)(offset * (float)direction.getStepX());
         double zo = (double)(offset * (float)direction.getStepZ());
         level.addParticle(DustParticleOptions.REDSTONE, x + xo, y, z + zo, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING, DELAY, LOCKED, POWERED);
   }
}
