package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class StandingSignBlock extends SignBlock implements PlainSignBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

   public StandingSignBlock(final WoodType type, final BlockBehaviour.Properties properties) {
      super(type, properties.sound(type.soundType()));
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, Integer.valueOf(8))).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).isSolid();
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(context.getRotation() + 180.0F)))).setValue(WATERLOGGED, Boolean.valueOf(replacedFluidState.is(Fluids.WATER)));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.DOWN && !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public float getYRotationDegrees(final BlockState state) {
      return RotationSegment.convertToDegrees(state.getValue(ROTATION));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(ROTATION, Integer.valueOf(rotation.rotate(state.getValue(ROTATION), 16)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(ROTATION, Integer.valueOf(mirror.mirror(state.getValue(ROTATION), 16)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(ROTATION, WATERLOGGED);
   }

   public PlainSignBlock.Attachment attachmentPoint(final BlockState state) {
      return PlainSignBlock.Attachment.GROUND;
   }
}
