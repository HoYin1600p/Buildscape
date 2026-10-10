package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class IcicleCauldronBlock extends CauldronBlock implements EntityBlock, ICommonInteractable, ICommonAnalogOutput, ICommonRemoval {

    private static final VoxelShape INSIDE = Block.box(
            2.0D,
            4.0D,
            2.0D,
            14.0D,
            16.0D,
            14.0D
    );
    protected static final VoxelShape SHAPE = Shapes.join(
            Shapes.block(),
            INSIDE,
            net.minecraft.world.phys.shapes.BooleanOp.ONLY_FIRST
    );

    public IcicleCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
    public RenderShape getRenderShape(BlockState state) {
        return Services.PLATFORM.getEntityBlockRenderShape();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IcicleCauldronBlockEntity(pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutput(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
            return cauldronEntity.hasIcicle() ? 15 : 0;
        }
        return 0;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack storedIcicle = cauldronEntity.getStoredIcicle();

        boolean isIcicle = isIcicleBlockItem(heldItem);
        if (isIcicle && storedIcicle.isEmpty()) {
            if (!level.isClientSide()) {
                cauldronEntity.setStoredIcicle(heldItem.copy().split(1));
                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
                Services.PLATFORM.playBucketEmpty(level, pos);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        if (!storedIcicle.isEmpty()) {
            if (!level.isClientSide()) {
                ItemStack extracted = storedIcicle.copy();
                cauldronEntity.setStoredIcicle(ItemStack.EMPTY);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(hand, extracted);
                } else if (!player.getInventory().add(extracted)) {
                    player.drop(extracted, false);
                }
                Services.PLATFORM.playItemPickup(level, pos, 0.2f, 1.0f);
            }
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onBlockRemoved(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof IcicleCauldronBlockEntity cauldronEntity) {
                cauldronEntity.dropContents(level, pos);
            }
        }
    }
    public static boolean isIcicleBlockItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item expected = Services.PLATFORM.getItem(CommonId.of("buildscape", "icicle_block"));
        if (expected != null && stack.getItem() == expected) return true;
        String desc = stack.getItem().getDescriptionId();
        return desc.contains("icicle_block") || (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof IcicleBlock && !(bi.getBlock() instanceof PackedIcicleBlock));
    }
}
