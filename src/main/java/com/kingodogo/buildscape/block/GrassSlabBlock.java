package com.kingodogo.buildscape.block;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.BlockHitResult;

public class GrassSlabBlock extends SlabBlock implements BonemealableBlock {

    public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

    public GrassSlabBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SNOWY);
    }

    private static boolean isSnowySetting(LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        return aboveState.is(Blocks.SNOW) || aboveState.is(ModBlocks.SNOW_OVERLAY.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            return state.setValue(SNOWY, isSnowySetting(context.getLevel(), context.getClickedPos()));
        }
        return null;
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
        if (direction == Direction.UP) {
            return state.setValue(SNOWY, isSnowySetting(level, pos));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        }

        if (aboveState.getFluidState().getAmount() == 8) {
            return false;
        }

        int lightLevel = LayerLightEngine.getLightBlockInto(
                level,
                state,
                pos,
                aboveState,
                abovePos,
                Direction.UP,
                aboveState.getLightBlock(level, abovePos)
        );

        return lightLevel < level.getMaxLightLevel();
    }

    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        return canBeGrass(state, level, pos) && !level.getFluidState(abovePos).is(FluidTags.WATER);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (!canBeGrass(state, level, pos)) {
            // Decay to Dirt Slab
            BlockState dirtSlabState = ModBlocks.DIRT_SLAB.get().defaultBlockState()
                    .setValue(TYPE, state.getValue(TYPE))
                    .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
            level.setBlockAndUpdate(pos, dirtSlabState);
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            for (int i = 0; i < 4; ++i) {
                BlockPos targetPos = pos.offset(
                        random.nextInt(3) - 1,
                        random.nextInt(5) - 3,
                        random.nextInt(3) - 1
                );

                BlockState targetState = level.getBlockState(targetPos);

                if (targetState.is(ModBlocks.DIRT_SLAB.get()) && canPropagate(state, level, targetPos)) {
                    BlockState grassSlabState = this.defaultBlockState()
                            .setValue(TYPE, targetState.getValue(TYPE))
                            .setValue(WATERLOGGED, targetState.getValue(WATERLOGGED))
                            .setValue(SNOWY, isSnowySetting(level, targetPos));
                    level.setBlockAndUpdate(targetPos, grassSlabState);
                } else if (targetState.is(Blocks.DIRT) && canPropagate(Blocks.GRASS_BLOCK.defaultBlockState(), level, targetPos)) {
                    level.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        LootContext ctx = builder.withParameter(LootContextParams.BLOCK_STATE, state).create(LootContextParamSets.BLOCK);
        ItemStack tool = ctx.getParamOrNull(LootContextParams.TOOL);

        int count = state.getValue(TYPE) == SlabType.DOUBLE ? 2 : 1;

        if (tool != null && !tool.isEmpty()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
                return Collections.singletonList(new ItemStack(this, count));
            }
        }
        return Collections.singletonList(new ItemStack(ModBlocks.DIRT_SLAB.get(), count));
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
                if (level instanceof ServerLevel serverLevel) {
                    if (this.isBonemealSuccess(level, level.getRandom(), pos, state)) {
                        this.performBonemeal(serverLevel, level.getRandom(), pos, state);
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
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos abovePos = pos.above();
        return level.getBlockState(abovePos).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState state) {
        BlockPos abovePos = pos.above();
        BlockState foliageState = Blocks.GRASS.defaultBlockState();
        if (random.nextInt(8) == 0) {
            foliageState = state.is(ModBlocks.SNOWY_GRASS_BLOCK_SLAB.get())
                    ? ModBlocks.SNOWY_SHORT_GRASS.get().defaultBlockState()
                    : Blocks.GRASS.defaultBlockState();
        }
        if (foliageState.canSurvive(level, abovePos)) {
            level.setBlock(abovePos, foliageState, 3);
        }
    }

    @Override
    public float getDestroyProgress(
            BlockState state,
            Player player,
            BlockGetter level,
            BlockPos pos
    ) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = EnchantmentHelper.getBlockEfficiency(player);
        ItemStack tool = player.getMainHandItem();

        float speedMultiplier = 1.0F;
        if (!tool.isEmpty()) {
            speedMultiplier = tool.getDestroySpeed(state);
        }

        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0
                    ? efficiencyLevel * efficiencyLevel + 1
                    : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state)
                ? 30.0F
                : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }
}
