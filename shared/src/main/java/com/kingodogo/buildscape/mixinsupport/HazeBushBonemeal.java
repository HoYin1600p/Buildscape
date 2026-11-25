package com.kingodogo.buildscape.mixinsupport;

import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class HazeBushBonemeal {
    private HazeBushBonemeal() {}
    public static boolean grow(ItemStack stack, Level level, BlockPos pos) {
        if (!level.getBlockState(pos).is(Blocks.PODZOL) || !level.isEmptyBlock(pos.above())) return false;
        if (!level.isClientSide()) {
            if (!level.setBlock(pos.above(), ModBlocks.WHITE_HAZE_BUSH.get().defaultBlockState(), 3)) return false;
            stack.shrink(1);
        }
        return true;
    }
}
