package net.minecraft.world.level.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingVinesPlantBlock extends GrowingPlantBodyBlock {
   private static final VoxelShape SHAPE = Block.column(14.0D, 0.0D, 16.0D);

   public WeepingVinesPlantBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false);
   }

   protected GrowingPlantHeadBlock getHeadBlock() {
      return (GrowingPlantHeadBlock)Blocks.WEEPING_VINES;
   }
}
