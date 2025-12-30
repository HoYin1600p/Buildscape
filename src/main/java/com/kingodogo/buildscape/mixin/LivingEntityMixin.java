package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.ClimbableChainBlock;
import com.kingodogo.buildscape.block.LargeChainBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void onOnClimbable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Player) {
            return;
        }

        if (cir.getReturnValue()) {
            BlockPos pos = self.blockPosition();
            BlockState state = self.level.getBlockState(pos);

            if (
                    state.getBlock() instanceof ChainBlock ||
                            state.getBlock() instanceof ClimbableChainBlock ||
                            state.getBlock() instanceof LargeChainBlock
            ) {
                cir.setReturnValue(false);
            }
        }
    }
}
