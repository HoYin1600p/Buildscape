package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSkullBlock extends BaseEntityBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final SkullBlock.Type type;

   public AbstractSkullBlock(final SkullBlock.Type type, final BlockBehaviour.Properties properties) {
      super(properties);
      this.type = type;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false)));
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new SkullBlockEntity(worldPosition, blockState);
   }

   public @Nullable BlockEntityTicker getTicker(final Level level, final BlockState blockState, final BlockEntityType type) {
      if (level.isClientSide()) {
         boolean isAnimated = blockState.is(Blocks.DRAGON_HEAD) || blockState.is(Blocks.DRAGON_WALL_HEAD) || blockState.is(Blocks.PIGLIN_HEAD) || blockState.is(Blocks.PIGLIN_WALL_HEAD);
         if (isAnimated) {
            return createTickerHelper(type, BlockEntityTypes.SKULL, SkullBlockEntity::animation);
         }
      }

      return null;
   }

   public SkullBlock.Type getType() {
      return this.type;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(POWERED);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(POWERED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean signal = level.hasNeighborSignal(pos);
         if (signal != state.getValue(POWERED)) {
            level.setBlock(pos, (BlockState)state.setValue(POWERED, Boolean.valueOf(signal)), 2);
         }

      }
   }
}
