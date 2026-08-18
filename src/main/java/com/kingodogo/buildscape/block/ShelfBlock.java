package com.kingodogo.buildscape.block;

import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import com.kingodogo.buildscape.sound.ModSounds;

public class ShelfBlock extends BaseEntityBlock implements SelectableSlotContainer, SideChainPartBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<SideChainPart> SIDE_CHAIN_PART = EnumProperty.create("side_chain", SideChainPart.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   private static final VoxelShape SHAPE_NORTH = Shapes.or(
      Block.box(0.0D, 12.0D, 11.0D, 16.0D, 16.0D, 13.0D),
      Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D),
      Block.box(0.0D, 0.0D, 11.0D, 16.0D, 4.0D, 13.0D)
   );

   private static final VoxelShape SHAPE_SOUTH = Shapes.or(
      Block.box(0.0D, 12.0D, 3.0D, 16.0D, 16.0D, 5.0D),
      Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D),
      Block.box(0.0D, 0.0D, 3.0D, 16.0D, 4.0D, 5.0D)
   );

   private static final VoxelShape SHAPE_WEST = Shapes.or(
      Block.box(11.0D, 12.0D, 0.0D, 13.0D, 16.0D, 16.0D),
      Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
      Block.box(11.0D, 0.0D, 0.0D, 13.0D, 4.0D, 16.0D)
   );

   private static final VoxelShape SHAPE_EAST = Shapes.or(
      Block.box(3.0D, 12.0D, 0.0D, 5.0D, 16.0D, 16.0D),
      Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D),
      Block.box(3.0D, 0.0D, 0.0D, 5.0D, 4.0D, 16.0D)
   );

   public ShelfBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any()
              .setValue(FACING, Direction.NORTH)
              .setValue(POWERED, false)
              .setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED)
              .setValue(WATERLOGGED, false));
   }

   @Override
   public VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      switch (state.getValue(FACING)) {
         case SOUTH:
            return SHAPE_SOUTH;
         case EAST:
            return SHAPE_EAST;
         case WEST:
            return SHAPE_WEST;
         case NORTH:
         default:
            return SHAPE_NORTH;
      }
   }

   @Override
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Override
   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ShelfBlockEntity(worldPosition, blockState);
   }

   @Override
   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED);
   }

   @Override
   public void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final BlockPos fromPos, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean signal = level.hasNeighborSignal(pos);
         if (state.getValue(POWERED) != signal) {
            BlockState newState = state.setValue(POWERED, signal);
            if (!signal) {
               newState = newState.setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
            }

            level.setBlock(pos, newState, 3);
            this.playSound(level, pos, signal ? ModSounds.SHELF_ACTIVATE.get() : ModSounds.SHELF_DEACTIVATE.get());
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      return this.defaultBlockState()
              .setValue(FACING, context.getHorizontalDirection().getOpposite())
              .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
              .setValue(WATERLOGGED, replacedFluidState.getType() == Fluids.WATER);
   }

   @Override
   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
   }

   @Override
   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation(state.getValue(FACING)));
   }

   @Override
   public int getRows() {
      return 1;
   }

   @Override
   public int getColumns() {
      return 3;
   }

   @Override
   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ShelfBlockEntity shelfBlockEntity) {
         if (hand == InteractionHand.MAIN_HAND) {
            OptionalInt hitSlot = this.getHitSlot(hitResult, state.getValue(FACING));
            if (hitSlot.isEmpty()) {
               return InteractionResult.PASS;
            }

            Inventory inventory = player.getInventory();
            ItemStack itemStack = player.getItemInHand(hand);
            if (level.isClientSide()) {
               return itemStack.isEmpty() && shelfBlockEntity.getItem(hitSlot.getAsInt()).isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
            }

            if (!state.getValue(POWERED)) {
               boolean itemRemoved = swapSingleItem(itemStack, player, shelfBlockEntity, hitSlot.getAsInt(), inventory);
               if (itemRemoved) {
                  this.playSound(level, pos, itemStack.isEmpty() ? ModSounds.SHELF_TAKE_ITEM.get() : ModSounds.SHELF_SINGLE_SWAP.get());
               } else {
                  if (itemStack.isEmpty()) {
                     return InteractionResult.PASS;
                  }
                  this.playSound(level, pos, ModSounds.SHELF_PLACE_ITEM.get());
               }

               return InteractionResult.CONSUME;
            }

            boolean anySwapped = this.swapHotbar(level, pos, inventory);
            if (!anySwapped) {
               return InteractionResult.CONSUME;
            }

            this.playSound(level, pos, ModSounds.SHELF_MULTI_SWAP.get());
            return InteractionResult.CONSUME;
         }
      }

      return InteractionResult.PASS;
   }

   private static boolean swapSingleItem(final ItemStack itemStack, final Player player, final ShelfBlockEntity shelfBlockEntity, final int hitSlot, final Inventory inventory) {
      ItemStack removedItem = shelfBlockEntity.swapItemNoUpdate(hitSlot, itemStack);
      ItemStack newInventoryItem = player.getAbilities().instabuild && removedItem.isEmpty() ? itemStack.copy() : removedItem;
      inventory.setItem(inventory.selected, newInventoryItem);
      inventory.setChanged();
      shelfBlockEntity.setChanged();
      return !removedItem.isEmpty();
   }

   private boolean swapHotbar(final Level level, final BlockPos pos, final Inventory inventory) {
      List<BlockPos> connectedBlocks = this.getAllBlocksConnectedTo(level, pos);
      if (connectedBlocks.isEmpty()) {
         return false;
      } else {
         boolean anySwapped = false;

         for(int shelfPartIndex = 0; shelfPartIndex < connectedBlocks.size(); ++shelfPartIndex) {
            ShelfBlockEntity shelfPart = (ShelfBlockEntity)level.getBlockEntity(connectedBlocks.get(shelfPartIndex));
            if (shelfPart != null) {
               for(int slot = 0; slot < shelfPart.getContainerSize(); ++slot) {
                  int inventorySlot = 9 - (connectedBlocks.size() - shelfPartIndex) * shelfPart.getContainerSize() + slot;
                  if (inventorySlot >= 0 && inventorySlot < 9) {
                     ItemStack placedInventoryItem = inventory.removeItemNoUpdate(inventorySlot);
                     ItemStack removedShelfItem = shelfPart.swapItemNoUpdate(slot, placedInventoryItem);
                     if (!placedInventoryItem.isEmpty() || !removedShelfItem.isEmpty()) {
                        inventory.setItem(inventorySlot, removedShelfItem);
                        anySwapped = true;
                     }
                  }
               }

               inventory.setChanged();
               shelfPart.setChanged();
            }
         }

         return anySwapped;
      }
   }

   @Override
   public SideChainPart getSideChainPart(final BlockState state) {
      return state.getValue(SIDE_CHAIN_PART);
   }

   @Override
   public BlockState setSideChainPart(final BlockState state, final SideChainPart newPart) {
      return state.setValue(SIDE_CHAIN_PART, newPart);
   }

   @Override
   public Direction getFacing(final BlockState state) {
      return state.getValue(FACING);
   }

   @Override
   public boolean isConnectable(final BlockState state) {
      // In 1.18.2, tag checking is state.is(TagKey<Block>)
      // We will define a custom tag or use state.getBlock() instanceof ShelfBlock directly!
      // Since all shelves in our mod extend ShelfBlock, checking state.getBlock() instanceof ShelfBlock is extremely robust and avoids tag registration dependency!
      return state.getBlock() instanceof ShelfBlock && state.getValue(POWERED);
   }

   @Override
   public int getMaxChainLength() {
      return 3;
   }

   @Override
   public void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (state.getValue(POWERED)) {
         this.updateSelfAndNeighborsOnPoweringUp(level, pos, state, oldState);
      } else {
         this.updateNeighborsAfterPoweringDown(level, pos, state);
      }
   }

   @Override
   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         BlockEntity blockentity = level.getBlockEntity(pos);
         if (blockentity instanceof ShelfBlockEntity shelfBlockEntity) {
            Containers.dropContents(level, pos, shelfBlockEntity);
            level.updateNeighbourForOutputSignal(pos, this);
         }
         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   private void playSound(final Level level, final BlockPos pos, final SoundEvent sound) {
      level.playSound((Player)null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   @Override
   public FluidState getFluidState(final BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Override
   public BlockState updateShape(final BlockState state, final Direction direction, final BlockState neighborState, final LevelAccessor level, final BlockPos pos, final BlockPos neighborPos) {
      if (state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }
      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   @Override
   public boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ShelfBlockEntity shelfBlockEntity) {
         int item1Bit = shelfBlockEntity.getItem(0).isEmpty() ? 0 : 1;
         int item2Bit = shelfBlockEntity.getItem(1).isEmpty() ? 0 : 2;
         int item3Bit = shelfBlockEntity.getItem(2).isEmpty() ? 0 : 4;
         return item1Bit | item2Bit | item3Bit;
      }
      return 0;
   }
}
