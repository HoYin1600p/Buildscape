package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherRootsBlock extends VegetationBlock {
   private static final VoxelShape SHAPE = Block.column(12.0D, 0.0D, 13.0D);
   private final TagKey supportBlocks;

   protected NetherRootsBlock(final TagKey supportBlocks, final BlockBehaviour.Properties properties) {
      super(properties);
      this.supportBlocks = supportBlocks;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.is(this.supportBlocks);
   }
}
