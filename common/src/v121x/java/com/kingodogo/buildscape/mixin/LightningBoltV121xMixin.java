package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v121x.LightningCopperCleaning;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltV121xMixin {
    @Shadow
    private BlockPos getStrikePosition() {
        throw new AssertionError();
    }

    @Dynamic
    @Inject(method = "powerLightningRod", at = @At("TAIL"), require = 0)
    private void buildscape$powerCopperRods(CallbackInfo ci) {
        LightningBolt bolt = (LightningBolt) (Object) this;
        BlockPos pos = getStrikePosition();
        BlockState state = bolt.level().getBlockState(pos);
        if (state.getBlock() instanceof LightningRodBlock rod && !state.is(Blocks.LIGHTNING_ROD)) {
            rod.onLightningStrike(state, bolt.level(), pos);
        }
    }

    @Dynamic
    @Inject(method = "clearCopperOnLightningStrike", at = @At("HEAD"), cancellable = true, require = 0)
    private static void buildscape$clearCopper(Level level, BlockPos pos, CallbackInfo ci) {
        LightningCopperCleaning.clear(level, pos);
        ci.cancel();
    }
}
