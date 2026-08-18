package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailBlock extends BaseRailBlock {
   public static final EnumProperty SHAPE = BlockStateProperties.RAIL_SHAPE;

   protected RailBlock(final BlockBehaviour.Properties properties) {
      super(false, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   protected void updateState(final BlockState state, final Level level, final BlockPos pos, final Block block) {
      if (block.defaultBlockState().isSignalSource() && (new RailState(level, pos, state)).countPotentialConnections() == 3) {
         this.updateDir(level, pos, state, false);
      }

   }

   public Property getShapeProperty() {
      return SHAPE;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.rotate(currentShape, rotation);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.mirror(currentShape, mirror);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(SHAPE, WATERLOGGED);
   }
}
