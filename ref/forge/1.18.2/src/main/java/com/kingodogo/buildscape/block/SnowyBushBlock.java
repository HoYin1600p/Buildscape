package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowyBushBlock extends BushBlock implements SinksOnFarmland {

    protected static final VoxelShape SHAPE = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );

    public SnowyBushBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ON_FARMLAND, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ON_FARMLAND);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        VoxelShape shape = SHAPE;
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
                        state.is(net.minecraft.world.level.block.Blocks.FARMLAND) ||
                        state.is(net.minecraft.world.level.block.Blocks.SNOW_BLOCK) ||
                        state.is(com.kingodogo.buildscape.block.ModBlocks.SNOW_BRICKS.get())
        );
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(ON_FARMLAND, shouldSink(context.getLevel(), context.getClickedPos()));
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, net.minecraft.core.Direction direction, BlockState adjacentState, LevelAccessor level, BlockPos pos, BlockPos adjacentPos) {
        if (direction == net.minecraft.core.Direction.DOWN) {
            return state.setValue(ON_FARMLAND, shouldSink(level, pos));
        }
        return super.updateShape(state, direction, adjacentState, level, pos, adjacentPos);
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
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
