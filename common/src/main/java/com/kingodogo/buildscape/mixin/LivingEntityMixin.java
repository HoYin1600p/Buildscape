package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
            Level level = Services.PLATFORM.getEntityLevel(self);
            if (level != null) {
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                if (block instanceof ChainBlock || isBuildscapeChain(block)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    private static boolean isBuildscapeChain(Block block) {
        if (block == null) return false;
        CommonId key = Services.PLATFORM.getBlockId(block);
        return key != null && "buildscape".equals(key.getNamespace()) && key.getPath().contains("chain");
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void buildscape$onLivingTick(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        com.kingodogo.buildscape.event.ModCommonEvents.onLivingUpdate(self);
        if (self instanceof Player player) {
            com.kingodogo.buildscape.event.ModCommonEvents.onPlayerTick(player);
        }
    }
}
