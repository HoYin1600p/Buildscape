package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.sound.ModSounds;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FestiveStockingBlock
        extends Block
        implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty FLIPPED = BooleanProperty.create(
            "flipped"
    );

    private static final VoxelShape SHAPE_NORTH = Block.box(
            0.0D,
            0.0D,
            15.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_SOUTH = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            1.0D
    );
    private static final VoxelShape SHAPE_EAST = Block.box(
            0.0D,
            0.0D,
            0.0D,
            1.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_WEST = Block.box(
            15.0D,
            0.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_UP = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            1.0D,
            16.0D
    );
    private static final VoxelShape SHAPE_DOWN = Block.box(
            0.0D,
            15.0D,
            0.0D,
            16.0D,
            16.0D,
            16.0D
    );

    private final String colorVariant;

    public FestiveStockingBlock(
            BlockBehaviour.Properties properties,
            String color
    ) {
        super(properties);
        this.colorVariant = color;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(FLIPPED, false)
                        .setValue(WATERLOGGED, false)
        );
    }

    public String getColorVariant() {
        return colorVariant;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, FLIPPED, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        Direction clickedFace = context.getClickedFace();
        BlockPos attachPos = context
                .getClickedPos()
                .relative(clickedFace.getOpposite());
        if (canAttachTo(context.getLevel(), attachPos, clickedFace)) {
            return this.defaultBlockState()
                    .setValue(FACING, clickedFace)
                    .setValue(FLIPPED, false)
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        }

        for (Direction direction : Direction.values()) {
            attachPos = context.getClickedPos().relative(direction.getOpposite());
            if (canAttachTo(context.getLevel(), attachPos, direction)) {
                return this.defaultBlockState()
                        .setValue(FACING, direction)
                        .setValue(FLIPPED, false)
                        .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLIPPED, false)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos attachPos = pos.relative(facing.getOpposite());
        return canAttachTo(level, attachPos, facing);
    }

    private boolean canAttachTo(
            LevelReader level,
            BlockPos pos,
            Direction direction
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
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            if (!canSurvive(state, level, pos)) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(
                state,
                direction,
                neighborState,
                level,
                pos,
                neighborPos
        );
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(state);
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
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            case UP:
                return SHAPE_UP;
            case DOWN:
                return SHAPE_DOWN;
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
        return new FestiveStockingBlockEntity(pos, state);
    }

    @Override
    public <
            T extends BlockEntity
            > net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            net.minecraft.world.level.block.entity.BlockEntityType<T> type
    ) {
        return null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(
            BlockState state,
            Level level,
            BlockPos pos
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FestiveStockingBlockEntity) {
            FestiveStockingBlockEntity stockingEntity =
                    (FestiveStockingBlockEntity) be;
            return stockingEntity.getComparatorOutput();
        }
        return 0;
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
        if (be instanceof FestiveStockingBlockEntity) {
            FestiveStockingBlockEntity stockingEntity =
                    (FestiveStockingBlockEntity) be;
            ItemStack heldItem = player.getItemInHand(hand);

            if (
                    heldItem.isEmpty() &&
                            !player.isShiftKeyDown() &&
                            !stockingEntity.isEmpty()
            ) {
                if (!level.isClientSide) {
                    ItemStack stored = stockingEntity.getStoredItem().copy();
                    stockingEntity.setStoredItem(ItemStack.EMPTY, true);

                    if (!player.getInventory().add(stored)) {
                        ItemEntity itemEntity = new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                stored
                        );
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    }
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                            SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (heldItem.isEmpty() && player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    boolean currentFlipped = state.getValue(FLIPPED);
                    level.setBlock(pos, state.setValue(FLIPPED, !currentFlipped), 3);
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.ITEM_FRAME_ROTATE_ITEM,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f
                    );
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (
                    player.isShiftKeyDown() &&
                            !heldItem.isEmpty() &&
                            !stockingEntity.isEmpty()
            ) {
                if (!level.isClientSide) {
                    ItemStack stored = stockingEntity.getStoredItem().copy();
                    stockingEntity.setStoredItem(ItemStack.EMPTY, true);

                    if (!player.getInventory().add(stored)) {
                        ItemEntity itemEntity = new ItemEntity(
                                level,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                stored
                        );
                        itemEntity.setDefaultPickUpDelay();
                        level.addFreshEntity(itemEntity);
                    }
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                            SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!heldItem.isEmpty()) {
                if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    net.minecraft.world.item.BlockItem blockItem =
                            (net.minecraft.world.item.BlockItem) heldItem.getItem();
                    if (blockItem.getBlock() instanceof FestiveStockingBlock) {
                        return InteractionResult.PASS;
                    }
                }

                ItemStack stored = stockingEntity.getStoredItem();

                if (stored.isEmpty()) {
                    if (!level.isClientSide) {
                        ItemStack toStore = heldItem.copy();
                        int maxStack = toStore.getMaxStackSize();
                        int toTake = player.isShiftKeyDown()
                                ? Math.min(heldItem.getCount(), maxStack)
                                : 1;
                        toStore.setCount(toTake);
                        stockingEntity.setStoredItem(toStore, true);
                        heldItem.shrink(toTake);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                        level.playSound(
                                null,
                                pos,
                                ModSounds.DECORATED_POT_INSERT_ITEM.get(),
                                SoundSource.BLOCKS,
                                0.5f,
                                1.0f
                        );
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else if (
                        stored.sameItem(heldItem) &&
                                stored.getCount() < stored.getMaxStackSize()
                ) {
                    if (!level.isClientSide) {
                        int maxStack = stored.getMaxStackSize();
                        int spaceAvailable = maxStack - stored.getCount();
                        int toAdd = player.isShiftKeyDown()
                                ? Math.min(heldItem.getCount(), spaceAvailable)
                                : 1;
                        int canAdd = Math.min(toAdd, spaceAvailable);
                        stored.grow(canAdd);
                        stockingEntity.setStoredItem(stored, true);
                        heldItem.shrink(canAdd);
                        if (heldItem.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                        level.playSound(
                                null,
                                pos,
                                ModSounds.DECORATED_POT_INSERT_ITEM.get(),
                                SoundSource.BLOCKS,
                                0.5f,
                                1.0f
                        );
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    if (!level.isClientSide) {
                        level.playSound(
                                null,
                                pos,
                                ModSounds.DECORATED_POT_INSERT_FAIL.get(),
                                SoundSource.BLOCKS,
                                0.5f,
                                1.0f
                        );
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
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
        if (be instanceof FestiveStockingBlockEntity) {
            FestiveStockingBlockEntity stockingEntity =
                    (FestiveStockingBlockEntity) be;
            if (!stockingEntity.isEmpty()) {
                ItemStack tool = player.getMainHandItem();
                boolean hasSilkTouch =
                        EnchantmentHelper.getItemEnchantmentLevel(
                                Enchantments.SILK_TOUCH,
                                tool
                        ) >
                                0;

                if (!hasSilkTouch) {
                    ItemStack stored = stockingEntity.getStoredItem().copy();
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            stored
                    );
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        LootContext ctx = builder
                .withParameter(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_STATE,
                        state
                )
                .create(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK
                );
        ItemStack tool = ctx.getParamOrNull(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL
        );
        BlockEntity be = ctx.getParamOrNull(
                net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY
        );
        ItemStack result = new ItemStack(this);

        if (
                tool != null &&
                        !tool.isEmpty() &&
                        EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) >
                                0 &&
                        be instanceof FestiveStockingBlockEntity
        ) {
            FestiveStockingBlockEntity stockingEntity =
                    (FestiveStockingBlockEntity) be;
            if (!stockingEntity.isEmpty()) {
                CompoundTag tag = result.getOrCreateTag();
                CompoundTag storedTag = new CompoundTag();
                stockingEntity.getStoredItem().save(storedTag);
                tag.put("StoredItem", storedTag);
            }
        }

        return List.of(result);
    }
}
