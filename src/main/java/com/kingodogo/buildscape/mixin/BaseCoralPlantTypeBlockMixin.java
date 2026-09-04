package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void onCanSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.below()).is(ModBlocks.MUD.get())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "scanForWater", at = @At("HEAD"), cancellable = true)
    private static void onScanForWater(BlockState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        for (Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(ModBlocks.MUD.get())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
