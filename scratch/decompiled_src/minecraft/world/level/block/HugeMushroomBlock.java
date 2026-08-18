package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class HugeMushroomBlock extends Block {
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   public static final BooleanProperty DOWN = PipeBlock.DOWN;
   private static final Map PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

   public HugeMushroomBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(true))).setValue(EAST, Boolean.valueOf(true))).setValue(SOUTH, Boolean.valueOf(true))).setValue(WEST, Boolean.valueOf(true))).setValue(UP, Boolean.valueOf(true))).setValue(DOWN, Boolean.valueOf(true)));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockGetter level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(DOWN, Boolean.valueOf(!level.getBlockState(pos.below()).is(this)))).setValue(UP, Boolean.valueOf(!level.getBlockState(pos.above()).is(this)))).setValue(NORTH, Boolean.valueOf(!level.getBlockState(pos.north()).is(this)))).setValue(EAST, Boolean.valueOf(!level.getBlockState(pos.east()).is(this)))).setValue(SOUTH, Boolean.valueOf(!level.getBlockState(pos.south()).is(this)))).setValue(WEST, Boolean.valueOf(!level.getBlockState(pos.west()).is(this)));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return neighbourState.is(this) ? (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(directionToNeighbour), Boolean.valueOf(false)) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.NORTH)), (Boolean)state.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.SOUTH)), (Boolean)state.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.EAST)), (Boolean)state.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.WEST)), (Boolean)state.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.UP)), (Boolean)state.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(rotation.rotate(Direction.DOWN)), (Boolean)state.getValue(DOWN));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), (Boolean)state.getValue(NORTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), (Boolean)state.getValue(SOUTH))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), (Boolean)state.getValue(EAST))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), (Boolean)state.getValue(WEST))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), (Boolean)state.getValue(UP))).setValue((Property)PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), (Boolean)state.getValue(DOWN));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
   }
}
