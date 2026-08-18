package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jspecify.annotations.Nullable;

public interface EntityBlock {
   @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState);

   default @Nullable BlockEntityTicker getTicker(final Level level, final BlockState blockState, final BlockEntityType type) {
      return null;
   }

   default @Nullable GameEventListener getListener(final ServerLevel level, final BlockEntity blockEntity) {
      if (blockEntity instanceof GameEventListener.Provider provider) {
         return provider.getListener();
      } else {
         return null;
      }
   }
}
