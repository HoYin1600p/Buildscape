package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BrewingStandBlock extends BaseEntityBlock {
   public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
   private static final VoxelShape SHAPE = Shapes.or(Block.column(2.0D, 2.0D, 14.0D), Block.column(14.0D, 0.0D, 2.0D));

   public BrewingStandBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_BOTTLE[0], Boolean.valueOf(false))).setValue(HAS_BOTTLE[1], Boolean.valueOf(false))).setValue(HAS_BOTTLE[2], Boolean.valueOf(false)));
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new BrewingStandBlockEntity(worldPosition, blockState);
   }

   public @Nullable BlockEntityTicker getTicker(final Level level, final BlockState blockState, final BlockEntityType type) {
      BlockEntityTicker var10000;
      if (level instanceof ServerLevel serverLevel) {
         var10000 = createTickerHelper(type, BlockEntityTypes.BREWING_STAND, (var1, pos, state, entity) -> BrewingStandBlockEntity.serverTick(serverLevel, pos, state, entity));
      } else {
         var10000 = null;
      }

      return var10000;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         BlockEntity var7 = level.getBlockEntity(pos);
         if (var7 instanceof BrewingStandBlockEntity) {
            BrewingStandBlockEntity brewingStandBlockEntity = (BrewingStandBlockEntity)var7;
            player.openMenu(brewingStandBlockEntity);
            player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }
      }

      return InteractionResult.SUCCESS;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      double x = (double)pos.getX() + 0.4D + (double)random.nextFloat() * 0.2D;
      double y = (double)pos.getY() + 0.7D + (double)random.nextFloat() * 0.3D;
      double z = (double)pos.getZ() + 0.4D + (double)random.nextFloat() * 0.2D;
      level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }
}
