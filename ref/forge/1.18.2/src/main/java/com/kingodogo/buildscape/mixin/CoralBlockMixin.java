package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CoralBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CoralBlock.class)
public class CoralBlockMixin {
    @Inject(method = "scanForWater", at = @At("HEAD"), cancellable = true)
    private void onScanForWater(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlocks.MUD.get())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
