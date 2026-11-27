package com.kingodogo.buildscape.mixin;

import com.kingodogo.buildscape.block.GlassJarBlock;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The jar shell belongs to its animated renderer, rather than the static chunk mesh. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class GlassJarRenderShapeMixin {
    @Dynamic
    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true, require = 0)
    private void buildscape$animatedJar(CallbackInfoReturnable<RenderShape> callback) {
        if (((BlockState) (Object) this).getBlock() instanceof GlassJarBlock)
            callback.setReturnValue(Services.PLATFORM.getEntityBlockRenderShape());
    }
}
