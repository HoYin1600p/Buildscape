package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.PlanterHelper;
import com.kingodogo.buildscape.block.PlanterHelper.PlanterType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {
    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void onCanSurvive(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.is(Blocks.COMPOSTER)) {
            if (belowState.hasProperty(PlanterHelper.PLANTER) && belowState.getValue(PlanterHelper.PLANTER) != PlanterType.NONE) {
                cir.setReturnValue(true);
            }
        }
    }
}
