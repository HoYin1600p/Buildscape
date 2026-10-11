package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.adapter.v118x.client.HazeBushParticleHandler;
import com.kingodogo.buildscape.block.HazeBushBlock;
import com.kingodogo.buildscape.block.PottedHazeBushBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class HazeBushTrackingMixin {
    @Dynamic
    @Inject(method = "setBlockState", at = @At("RETURN"), require = 0)
    private void buildscape$trackHaze(BlockPos pos, BlockState state, boolean moving, CallbackInfoReturnable<BlockState> callback) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        if (!chunk.getLevel().isClientSide() || callback.getReturnValue() == null) return;
        if (state.getBlock() instanceof HazeBushBlock || state.getBlock() instanceof PottedHazeBushBlock) {
            HazeBushParticleHandler.track(pos);
        } else {
            HazeBushParticleHandler.untrack(pos);
        }
    }
}
