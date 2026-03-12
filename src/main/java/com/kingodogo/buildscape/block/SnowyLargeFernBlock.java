package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class SnowyLargeFernBlock extends DoublePlantBlock implements SinksOnFarmland {

    public SnowyLargeFernBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(HALF, ON_FARMLAND);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        net.minecraft.world.phys.shapes.VoxelShape shape = super.getShape(state, level, pos, context);
        if (state.getValue(ON_FARMLAND)) {
            return shape.move(0, -0.0625D, 0);
        }
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return (
                state.is(BlockTags.DIRT) ||
                        state.is(Blocks.FARMLAND) ||
                        state.is(Blocks.SNOW_BLOCK) ||
                        state.is(com.kingodogo.buildscape.block.ModBlocks.SNOW_BRICKS.get())
        );
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (pos.getY() < context.getLevel().getMaxBuildHeight() - 1 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) {
            BlockState state = super.getStateForPlacement(context);
            if (state != null) {
                return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), pos));
            }
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, net.minecraft.core.Direction direction, BlockState adjacentState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
        BlockState updatedState = super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
        if (!updatedState.isAir()) {
            BlockPos basePos = updatedState.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
            return updatedState.setValue(ON_FARMLAND, shouldSink(level, basePos));
        }
        return updatedState;
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        if (state.getValue(HALF) != DoubleBlockHalf.LOWER) {
            return Collections.emptyList();
        }

        LootContext ctx = builder
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .create(
                        net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK
                );
        ItemStack tool = ctx.getParamOrNull(LootContextParams.TOOL);

        if (tool != null && !tool.isEmpty()) {
            if (tool.is(Items.SHEARS)) {
                return Collections.singletonList(new ItemStack(this));
            }
            if (
                    net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                            Enchantments.SILK_TOUCH,
                            tool
                    ) >
                            0
            ) {
                return Collections.singletonList(new ItemStack(this));
            }
        }

        return Collections.emptyList();
    }
}
