package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.client.performance.BuildscapeBlockStateCacheCoordinator;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BuildscapeBlockStateCacheMixin {
    @Dynamic
    @Inject(method = "initCache", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$deferCacheInitialization(CallbackInfo callback) {
        BlockState state = (BlockState) (Object) this;
        if (BuildscapeBlockStateCacheCoordinator.deferIfBuildscape(state)) {
            callback.cancel();
        }
    }
}
