package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowyShortGrassBlock
        extends BushBlock
        implements BonemealableBlock {

    protected static final VoxelShape SHAPE = Block.box(
            2.0D,
            0.0D,
            2.0D,
            14.0D,
            13.0D,
            14.0D
    );

    public SnowyShortGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return (
                state.is(BlockTags.DIRT) ||
                        state.is(net.minecraft.world.level.block.Blocks.SNOW_BLOCK) ||
                        state.is(com.kingodogo.buildscape.block.ModBlocks.SNOW_BRICKS.get())
        );
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
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof BoneMealItem) {
            if (this.isValidBonemealTarget(level, pos, state, level.isClientSide)) {
                if (level instanceof ServerLevel) {
                    if (this.isBonemealSuccess(level, level.getRandom(), pos, state)) {
                        this.performBonemeal(
                                (ServerLevel) level,
                                level.getRandom(),
                                pos,
                                state
                        );
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                        level.levelEvent(2005, pos, 0);
                    }
                }
                level.playSound(
                        player,
                        pos,
                        net.minecraft.sounds.SoundEvents.BONE_MEAL_USE,
                        net.minecraft.sounds.SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isValidBonemealTarget(
            BlockGetter level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        BlockPos abovePos = pos.above();
        return level.getBlockState(abovePos).isAir();
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        return true;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            Random random,
            BlockPos pos,
            BlockState state
    ) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.isAir()) {
            BlockPos upperPos = abovePos.above();
            BlockState upperState = level.getBlockState(upperPos);
            if (upperState.isAir()) {
                BlockState tallGrassState =
                        com.kingodogo.buildscape.block.ModBlocks.SNOWY_TALL_GRASS.get()
                                .defaultBlockState()
                                .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
                if (tallGrassState.canSurvive(level, pos)) {
                    level.setBlock(pos, tallGrassState, 3);
                    level.setBlock(
                            abovePos,
                            tallGrassState.setValue(
                                    DoublePlantBlock.HALF,
                                    DoubleBlockHalf.UPPER
                            ),
                            3
                    );
                }
            }
        }
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
