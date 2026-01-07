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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

public class SnowyGrassBlock extends Block implements BonemealableBlock {

    public SnowyGrassBlock(Properties properties) {
        super(properties);
    }

    private static boolean canBeGrass(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (
                aboveState.is(Blocks.SNOW) &&
                        aboveState.getValue(SnowLayerBlock.LAYERS) == 1
        ) {
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

    private static boolean canPropagate(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        BlockPos abovePos = pos.above();
        return (
                canBeGrass(state, level, pos) &&
                        !level.getFluidState(abovePos).is(FluidTags.WATER)
        );
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random
    ) {
        if (!canBeGrass(state, level, pos)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            return;
        }

        if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
            BlockState snowyGrassState = ModBlocks.SNOWY_GRASS_BLOCK.get()
                    .defaultBlockState();

            for (int i = 0; i < 4; ++i) {
                BlockPos targetPos = pos.offset(
                        random.nextInt(3) - 1,
                        random.nextInt(5) - 3,
                        random.nextInt(3) - 1
                );

                BlockState targetState = level.getBlockState(targetPos);

                if (
                        targetState.is(Blocks.DIRT) &&
                                canPropagate(snowyGrassState, level, targetPos)
                ) {
                    level.setBlockAndUpdate(targetPos, snowyGrassState);
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
        return Collections.singletonList(new ItemStack(Blocks.DIRT));
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
            net.minecraft.world.level.BlockGetter level,
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
        int radius = 1 + random.nextInt(3);
        int spawnCount;
        int weight = random.nextInt(100);
        if (weight < 30) {
            spawnCount = 5 + random.nextInt(6);
        } else if (weight < 70) {
            spawnCount = 11 + random.nextInt(5);
        } else {
            spawnCount = 16 + random.nextInt(5);
        }
        int spawnedCount = 0;
        int attempts = 0;
        int maxAttempts = radius * radius * 20;

        while (spawnedCount < spawnCount && attempts < maxAttempts) {
            attempts++;
            int offsetX = random.nextInt(radius * 2 + 1) - radius;
            int offsetZ = random.nextInt(radius * 2 + 1) - radius;
            BlockPos targetPos = pos.offset(offsetX, 0, offsetZ);
            BlockPos abovePos = targetPos.above();
            BlockState targetState = level.getBlockState(targetPos);
            BlockState aboveState = level.getBlockState(abovePos);
            if (!targetState.is(this) || !aboveState.isAir()) {
                continue;
            }
            Block foliageBlock = getRandomSnowyFoliage(random);
            if (foliageBlock != null) {
                BlockState foliageState = foliageBlock.defaultBlockState();
                if (foliageBlock instanceof net.minecraft.world.level.block.BushBlock) {
                    if (
                            !((net.minecraft.world.level.block.BushBlock) foliageBlock).canSurvive(
                                    foliageState,
                                    level,
                                    abovePos
                            )
                    ) {
                        continue;
                    }
                }
                if (foliageBlock instanceof DoublePlantBlock) {
                    foliageState = foliageState.setValue(
                            DoublePlantBlock.HALF,
                            DoubleBlockHalf.LOWER
                    );
                    BlockPos upperPos = abovePos.above();
                    if (!level.getBlockState(upperPos).isAir()) {
                        continue;
                    }
                    level.setBlock(abovePos, foliageState, 3);
                    level.setBlock(
                            upperPos,
                            foliageState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                            3
                    );
                } else {
                    level.setBlock(abovePos, foliageState, 3);
                }
                spawnedCount++;
            }
        }
    }

    private Block getRandomSnowyFoliage(Random random) {
        int roll = random.nextInt(100);
        if (roll < 50) {
            return ModBlocks.SNOWY_SHORT_GRASS.get();
        } else if (roll < 70) {
            return ModBlocks.SNOWY_BUSH.get();
        } else if (roll < 85) {
            return ModBlocks.SNOWY_FERN.get();
        } else if (roll < 90) {
            return ModBlocks.SNOWY_TALL_GRASS.get();
        } else if (roll < 95) {
            return ModBlocks.SNOWY_LARGE_FERN.get();
        } else {
            return ModBlocks.SNOWY_SHORT_GRASS.get();
        }
    }
}
