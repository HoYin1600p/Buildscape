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
            if (isMud(level.getBlockState(pos.relative(dir)))) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    private static boolean isMud(net.minecraft.world.level.block.state.BlockState state) {
        if (state == null) return false;
        com.kingodogo.buildscape.util.CommonId id = com.kingodogo.buildscape.platform.Services.PLATFORM.getBlockId(state.getBlock());
        return id != null && "buildscape".equals(id.getNamespace()) && "mud".equals(id.getPath());
    }
}
