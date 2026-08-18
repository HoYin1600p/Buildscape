package net.minecraft.world.level.block.entity;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockEntityType {
   private final BlockEntityType.BlockEntitySupplier factory;
   private final Set validBlocks;
   private final Holder.Reference builtInRegistryHolder = BuiltInRegistries.BLOCK_ENTITY_TYPE.createIntrusiveHolder(this);

   public BlockEntityType(final BlockEntityType.BlockEntitySupplier factory, final Set validBlocks) {
      this.factory = factory;
      this.validBlocks = validBlocks;
   }

   public BlockEntity create(final BlockPos worldPosition, final BlockState blockState) {
      return this.factory.create(worldPosition, blockState);
   }

   public boolean isValid(final BlockState state) {
      return this.validBlocks.contains(state.getBlock());
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public @Nullable BlockEntity getBlockEntity(final BlockGetter level, final BlockPos pos) {
      BlockEntity entity = level.getBlockEntity(pos);
      return entity != null && entity.getType() == this ? entity : null;
   }

   public boolean onlyOpCanSetNbt() {
      return BlockEntityTypes.OP_ONLY_CUSTOM_DATA.contains(this);
   }

   @FunctionalInterface
   public interface BlockEntitySupplier {
      BlockEntity create(BlockPos worldPosition, BlockState blockState);
   }
}
