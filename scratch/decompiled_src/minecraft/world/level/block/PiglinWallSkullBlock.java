package net.minecraft.world.level.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PiglinWallSkullBlock extends WallSkullBlock {
   private static final Map SHAPES = Shapes.rotateHorizontal(Block.boxZ(10.0D, 8.0D, 8.0D, 16.0D));

   public PiglinWallSkullBlock(final BlockBehaviour.Properties properties) {
      super(SkullBlock.Types.PIGLIN, properties);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }
}
