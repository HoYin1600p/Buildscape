package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ChestBlock extends AbstractChestBlock implements SimpleWaterloggedBlock {
   public static final EnumProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int EVENT_SET_OPEN_COUNT = 1;
   private static final VoxelShape SHAPE = Block.column(14.0D, 0.0D, 14.0D);
   private static final Map HALF_SHAPES = Shapes.rotateHorizontal(Block.boxZ(14.0D, 0.0D, 14.0D, 0.0D, 15.0D));
   private final SoundEvent openSound;
   private final SoundEvent closeSound;
   private static final DoubleBlockCombiner.Combiner CHEST_COMBINER = new DoubleBlockCombiner.Combiner() {
      public Optional acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
         return Optional.of(new CompoundContainer(first, second));
      }

      public Optional acceptSingle(final ChestBlockEntity single) {
         return Optional.of(single);
      }

      public Optional acceptNone() {
         return Optional.empty();
      }
   };
   private static final DoubleBlockCombiner.Combiner MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner() {
      public Optional acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
         final Container container = new CompoundContainer(first, second);
         return Optional.of(new MenuProvider(this) {
            {
               Objects.requireNonNull(this$0);
            }

            public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
               if (first.canOpen(player) && second.canOpen(player)) {
                  first.unpackLootTable(inventory.player);
                  second.unpackLootTable(inventory.player);
                  return ChestMenu.sixRows(containerId, inventory, container);
               } else {
                  Direction connectedDirection = ChestBlock.getConnectedDirection(first.getBlockState());
                  Vec3 firstCenter = Vec3.atCenterOf(first.getBlockPos());
                  Vec3 centerBetweenChests = firstCenter.add((double)connectedDirection.getStepX() / 2.0D, 0.0D, (double)connectedDirection.getStepZ() / 2.0D);
                  BaseContainerBlockEntity.sendChestLockedNotifications(centerBetweenChests, player, this.getDisplayName());
                  return null;
               }
            }

            public Component getDisplayName() {
               if (first.hasCustomName()) {
                  return first.getDisplayName();
               } else {
                  return (Component)(second.hasCustomName() ? second.getDisplayName() : Component.translatable("container.chestDouble"));
               }
            }
         });
      }

      public Optional acceptSingle(final ChestBlockEntity single) {
         return Optional.of(single);
      }

      public Optional acceptNone() {
         return Optional.empty();
      }
   };

   protected ChestBlock(final Supplier blockEntityType, final SoundEvent openSound, final SoundEvent closeSound, final BlockBehaviour.Properties properties) {
      super(properties, blockEntityType);
      this.openSound = openSound;
      this.closeSound = closeSound;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public static DoubleBlockCombiner.BlockType getBlockType(final BlockState state) {
      ChestType type = (ChestType)state.getValue(TYPE);
      if (type == ChestType.SINGLE) {
         return DoubleBlockCombiner.BlockType.SINGLE;
      } else {
         return type == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
      }
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      if (this.chestCanConnectTo(neighbourState) && directionToNeighbour.getAxis().isHorizontal()) {
         ChestType neighbourType = (ChestType)neighbourState.getValue(TYPE);
         if (state.getValue(TYPE) == ChestType.SINGLE && neighbourType != ChestType.SINGLE && state.getValue(FACING) == neighbourState.getValue(FACING) && getConnectedDirection(neighbourState) == directionToNeighbour.getOpposite()) {
            return (BlockState)state.setValue(TYPE, neighbourType.getOpposite());
         }
      } else if (getConnectedDirection(state) == directionToNeighbour) {
         return (BlockState)state.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public boolean chestCanConnectTo(final BlockState blockState) {
      return blockState.is(this);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape var10000;
      switch ((ChestType)state.getValue(TYPE)) {
         case SINGLE:
            var10000 = SHAPE;
            break;
         case LEFT:
         case RIGHT:
            var10000 = (VoxelShape)HALF_SHAPES.get(getConnectedDirection(state));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static Direction getConnectedDirection(final BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);
      return state.getValue(TYPE) == ChestType.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
   }

   public static BlockPos getConnectedBlockPos(final BlockPos pos, final BlockState state) {
      Direction connectedDirection = getConnectedDirection(state);
      return pos.relative(connectedDirection);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      ChestType type = ChestType.SINGLE;
      Direction facingDirection = context.getHorizontalDirection().getOpposite();
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean secondaryUse = context.isSecondaryUseActive();
      Direction clickedFace = context.getClickedFace();
      if (clickedFace.getAxis().isHorizontal() && secondaryUse) {
         Direction neighbourFacing = this.candidatePartnerFacing(context.getLevel(), context.getClickedPos(), clickedFace.getOpposite());
         if (neighbourFacing != null && neighbourFacing.getAxis() != clickedFace.getAxis()) {
            facingDirection = neighbourFacing;
            type = neighbourFacing.getCounterClockWise() == clickedFace.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (type == ChestType.SINGLE && !secondaryUse) {
         type = this.getChestType(context.getLevel(), context.getClickedPos(), facingDirection);
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facingDirection)).setValue(TYPE, type)).setValue(WATERLOGGED, Boolean.valueOf(replacedFluidState.is(Fluids.WATER)));
   }

   protected ChestType getChestType(final Level level, final BlockPos pos, final Direction facingDirection) {
      if (facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getClockWise())) {
         return ChestType.LEFT;
      } else {
         return facingDirection == this.candidatePartnerFacing(level, pos, facingDirection.getCounterClockWise()) ? ChestType.RIGHT : ChestType.SINGLE;
      }
   }

   protected FluidState getFluidState(final BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   private @Nullable Direction candidatePartnerFacing(final Level level, final BlockPos pos, final Direction neighbourDirection) {
      BlockState state = level.getBlockState(pos.relative(neighbourDirection));
      return this.chestCanConnectTo(state) && state.getValue(TYPE) == ChestType.SINGLE ? (Direction)state.getValue(FACING) : null;
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (level instanceof ServerLevel serverLevel) {
         MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
         if (menuProvider != null) {
            player.openMenu(menuProvider);
            player.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins(serverLevel, player, true);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected Stat getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   public BlockEntityType blockEntityType() {
      return (BlockEntityType)this.blockEntityType.get();
   }

   public static @Nullable Container getContainer(final ChestBlock block, final BlockState state, final Level level, final BlockPos pos, final boolean ignoreBeingBlocked) {
      return (Container)((Optional)block.combine(state, level, pos, ignoreBeingBlocked).apply(CHEST_COMBINER)).orElse((Object)null);
   }

   public DoubleBlockCombiner.NeighborCombineResult combine(final BlockState state, final Level level, final BlockPos pos, final boolean ignoreBeingBlocked) {
      BiPredicate predicate;
      if (ignoreBeingBlocked) {
         predicate = (levelAccessor, blockPos) -> false;
      } else {
         predicate = ChestBlock::isChestBlockedAt;
      }

      return DoubleBlockCombiner.combineWithNeigbour((BlockEntityType)this.blockEntityType.get(), ChestBlock::getBlockType, ChestBlock::getConnectedDirection, FACING, state, level, pos, predicate);
   }

   protected @Nullable MenuProvider getMenuProvider(final BlockState state, final Level level, final BlockPos pos) {
      return (MenuProvider)((Optional)this.combine(state, level, pos, false).apply(MENU_PROVIDER_COMBINER)).orElse((Object)null);
   }

   public static DoubleBlockCombiner.Combiner opennessCombiner(final LidBlockEntity entity) {
      return new DoubleBlockCombiner.Combiner() {
         public Float2FloatFunction acceptDouble(final ChestBlockEntity first, final ChestBlockEntity second) {
            return (partialTickTime) -> Math.max(first.getOpenNess(partialTickTime), second.getOpenNess(partialTickTime));
         }

         public Float2FloatFunction acceptSingle(final ChestBlockEntity single) {
            return single::getOpenNess;
         }

         public Float2FloatFunction acceptNone() {
            return entity::getOpenNess;
         }
      };
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ChestBlockEntity(worldPosition, blockState);
   }

   public @Nullable BlockEntityTicker getTicker(final Level level, final BlockState blockState, final BlockEntityType type) {
      return level.isClientSide() ? createTickerHelper(type, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
   }

   public static boolean isChestBlockedAt(final LevelAccessor level, final BlockPos pos) {
      return isBlockedChestByBlock(level, pos) || isCatSittingOnChest(level, pos);
   }

   private static boolean isBlockedChestByBlock(final BlockGetter level, final BlockPos pos) {
      BlockPos above = pos.above();
      return level.getBlockState(above).isRedstoneConductor(level, above);
   }

   private static boolean isCatSittingOnChest(final LevelAccessor level, final BlockPos pos) {
      List cats = level.getEntitiesOfClass(Cat.class, new AABB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));
      if (!cats.isEmpty()) {
         for(Cat cat : cats) {
            if (cat.isInSittingPose()) {
               return true;
            }
         }
      }

      return false;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(this, state, level, pos, false));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder builder) {
      builder.add(FACING, TYPE, WATERLOGGED);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
         chestBlockEntity.recheckOpen();
      }

   }

   public SoundEvent getOpenChestSound() {
      return this.openSound;
   }

   public SoundEvent getCloseChestSound() {
      return this.closeSound;
   }
}
