package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.sound.ModSounds;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;

public class DecoratedPotBlock
        extends Block
        implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public DecoratedPotBlock() {
        super(
                Properties.of(Material.STONE)
                        .strength(0.0f)
                        .sound(createDecoratedPotSoundType())
                        .noOcclusion()
        );
        this.registerDefaultState(
                this.stateDefinition.any().setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context
                .getLevel()
                .getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
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
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPotBlockEntity(pos, state);
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
        if (be instanceof DecoratedPotBlockEntity potEntity) {
            return potEntity.getComparatorOutput();
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
        if (level.getBlockEntity(pos) instanceof DecoratedPotBlockEntity be) {
            ItemStack heldItem = player.getItemInHand(hand);

            if (!heldItem.isEmpty() && player.isShiftKeyDown()) {
                ItemStack stored = be.getStoredItem();

                if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem) {
                    Block heldBlock =
                            ((net.minecraft.world.item.BlockItem) heldItem.getItem()).getBlock();
                    if (heldBlock instanceof DecoratedPotBlock) {
                        return InteractionResult.PASS;
                    }
                }

                if (stored.isEmpty()) {
                    if (!level.isClientSide) {
                        ItemStack toStore = heldItem.copy();
                        int maxStack = toStore.getMaxStackSize();
                        int toTake = Math.min(heldItem.getCount(), maxStack);
                        toStore.setCount(toTake);
                        be.setStoredItem(toStore, false);
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
                        spawnInsertParticles(level, pos);
                        be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
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
                        int toAdd = Math.min(heldItem.getCount(), spaceAvailable);
                        int canAdd = Math.min(toAdd, spaceAvailable);
                        stored.grow(canAdd);
                        be.setStoredItem(stored, true);
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
                        spawnInsertParticles(level, pos);
                        be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            pos,
                            ModSounds.DECORATED_POT_INSERT_FAIL.get(),
                            SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                    be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.PASS;
            }

            if (heldItem.isEmpty() && !player.isShiftKeyDown()) {
                if (!level.isClientSide) {
                    level.playSound(
                            null,
                            pos,
                            ModSounds.DECORATED_POT_HIT.get(),
                            SoundSource.BLOCKS,
                            0.5f,
                            1.0f
                    );
                    be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (
                    !heldItem.isEmpty() &&
                            heldItem.getItem() instanceof net.minecraft.world.item.BlockItem
            ) {
                Block heldBlock =
                        ((net.minecraft.world.item.BlockItem) heldItem.getItem()).getBlock();
                if (heldBlock instanceof DecoratedPotBlock) {
                    return InteractionResult.PASS;
                }
            }

            if (!heldItem.isEmpty() && !player.isShiftKeyDown()) {
                ItemStack stored = be.getStoredItem();

                if (stored.isEmpty()) {
                    if (!level.isClientSide) {
                        ItemStack toStore = heldItem.copy();
                        toStore.setCount(1);
                        be.setStoredItem(toStore, true);
                        heldItem.shrink(1);
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
                        spawnInsertParticles(level, pos);
                        be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
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
                        int toAdd = 1;
                        int canAdd = Math.min(toAdd, spaceAvailable);
                        stored.grow(canAdd);
                        be.setStoredItem(stored, true);
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
                        spawnInsertParticles(level, pos);
                        be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
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
                        be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                }
            }

            if (heldItem.isEmpty() && player.isShiftKeyDown() && !be.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack stored = be.getStoredItem().copy();
                    be.setStoredItem(ItemStack.EMPTY, true);

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
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
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
        if (
                be instanceof DecoratedPotBlockEntity potEntity && !potEntity.isEmpty()
        ) {
            ItemStack stored = potEntity.getStoredItem().copy();
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
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(
            Level level,
            Player player,
            BlockPos pos,
            BlockState state,
            BlockEntity be,
            ItemStack tool
    ) {
        if (tool.isEmpty()) {
            level.playSound(
                    null,
                    pos,
                    ModSounds.DECORATED_POT_BREAK.get(),
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
        } else {
            level.playSound(
                    null,
                    pos,
                    ModSounds.DECORATED_POT_BREAK.get(),
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
        }
        super.playerDestroy(level, player, pos, state, be, tool);
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

        if (tool != null && !tool.isEmpty()) {
            return java.util.Collections.singletonList(new ItemStack(Items.BRICK, 4));
        }
        return java.util.Collections.singletonList(new ItemStack(this));
    }

    private static SoundType createDecoratedPotSoundType() {
        return new ForgeSoundType(
                1.0f,
                1.0f,
                () -> ModSounds.DECORATED_POT_BREAK.get(),
                () -> ModSounds.DECORATED_POT_STEP.get(),
                () -> ModSounds.DECORATED_POT_PLACE.get(),
                () -> ModSounds.DECORATED_POT_HIT.get(),
                () -> ModSounds.DECORATED_POT_FALL.get()
        );
    }

    private void spawnInsertParticles(Level level, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double topY = pos.getY() + 1.0;
        double centerZ = pos.getZ() + 0.5;
        double upwardVelocity = 0.1;

        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < 2; i++) {
                serverLevel.sendParticles(
                        ParticleTypes.LARGE_SMOKE,
                        centerX,
                        topY,
                        centerZ,
                        0,
                        0.0,
                        upwardVelocity,
                        0.0,
                        1.0
                );
            }
        } else if (level.isClientSide) {
            for (int i = 0; i < 2; i++) {
                level.addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        centerX,
                        topY,
                        centerZ,
                        0.0,
                        upwardVelocity,
                        0.0
                );
            }
        }
    }
}
