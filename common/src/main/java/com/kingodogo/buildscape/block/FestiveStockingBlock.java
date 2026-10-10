package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
public class FestiveStockingBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ICommonNeighborAware, ICommonInteractable, ICommonAnalogOutput {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

    private static final VoxelShape SHAPE_NORTH = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SHAPE_EAST = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_WEST = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_UP = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape SHAPE_DOWN = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public FestiveStockingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLIPPED, false)
                .setValue(WATERLOGGED, false));
    }

    public String getColorVariant() {
        com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlockId(this);
        if (id != null) {
            String path = id.getPath();
            if (path.endsWith("_festive_stocking")) {
                return path.substring(0, path.length() - "_festive_stocking".length());
            }
        }
        return "festive";
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLIPPED, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        Direction clickedFace = context.getClickedFace();
        BlockPos attachPos = context.getClickedPos().relative(clickedFace.getOpposite());

        if (canAttachTo(context.getLevel(), attachPos, clickedFace)) {
            return this.defaultBlockState()
                    .setValue(FACING, clickedFace)
                    .setValue(FLIPPED, false)
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        for (Direction direction : Direction.values()) {
            attachPos = context.getClickedPos().relative(direction.getOpposite());
            if (canAttachTo(context.getLevel(), attachPos, direction)) {
                return this.defaultBlockState()
                        .setValue(FACING, direction)
                        .setValue(FLIPPED, false)
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLIPPED, false)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing.getOpposite());
        return canAttachTo(level, attachPos, facing);
    }
    private boolean canAttachTo(LevelReader level, BlockPos pos, Direction direction) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, direction);
    }

    @Override
    public void onNeighborUpdate(Level level, BlockPos pos, BlockState state, Block neighborBlock, BlockPos fromPos, boolean isMoving) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FestiveStockingBlockEntity(pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FestiveStockingBlockEntity stockingEntity) {
            return stockingEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof FestiveStockingBlockEntity be)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.isEmpty()) {
            if (player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    boolean currentFlipped = state.getValue(FLIPPED);
                    level.setBlock(pos, state.setValue(FLIPPED, !currentFlipped), 3);
                    Services.PLATFORM.playItemFrameRotate(level, pos);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.SUCCESS;
            }

            if (!be.isEmpty()) {
                if (!level.isClientSide()) {
                    ItemStack stored = be.getStoredItem().copy();
                    be.setStoredItem(ItemStack.EMPTY, true);
                    if (!player.getInventory().add(stored)) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stored);
                    }
                    Services.PLATFORM.playItemFrameRemove(level, pos);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem && blockItem.getBlock() instanceof FestiveStockingBlock) {
            return InteractionResult.PASS;
        }

        ItemStack stored = be.getStoredItem();
        boolean shift = player.isShiftKeyDown();

        if (stored.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack toStore = heldItem.copy();
                int maxStack = toStore.getMaxStackSize();
                int toTake = shift ? Math.min(heldItem.getCount(), maxStack) : 1;
                toStore.setCount(toTake);
                be.setStoredItem(toStore, true);
                heldItem.shrink(toTake);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.SUCCESS;
        } else if (com.kingodogo.buildscape.platform.Services.PLATFORM.isSameItemSameComponents(stored, heldItem) && stored.getCount() < stored.getMaxStackSize()) {
            if (!level.isClientSide()) {
                int space = stored.getMaxStackSize() - stored.getCount();
                int toAdd = shift ? Math.min(heldItem.getCount(), space) : 1;
                stored.grow(toAdd);
                be.setStoredItem(stored, true);
                heldItem.shrink(toAdd);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
