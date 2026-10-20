package com.kingodogo.buildscape.feature.icicle;

import com.kingodogo.buildscape.registry.items.WoodItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;
import javax.annotation.Nullable;

public class IcicleCauldronBlock extends CauldronBlock implements EntityBlock {

    private static final VoxelShape INSIDE = Block.box(
            2.0,
            4.0,
            2.0,
            14.0,
            16.0,
            14.0);
    protected static final VoxelShape SHAPE = Shapes.join(
            Shapes.block(),
            INSIDE,
            BooleanOp.ONLY_FIRST);

    public IcicleCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IcicleCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        return null;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack storedIcicle = cauldronEntity.getStoredIcicle();

            boolean isIcicleBlock = heldItem.getItem() == WoodItems.ICICLE_BLOCK.get();
            if (isIcicleBlock && storedIcicle.isEmpty()) {
                if (!level.isClientSide) {
                    cauldronEntity.setStoredIcicle(heldItem.copy().split(1));
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.BUCKET_EMPTY,
                            SoundSource.BLOCKS,
                            1.0f,
                            1.0f);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!storedIcicle.isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack extracted = storedIcicle.copy();
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, extracted);
                    } else if (!player.getInventory().add(extracted)) {
                        player.drop(extracted, false);
                    }
                    cauldronEntity.setStoredIcicle(ItemStack.EMPTY);
                    level.playSound(
                            null,
                            pos,
                            SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS,
                            0.2f,
                            1.0f);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level,
            BlockPos pos, Random random) {
        if (level.getBlockEntity(pos) instanceof IcicleCauldronBlockEntity cauldronEntity) {
            cauldronEntity.convertToRegularCauldron();
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(
            BlockState state,
            Level level,
            BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
            return cauldronEntity.hasIcicle() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
                ItemStack storedIcicle = cauldronEntity.getStoredIcicle();
                if (!storedIcicle.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(
                            level,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            storedIcicle.copy());
                    itemEntity.setDefaultPickUpDelay();
                    level.addFreshEntity(itemEntity);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
            Player player) {
        return new ItemStack(Items.CAULDRON);
    }
}
