package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.CopperRodHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {

    @Shadow
    private BlockPos getStrikePosition() {
        throw new AssertionError();
    }

    @Inject(method = "powerLightningRod", at = @At("TAIL"))
    private void buildscape$powerCopperRods(CallbackInfo ci) {
        LightningBolt bolt = (LightningBolt) (Object) this;
        BlockPos strikePos = this.getStrikePosition();
        BlockState state = bolt.level.getBlockState(strikePos);
        if (state.getBlock() instanceof LightningRodBlock && !state.is(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock) state.getBlock()).onLightningStrike(state, bolt.level, strikePos);
        }
    }

    /**
     * The handler reproduces the vanilla cleaning (vanilla copper plus Buildscape copper), so it
     * replaces the vanilla method instead of running before it; otherwise each strike cleans twice.
     */
    @Inject(method = "clearCopperOnLightningStrike", at = @At("HEAD"), cancellable = true)
    private static void buildscape$clearCopperOnLightningStrike(Level level, BlockPos pos, CallbackInfo ci) {
        CopperRodHandler.onLightningClearCopper(level, pos);
        ci.cancel();
    }
}
