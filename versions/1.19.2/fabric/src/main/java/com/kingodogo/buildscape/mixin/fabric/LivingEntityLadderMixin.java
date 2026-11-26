package com.kingodogo.buildscape.mixin.fabric;

import com.kingodogo.buildscape.event.FabricLadderBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/** Forge asks the block at the entity's feet whether it is a ladder; Fabric has no such hook, so ask it here. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityLadderMixin {
    @Shadow private Optional<BlockPos> lastClimbablePos;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void buildscape$ladderBlocks(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isSpectator()) return;
        BlockPos pos = self.blockPosition();
        BlockState state = self.level.getBlockState(pos);
        if (FabricLadderBlocks.isLadder(state, self.level, pos, self)) {
            this.lastClimbablePos = Optional.of(pos);
            cir.setReturnValue(true);
        }
    }
}
