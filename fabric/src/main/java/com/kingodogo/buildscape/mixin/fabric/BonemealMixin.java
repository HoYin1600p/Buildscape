package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public abstract class BonemealMixin {
    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void buildscape$podzol(ItemStack stack, Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos).is(Blocks.PODZOL) && level.isEmptyBlock(pos.above())) {
            if (!level.isClientSide) {
                level.setBlock(pos.above(), com.kingodogo.buildscape.platform.Services.PLATFORM.getBlock(
                        new com.kingodogo.buildscape.util.CommonId("buildscape", "white_haze_bush")).defaultBlockState(), 3);
                stack.shrink(1);
            }
            cir.setReturnValue(true);
        }
    }
}
