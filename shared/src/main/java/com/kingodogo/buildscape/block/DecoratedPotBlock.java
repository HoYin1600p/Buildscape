package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
public class DecoratedPotBlock extends Block implements EntityBlock, SimpleWaterloggedBlock, ICommonInteractable, ICommonAnalogOutput {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public DecoratedPotBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return com.kingodogo.buildscape.platform.Services.PLATFORM.getEntityBlockRenderShape();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPotBlockEntity(pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof DecoratedPotBlockEntity potEntity) {
            return potEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof DecoratedPotBlockEntity be)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.isEmpty()) {
            if (player.isShiftKeyDown() && !be.isEmpty()) {
                if (!level.isClientSide()) {
                    ItemStack stored = be.getStoredItem().copy();
                    be.setStoredItem(ItemStack.EMPTY, true);
                    if (!player.getInventory().add(stored)) {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stored);
                    }
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
            }

            if (!player.isShiftKeyDown()) {
                if (!level.isClientSide()) {
                    level.playSound(null, pos, ModSounds.DECORATED_POT_HIT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                    be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                }
                return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
            }

            return InteractionResult.PASS;
        }

        if (heldItem.getItem() instanceof net.minecraft.world.item.BlockItem blockItem && blockItem.getBlock() instanceof DecoratedPotBlock) {
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
                be.setStoredItem(toStore, !shift);
                heldItem.shrink(toTake);
                if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                spawnInsertParticles(level, pos);
                be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
        } else if (com.kingodogo.buildscape.platform.Services.PLATFORM.isSameItemSameComponents(stored, heldItem) && stored.getCount() < stored.getMaxStackSize()) {
            if (!level.isClientSide()) {
                int space = stored.getMaxStackSize() - stored.getCount();
                int toAdd = shift ? Math.min(heldItem.getCount(), space) : 1;
                stored.grow(toAdd);
                be.setStoredItem(stored, true);
                heldItem.shrink(toAdd);
                if (heldItem.isEmpty()) player.setItemInHand(hand, ItemStack.EMPTY);
                level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_ITEM.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                spawnInsertParticles(level, pos);
                be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return com.kingodogo.buildscape.platform.Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        if (!level.isClientSide()) {
            level.playSound(null, pos, ModSounds.DECORATED_POT_INSERT_FAIL.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            be.triggerWobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.PASS;
    }

    public void dropStoredContents(Level level, BlockPos pos) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DecoratedPotBlockEntity potEntity && !potEntity.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, potEntity.getStoredItem().copy());
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        level.playSound(null, pos, ModSounds.DECORATED_POT_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    public List<ItemStack> getReferenceDrops(ItemStack tool) {
        return getReferenceDrops(tool != null && !tool.isEmpty());
    }

    public List<ItemStack> getReferenceDrops(boolean hasTool) {
        return Collections.singletonList(hasTool ? new ItemStack(Items.BRICK, 4) : new ItemStack(this));
    }
    private void spawnInsertParticles(Level level, BlockPos pos) {
        double centerX = pos.getX() + 0.5;
        double topY = pos.getY() + 1.0;
        double centerZ = pos.getZ() + 0.5;

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 2; i++) {
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, centerX, topY, centerZ, 0, 0.0, 0.1, 0.0, 1.0);
            }
        }
    }
}
