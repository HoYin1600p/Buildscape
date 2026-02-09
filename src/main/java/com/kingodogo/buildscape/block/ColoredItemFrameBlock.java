package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColoredItemFrameBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NORTH = Block.box(
            2.0D, 2.0D, 15.0D, 14.0D, 14.0D, 16.0D
    );
    private static final VoxelShape SHAPE_SOUTH = Block.box(
            2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 1.0D
    );
    private static final VoxelShape SHAPE_WEST = Block.box(
            15.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D
    );
    private static final VoxelShape SHAPE_EAST = Block.box(
            0.0D, 2.0D, 2.0D, 1.0D, 14.0D, 14.0D
    );

    private final DyeColor color;

    public ColoredItemFrameBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal()) {
            BlockPos attachPos = context.getClickedPos().relative(clickedFace.getOpposite());
            if (canAttachTo(context.getLevel(), attachPos, clickedFace)) {
                return this.defaultBlockState().setValue(FACING, clickedFace);
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos attachPos = context.getClickedPos().relative(direction.getOpposite());
            if (canAttachTo(context.getLevel(), attachPos, direction)) {
                return this.defaultBlockState().setValue(FACING, direction);
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing.getOpposite());
        return canAttachTo(level, attachPos, facing);
    }

    private boolean canAttachTo(
            LevelReader level, BlockPos pos, Direction direction
    ) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, direction);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            if (!canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(
                state, direction, neighborState, level, pos, neighborPos
        );
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            case NORTH:
            default:
                return SHAPE_NORTH;
        }
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean isMoving
    ) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColoredItemFrameBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ColoredItemFrameBlockEntity frameEntity)) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);

        if (!frameEntity.getDisplayedItem().isEmpty()) {
            if (heldItem.isEmpty()) {
                if (!level.isClientSide) {
                    int newRotation = (frameEntity.getRotation() + 1) % 8;
                    frameEntity.setRotation(newRotation);
                    level.playSound(
                            null, pos,
                            SoundEvents.ITEM_FRAME_ROTATE_ITEM,
                            SoundSource.BLOCKS, 1.0f, 1.0f
                    );
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                if (!level.isClientSide) {
                    ItemStack displayed = frameEntity.getDisplayedItem().copy();
                    frameEntity.setDisplayedItem(ItemStack.EMPTY);
                    if (!player.getInventory().add(displayed)) {
                        ItemEntity itemEntity = new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                displayed
                        );
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    }
                    level.playSound(
                            null, pos,
                            SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                            SoundSource.BLOCKS, 1.0f, 1.0f
                    );
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (!heldItem.isEmpty()) {
            if (!level.isClientSide) {
                ItemStack toPlace = heldItem.copy();
                toPlace.setCount(1);
                frameEntity.setDisplayedItem(toPlace);
                heldItem.shrink(1);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(hand, ItemStack.EMPTY);
                }
                level.playSound(
                        null, pos,
                        SoundEvents.ITEM_FRAME_ADD_ITEM,
                        SoundSource.BLOCKS, 1.0f, 1.0f
                );
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ColoredItemFrameBlockEntity frameEntity) {
            if (!frameEntity.getDisplayedItem().isEmpty() && !level.isClientSide) {
                ItemStack displayed = frameEntity.getDisplayedItem().copy();
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        displayed
                );
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(
            BlockState state, Level level, BlockPos pos
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ColoredItemFrameBlockEntity frameEntity) {
            if (!frameEntity.getDisplayedItem().isEmpty()) {
                return frameEntity.getRotation() + 1;
            }
        }
        return 0;
    }
}
