package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class SnowyGrassBlock extends Block {

    private final Supplier<Block> snowyShortGrassSupplier;
    private final Supplier<Block> snowyBushSupplier;
    private final Supplier<Block> snowyFernSupplier;
    private final Supplier<Block> snowyTallGrassSupplier;
    private final Supplier<Block> snowyLargeFernSupplier;

    public SnowyGrassBlock(BlockBehaviour.Properties properties) {
        this(properties, null, null, null, null, null);
    }

    public SnowyGrassBlock(
            BlockBehaviour.Properties properties,
            Supplier<Block> snowyShortGrassSupplier,
            Supplier<Block> snowyBushSupplier,
            Supplier<Block> snowyFernSupplier,
            Supplier<Block> snowyTallGrassSupplier,
            Supplier<Block> snowyLargeFernSupplier
    ) {
        super(properties);
        this.snowyShortGrassSupplier = snowyShortGrassSupplier;
        this.snowyBushSupplier = snowyBushSupplier;
        this.snowyFernSupplier = snowyFernSupplier;
        this.snowyTallGrassSupplier = snowyTallGrassSupplier;
        this.snowyLargeFernSupplier = snowyLargeFernSupplier;
    }

    public List<ItemStack> getReferenceDrops(ItemStack tool) {
        if (tool != null && !tool.isEmpty() && Services.PLATFORM.hasSilkTouch(tool)) {
            return Collections.singletonList(new ItemStack(this));
        }
        return Collections.singletonList(new ItemStack(Blocks.DIRT));
    }

    public boolean isValidBonemeal(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).isAir();
    }

    public void performBonemealFoliage(ServerLevel level, BlockPos pos) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
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
            Block foliageBlock = getRandomSnowyFoliage(random.nextInt(100));
            if (foliageBlock != null) {
                BlockState foliageState = foliageBlock.defaultBlockState();
                if (!foliageState.canSurvive(level, abovePos)) {
                    continue;
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

    private Block getRandomSnowyFoliage(int roll) {
        if (roll < 50) {
            return getBlock(snowyShortGrassSupplier);
        } else if (roll < 70) {
            return getBlock(snowyBushSupplier);
        } else if (roll < 85) {
            return getBlock(snowyFernSupplier);
        } else if (roll < 90) {
            return getBlock(snowyTallGrassSupplier);
        } else if (roll < 95) {
            return getBlock(snowyLargeFernSupplier);
        } else {
            return getBlock(snowyShortGrassSupplier);
        }
    }

    private static Block getBlock(Supplier<Block> supplier) {
        return supplier != null ? supplier.get() : null;
    }
}
