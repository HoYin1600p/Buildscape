package com.kingodogo.buildscape.mixin.fabric;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fabric has no break-speed event: shears get leaf speed on hedges and leaf layers here. */
@Mixin(Player.class)
public abstract class LeafShearsSpeedMixin {
    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void buildscape$leafShearsSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(com.kingodogo.buildscape.block.LeafMiningSpeed.adjust(
                (Player) (Object) this, state, cir.getReturnValueF()));
    }
}
