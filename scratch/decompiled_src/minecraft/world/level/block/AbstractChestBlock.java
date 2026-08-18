package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractChestBlock extends BaseEntityBlock {
   protected final Supplier blockEntityType;

   protected AbstractChestBlock(final BlockBehaviour.Properties properties, final Supplier blockEntityType) {
      super(properties);
      this.blockEntityType = blockEntityType;
   }

   public abstract DoubleBlockCombiner.NeighborCombineResult combine(BlockState state, Level level, BlockPos pos, boolean ignoreBeingBlocked);
}
