package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface SegmentableBlock {
   int MIN_SEGMENT = 1;
   int MAX_SEGMENT = 4;
   IntegerProperty AMOUNT = BlockStateProperties.SEGMENT_AMOUNT;

   default Function getShapeCalculator(final EnumProperty facing, final IntegerProperty amount) {
      Map shapes = Shapes.rotateHorizontal(Block.box(0.0D, 0.0D, 0.0D, 8.0D, this.getShapeHeight(), 8.0D));
      return (state) -> {
         VoxelShape shape = Shapes.empty();
         Direction direction = (Direction)state.getValue(facing);
         int count = state.getValue(amount);

         for(int i = 0; i < count; ++i) {
            shape = Shapes.or(shape, (VoxelShape)shapes.get(direction));
            direction = direction.getCounterClockWise();
         }

         return shape.singleEncompassing();
      };
   }

   default IntegerProperty getSegmentAmountProperty() {
      return AMOUNT;
   }

   default double getShapeHeight() {
      return 1.0D;
   }

   default boolean canBeReplaced(final BlockState state, final BlockPlaceContext context, final IntegerProperty segment) {
      return !context.isSecondaryUseActive() && context.getItemInHand().is(state.getBlock().asItem()) && state.getValue(segment) < 4;
   }

   default BlockState getStateForPlacement(final BlockPlaceContext context, final Block block, final IntegerProperty segment, final EnumProperty facing) {
      BlockState state = context.getLevel().getBlockState(context.getClickedPos());
      return state.is(block) ? (BlockState)state.setValue(segment, Integer.valueOf(Math.min(4, state.getValue(segment) + 1))) : (BlockState)block.defaultBlockState().setValue(facing, context.getHorizontalDirection().getOpposite());
   }
}
