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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowyTallGrassBlock extends DoublePlantBlock {

    public SnowyTallGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return (
                state.is(BlockTags.DIRT) ||
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
        return (
                pos.getY() < context.getLevel().getMaxBuildHeight() - 1 &&
                        context.getLevel().getBlockState(pos.above()).canBeReplaced(context)
        )
                ? super.getStateForPlacement(context)
                : null;
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
