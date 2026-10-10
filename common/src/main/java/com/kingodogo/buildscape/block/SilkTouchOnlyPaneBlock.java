package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SilkTouchOnlyPaneBlock extends IronBarsBlock implements ICommonInteractable {

    public SilkTouchOnlyPaneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    public boolean shouldDisplayFluidOverlay(BlockState state, BlockGetter level, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        net.minecraft.world.item.DyeColor dyeColor = Services.PLATFORM.getDyeColor(heldItem);
        if (dyeColor != null) {
            String dyeColorName = dyeColor.getName();
            CommonId currentId = Services.PLATFORM.getBlockId(this);
            String currentName = currentId != null ? currentId.getPath() : "";

            String newName = null;
            if (currentName.startsWith("factory_")) {
                newName = "factory_" + dyeColorName + "_glass_pane";
            } else if (currentName.contains("_mosaic_glass_pane")) {
                newName = dyeColorName + "_mosaic_glass_pane";
            } else if (currentName.contains("_glazed_glass_pane")) {
                newName = dyeColorName + "_glazed_glass_pane";
            }

            if (newName != null) {
                Block newBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", newName));
                if (newBlock != null && newBlock != this) {
                    if (!level.isClientSide()) {
                        BlockState newState = newBlock.defaultBlockState();
                        if (state.hasProperty(NORTH)) newState = newState.setValue(NORTH, state.getValue(NORTH));
                        if (state.hasProperty(SOUTH)) newState = newState.setValue(SOUTH, state.getValue(SOUTH));
                        if (state.hasProperty(EAST)) newState = newState.setValue(EAST, state.getValue(EAST));
                        if (state.hasProperty(WEST)) newState = newState.setValue(WEST, state.getValue(WEST));
                        if (state.hasProperty(WATERLOGGED)) newState = newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));

                        level.setBlock(pos, newState, 3);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        Services.PLATFORM.playBlockSound(level, pos, new CommonId("minecraft", "item.dye.use"));
                    }
                    return Services.PLATFORM.sidedSuccess(level.isClientSide());
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = Services.PLATFORM.getEfficiencyLevel(player.getMainHandItem());
        ItemStack tool = player.getMainHandItem();
        float speedMultiplier = tool.isEmpty() ? 1.0F : tool.getDestroySpeed(state);
        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0 ? efficiencyLevel * efficiencyLevel + 1 : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state) ? 30.0F : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
