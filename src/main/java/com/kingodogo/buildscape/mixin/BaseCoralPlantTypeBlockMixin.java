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

    /**
     * Allow coral plants and fans to be placed on mud blocks.
     * MudBlock's collision shape is 15 units tall, so isFaceSturdy(UP) returns false
     * for mud — we override canSurvive to explicitly permit placement on mud.
     */
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void onCanSurvive(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.below()).is(ModBlocks.MUD.get())) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Treat adjacent mud as water so coral does not dry out when placed on mud.
     */
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
